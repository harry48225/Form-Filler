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
	
	private String formID = "";
	
	private Form.FormBuilder formBeingCreated; // Store the form that is currently being created
	
	private JPanel entryContainerPanel = new JPanel(); // Stores all of the entry panels
	
	private SelectQuestionsPanel selectionPanel;
	private JPanel formPreview;
	private JPanel[] questionPreviews = new JPanel[50];
	private int nextQuestionPreviewLocation = 0;
	
	private JButton addQuestionButton = new JButton("Add");
	
	private JPanel editFormPanel;
	private JLabel editFormLabel = new JLabel("Select a form to edit");
	private JComboBox<String> editFormComboBox;
	private JButton editFormButton = new JButton("Edit form");
	
	private JButton saveFormButton = new JButton("Save Form");
	
	Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED); // Border style
	
	public FormCreationPanel(QuestionList tempQuestions, FormList tempForms)
	{
		questions = tempQuestions;
		forms = tempForms;
		
		formID = forms.getFreeID(); // Get a unique id for the form
		
		formBeingCreated = new Form.FormBuilder(formID, questions);
		
		prepareGUI();
		
	}
	
	private void prepareGUI()
	{
		System.out.println("[INFO] <FORM_CREATION_PANEL> Running prepareGUI"); // Debug
		
		prepareMainPanel();
	
	}
	
	private void prepareEditPanel()
	{
		
		Form[] formArray = forms.getTrimmedArray(); // Get the array of forms
		
		String[] formIDs = new String[formArray.length + 1]; // Create an array to store the ids
		
		formIDs[0] = "Creating new form: " + formID; // Default entry for when a form hasn't been selected to edit
		
		for (int i = 0; i < formArray.length; i++) // For each form
		{
			formIDs[i+1] = formArray[i].getID();
		}
		
		editFormComboBox = new JComboBox<String>(formIDs); // Fill the combobox with the ids
		
		editFormPanel = new JPanel();
		
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
		//Font currentFont = border.getTitleFont();
		//border.setTitleFont(currentFont.deriveFont(Font.BOLD, 16)); // Make the font larger and bold
		
		editFormPanel.setBorder(border);
	}
	
	private void prepareMainPanel()
	{
		
		this.setLayout(new GridLayout(0,2)); // Infinite rows 2 columns
		
		// IMPLEMENT THE ENTRY CONTAINER PANEL
		entryContainerPanel.setLayout(new BoxLayout(entryContainerPanel, BoxLayout.PAGE_AXIS)); // Vertical box layout

		prepareEditPanel();
		
		entryContainerPanel.add(editFormPanel);
		
		prepareQuestionSelectionPanel();
		
		entryContainerPanel.add(selectionPanel);
		entryContainerPanel.add(saveFormButton);
		
		prepareFormPreview();
		
		this.add(entryContainerPanel);
		this.add(formPreview);
		
		
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
		
		formPreview.setLayout(new GridLayout(0,1)); // Infinite rows 1 column
		
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Form preview");
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		formPreview.setBorder(border);
		
	}
	
	private void saveForm()
	{
		Form existingForm = forms.getFormByID(formID); // Get the form if it exists 
		
		if (existingForm != null) // If the form already exists
		{
			forms.removeForm(existingForm.getID()); // Remove the form
		}
		
		getFinalDetails();
		
		forms.addForm(formBeingCreated.build()); // Add the form to the list
		
		int save = JOptionPane.showConfirmDialog(null, "Form added! Would you like to save?", "Save?", JOptionPane.YES_NO_OPTION); // Ask if they want to save
		
		if (save == 0) // If they selected yes
		{
			forms.writeDatabase(); 
			JOptionPane.showMessageDialog(null, "Form saved!");
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
			
			Form formBeingEdited = forms.getFormByID((String) editFormComboBox.getSelectedItem()); // Get the selected form
			
			loadFormToBeEdited(formBeingEdited); // Load the form to be edited
		}
		else if (evt.getSource() == addQuestionButton)
		{
			addQuestionToForm(selectionPanel.getSelectedQuestionID());
		}
		else if (evt.getSource() instanceof JButton) // If it's a JButton
		{
			JButton pressedButton = (JButton) evt.getSource(); // Get the button
			
			String questionID = pressedButton.getParent().getName(); // The name of the JPanel that the button is in is the questionID that it belongs to 
			
			if (pressedButton.getText().equals("delete")) // If it's a delete button
			{
				removeQuestionFromForm(questionID); // Remove the question from the form
			}
			else if (pressedButton.getText().equals("/\\")) // If an up arrow was pressed
			{
				moveQuestionUp(questionID); // Move the question up
			}
			else if (pressedButton.getText().equals("\\/")) // If an down arrow was pressed
			{
				moveQuestionDown(questionID); // Move the question down
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
			addQuestionToForm(question); // Add the question to the form preview
		}
	}
	
	private void addQuestionToForm(String questionID) // Adds a question to the preview and to the form
	{
		System.out.println("[INFO] <FORM_CREATION_PANEL> Running addQuestionToForm"); // Debug
		
		JPanel questionPreviewPanel = new JPanel();
		questionPreviewPanel.setName(questionID); // For reference later
		
		questionPreviewPanel.setLayout(new BorderLayout());
		questionPreviewPanel.add(questions.getPanelByID(questionID), BorderLayout.CENTER); // Add the question panel to the preview
		
		// Prepare the actionButtonPanel
		JPanel actionButtonPanel = new JPanel();
		actionButtonPanel.setName(questionID); // Allows the question that the buttons belong to to be identified.
		actionButtonPanel.setLayout(new GridLayout(1,2));
		
		// Prepare the delete button
		JButton deleteButton = new JButton("delete");
		deleteButton.addActionListener(this);
		actionButtonPanel.add(deleteButton);
		
		// Prepare the movement buttons
		JButton upButton = new JButton("/\\"); // The button text is an upwards arrow
		JButton downButton = new JButton("\\/"); // The button text is a downwards arrow
		upButton.addActionListener(this);
		downButton.addActionListener(this);
		
		JPanel upAndDownPanel = new JPanel(); // Create a JPanel to store them
		upAndDownPanel.setName(questionID); // Allows the question that the buttons belong to to be identified.
		upAndDownPanel.setLayout(new GridLayout(2,1)); // 2 rows 1 column
		
		upAndDownPanel.add(upButton);
		upAndDownPanel.add(downButton);
		
		actionButtonPanel.add(upAndDownPanel);
		
		questionPreviewPanel.add(actionButtonPanel, BorderLayout.EAST);
		
		questionPreviews[nextQuestionPreviewLocation] = questionPreviewPanel; // Add the panel to the array
		nextQuestionPreviewLocation++;
		
		formBeingCreated = formBeingCreated.add(questionID); // Add the question to the form
	
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
		
		formPreview.revalidate();
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
		
		String title = "PLACEHOLDER";
		
		String description = JOptionPane.showInputDialog("Please provide a description of the form. "); // Get a description of the form
		
		int difficulty = Integer.parseInt(JOptionPane.showInputDialog("What's the form's difficulty?")); // Get the form's difficulty
		
		formBeingCreated = formBeingCreated.setFinalDetails(title, description, difficulty);
	}
}