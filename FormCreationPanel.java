import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

import java.util.*;

public class FormCreationPanel extends JPanel implements ActionListener
{
	
	private QuestionList questions;
	private FormList forms;
	private GUI gui;
	
	private String formID = "";
	
	private Form.FormBuilder formBeingCreated; // Store the form that is currently being created
	
	private JPanel entryContainerPanel = new JPanel(); // Stores all of the entry panels
	
	private SelectQuestionsPanel selectionPanel;
	private JPanel formPreview;
	private JScrollPane formPreviewScroller;
	
	private JPanel[] questionPreviews = new JPanel[50];
	
	private int nextQuestionPreviewLocation = 0;
	
	private JButton addQuestionButton = new JButton("Add");
	
	// Form information panel
	private JPanel formInformationPanel = new JPanel();
	private JLabel formTitleLabel = new JLabel("Form title");
	private JLabel formDifficultyLabel = new JLabel("Form difficulty");
	private JLabel formDescriptionLabel = new JLabel("Form description");
	private JTextField formTitleField = new JTextField();
	private JComboBox<String> formDifficultyCombobox = new JComboBox<String>(new String[] {"Please select a difficulty", "1", "2", "3", "4", "5", 
																								"6", "7", "8", "9", "10"});
	private JTextArea formDescriptionField = new JTextArea(5, 20); // 5 rows 20 columns (hints)
	private JScrollPane formDescriptionScroller = new JScrollPane(formDescriptionField);
	
	// Edit form panel
	private JPanel editFormPanel = new JPanel();
	private JLabel editFormLabel = new JLabel("Select a form to edit");
	private JComboBox<String> editFormComboBox;
	private JButton editFormButton = new JButton("Edit");
	
	// General buttons
	private JButton addHeaderButton = new JButton("Add header");
	private JButton resetFormButton = new JButton("Reset Form");
	private JButton saveFormButton = new JButton("Save Form");
	
	private ImageIcon deleteIcon;
	private ImageIcon requiredIcon;
	private ImageIcon upArrow;
	private ImageIcon downArrow;
	
	Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED); // Border style
	
	public FormCreationPanel(QuestionList tempQuestions, FormList tempForms, GUI tempGUI)
	{
		questions = tempQuestions;
		forms = tempForms;
		gui = tempGUI;
		
		formID = forms.getFreeID(); // Get a unique id for the form
		
		formBeingCreated = new Form.FormBuilder(formID, questions);
		
		prepareGUI();	
	}
	
	private void prepareGUI()
	{
		System.out.println("[INFO] <FORM_CREATION_PANEL> Running prepareGUI"); // Debug
		
		deleteIcon = new ImageIcon("bin.png");
		deleteIcon = new ImageIcon(deleteIcon.getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT)); // Make the icon smaller
		
		requiredIcon = new ImageIcon("star.png");
		requiredIcon = new ImageIcon(requiredIcon.getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT)); // Make the icon smaller
		
		upArrow = new ImageIcon("uparrow.png");
		upArrow = new ImageIcon(upArrow.getImage().getScaledInstance(30, 15, Image.SCALE_DEFAULT)); // Make the icon smaller
		
		downArrow = new ImageIcon("downarrow.png");
		downArrow = new ImageIcon(downArrow.getImage().getScaledInstance(30, 15, Image.SCALE_DEFAULT)); // Make the icon smaller
		
		prepareMainPanel();
	}
	
	private void prepareInformationPanel()
	{
		formInformationPanel.setLayout(new BoxLayout(formInformationPanel, BoxLayout.PAGE_AXIS));
		
		// Add the border
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Form information");
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		formInformationPanel.setBorder(border);
		
		formInformationPanel.setPreferredSize(new Dimension(10000, 300));
		formInformationPanel.setMaximumSize(new Dimension(10000, 500));
		
		// Form description field setup
		formDescriptionField.setEditable(true);
		formDescriptionField.setLineWrap(true);
		formDescriptionField.setWrapStyleWord(true);
		
		JPanel titlePanel = new JPanel();
		JPanel difficultyPanel = new JPanel();
		JPanel descriptionPanel = new JPanel();
		
		titlePanel.setLayout(new GridLayout(1,2));
		difficultyPanel.setLayout(new GridLayout(1,2));
		descriptionPanel.setLayout(new GridLayout(1,2));
		
		formTitleField.setMaximumSize(new Dimension(10000, 40));
		formTitleField.setPreferredSize(new Dimension(10000, 40));
		descriptionPanel.setMaximumSize(new Dimension(10000, 80));
		
		// Do each row one at a time
		titlePanel.add(formTitleLabel);
		titlePanel.add(formTitleField);
		
		difficultyPanel.add(formDifficultyLabel);
		difficultyPanel.add(formDifficultyCombobox);
		
		descriptionPanel.add(formDescriptionLabel);
		descriptionPanel.add(formDescriptionScroller);
		
		// Add them to the information panel
		formInformationPanel.add(titlePanel);
		formInformationPanel.add(Box.createVerticalGlue());
		formInformationPanel.add(difficultyPanel);
		formInformationPanel.add(Box.createVerticalGlue());
		formInformationPanel.add(descriptionPanel);
	}
	
	private void prepareEditPanel()
	{
		
		Form[] formArray = forms.getTrimmedArray(); // Get the array of forms
		
		String[] formIDs = new String[formArray.length + 1]; // Create an array to store the ids
		
		formIDs[0] = "Creating new form: " + formID; // Default entry for when a form hasn't been selected to edit
		
		for (int i = 0; i < formArray.length; i++) // For each form
		{
			formIDs[i+1] = formArray[i].getID() + ": " + formArray[i].getTitle();
		}
		
		editFormComboBox = new JComboBox<String>(formIDs); // Fill the combobox with the ids
		
		editFormPanel.setLayout(new BoxLayout(editFormPanel, BoxLayout.LINE_AXIS)); // Only 1 row
		
		editFormButton.setBackground(new Color(169,196,235));
		editFormButton.addActionListener(this);
		
		editFormComboBox.setMaximumSize(new Dimension(100, 30));
		
		editFormPanel.add(editFormLabel);
		editFormPanel.add(Box.createHorizontalGlue());
		editFormPanel.add(editFormComboBox);
		editFormPanel.add(Box.createHorizontalGlue());
		editFormPanel.add(editFormButton);
		
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Edit Forms");
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		editFormPanel.setBorder(border);
	}
	
	private void prepareMainPanel()
	{
		
		this.setLayout(new GridLayout(0,2)); // Infinite rows 2 columns
		
		// IMPLEMENT THE ENTRY CONTAINER PANEL
		entryContainerPanel.setLayout(new BoxLayout(entryContainerPanel, BoxLayout.PAGE_AXIS)); // Vertical box layout

		prepareEditPanel();
		
		entryContainerPanel.add(editFormPanel);
		
		prepareInformationPanel();
		
		entryContainerPanel.add(formInformationPanel);
		
		prepareQuestionSelectionPanel();
		
		entryContainerPanel.add(selectionPanel);
		
		addHeaderButton.addActionListener(this);
		addHeaderButton.setBackground(new Color(169,196,235));
		addHeaderButton.setMaximumSize(new Dimension(10000, 30)); // Large width to ensure that it fills the screen horizontally
		addHeaderButton.setMinimumSize(new Dimension(10000, 30)); // Large width to ensure that it fills the screen horizontally
		
		entryContainerPanel.add(addHeaderButton);
		entryContainerPanel.add(Box.createVerticalGlue());
		resetFormButton.setBackground(new Color(255,127,127)); // Make the button red
		resetFormButton.setForeground(Color.WHITE);
		resetFormButton.addActionListener(this);
		resetFormButton.setMaximumSize(new Dimension(10000, 30)); // Large width to ensure that it fills the screen horizontally
		resetFormButton.setMinimumSize(new Dimension(10000, 30)); // Large width to ensure that it fills the screen horizontally
		
		entryContainerPanel.add(resetFormButton);
		entryContainerPanel.add(Box.createVerticalGlue());
		
		saveFormButton.setBackground(new Color(130,183,75)); // Make the button green
		saveFormButton.setMaximumSize(new Dimension(10000, 30)); // Large width to ensure that it fills the screen horizontally
		saveFormButton.setMinimumSize(new Dimension(10000, 30)); // Large width to ensure that it fills the screen horizontally
		
		entryContainerPanel.add(saveFormButton);
		
		prepareFormPreview();

		
		this.add(entryContainerPanel);
		this.add(formPreviewScroller);
		
		saveFormButton.addActionListener(this);
		

		this.setVisible(true);
	}

	
	private void prepareQuestionSelectionPanel()
	{
		
		selectionPanel = new SelectQuestionsPanel(questions);
		
		addQuestionButton.addActionListener(this);
		addQuestionButton.setBackground(new Color(169,196,235));
		addQuestionButton.setMaximumSize(new Dimension(80, 40));
		selectionPanel.addNewButton(addQuestionButton);
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Select Questions");
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		selectionPanel.setBorder(border);
	}
	
	private void prepareFormPreview()
	{
		formPreview = new JPanel();
		
		formPreview.setLayout(new BoxLayout(formPreview, BoxLayout.PAGE_AXIS));
		
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Form preview");
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		formPreview.setBorder(border);
		
		formPreviewScroller = new JScrollPane(formPreview);
		formPreviewScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
	}
	
	private void saveForm()
	{
		Form existingForm = forms.getFormByID(formID); // Get the form if it exists 
		
		if (existingForm != null) // If the form already exists
		{
			forms.removeForm(existingForm.getID()); // Remove the form
		}
		
		if (formInformationPresenceCheck()) // If all of the fields have the correct data
		{
			getFinalDetails();
		
			forms.addForm(formBeingCreated.build()); // Add the form to the list
		
			int save = JOptionPane.showConfirmDialog(null, "Form added! Would you like to save?", "Save?", JOptionPane.YES_NO_OPTION); // Ask if they want to save
		
			if (save == 0) // If they selected yes
			{
				forms.writeDatabase(); 
				JOptionPane.showMessageDialog(null, "Form saved!");
			}
		}
		
	}
	
	private boolean formInformationPresenceCheck()
	{
		String errorString = "";
			
		// Check to see that they've filled it all in
		if (formTitleField.getText().isEmpty())
		{
			errorString += "Please enter a title. ";
		}
		if (formDescriptionField.getText().isEmpty())
		{
			errorString += "Please enter a description. ";
		}
		if (formDifficultyCombobox.getSelectedIndex() == 0)
		{
			errorString += "Please select a difficulty";
		}
		
		if (errorString != "") // If the user failed to fill something in
		{
			JOptionPane.showMessageDialog(null, "Please address the following errors: " + errorString, "Insufficent details entered", JOptionPane.ERROR_MESSAGE);
		}
		
		return errorString == "";
	}
	
	private void addHeader()
	{
		String headerText = JOptionPane.showInputDialog(null, "Please enter a header");
		// The input dialog will return null if the user pressed cancel
		// If this happens we should stop adding the new header
		if (headerText != null & !headerText.isEmpty()) // If they pressed ok and typed something
		{
			addComponentToForm(headerText);
		}
	}
	
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == saveFormButton) // If the save question button was pressed
		{
			System.out.println("[INFO] <FORM_CREATION_PANEL> saveFormButton pressed");
			
			saveForm();
			
		}
		else if (evt.getSource() == editFormButton)
		{
			System.out.println("[INFO] <FORM_CREATION_PANEL> editFormButton pressed");
			
			if (editFormComboBox.getSelectedIndex() != 0)
			{
				String selectedFormID = ((String) editFormComboBox.getSelectedItem()).split(":")[0]; // Get the id
				Form formBeingEdited = forms.getFormByID(selectedFormID); // Get the selected form
				
				loadFormToBeEdited(formBeingEdited); // Load the form to be edited
			}
		}
		else if (evt.getSource() == addQuestionButton)
		{
			addComponentToForm(selectionPanel.getSelectedQuestionID());
		}
		else if (evt.getSource() == addHeaderButton)
		{
			System.out.println("[INFO] <FORM_CREATION_PANEL> addHeaderButton pressed");
			addHeader();
		}
		else if (evt.getSource() == resetFormButton)
		{
			System.out.println("[INFO] <FORM_CREATION_PANEL> resetFormButton pressed");
			gui.resetTab(this); // Reset the tab
		}
		else if (evt.getSource() instanceof JButton) // If it's a JButton
		{
			JButton pressedButton = (JButton) evt.getSource(); // Get the button
			
			String questionID = pressedButton.getParent().getName(); // The name of the JPanel that the button is in is the questionID that it belongs to 
			
			if (pressedButton.getName().equals("delete")) // If it's a delete button
			{
				removeQuestionFromForm(questionID); // Remove the question from the form
			}
			else if (pressedButton.getName().equals("up")) // If an up arrow was pressed
			{
				moveQuestionUp(questionID); // Move the question up
			}
			else if (pressedButton.getName().equals("down")) // If an down arrow was pressed
			{
				moveQuestionDown(questionID); // Move the question down
			}
			else if (pressedButton.getName().equals("required"))
			{
				
				JPanel questionPreviewPanel = (JPanel) pressedButton.getParent();
				
				if (pressedButton.getIcon() == requiredIcon) // If the question was required
				{
					// Make it unrequired
					pressedButton.setIcon(null);
					formBeingCreated = formBeingCreated.setRequired(questionID, false);
				}
				else
				{
					// Make it required
					pressedButton.setIcon(requiredIcon);
					formBeingCreated = formBeingCreated.setRequired(questionID, true);
				}
			}

		}
	}
	
	private void loadFormToBeEdited(Form formToEdit)
	{
		formBeingCreated = new Form.FormBuilder(formID, questions);
		formID = formToEdit.getID();
		formPreview.removeAll(); // Clear the formPreview
		
		for (String question : formToEdit.getQuestionIDs()) // For each question currently in the form
		{
			addComponentToForm(question); // Add the question to the form preview
		}
		
		for (Component questionRow : formPreview.getComponents())
		{
			if (questionRow instanceof JPanel)
			{
				String questionID = questionRow.getName();
				JPanel questionRowPanel = (JPanel) questionRow;
				JPanel questionRowActionButtonPanel = (JPanel) questionRowPanel.getComponents()[2]; // The first item is the actual question panel and the second is some glue but we want the buttons
				
				for (Component rawButton: questionRowActionButtonPanel.getComponents()) // Iterate over the components in the questionRow's actionButton panel
				{
					if (rawButton instanceof JButton)  // If it's a button
					{
						JButton button = (JButton) rawButton;
						if (button.getName().equals("required")) // If it's the required button
						{
							button.setIcon(formToEdit.isQuestionRequired(questionID) ? requiredIcon : null); // If the question is required set the icon as the red star
						}
					}
				}				
			}
		}
		
		formTitleField.setText(formToEdit.getTitle());
		formDescriptionField.setText(formToEdit.getDescription());
		formDifficultyCombobox.setSelectedIndex(formToEdit.getDifficulty());
	}
	
	private void addComponentToForm(String component) // Adds a question or header to the preview and to the form
	{
		System.out.println("[INFO] <FORM_CREATION_PANEL> Running addComponentToForm"); // Debug
		
		JPanel questionPreviewPanel = new JPanel();
		questionPreviewPanel.setName(component); // For reference later
		
		questionPreviewPanel.setPreferredSize(new Dimension(100,30));
		questionPreviewPanel.setMaximumSize(new Dimension(700,50));
		
		questionPreviewPanel.setLayout(new BoxLayout(questionPreviewPanel, BoxLayout.LINE_AXIS)); // Horizontal box layout
		
		// If a question is being added we should add the question panel
		// otherwise we should add a header
		boolean isAQuestion = questions.getQuestionByID(component) != null; // If a question is returned then the component is a question
		if (isAQuestion)
		{
			JPanel questionPanel = questions.getPanelByID(component);
			questionPanel.setPreferredSize(new Dimension(300, 50));
			questionPreviewPanel.add(questionPanel); // Add the question panel to the preview
		}
		else // It's a header
		{
			JPanel headerPanel = new JPanel();
			headerPanel.setPreferredSize(new Dimension(300, 50));
			TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(2,0,0,0, Color.BLACK), component);
			border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
			headerPanel.setBorder(border);

			questionPreviewPanel.add(headerPanel);
			
		}
		// Prepare the actionButtonPanel
		JPanel actionButtonPanel = new JPanel();
		actionButtonPanel.setName(component); // Allows the question that the buttons belong to to be identified.
		actionButtonPanel.setLayout(new GridLayout(1,3));
		actionButtonPanel.setPreferredSize(new Dimension(90, 30));

		actionButtonPanel.add(Box.createHorizontalGlue());

		if (isAQuestion)
		{
			// Prepare the required button
			JButton requiredButton = new JButton();
			requiredButton.setBackground(new Color(169,196,235));
			requiredButton.setName("required"); // For reference later
			requiredButton.setIcon(requiredIcon);
			requiredButton.addActionListener(this);
			requiredButton.setPreferredSize(new Dimension(30, 30));
			actionButtonPanel.add(requiredButton);
		}
		
		// Prepare the delete button
		JButton deleteButton = new JButton();
		deleteButton.setIcon(deleteIcon);
		deleteButton.setBackground(new Color(169,196,235));
		deleteButton.setName("delete"); // For reference later 
		deleteButton.addActionListener(this);
		deleteButton.setPreferredSize(new Dimension(30, 30));
		actionButtonPanel.add(deleteButton);
		
		// Prepare the movement buttons
		
		JButton upButton = new JButton(); // The button text is an upwards arrow
		upButton.setIcon(upArrow);
		upButton.setName("up");
		upButton.setBackground(new Color(169,196,235));
		JButton downButton = new JButton(); // The button text is a downwards arrow
		downButton.setIcon(downArrow);
		downButton.setName("down");
		downButton.setBackground(new Color(169,196,235));
		upButton.addActionListener(this);
		downButton.addActionListener(this);
		
		JPanel upAndDownPanel = new JPanel(); // Create a JPanel to store them
		upAndDownPanel.setName(component); // Allows the question that the buttons belong to to be identified.
		upAndDownPanel.setLayout(new GridLayout(2,1)); // 2 rows 1 column
		upAndDownPanel.setMaximumSize(new Dimension(30,30));
		upAndDownPanel.add(upButton);
		upAndDownPanel.add(downButton);
		
		actionButtonPanel.add(upAndDownPanel);
		
		questionPreviewPanel.add(Box.createHorizontalGlue());
		questionPreviewPanel.add(actionButtonPanel);
		
		questionPreviews[nextQuestionPreviewLocation] = questionPreviewPanel; // Add the panel to the array
		
		nextQuestionPreviewLocation++;
		
		formBeingCreated = formBeingCreated.add(component, isAQuestion); // Add the question to the form, and make it required if it's a question
		
		updateFormPreview();
	}
	
	private void moveQuestionUp(String questionID) // Moves a question up the form
	{
		System.out.println("[INFO] <FORM_CREATION_PANEL> Running moveQuestionUp"); // Debug
		
		formBeingCreated = formBeingCreated.moveUp(questionID);
		
		int questionLocation = -1;
		
		for (int i = 0; i < nextQuestionPreviewLocation; i++) // Iterate over the question previews
		{
			if (questionPreviews[i].getName().equals(questionID)) // If we've found the required question
			{
				questionLocation = i;
				break;
			}
		}
		
		if (questionLocation > 0) // If there is room to move it up
		{
			// Swap the questions
			
			JPanel temp = questionPreviews[questionLocation - 1]; // Store the question above it
			questionPreviews[questionLocation - 1] = questionPreviews[questionLocation]; // Move it up
			questionPreviews[questionLocation] = temp; // Move the one above into the now free space
		}
		
		updateFormPreview();
	}
	
	private void moveQuestionDown(String questionID) // Moves a question down the form
	{
		System.out.println("[INFO] <FORM_CREATION_PANEL> Running moveQuestionDown"); // Debug
		
		formBeingCreated = formBeingCreated.moveDown(questionID);
		
		int questionLocation = -1;
		
		for (int i = 0; i < nextQuestionPreviewLocation; i++) // Iterate over the question previews
		{
			if (questionPreviews[i].getName().equals(questionID)) // If we've found the required question
			{
				questionLocation = i;
				break;
			}
		}
		
		if (questionLocation < nextQuestionPreviewLocation - 1) // If there is room to move it down
		{
			// Swap the questions
			
			JPanel temp = questionPreviews[questionLocation + 1]; // Store the question below it
			questionPreviews[questionLocation + 1] = questionPreviews[questionLocation]; // Move it down
			questionPreviews[questionLocation] = temp; // Move the one below it into the now free space
		}
		
		updateFormPreview();
	}

	private void updateFormPreview()
	{
		System.out.println("[INFO] <FORM_CREATION_PANEL> Running updateFormPreview"); // Debug
		
		formPreview.removeAll(); // Remove all panels from the preview
		
		for (int i = 0; i < nextQuestionPreviewLocation; i++) // For each question preview location
		{
			formPreview.add(questionPreviews[i]); // Add the preview
		}	
		
		formPreview.add(Box.createVerticalGlue());
		formPreview.validate();
		formPreview.repaint();
		formPreviewScroller.validate();
		formPreviewScroller.repaint();
	}	
	
	private void removeQuestionFromForm(String questionID) // Removes a question from the form
	{
		System.out.println("[INFO] <FORM_CREATION_PANEL> Running removeQuestionFromForm"); // Debug
		
		formBeingCreated = formBeingCreated.remove(questionID); // Remove the question from the builder
		
		JPanel[] newArray = new JPanel[questionPreviews.length]; // Create a new array of the correct size
		
		int j = 0; // The location in newArray
			
		for (int i = 0; i < nextQuestionPreviewLocation; i++)
		{
			if (!questionPreviews[i].getName().equals(questionID)) // If the question isn't the one that we don't want
			{
				newArray[j] = questionPreviews[i];
				j++;
			}
		}
	
		questionPreviews = newArray; // Overwrite the old array
		nextQuestionPreviewLocation--; // There is one less question preview  in the array so a free spot has opened
		
		updateFormPreview();
	}
	
	private void getFinalDetails() // Gets the type and difficulty of the question from the user
	{
		System.out.println("[INFO] <FORM_CREATION_PANEL> Running getFinalDetails"); // Debug
		
		String title = formTitleField.getText();
		
		String description = formDescriptionField.getText(); // Get a description of the form
		
		int difficulty = Integer.parseInt((String) formDifficultyCombobox.getSelectedItem()); // Get the form's difficulty
		
		formBeingCreated = formBeingCreated.setFinalDetails(title, description, difficulty);
	}
}