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

	private JPanel questionPreview; // To show the question being created to the user
	
	private JPanel componentCreationButtons; // Stores the buttons that are used to add components to the question
	private JButton addLabelButton = new JButton("Add label");
	private JButton addTextFieldButton = new JButton("Add text field");
	private JButton addRadioButtonsButton = new JButton("Add radio buttons");
	private JButton addComboboxButton = new JButton("Add drop-down");
	private JButton addCheckboxesButton = new JButton("Add check boxes");
	private JButton addPasswordFieldButton = new JButton("Add password field");
	private JButton addCalendarEntryButton = new JButton("Add calendar entry");
	private JButton addFileChooserButton = new JButton("Add file chooser");
	private JButton[] creationButtons = {addLabelButton, addTextFieldButton, addRadioButtonsButton,
										 addComboboxButton, addCheckboxesButton, addPasswordFieldButton,
										 addCalendarEntryButton, addFileChooserButton}; // To handle the buttons all at once more easily
	
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
	
	private void prepareMainPanel()
	{
		
		this.setLayout(new GridLayout(0,1));
		
	
		prepareVisualRepresentation();
	
		this.add(questionPreview);
		
		prepareComponentCreationButtons();

		saveQuestionButton.addActionListener(this);
		
		JPanel allButtons = new JPanel(); // To hold all of the buttons
		allButtons.setLayout(new GridLayout(1,2));
		
		allButtons.add(componentCreationButtons);
		allButtons.add(saveQuestionButton);
	
		this.add(allButtons);
		
		this.setVisible(true);
	}
	
	private void prepareComponentCreationButtons()
	{
		componentCreationButtons = new JPanel();
		
		componentCreationButtons.setLayout(new GridLayout(0,2)); // Infinite rows only 2 columns
		
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Create components");
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		componentCreationButtons.setBorder(border); // Set the border
		
		for (JButton b : creationButtons) // For each button
		{
			b.addActionListener(this);
			componentCreationButtons.add(b);
		}
		
	}
	
	private void prepareVisualRepresentation()
	{
		questionPreview = new JPanel(); // Make the panel
		
		questionPreview.setLayout(new GridLayout(0,2)); // Set the correct layout
		
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Question Preview");
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		questionPreview.setBorder(border); // Set the border
		
	}
	
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == addLabelButton) // If the add label button was pressed
		{
			System.out.println("[INFO] <QUESTION_CREATION_PANEL> addLabelButton pressed");
			
			addLabel();
		}
		else if (evt.getSource() == addTextFieldButton) // If the add text field button was pressed
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
	}
	
	private void addLabel() // Adds a label component to the question
	{
		System.out.println("[INFO] <QUESTION_CREATION_PANEL> Running addLabel"); // Debug
		
		String text = JOptionPane.showInputDialog("What should the text be?"); // Create a pop up window to ask for the text
		
		addComponent(new JLabel(text)); // Create a new JLabel with the required text and add it
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
	
	private void addComponent(JComponent component) // Adds the component to the question
	{
		// Add the label to both the visual representation and the builder
		questionPreview.add(component);
		questionPreview.revalidate();
		
		questionPanelBeingCreated = questionPanelBeingCreated.add(component);
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