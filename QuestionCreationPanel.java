import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import components.*;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

import java.util.*;
import java.io.*;

public class QuestionCreationPanel extends JPanel implements ActionListener
{		
	private QuestionList questions;
	
	private String questionID = "";
	private int difficulty = -1;
	private String type = "";
	
	private Question questionBeingCreated; // Store the question that is currently being created
	private QuestionPanel.QuestionPanelBuilder questionPanelBeingCreated; // Store the questionPanel that is currently being created

	private JComponent[] questionComponents = new JComponent[2]; // Each question will only have 2 components
	
	private JPanel questionPreview; // To show the question being created to the user
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
	private JButton[] creationButtons = {addTextFieldButton, addRadioButtonsButton,
										 addComboboxButton, addCheckboxesButton, addPasswordFieldButton,
										 addCalendarEntryButton, addFileChooserButton}; // To handle the buttons all at once more easily
	
	
	// For the add final details panel
	private JLabel questionTitleLabel = new JLabel("Question title");
	private JLabel questionTypeLabel = new JLabel("Question type");
	private JLabel questionDifficultyLabel = new JLabel("Question difficulty");
	
	private JTextField questionTitleField = new JTextField();
	private JComboBox<String> questionTypeCombobox = new JComboBox<String>();
	private JComboBox<String> questionDifficultyCombobox = new JComboBox<String>();
	private JButton newTypeButton = new JButton("New type");
	
	private JButton saveQuestionButton = new JButton("Save Question");
	
	private Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED); // Border style
	
	public QuestionCreationPanel(QuestionList tempQuestions)
	{
		questions = tempQuestions;
		
		questionID = questions.getFreeID(); // Get a unique id for the question
		
		questionPanelBeingCreated = new QuestionPanel.QuestionPanelBuilder(questionID);
		
		prepareGUI();
		
	}
	
	private void prepareGUI()
	{
		System.out.println("[INFO] <QUESTION_CREATION_PANEL> Running prepareGUI"); // Debug
		
		prepareMainPanel();
	
	}
	
	private void prepareAddLabelPanel()
	{
		addLabelPanel = new JPanel();
		addLabelPanel.setLayout(new GridBagLayout());
		GridBagConstraints addLabelConstraints = new GridBagConstraints();
		
		
		addLabelConstraints.fill = GridBagConstraints.BOTH;
		addLabelConstraints.weightx = 1;
		addLabelConstraints.weighty = 1;
		addLabelConstraints.gridx = 0;
		addLabelConstraints.gridy = 0;
		addLabelConstraints.insets = new Insets(5,5,5,5); // 5 px padding all around
		
		addLabelPanel.add(labelTextLabel, addLabelConstraints);
		
		addLabelConstraints.gridx = 1;
		
		addLabelPanel.add(labelTextField, addLabelConstraints);
		
		addLabelPanel.setMaximumSize(new Dimension(1000, 80));
		addLabelPanel.setPreferredSize(new Dimension(1000, 80));
	}
	
	private void prepareAddComponentPanel()
	{
		prepareComponentCreationButtons();
		
		addComponentPanel = new JPanel();
		addComponentPanel.setLayout(new GridLayout(1,1));
		
		addComponentPanel.add(componentCreationButtons);
		
		addComponentPanel.setMaximumSize(new Dimension(800, 200));
		addComponentPanel.setPreferredSize(new Dimension(800, 200));
	}
	
	private void prepareEnterFinalDetailsPanel()
	{
		addFinalDetailsPanel = new JPanel();
		addFinalDetailsPanel.setLayout(new GridBagLayout());
		
		
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
		
		addFinalDetailsPanel.setMaximumSize(new Dimension(1000, 180));
		addFinalDetailsPanel.setPreferredSize(new Dimension(1000, 180));
	}
	
	private void prepareCreateQuestionPanel() // The lower half of the screen concerned with inputting the information
	{
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
		
		questionCreationStages = new JPanel[] {addLabelPanel, addComponentPanel, addFinalDetailsPanel};
		
		createQuestionPanel.add(Box.createVerticalGlue());
		createQuestionPanel.add(addLabelPanel);
		createQuestionPanel.add(addComponentPanel);
		createQuestionPanel.add(addFinalDetailsPanel);
		createQuestionPanel.add(Box.createVerticalGlue());
		
		// Setup the next back and finish buttons
		prepareButtonNavigationPanel();
		
		
		createQuestionPanel.add(buttonNavigationPanel);
		
	}
	
	private void prepareButtonNavigationPanel()
	{
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
		
		buttonNavigationPanel.setMaximumSize(new Dimension(1000, 60));
	}
	
	private void prepareMainPanel()
	{
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		this.add(Box.createVerticalGlue());
		
		prepareVisualRepresentation();
	
		this.add(questionPreview);
		
		this.add(Box.createVerticalGlue());
		
		prepareCreateQuestionPanel();
		
		this.add(createQuestionPanel);
		
		this.add(Box.createVerticalGlue());
	
		//this.add(allButtons);
		
		this.setVisible(true);
	}
	
	private void prepareComponentCreationButtons()
	{
		componentCreationButtons = new JPanel();
		
		JPanel row1 = new JPanel();
		JPanel row2 = new JPanel();
		
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
			b.setBackground(new Color(169,196,235));
		}
		
		// 4 Buttons on row 1
		row1.add(creationButtons[0]);
		row1.add(Box.createHorizontalGlue());
		row1.add(creationButtons[1]);
		row1.add(Box.createHorizontalGlue());
		row1.add(creationButtons[2]);
		row1.add(Box.createHorizontalGlue());
		row1.add(creationButtons[3]);
		
		// 3 Buttons on row 2
		row2.add(Box.createHorizontalGlue());
		row2.add(creationButtons[4]);
		row2.add(Box.createHorizontalGlue());
		row2.add(creationButtons[5]);
		row2.add(Box.createHorizontalGlue());
		row2.add(creationButtons[6]);
		row2.add(Box.createHorizontalGlue());
		
		componentCreationButtons.add(row1);
		componentCreationButtons.add(row2);
		
	}
	
	private void prepareVisualRepresentation()
	{
		questionPreview = new JPanel(); // Make the panel
		
		questionPreview.setLayout(new GridLayout(0,2)); // Set the correct layout
		
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Question Preview");
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		questionPreview.setBorder(border); // Set the border
		
		questionPreview.setPreferredSize(new Dimension(600,300));
		questionPreview.setMaximumSize(new Dimension(800,700));
		
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
		else if (evt.getSource() == saveQuestionButton) // If the save question button was pressed
		{
			System.out.println("[INFO] <QUESTION_CREATION_PANEL> saveQuestionButton pressed");
			
			getFinalDetails();
			
			questionBeingCreated = new Question(questionID, difficulty, type, "PLACE HOLDER TITLE"); // Create the question
			
			questions.addQuestion(questionBeingCreated, questionPanelBeingCreated.build()); // Add the question being created along with the question panel to the database
		
			int save = JOptionPane.showConfirmDialog(null, "Question added! Would you like to save?", "Save?", JOptionPane.YES_NO_OPTION); // Ask if they want to save
			
			if (save == 0) // If they selected yes
			{
				questions.writeDatabase();
				
				
				JOptionPane.showMessageDialog(null, "Question saved!");
			}
		}
		else if (evt.getSource() == nextButton)
		{
			System.out.println("[INFO] <QUESTION_CREATION_PANEL> nextButton pressed");
			
			// Check to see if the label has been added if we are moving from the first screen
			if (currentStage == 0)
			{
				if (labelTextField.getText().isEmpty())
				{
					JOptionPane.showMessageDialog(null, "Please enter some text for the label to have before continuing", "No label text", JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					addComponent(new JLabel(labelTextField.getText())); // Add a label with the entered text to the question
					goForward();
				}
			}
			else if (currentStage == 1) // Check to see if a component has been added before moving to the final screen
			{
				if (questionComponents[1] == null) // If no component has been added
				{
					JOptionPane.showMessageDialog(null, "Please add a component before continuing", "No component", JOptionPane.ERROR_MESSAGE);
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
	}
	
	private void goForward() // Goes forward a step
	{
		questionCreationStages[currentStage].setVisible(false); // Make the current stage invisible
		currentStage++;
		questionCreationStages[currentStage].setVisible(true); // Make the next stage visible
		
		if (currentStage > 0) // Show the back button if we're not on the first stage
		{
			backButton.setVisible(true);
		}
		
		if (currentStage == questionCreationStages.length-1) // If we're at the last stage
		{
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
	
	private void addTextField() // Adds a text field to the question
	{
		System.out.println("[INFO] <QUESTION_CREATION_PANEL> Running addTextField"); // Debug
		
		Object[] possibilities = {"none", "phone", "email"};
		String s = (String)JOptionPane.showInputDialog(null,"What validation is required?",
					"Validation?",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    possibilities,
                    "none");

		addComponent(new JValidatedTextField(s)); // Create a new JValidatedTextField of the correct type
	}

	private void addFileChooser() // Adds a file chooser to the question
	{
		System.out.println("[INFO] <QUESTION_CREATION_PANEL> Running addFileChooser"); // Debug

		Object[] possibilities = {"all", "image", "document", "video"};
		String userChoice = (String)JOptionPane.showInputDialog(null,"What types of files should be accepted?",
					"Validation?",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    possibilities,
                    "none");

		addComponent(new JValidatedFileChooser(userChoice)); // Create a new JValidatedFileChooser of the correct type
	}
	
	private void addCombobox()
	{
		System.out.println("[INFO] <QUESTION_CREATION_PANEL> Running addCombobox"); // Debug
		
		String[] options = getOptions();
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
	
	private void addRadioButtons()
	{
		System.out.println("[INFO] <QUESTION_CREATION_PANEL> Running addRadioButtons");
		
		String[] options = getOptions();
		
		RadioButtonPanel.RadioButtonPanelBuilder rBuilder = new RadioButtonPanel.RadioButtonPanelBuilder();
		for (String option : options) // For each option that the user entered
		{
			rBuilder = rBuilder.add(option);
		}
		
		addComponent(rBuilder.build());
	}
	
	private void addCheckboxes()
	{
		System.out.println("[INFO] <QUESTION_CREATION_PANEL> Running addCheckboxes"); // Debug
		
		String[] options = getOptions();
		
		CheckBoxPanel.CheckBoxPanelBuilder cBuilder = new CheckBoxPanel.CheckBoxPanelBuilder(); 
		for (String option : options) // For each option that the user entered
		{
			cBuilder = cBuilder.add(option);
		}
		
		addComponent(cBuilder.build()); // Add the checkboxpanel
	}
	
	private void addPasswordField()
	{
		System.out.println("[INFO] <QUESTION_CREATION_PANEL> Running addPasswordField");
		
		addComponent(new JValidatedPasswordField());
	}
	
	private void addCalendarEntry()
	{
		System.out.println("[INFO] <QUESTION_CREATION_PANEL> Running addCalendarEntry");
		
		
		
		addComponent(new JValidatedDatePicker());
	}
	
	private String[] getOptions() // Gets a list of options from the user
	{
		
		System.out.println("[INFO] <QUESTION_CREATION_PANEL> Running getOptions"); // Debug
		
		int finished = 0; // To be used with a confirmationDialog. Stores whether the user is finished adding options
		
		int nextOptionLocation = 0; // Store the next free space in the options array
		
		String[] tempOptions = new String[100]; // To store the options for the combobox
	
		while (finished == 0)
		{
			String option = JOptionPane.showInputDialog("Enter an option"); // Get an option
		
			tempOptions[nextOptionLocation] = option; // Store the option
			
			nextOptionLocation++;
			
			finished = JOptionPane.showConfirmDialog(null, "Would you like to add any more options?", "Finished?", JOptionPane.YES_NO_OPTION); // Returns 1 if they press no
		}
		
		String[] options = new String[nextOptionLocation]; // Create an array of the exact size required
		
		for (int i = 0; i < nextOptionLocation; i++)
		{
			options[i] = tempOptions[i]; // Copy across the options
		}
		
		return options;
	}
	
	private void updatePreview()
	{
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
	
	private void addComponent(JComponent component) // Adds the component to the question
	{
		if (component instanceof JLabel) // If it's a label
		{
			questionComponents[0] = component; // Overwrite the label
		}
		else
		{
			questionComponents[1] = component;
		}
		
		updatePreview(); // Update the question preview
	}
	
	private void getFinalDetails() // Gets the type and difficulty of the question from the user
	{
		System.out.println("[INFO] <QUESTION_CREATION_PANEL> Running getFinalDetails"); // Debug
		getType();
		difficulty = Integer.parseInt(JOptionPane.showInputDialog("What's the question's difficulty?")); // Get the question's difficulty
	}
	
	private void getType()
	{
		String[] types = questions.getTypes();
		String[] options = new String[types.length + 1];
		
		for (int i = 0; i <= types.length; i++) // For each type of question
		{
			
			if (i == types.length)
			{
				options[i] = "New type"; // Add a new type opiton
			}
			else
			{
				options[i] = types[i]; 
			}
		}
		
		type = (String)JOptionPane.showInputDialog(
                    null,
                    "What's the question's type?",
                    "Type?",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]); // Show the user a dropdown with all the current question types

		if (type.equals("New type")) // If they selected new type
		{
			System.out.println(type);
			type = JOptionPane.showInputDialog("What's the question's type?"); // Get the type
			questions.addType(type); // Add the type to the type list
		}
		
	}
	
	
}