import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

import java.util.*;

public class FormDisplayPanel extends JPanel implements ActionListener
{
	private FormList forms;
	private QuestionList questions;
	private GUI gui;
	private FormsInProgressList formsInProgress;
	
	
	private JPanel mainPanel = new JPanel();
	private JButton helpButton = new JButton("Help");
	
	// For the view table
	private String[] tableHeaders = new String[] {"ID","Questions","Description","Main Skills Tested","Difficulty", "Percent Complete"}; // The headers for the table
	private String[][] formData = new String[0][0];
	private DefaultTableModel formTableModel = new DefaultTableModel(formData, tableHeaders);
	private JTable formTable = new JTable(formTableModel); // Create a table to hold the questions
	private JScrollPane formTableScrollPane = new JScrollPane(formTable); // Create a scroll pane
	
	// Sort buttons
	private JPanel buttonPanel; // To hold the buttons
	private JButton sortDifficultyButton = new JButton("Difficulty Sort"); // Button to sort by difficulty
	//private JButton sortTypeButton = new JButton("Type Sort"); // Sorts by type
	private JButton deleteButton = new JButton("Delete Form"); // Button to delete the form
	private JButton attemptUserWeaknessesFormButton = new JButton("Attempt form based on weaknesses"); // Button that the user can press to attempt a form based on their weaknesses
	
	// Filters
	private JPanel filterPanel; // To hold the sliders
	private JSlider difficultySlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 1); // Filter slider
	private JButton difficultyFilterButton = new JButton("Filter by difficulty"); // JButton that will apply the filter
	private JCheckBox[] typeCheckBoxes; // Holds the check boxes for the types
	private JPanel typeCheckBoxPanel = new JPanel();
	private JButton typeFilterButton = new JButton("Filter by type");
	
	private JButton attemptButton = new JButton("Attempt Form"); // User presses this to attempt the selected question
	
	
	public FormDisplayPanel(FormList tempList, GUI tempGUI, QuestionList tempQuestions, FormsInProgressList tempFormsInProgress) // Constructor
	{
		forms = tempList; // Store the form list
		gui = tempGUI;
		questions = tempQuestions;
		formsInProgress = tempFormsInProgress;
		prepareGUI();
	}
	
	private void prepareGUI() // Makes the window
	{
		System.out.println("[INFO] <FORM_DISPLAY_PANEL> Running prepareGUI"); // Debug
			
		this.setLayout(new BorderLayout());
		
		GridLayout layout = new GridLayout(1,2); // Create a new grid layout
			
		mainPanel.setLayout(layout); // Get the layout
			
		mainPanel.add(formTableScrollPane); // Add the table to the view
		
		
		// Prepare sort and attempt buttons
		// Add action listeners 
		sortDifficultyButton.addActionListener(this);
		//sortTypeButton.addActionListener(this);
		attemptButton.addActionListener(this);
		deleteButton.addActionListener(this);
		attemptUserWeaknessesFormButton.addActionListener(this);
		
		buttonPanel = new JPanel(); // Create a new JPanel
		buttonPanel.setLayout(new GridLayout(0,1)); // Create a grid layout with 1 column
		buttonPanel.add(sortDifficultyButton);
		//buttonPanel.add(sortTypeButton);
		buttonPanel.add(attemptButton);
		buttonPanel.add(deleteButton);
		buttonPanel.add(attemptUserWeaknessesFormButton);
		 
		mainPanel.add(buttonPanel);
		
		// Prepare the filters
		filterPanel = new JPanel();
		filterPanel.setLayout(new GridLayout(0,1)); // Create a grid layout with 1 column
		
		difficultySlider.setMajorTickSpacing(1);
		difficultySlider.setPaintTicks(true); // Add the ticks
		difficultySlider.setPaintLabels(true);
		
		difficultyFilterButton.addActionListener(this);
		
		filterPanel.add(difficultySlider);
		filterPanel.add(difficultyFilterButton);

		prepareTypeCheckBoxes();

		typeFilterButton.addActionListener(this);

		filterPanel.add(typeCheckBoxPanel);
		filterPanel.add(typeFilterButton);
		
		mainPanel.add(filterPanel);
		
		// Hide the first column as it contains the id and we don't want that displayed to the user
		TableColumnModel tcm = formTable.getColumnModel();
		tcm.removeColumn(tcm.getColumn(0));
		
		this.add(mainPanel, BorderLayout.CENTER);
		
		helpButton.addActionListener(this);
		
		this.add(helpButton, BorderLayout.NORTH);
		
		populateTable(forms.getArray()); // Populate the table with the questions
	}

	private void prepareTypeCheckBoxes()
	{
		typeCheckBoxPanel.setLayout(new GridLayout(0,4)); // 4 rows infinite columns
		
		String[] questionTypes = questions.getTypes(); // Get the types

		typeCheckBoxes = new JCheckBox[questionTypes.length]; // Create an array large enough to store the check boxes

		for (int i = 0; i < typeCheckBoxes.length; i++) // For each type of question
		{
			typeCheckBoxes[i] = new JCheckBox(questionTypes[i]); // Create a checkbox with the correct name
			typeCheckBoxPanel.add(typeCheckBoxes[i]); // Add the check boxes to the check box panel 
		}
	}
	
	private void populateTable(Form[] data) // Populates the table with data
	{
		
		System.out.println("[INFO] <FORM_DISPLAY_PANEL> Running populateTable"); // Debug
		
		formTableModel.setRowCount(0); // Start a zero rows
		
		for (int i =0; i < data.length; i++) // For each form in the array
		{
			if(data[i] != null) // If there is data
			{
				String[] formData = data[i].toStringArray(); // Convert the form to a String array
				
				String[] rowData = new String[formData.length + 1]; // Add one more space for the percentage complete
				
				for (int j = 0; j < formData.length; j++)
				{
					rowData[j] = formData[j]; // Copy across the data
				}
				
				String formID = rowData[0]; // The first item is the formId
				
				if (formsInProgress.isFormPresent(formID)) // If the form is partially completed / has already been attempted by the user
				{
					rowData[formData.length] = formsInProgress.getByID(formID).getPercentComplete() + "%"; // Get the percentage that the form is complete, append %, and add it to the end of the array
				}
				else // The form hasn't been started
				{
					rowData[formData.length] = "0%"; // Add 0% to the end of the array
				}
				
				formTableModel.addRow(rowData); // Add the form to the table
			}
		}
	}

	private String[] getTypesSelected() // Gets which type checkboxes are selected
	{
		String[] typesSelected = new String[questions.getTypes().length]; // Create an array of the correct size

		int nextLocation = 0;
		
		for (JCheckBox cB : typeCheckBoxes)
		{
			if (cB.isSelected()) //  If the box is checked
			{
				typesSelected[nextLocation] = cB.getText();
				nextLocation++;
			}
		}

		String[] typesSelectedTrimmed = new String[nextLocation]; // Create a new array of just the right size

		for (int i = 0; i < nextLocation; i++)
		{
			typesSelectedTrimmed[i] = typesSelected[i]; // Copy across the types
		}

		return typesSelectedTrimmed;
	}
	
	public void actionPerformed(ActionEvent evt)
	{
		
		if (evt.getSource() == sortDifficultyButton)
		{
			System.out.println("[INFO] <FORM_DISPLAY_PANEL> sortDifficultyButton pressed"); // Debug
			forms.sortByDifficulty(); // Sort the list by type
			populateTable(forms.getArray());
		}
		else if (evt.getSource() == typeFilterButton)
		{
			System.out.println("[INFO] <FORM_DISPLAY_PANEL> typeFilterButton pressed"); // Debug
			populateTable(forms.filterByType(getTypesSelected()));
		}
		else if (evt.getSource() == difficultyFilterButton)
		{
			System.out.println("[INFO] <FORM_DISPLAY_PANEL> difficultyFilterButton pressed"); // Debug
			populateTable(forms.filterByDifficulty(difficultySlider.getValue()));
		}
		else if (evt.getSource() == attemptButton)
		{
			System.out.println("[INFO] <FORM_DISPLAY_PANEL> attemptButton pressed"); // Debug
			int row = formTable.getSelectedRow();
			Form selectedForm = forms.getFormByID(formTable.getModel().getValueAt(row, 0).toString()); // Get the form id and get the form
			gui.openForm(selectedForm); // Open the form
		}
		else if (evt.getSource() == deleteButton)
		{
			/*
			System.out.println("[INFO] <FORM_DISPLAY_PANEL> deleteButton pressed"); // Debug
			int row = formTable.getSelectedRow();
			String  selectedFormID = formTable.getModel().getValueAt(row, 0).toString(); // Get the form id
			
			forms.removeForm(selectedFormID); // Remove the form
			*/
		}
		else if (evt.getSource() == attemptUserWeaknessesFormButton)
		{
			System.out.println("[INFO] <FORM_DISPLAY_PANEL> attemptUserWeaknessesFormButton pressed");
			gui.attemptFormFromUserWeaknesses();
		}
		else if (evt.getSource() == helpButton)
		{
			System.out.println("[INFO] <FORM_DISPLAY_PANEL> helpButton pressed");
			JOptionPane.showMessageDialog(null,"This is the form display panel from here you can attempt a form by selecting one from the table and pressing attempt. \r\n By pressing attempt form based on weaknesses you can attempt a form based on your weak areas. \r\n You can filter and sort forms using the buttons on the right.");
		}
	}

}