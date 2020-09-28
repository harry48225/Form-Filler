package com.harry.formfiller.gui.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.harry.formfiller.form.Form;
import com.harry.formfiller.form.FormList;
import com.harry.formfiller.gui.GUI;
import com.harry.formfiller.gui.question.component.HeaderPanel;
import com.harry.formfiller.question.QuestionList;

public class FormCreationPanel extends JPanel implements ActionListener
{
	private static final String REQUIRED = "required";
	/* This panel allows the user to create forms */
	private transient QuestionList questions;
	private transient FormList forms;
	private GUI gui;
	
	private String formID = "";
	
	private transient Form.FormBuilder formBeingCreated; // Store the form that is currently being created
	
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
	private JComboBox<String> formDifficultyCombobox = new JComboBox<>(new String[] {"Please select a difficulty", "1", "2", "3", "4", "5", 
																								"6", "7", "8", "9", "10"});
	private JTextArea formDescriptionField = new JTextArea(5, 20); // 5 rows 20 columns (hints)
	private JScrollPane formDescriptionScroller = new JScrollPane(formDescriptionField);
	
	// Edit form panel
	private JPanel editFormPanel = new JPanel();
	private JLabel editFormLabel = new JLabel("Select a form to edit");
	private JComboBox<String> editFormComboBox = new JComboBox<>();
	private JButton editFormButton = new JButton("Edit");
	
	// General buttons
	private JButton addHeaderButton = new JButton("Add header");
	private JButton resetFormButton = new JButton("Reset Form");
	private JButton saveFormButton = new JButton("Save Form");
	private JPanel buttonPanel = new JPanel();
	
	private ImageIcon deleteIcon;
	private ImageIcon requiredIcon;
	private ImageIcon upArrow;
	private ImageIcon downArrow;
	
	private transient Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED); // Border style
	
	public FormCreationPanel(QuestionList tempQuestions, FormList tempForms, GUI tempGUI)
	{
		questions = tempQuestions;
		forms = tempForms;
		gui = tempGUI;
		
		formID = forms.getFreeID(); // Get a unique id for the form
		
		formBeingCreated = new Form.FormBuilder(formID, questions);
		
		prepareGUI();	
	}
	
	public void refreshTable()
	{
		/* Refreshs the table with updated information */
		selectionPanel.refreshTable();
		refreshEditFormDropdown();
		
		this.revalidate();
		this.repaint();
	}
	
	private void prepareGUI()
	{
		/* Prepares the panel for display to the user and adds all of the components to it */
		
		System.out.println("[INFO] <FORM_CREATION_PANEL> Running prepareGUI"); // Debug
		
		deleteIcon = new ImageIcon(this.getClass().getResource("/icons/bin.png"));
		deleteIcon = new ImageIcon(deleteIcon.getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT)); // Make the icon smaller
		
		requiredIcon = new ImageIcon(this.getClass().getResource("/icons/star.png"));
		requiredIcon = new ImageIcon(requiredIcon.getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT)); // Make the icon smaller
		
		upArrow = new ImageIcon(this.getClass().getResource("/icons/uparrow.png"));
		upArrow = new ImageIcon(upArrow.getImage().getScaledInstance(30, 15, Image.SCALE_DEFAULT)); // Make the icon smaller
		
		downArrow = new ImageIcon(this.getClass().getResource("/icons/downarrow.png"));
		downArrow = new ImageIcon(downArrow.getImage().getScaledInstance(30, 15, Image.SCALE_DEFAULT)); // Make the icon smaller
		
		prepareMainPanel();
	}
	
	private void prepareInformationPanel()
	{
		/* Prepares the panel that allows the admin to enter the title, description, and difficulty of the form */
		formInformationPanel.setLayout(new BoxLayout(formInformationPanel, BoxLayout.PAGE_AXIS)); // Vertical box layout
		
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
		
		// Large width so that it fills the horizontal space
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
		formInformationPanel.add(Box.createVerticalGlue()); // The glue fills the vertical save between each panel
		formInformationPanel.add(difficultyPanel);
		formInformationPanel.add(Box.createVerticalGlue());
		formInformationPanel.add(descriptionPanel);
	}
	
	private void refreshEditFormDropdown()
	{
		/* Refreshes the dropdown that allows the user to select a form to edit with up to date information */
		
		System.out.println("[INFO] <FORM_CREATION_PANEL> Running refreshEditFormDropdown");
		
		Form[] formArray = forms.getTrimmedArray(); // Get the array of forms
		
		String[] formTitles = new String[formArray.length + 1]; // Create an array to store the titles of the forms
		
		formTitles[0] = "Creating new form"; // Default entry for when a form hasn't been selected to edit
		
		for (int i = 0; i < formArray.length; i++) // For each form
		{
			formTitles[i+1] = formArray[i].getTitle();
		}
		
		// Create a combobox model with the titles
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(formTitles);
		
		// Give the edit form combobox the model
		editFormComboBox.setModel(model);
	}
	
	private void prepareEditPanel()
	{
		/* Prepares the panel that allow the admin to select a form to edit */
		
		refreshEditFormDropdown();
		
		editFormPanel.setLayout(new BoxLayout(editFormPanel, BoxLayout.LINE_AXIS)); // Only 1 row
		
		editFormButton.setBackground(new Color(169,196,235)); // Blue
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
	
	private void prepareButtonPanel()
	{
		/* Prepares the panel which contains the addHeader, resetForm, and saveForm buttons */
		
		buttonPanel.setLayout(new GridLayout(3,1)); // 3 rows 1 column
		
		// Make it have a very large horizontal width so it fills all available horizontal width
		buttonPanel.setMaximumSize(new Dimension(10000, 180));
	

		// Make the buttons the correct colours
		addHeaderButton.setBackground(new Color(169,196,235)); // Blue
		resetFormButton.setBackground(new Color(255,127,127)); // Make the button red
		resetFormButton.setForeground(Color.WHITE); // Make the text white
		saveFormButton.setBackground(new Color(130,183,75)); // Make the button green
		
		// Add action listeners
		addHeaderButton.addActionListener(this);
		resetFormButton.addActionListener(this);
		saveFormButton.addActionListener(this);
		
		
		// Add them to the panel
		buttonPanel.add(addHeaderButton);
		buttonPanel.add(resetFormButton);
		buttonPanel.add(saveFormButton);
	}
	
	private void prepareMainPanel()
	{
		/* Prepares the main panel of the formCreationPanel. This contains the form preview on the right and the entry panel on the left */
		this.setLayout(new GridLayout(0,2)); // Infinite rows 2 columns
		
		// This contains all components that are not the form preview
		entryContainerPanel.setLayout(new BoxLayout(entryContainerPanel, BoxLayout.PAGE_AXIS)); // Vertical box layout

		prepareEditPanel();
		
		entryContainerPanel.add(editFormPanel);
		
		prepareInformationPanel();
		
		entryContainerPanel.add(formInformationPanel);
		
		prepareQuestionSelectionPanel();
		
		entryContainerPanel.add(selectionPanel);
		
		prepareButtonPanel();
		
		entryContainerPanel.add(buttonPanel);
		
		prepareFormPreview();

		
		this.add(entryContainerPanel);
		this.add(formPreviewScroller);
		
		this.setVisible(true);
	}

	
	private void prepareQuestionSelectionPanel()
	{
		/* This creates a question selection panel with an add to form button */
		selectionPanel = new SelectQuestionsPanel(questions);
		
		// Add the add question button to///// t h e   s elect questions panel and make it the correct colour and size
		addQuestionButton.addActionListener(this);
		addQuestionButton.setBackground(new Color(169,196,235)); // Blue
		addQuestionButton.setMaximumSize(new Dimension(80, 40));
		selectionPanel.addNewButton(addQuestionButton);
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Select Questions");
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		selectionPanel.setBorder(border);
	}
	
	private void prepareFormPreview()
	{
		/* Prepares the from preview */
		
		formPreview = new JPanel();
		
		formPreview.setLayout(new BoxLayout(formPreview, BoxLayout.PAGE_AXIS)); // Vertical box layout
		
		// Create and add the title
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Form preview");
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		formPreview.setBorder(border);
		
		// Create a scroll pane so that the preview can be scrolled if it's too long
		formPreviewScroller = new JScrollPane(formPreview);
		formPreviewScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
	}
	
	private void saveForm()
	{
		/* Saves the form that is currently being edited to the form database */
		
		Form existingForm = forms.getFormByID(formID); // Get the form if it exists 
		
		if (existingForm != null) // If the form already exists
		{
			forms.removeForm(existingForm.getID()); // Remove the form
		}
		
		if (formInformationPresenceCheck()) // If all of the fields have the correct data
		{
			getFinalDetails(); // Get the title, description, and difficulty
		
			forms.addForm(formBeingCreated.build()); // Add the form to the list
		
			int save = JOptionPane.showConfirmDialog(this, "Form added! Would you like to save?", "Save?", JOptionPane.YES_NO_OPTION); // Ask if they want to save
			
			refreshEditFormDropdown();
		
			if (save == 0) // If they selected yes
			{
				// Save the form and tell the user that it's been saved
				forms.writeDatabase(); 
				JOptionPane.showMessageDialog(this, "Form saved!");
				
			}
				
			int reset = JOptionPane.showConfirmDialog(this, "Would you like to reset the panel?", "Reset?", JOptionPane.YES_NO_OPTION); // Ask if they want to reset the form creation panel
			
			if (reset == 0) // If they selected yes
			{
				gui.resetTab(this); // Reset the tab
			}
		}
		
	}
	
	private boolean formInformationPresenceCheck()
	{
		/* Does a presence check on the title, description, and difficutly controls */
		
		String errorString = ""; // This string will be filled with the errors that the user has made
			
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
			errorString += "Please select a difficulty. ";
		}
		
		// Check to see if there any questions in the form
		boolean anyQuestions = false;
		
		for (int i = 0; i < nextQuestionPreviewLocation; i++)
		{
			String questionID = questionPreviews[i].getName();
			boolean isAQuestion = questions.getQuestionByID(questionID) != null; // If a question is returned then the component is a question
			
			if (isAQuestion)
			{
				anyQuestions = true;
				break;
			}
		}
		
		if (!anyQuestions) // If there are no questions in the form
		{
			errorString += "Please add at least 1 question. ";	
		}
		
		if (!errorString.equals("")) // If the user failed to fill something in
		{
			JOptionPane.showMessageDialog(this, "Please address the following errors: " + errorString, "Insufficent details entered", JOptionPane.ERROR_MESSAGE);
		}
		
		return errorString.equals(""); // Return whether then made any errors - if no errors the string will be ""
	}
	
	private void addHeader()
	{
		/* Asks the user for header text and adds a header with that text to the form */
		
		String headerText = JOptionPane.showInputDialog(this, "Please enter a header");
		// The input dialog will return null if the user pressed cancel
		// If this happens we should stop adding the new header
		if (headerText != null && !headerText.isEmpty()) // If they pressed ok and typed something
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
			
			if (editFormComboBox.getSelectedIndex() != 0) // If they selected a form to edit
			{
				Form formBeingEdited = forms.getTrimmedArray()[editFormComboBox.getSelectedIndex() - 1]; // Subtract 1 because at index 0 is the current form in the dropdown
				
				loadFormToBeEdited(formBeingEdited); // Load the form to be edited
			}
		}
		else if (evt.getSource() == addQuestionButton)
		{
			String selectedQuestionID = selectionPanel.getSelectedQuestionID();
			
			// Check whether the question has been added to the form already, and if not, add it to the form. Otherwise show an error message
			if (selectedQuestionID != null)
			{
				boolean inForm = false;
				
				// Iterate through the questions in the form to see whether it's already in the form
				for (int i = 0; i < nextQuestionPreviewLocation; i++)
				{
					if (questionPreviews[i].getName().equals(selectedQuestionID))
					{
						inForm = true;
						break;
					}
				}
				
				if (inForm)
				{
					JOptionPane.showMessageDialog(this, "You may only add each question to a form once.", "Question already in form!", JOptionPane.ERROR_MESSAGE);
				}
				
				else
				{
					addComponentToForm(selectedQuestionID);
				}
			}
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
			/* This will be reached if the user has pressed any of the buttons that are after each question in the form preview.
			   This finds the question id associated with the button and performs the correct action on the question according to which
			   type of button was pressed. */
			   
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
			else if (pressedButton.getName().equals(REQUIRED))
			{
				
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
		/* Loads a form to be edited. Populates the form information with the information about the form and the form preview
			with the questions from the form */
			
		formID = formToEdit.getID();
		formBeingCreated = new Form.FormBuilder(formID, questions);
		
		formPreview.removeAll(); // Clear the formPreview
		formPreview.revalidate();
		formPreview.repaint();
		
		// Empty the questionPreviewPanel array
		questionPreviews = new JPanel[50];
		nextQuestionPreviewLocation = 0;
	
		for (String question : formToEdit.getQuestionIDs()) // For each question currently in the form
		{
			addComponentToForm(question); // Add the question to the form preview
			formBeingCreated = formBeingCreated.setRequired(question, formToEdit.isQuestionRequired(question));
		}
		
		// Find the required button for each question in the preview and make it marked as required or not required depending on whether the question is required in the form
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
						if (button.getName().equals(REQUIRED)) // If it's the required button
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
	
	private void addComponentToForm(String component)
	{
		/* Adds a question or header to the preview and to the form */
		
		System.out.println("[INFO] <FORM_CREATION_PANEL> Running addComponentToForm"); // Debug
		
		JPanel questionPreviewPanel = new JPanel();
		questionPreviewPanel.setName(component); // For reference later
		
		questionPreviewPanel.setMaximumSize(new Dimension(700,300));
		
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
			JPanel headerPanel = new HeaderPanel(component);
			questionPreviewPanel.add(headerPanel);
			
		}
		// Prepare the actionButtonPanel
		JPanel actionButtonPanel = new JPanel();
		actionButtonPanel.setName(component); // Allows the question that the buttons belong to to be identified.
		actionButtonPanel.setLayout(new BoxLayout(actionButtonPanel, BoxLayout.LINE_AXIS));
		actionButtonPanel.setPreferredSize(new Dimension(90, 30));

		actionButtonPanel.add(Box.createHorizontalGlue());

		if (isAQuestion)
		{
			// Prepare the required button
			JButton requiredButton = new JButton();
			requiredButton.setBackground(new Color(169,196,235)); // Blue
			requiredButton.setName(REQUIRED); // For reference later
			requiredButton.setIcon(requiredIcon);
			requiredButton.addActionListener(this);
			requiredButton.setPreferredSize(new Dimension(40, 40));
			requiredButton.setMaximumSize(new Dimension(40, 40));
			actionButtonPanel.add(requiredButton);
		}
		
		// Prepare the delete button
		JButton deleteButton = new JButton();
		deleteButton.setIcon(deleteIcon);
		deleteButton.setBackground(new Color(169,196,235)); // Blue
		deleteButton.setName("delete"); // For reference later 
		deleteButton.addActionListener(this);
		deleteButton.setPreferredSize(new Dimension(40, 40));
		deleteButton.setMaximumSize(new Dimension(40, 40));
		actionButtonPanel.add(deleteButton);
		
		// Prepare the movement buttons
		
		JButton upButton = new JButton(); // The button text is an upwards arrow
		upButton.setIcon(upArrow);
		upButton.setName("up");
		upButton.setBackground(new Color(169,196,235)); // Blue
		JButton downButton = new JButton(); // The button text is a downwards arrow
		downButton.setIcon(downArrow);
		downButton.setName("down");
		downButton.setBackground(new Color(169,196,235)); // Blue
		upButton.addActionListener(this);
		downButton.addActionListener(this);
		
		JPanel upAndDownPanel = new JPanel(); // Create a JPanel to store them
		upAndDownPanel.setName(component); // Allows the question that the buttons belong to to be identified.
		upAndDownPanel.setLayout(new GridLayout(2,1)); // 2 rows 1 column
		upAndDownPanel.setPreferredSize(new Dimension(40, 40));
		upAndDownPanel.setMaximumSize(new Dimension(40,40));
		upAndDownPanel.add(upButton);
		upAndDownPanel.add(downButton);
		
		actionButtonPanel.add(upAndDownPanel);
		
		questionPreviewPanel.add(Box.createHorizontalGlue()); // This fills the horizontal space between the question and the action buttons
		questionPreviewPanel.add(actionButtonPanel);
		
		questionPreviews[nextQuestionPreviewLocation] = questionPreviewPanel; // Add the panel to the array
		
		nextQuestionPreviewLocation++;
		
		formBeingCreated = formBeingCreated.add(component, isAQuestion); // Add the question to the form, and make it required if it's a question
		
		updateFormPreview();
	}
	
	private void moveQuestionUp(String questionID)
	{
		/* Moves a question up the form */
		
		System.out.println("[INFO] <FORM_CREATION_PANEL> Running moveQuestionUp"); // Debug
		
		// Move the question up in the form
		formBeingCreated = formBeingCreated.moveUp(questionID);
		
		
		// Move the question up in the form preview
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
	
	private void moveQuestionDown(String questionID)
	{
		/* Moves a question down the form */
		
		System.out.println("[INFO] <FORM_CREATION_PANEL> Running moveQuestionDown"); // Debug
		
		// Move the question down in the form
		formBeingCreated = formBeingCreated.moveDown(questionID);
		
		// Move the question down in the form preview
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
		/* Updates the form preview */
		
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
	
	private void removeQuestionFromForm(String questionID)
	{
		/* Removes a question from the form */
		System.out.println("[INFO] <FORM_CREATION_PANEL> Running removeQuestionFromForm"); // Debug
		
		formBeingCreated = formBeingCreated.remove(questionID); // Remove the question from the builder
		
		JPanel[] newArray = new JPanel[questionPreviews.length]; // Create a new array of the correct size
		
		int j = 0; // The location in newArray
			
		for (int i = 0; i < nextQuestionPreviewLocation; i++)
		{
			if (!questionPreviews[i].getName().equals(questionID)) // If the question isn't the one to delete
			{
				// Copy the question to the new array
				newArray[j] = questionPreviews[i];
				j++;
			}
		}
	
		questionPreviews = newArray; // Overwrite the old array
		nextQuestionPreviewLocation--; // There is one less question preview  in the array so a free spot has opened
		
		updateFormPreview();
	}
	
	private void getFinalDetails()
	{
		/* Gets the type and difficulty of the question from the user */
		
		System.out.println("[INFO] <FORM_CREATION_PANEL> Running getFinalDetails"); // Debug
		
		String title = formTitleField.getText().replace(",", ""); // Remove commas from the title and get the title that the user has entered.
		
		String description = formDescriptionField.getText(); // Get a description of the form
		
		int difficulty = Integer.parseInt((String) formDifficultyCombobox.getSelectedItem()); // Get the form's difficulty
		
		formBeingCreated = formBeingCreated.setFinalDetails(title, description, difficulty);
	}
}