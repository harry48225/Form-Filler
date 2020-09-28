package com.harry.formfiller.gui.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.harry.formfiller.gui.GUI;
import com.harry.formfiller.gui.question.QuestionPanel;
import com.harry.formfiller.gui.question.component.CheckBoxPanel;
import com.harry.formfiller.gui.question.component.JSaveableLabel;
import com.harry.formfiller.gui.question.component.JValidatedComboBox;
import com.harry.formfiller.gui.question.component.JValidatedDatePicker;
import com.harry.formfiller.gui.question.component.JValidatedFileChooser;
import com.harry.formfiller.gui.question.component.JValidatedLocationEntry;
import com.harry.formfiller.gui.question.component.JValidatedPasswordField;
import com.harry.formfiller.gui.question.component.JValidatedTextField;
import com.harry.formfiller.gui.question.component.RadioButtonPanel;
import com.harry.formfiller.question.Question;
import com.harry.formfiller.question.QuestionList;

public class QuestionCreationPanel extends JPanel implements ActionListener
{		
	/* This panel allows the admin to create questions */
	private transient QuestionList questions;
	private GUI gui;
	
	private String questionID = "";
	private String type = "";
	
	private Question questionBeingCreated; // Store the question that is currently being created
	private transient QuestionPanel.QuestionPanelBuilder questionPanelBeingCreated; // Store the questionPanel that is currently being created

	private JComponent[] questionComponents = new JComponent[2]; // Each question will only have 2 components
	
	private JPanel questionPreview; // To show the question being created to the user
	private JPanel questionPreviewOuter;
	private JPanel createQuestionPanel; // To store the three steps and the buttons
	private JPanel addLabelPanel; // The first step of the process
	private JPanel addComponentPanel; // The second step of the process
	private JPanel addFinalDetailsPanel; // The final step of the process
	private JPanel buttonNavigationPanel; // Allows the user to move between parts of the process
	
	private JPanel[] questionCreationStages;
	private int currentStage = 0;
	
	private JButton nextButton = new JButton("Next"); // Allows the user to go to the next step
	private JButton backButton = new JButton("Back"); // Allows the user to go to the previous step
	private JButton finishButton = new JButton("Finish"); // Allows the user to complete the process
	
	// For the addLabelPanel
	private JLabel labelTextLabel = new JLabel("Label text: ");
	private JTextField labelTextField = new JTextField();
	
	private JPanel componentCreationButtons; // Stores the buttons that are used to add components to the question
	private JButton addTextFieldButton = new JButton("Add text field");
	private JButton addRadioButtonsButton = new JButton("Add radio buttons");
	private JButton addComboboxButton = new JButton("Add drop-down");
	private JButton addCheckboxesButton = new JButton("Add check boxes");
	private JButton addPasswordFieldButton = new JButton("Add password field");
	private JButton addCalendarEntryButton = new JButton("Add calendar entry");
	private JButton addFileChooserButton = new JButton("Add file chooser");
	private JButton addLocationEntryButton = new JButton("Add location entry");
	private JButton[] creationButtons = {addTextFieldButton, addRadioButtonsButton,
										 addComboboxButton, addCheckboxesButton, addPasswordFieldButton,
										 addCalendarEntryButton, addFileChooserButton, addLocationEntryButton}; // To handle the buttons all at once more easily
	
	
	private EnterOptionsPanel optionEntry = new EnterOptionsPanel();
	// For the add final details panel
	private JLabel questionTitleLabel = new JLabel("Question title");
	private JLabel questionTypeLabel = new JLabel("Question type");
	private JLabel questionDifficultyLabel = new JLabel("Question difficulty");
	
	private JTextField questionTitleField = new JTextField();
	private JComboBox<String> questionTypeCombobox = new JComboBox<String>();
	private JComboBox<String> questionDifficultyCombobox = new JComboBox<String>(new String[] {"Please select a difficulty", "1", "2", "3", "4", "5", 
																								"6", "7", "8", "9", "10"});
	private JButton newTypeButton = new JButton("New type");
	
	private transient Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED); // Border style
	
	public QuestionCreationPanel(QuestionList tempQuestions, GUI tempGUI)
	{
		questions = tempQuestions;
		gui = tempGUI;
		questionID = questions.getFreeID(); // Get a unique id for the question
		
		questionPanelBeingCreated = new QuestionPanel.QuestionPanelBuilder(questionID);
		
		prepareGUI();
		
	}
	
	private void prepareGUI()
	{
		/* Sets up the panel to be displayed */
		System.out.println("[INFO] <QUESTION_CREATION_PANEL> Running prepareGUI"); // Debug
		
		prepareMainPanel();
	
	}
	
	private void prepareAddLabelPanel()
	{
		/* Prepares the panel that the user can use to add a label to the question */
		
		
		// Create the panel
		addLabelPanel = new JPanel();
		addLabelPanel.setLayout(new GridBagLayout());
		GridBagConstraints addLabelConstraints = new GridBagConstraints();
		
		// Set up constraints
		addLabelConstraints.fill = GridBagConstraints.BOTH;
		addLabelConstraints.weightx = 1;
		addLabelConstraints.weighty = 1;
		addLabelConstraints.gridx = 0;
		addLabelConstraints.gridy = 0;
		addLabelConstraints.insets = new Insets(5,5,5,5); // 5 px padding all around
		
		addLabelPanel.add(labelTextLabel, addLabelConstraints);
		
		addLabelConstraints.gridx = 1;
		
		labelTextField.addActionListener(this);
		
		addLabelPanel.add(labelTextField, addLabelConstraints);
		
		// Make the panel the correct size
		addLabelPanel.setMaximumSize(new Dimension(1000, 80));
		addLabelPanel.setPreferredSize(new Dimension(1000, 80));
	}
	
	private void prepareAddComponentPanel()
	{
		/* Prepares the panel that contains the buttons that allow the admin to add components to the question  */
		prepareComponentCreationButtons();
		
		// Make the panel
		addComponentPanel = new JPanel();
		addComponentPanel.setLayout(new GridLayout(1,1));
		
		addComponentPanel.add(componentCreationButtons);
		
		// Make it the correct size
		addComponentPanel.setMaximumSize(new Dimension(800, 200));
		addComponentPanel.setPreferredSize(new Dimension(800, 200));
	}
	
	private void updateTypeComboBox()
	{
		/* Updates the combobox that allows the admin to select the type of a question when a new type is added to the system */
		
		System.out.println("[INFO] <QUESTION_CREATION_PANEL> Running updateTypeComboBox");
		
		// Add all of the types to the combo box and  
		// a "Please select an option"
		questionTypeCombobox.removeAllItems();
		questionTypeCombobox.addItem("Please select a type");
		for (String questionType : questions.getTypes())
		{
			questionTypeCombobox.addItem(questionType);
		}
		
	}
	
	private void prepareEnterFinalDetailsPanel()
	{
		/* Prepares the last panel of the question creation process, the panel that allows the admin to enter the title, type, and difficulty. */
		addFinalDetailsPanel = new JPanel();
		addFinalDetailsPanel.setLayout(new GridBagLayout());
		
		// Prepare the constraints
		GridBagConstraints addFinalDetailsConstraints = new GridBagConstraints();
		addFinalDetailsConstraints.fill = GridBagConstraints.BOTH;
		addFinalDetailsConstraints.weightx = 1;
		addFinalDetailsConstraints.weighty = 1;
		addFinalDetailsConstraints.gridx = 0;
		addFinalDetailsConstraints.gridy = 0;
		addFinalDetailsConstraints.insets = new Insets(5,5,5,5); // 5 px padding all around
		
		// Question title
		addFinalDetailsPanel.add(questionTitleLabel, addFinalDetailsConstraints);
		addFinalDetailsConstraints.gridx = 1;
		addFinalDetailsPanel.add(questionTitleField, addFinalDetailsConstraints);
		
		// Question Type
		addFinalDetailsConstraints.gridy = 1;
		addFinalDetailsConstraints.gridx = 0;
		addFinalDetailsPanel.add(questionTypeLabel, addFinalDetailsConstraints);
		addFinalDetailsConstraints.gridx = 1;
		
		// Type combobox
		updateTypeComboBox();
		addFinalDetailsPanel.add(questionTypeCombobox, addFinalDetailsConstraints);
		addFinalDetailsConstraints.gridx = 2;
		newTypeButton.addActionListener(this);
		newTypeButton.setBackground(new Color(169,196,235));
		addFinalDetailsPanel.add(newTypeButton, addFinalDetailsConstraints);
		
		// Question difficulty
		addFinalDetailsConstraints.gridy = 2;
		addFinalDetailsConstraints.gridx = 0;
		addFinalDetailsPanel.add(questionDifficultyLabel, addFinalDetailsConstraints);
		addFinalDetailsConstraints.gridx = 1;
		
		addFinalDetailsPanel.add(questionDifficultyCombobox, addFinalDetailsConstraints);
		
		// Make the panel the correct size
		addFinalDetailsPanel.setMaximumSize(new Dimension(1000, 180));
		addFinalDetailsPanel.setPreferredSize(new Dimension(1000, 180));
	}
	
	private void prepareCreateQuestionPanel()
	{
		/* Prepares the lower half of the screen concerned with inputting the information */
		
		createQuestionPanel = new JPanel();
		
		// Setup the border
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Create question");
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		createQuestionPanel.setBorder(border); // Set the border
		
		// Setup the layout
		createQuestionPanel.setLayout(new BoxLayout(createQuestionPanel, BoxLayout.PAGE_AXIS)); // Create a new box layout that adds components top to bottom
		
		createQuestionPanel.setMaximumSize(new Dimension(1100, 1100));
		createQuestionPanel.setPreferredSize(new Dimension(900, 300));
		
		
		prepareAddLabelPanel();
		
		prepareAddComponentPanel();
		addComponentPanel.setVisible(false);
		
		prepareEnterFinalDetailsPanel();
		addFinalDetailsPanel.setVisible(false);
		
		// Store the panels in this array so that they can be easily accessed later
		questionCreationStages = new JPanel[] {addLabelPanel, addComponentPanel, addFinalDetailsPanel};
		
		// Add all of them to the panel
		createQuestionPanel.add(Box.createVerticalGlue());
		createQuestionPanel.add(addLabelPanel);
		createQuestionPanel.add(addComponentPanel);
		createQuestionPanel.add(addFinalDetailsPanel);
		createQuestionPanel.add(Box.createVerticalGlue());
		
		// Setup the next back and finish buttons
		prepareButtonNavigationPanel();
		
		// Add the navigation buttons
		createQuestionPanel.add(buttonNavigationPanel);
		
	}
	
	private void prepareButtonNavigationPanel()
	{
		/* Prepares the panel which contains the next, previous, and finish buttons */
		buttonNavigationPanel = new JPanel();
		buttonNavigationPanel.setLayout(new BoxLayout(buttonNavigationPanel, BoxLayout.LINE_AXIS)); // Create a new box layout left to right
		
		// Set the correct colours
		nextButton.setBackground(new Color(169,196,235));
		backButton.setBackground(new Color(255,127,127));
		finishButton.setBackground(new Color(130,183,75));
		
		// Add the action listener
		nextButton.addActionListener(this);
		backButton.addActionListener(this);
		finishButton.addActionListener(this);
		
		// Set the correct preferred sizes for the buttons
		nextButton.setMaximumSize(new Dimension(175, 60));
		nextButton.setPreferredSize(new Dimension(175, 60));
		
		backButton.setMaximumSize(new Dimension(175, 60));
		backButton.setPreferredSize(new Dimension(175, 60));
		
		finishButton.setMaximumSize(new Dimension(175, 60));
		finishButton.setPreferredSize(new Dimension(175, 60));
		
		// Hide the back and finish button
		nextButton.setVisible(true);
		backButton.setVisible(false);
		finishButton.setVisible(false);
		
		buttonNavigationPanel.add(backButton);
		buttonNavigationPanel.add(Box.createHorizontalGlue()); // This is invisible and will fill the space between the buttons as the window is resized.
		// It keeps the buttons at the far edges.
		
		buttonNavigationPanel.add(nextButton);
		buttonNavigationPanel.add(finishButton);
		
		// Make the panel the correct size
		buttonNavigationPanel.setMaximumSize(new Dimension(1000, 60));
	}
	
	private void prepareMainPanel()
	{
		/* Prepares the panel itself and adds all of the other panels to it */
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		this.add(Box.createVerticalGlue()); // Fills the empty vertical space
		
		prepareVisualRepresentation(); // Prepare the question preview
	
		this.add(questionPreviewOuter);
		
		this.add(Box.createVerticalGlue());
		
		prepareCreateQuestionPanel();
		
		this.add(createQuestionPanel);
		
		this.add(Box.createVerticalGlue());
		
		this.setVisible(true);
	}
	
	private void prepareComponentCreationButtons()
	{
		/* Prepares the buttons that allow the admin to add components to the question */
		
		componentCreationButtons = new JPanel();
		
		// The buttosns are in 2 rows
		JPanel row1 = new JPanel();
		JPanel row2 = new JPanel();
		
		// Give each panel a horizontal box layout
		row1.setLayout(new BoxLayout(row1, BoxLayout.LINE_AXIS));
		row2.setLayout(new BoxLayout(row2, BoxLayout.LINE_AXIS));
		
		componentCreationButtons.setLayout(new GridLayout(2,1)); // 2 rows 1 column
		
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Add Component");
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		componentCreationButtons.setBorder(border); // Set the border
		
		// Add action listeners to all buttons
		// Also set the sizes and colours
		Dimension buttonSize = new Dimension(175,60);
		for (JButton b : creationButtons) // For each button
		{
			b.addActionListener(this);
			b.setMaximumSize(buttonSize);
			b.setPreferredSize(buttonSize);
			b.setBackground(new Color(169,196,235)); // Blue
		}
		
		// 4 Buttons on row 1
		row1.add(creationButtons[0]);
		row1.add(Box.createHorizontalGlue());
		row1.add(creationButtons[1]);
		row1.add(Box.createHorizontalGlue());
		row1.add(creationButtons[2]);
		row1.add(Box.createHorizontalGlue());
		row1.add(creationButtons[3]);
		
		// 4 Buttons on row 2
		row2.add(creationButtons[4]);
		row2.add(Box.createHorizontalGlue());
		row2.add(creationButtons[5]);
		row2.add(Box.createHorizontalGlue());
		row2.add(creationButtons[6]);
		row2.add(Box.createHorizontalGlue());
		row2.add(creationButtons[7]);
		
		componentCreationButtons.add(row1);
		componentCreationButtons.add(row2);
		
	}
	
	private void prepareVisualRepresentation()
	{
		/* Prepares the question preview */
		
		// The outer panel has the border
		questionPreviewOuter = new JPanel();
		
		// This smaller inner panel contains the preview components
		questionPreview = new JPanel(); // Make the panel
		
		questionPreviewOuter.setLayout(new BoxLayout(questionPreviewOuter, BoxLayout.LINE_AXIS)); // Horizontal box layout
		
		questionPreview.setLayout(new GridLayout(0,2)); // Set the correct layout
		
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Question Preview");
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		questionPreviewOuter.setBorder(border); // Set the border
		
		questionPreviewOuter.add(Box.createHorizontalGlue());
		questionPreviewOuter.add(questionPreview);
		questionPreviewOuter.add(Box.createHorizontalGlue());
		
		// Make the panels the correct sizes
		questionPreviewOuter.setPreferredSize(new Dimension(600,300));
		questionPreviewOuter.setMaximumSize(new Dimension(1000,700));
		
		questionPreview.setPreferredSize(new Dimension(800,50));
		questionPreview.setMaximumSize(new Dimension(900,80));
		
	}
	
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == addTextFieldButton) // If the add text field button was pressed
		{
			System.out.println("[INFO] <QUESTION_CREATION_PANEL> addTextFieldButton pressed");
			
			addTextField();
		}
		else if (evt.getSource() == addComboboxButton)
		{
			System.out.println("[INFO] <QUESTION_CREATION_PANEL> addComboboxButton pressed");
			
			addCombobox();
		}
		else if (evt.getSource() == addRadioButtonsButton)
		{
			System.out.println("[INFO] <QUESTION_CREATION_PANEL> addRadioButtonsButton pressed");
			
			addRadioButtons();
		}
		else if (evt.getSource() == addCheckboxesButton)
		{
			System.out.println("[INFO] <QUESTION_CREATION_PANEL> addCheckboxesButton pressed");
			
			addCheckboxes();
		}
		else if (evt.getSource() == addPasswordFieldButton)
		{
			System.out.println("[INFO] <QUESTION_CREATION_PANEL> addPasswordFieldButton pressed");
			
			addPasswordField();
		}
		else if (evt.getSource() == addCalendarEntryButton)
		{
			System.out.println("[INFO] <QUESTION_CREATION_PANEL> addCalendarEntryButton pressed");
			
			addCalendarEntry();
		}
		else if (evt.getSource() == addFileChooserButton)
		{
			System.out.println("[INFO] <QUESTION_CREATION_PANEL> addFileChooserButton pressed");

			addFileChooser();
		}
		else if (evt.getSource() == addLocationEntryButton)
		{
			System.out.println("[INFO] <QUESTION_CREATION_PANEL> addLocationEntryButton pressed");
			
			addLocationEntry();
		}
		else if (evt.getSource() == finishButton) // If the finish button was pressed
		{
			System.out.println("[INFO] <QUESTION_CREATION_PANEL> finishButton pressed");
			
			String errorString = "";
			
			// Check to see that they've filled it all in
			if (questionTitleField.getText().isEmpty())
			{
				errorString += "Please enter a title. ";
			}
			if (questionTypeCombobox.getSelectedIndex() == 0)
			{
				errorString += "Please select a type. ";
			}
			if (questionDifficultyCombobox.getSelectedIndex() == 0)
			{
				errorString += "Please select a difficulty";
			}
			
			if (!errorString.equals("")) // If the user failed to fill something in
			{
				JOptionPane.showMessageDialog(this, "Please address the following errors: " + errorString, "Insufficent details entered", JOptionPane.ERROR_MESSAGE);
			}
			else // They entered everything correctly
			{
				// Get the info that the user has entered
				String title = questionTitleField.getText();
				int difficulty = Integer.parseInt((String) questionDifficultyCombobox.getSelectedItem());
				String selectedType = (String) questionTypeCombobox.getSelectedItem();
				
				questionBeingCreated = new Question(questionID, difficulty, selectedType, title); // Create the question
				
				questionPanelBeingCreated = questionPanelBeingCreated.add(questionComponents[0]).add(questionComponents[1]);
				questions.addQuestion(questionBeingCreated, questionPanelBeingCreated.build()); // Add the question being created along with the question panel to the database
			
				int save = JOptionPane.showConfirmDialog(this, "Question added! Would you like to save?", "Save?", JOptionPane.YES_NO_OPTION); // Ask if they want to save
				
				if (save == 0) // If they selected yes
				{
					questions.writeDatabase();
					
					
					JOptionPane.showMessageDialog(this, "Question saved!");
				}
				
				gui.resetTab(this); // Reset the questionCreationPanel
				
			}
		}
		else if (evt.getSource() == labelTextField) // Make pressing enter in the label text field the same as pressing next
		{
			nextButton.doClick();
		}
		else if (evt.getSource() == nextButton)
		{
			System.out.println("[INFO] <QUESTION_CREATION_PANEL> nextButton pressed");
			
			// Check to see if the label has been added if we are moving from the first screen
			if (currentStage == 0)
			{
				if (labelTextField.getText().trim().isEmpty()) // If no label was added
				{
					JOptionPane.showMessageDialog(this, "Please enter some text for the label to have before continuing", "No label text", JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					addComponent(new JSaveableLabel(labelTextField.getText())); // Add a label with the entered text to the question
					goForward();
				}
			}
			else if (currentStage == 1) // Check to see if a component has been added before moving to the final screen
			{
				if (questionComponents[1] == null) // If no component has been added
				{
					JOptionPane.showMessageDialog(this, "Please add a component before continuing", "No component", JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					goForward();
				}
			}
			
			
		}
		else if (evt.getSource() == backButton)
		{
			System.out.println("[INFO] <QUESTION_CREATION_PANEL> backButton pressed");
			goBackward();
		}
		else if (evt.getSource() == newTypeButton)
		{
			System.out.println("[INFO] <QUESTION_CREATION_PANEL> newTypeButton pressed");
			addNewType();
		}
	}
	
	
	private void addNewType()
	{
		/* Asks the user for a new type and adds it to the system */
		
		type = JOptionPane.showInputDialog(this, "What's the question's type?"); // Get the type
		
		if (type != null)
		{
			questions.addType(type.replace(",", "")); // Add the type to the type list after first removing commas.
			updateTypeComboBox();
			questionTypeCombobox.setSelectedIndex(questions.getTypes().length); // Select the most recently added type
			this.revalidate(); // Update the window
		}
	}
	
	private void goForward()
	{
		/* Goes forward a screen in the creation process */
		
		questionCreationStages[currentStage].setVisible(false); // Make the current stage invisible
		currentStage++;
		questionCreationStages[currentStage].setVisible(true); // Make the next stage visible
		
		if (currentStage > 0) // Show the back button if we're not on the first stage
		{
			backButton.setVisible(true);
		}
		
		if (currentStage == questionCreationStages.length-1) // If we're at the last stage
		{
			// Replace the next button with the finish button
			nextButton.setVisible(false);
			finishButton.setVisible(true);
		}
	}
	
	private void goBackward() // Goes backward a step
	{
		questionCreationStages[currentStage].setVisible(false); // Make the current stage invisible
		currentStage--;
		questionCreationStages[currentStage].setVisible(true); // Make the next stage visible
		
		if (currentStage == 0) // Show the back button if we're not on the first stage
		{
			backButton.setVisible(false);
		}
		
		if (currentStage < questionCreationStages.length-1) // If we're not at the last stage
		{
			nextButton.setVisible(true);
			finishButton.setVisible(false);
		}
	}
	
	private void addTextField()
	{
		/* Adds a text field to the question */
		
		System.out.println("[INFO] <QUESTION_CREATION_PANEL> Running addTextField"); // Debug
		
		// The validation types that the user can select
		Object[] possibilities = {"none", "phone", "email"};
		String s = (String)JOptionPane.showInputDialog(this,"What validation is required?",
					"Validation?",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    possibilities,
                    "none"); // Ask the user to select a type

		if (s != null) // If they selected an option
		{
			addComponent(new JValidatedTextField(s)); // Create a new JValidatedTextField of the correct type
		}
	}

	private void addLocationEntry()
	{
		/* Adds a location entry to the question */
		
		System.out.println("[INFO] <QUESTION_CREATION_PANEL> Running addLocationEntry");
		
		addComponent(new JValidatedLocationEntry());	
	}

	private void addFileChooser()
	{
		/* Adds a file chooser to the question */
		
		System.out.println("[INFO] <QUESTION_CREATION_PANEL> Running addFileChooser"); // Debug
	
		// The types of validation that can be added to the question
		Object[] possibilities = {"all", "image", "document", "video"};
		String userChoice = (String)JOptionPane.showInputDialog(this,"What types of files should be accepted?",
					"Validation?",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    possibilities,
                    "none"); // Ask the user to select a validation type
		
		if (userChoice != null) // If they selected an option
		{
			addComponent(new JValidatedFileChooser(userChoice)); // Create a new JValidatedFileChooser of the correct type
		}
	}
	
	private void addCombobox()
	{
		/* Allows the user to add a combobox to the question */
		System.out.println("[INFO] <QUESTION_CREATION_PANEL> Running addCombobox"); // Debug
		
		if (showEnterOptionsPanel()) // Show the option entry and run the following code if they pressed ok
		{
			String[] options = optionEntry.getOptions(); // Get the options that they entered
			String[] adjustedOptions = new String[options.length + 1]; // Create a new array with room for the select an option
			
			for (int i=0; i<adjustedOptions.length; i++)
			{
				if (i == 0) // If it 's the first option
				{
					adjustedOptions[i] = "Select an option";
				}
				else // Any other option
				{
					adjustedOptions[i] = options[i-1];
				}
			}
		
			addComponent(new JValidatedComboBox(adjustedOptions)); // Create and add the combobox
		}
	}
	
	private void addRadioButtons()
	{
		/* Allows the user to add radiobuttons to the question */
		
		System.out.println("[INFO] <QUESTION_CREATION_PANEL> Running addRadioButtons");
		
		if (showEnterOptionsPanel()) // Show the option entry and run the following code if they pressed ok
		{
			String[] options = optionEntry.getOptions(); // Get the options that they entered
			
			RadioButtonPanel.RadioButtonPanelBuilder rBuilder = new RadioButtonPanel.RadioButtonPanelBuilder();
			for (String option : options) // For each option that the user entered
			{
				rBuilder = rBuilder.add(option); // Add it to the radio button panel
			}
			
			addComponent(rBuilder.build());
		}
	}
	
	private void addCheckboxes()
	{
		/* Allows the user to add checkboxes to the question */
		System.out.println("[INFO] <QUESTION_CREATION_PANEL> Running addCheckboxes"); // Debug
		
		if (showEnterOptionsPanel()) // Show the option entry and run the following code if they pressed ok
		{
			String[] options = optionEntry.getOptions(); // Get the options that the user entered
			
			// Add the options to the checkbox builder
			CheckBoxPanel.CheckBoxPanelBuilder cBuilder = new CheckBoxPanel.CheckBoxPanelBuilder(); 
			for (String option : options) // For each option that the user entered
			{
				cBuilder = cBuilder.add(option); // Add the option
			}
			
			addComponent(cBuilder.build()); // Add the checkboxpanel
		}
	}
	
	private void addPasswordField()
	{
		/* Adds password fields to the question */
		
		System.out.println("[INFO] <QUESTION_CREATION_PANEL> Running addPasswordField");
		
		addComponent(new JValidatedPasswordField());
	}
	
	private void addCalendarEntry()
	{
		/* Adds calendar entry to the question */
		
		System.out.println("[INFO] <QUESTION_CREATION_PANEL> Running addCalendarEntry");
		
		addComponent(new JValidatedDatePicker());
	}
	
	private void updatePreview()
	{
		/* Updates the question preview by removing and readding all components */
		
		questionPreview.removeAll();
		
		// Add the non-null components to the question preview
		for (JComponent c : questionComponents)
		{
			if (c != null)
			{
				questionPreview.add(c);
			}
		}
		
		questionPreview.revalidate();
		
	}
	
	private void addComponent(JComponent component)
	{
		/* Adds a component to the question */
		
		if (component instanceof JLabel) // If it's a label
		{
			questionComponents[0] = component; // Overwrite the label
		}
		else // Add it to the entry component position
		{
			questionComponents[1] = component;
		}
		
		updatePreview(); // Update the question preview
	}
	
	private boolean showEnterOptionsPanel()
	{
		/*  Shows the enter options panel to the user and returns true if they didn't press cancel */
		int result = JOptionPane.showConfirmDialog(this, optionEntry, "Enter options", 
																		JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
																		
		return result == JOptionPane.YES_OPTION && optionEntry.getOptions().length > 0; // Return true if they pressed ok and entered at least 1 option
	}
}