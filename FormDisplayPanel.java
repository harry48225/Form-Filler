import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

import java.util.*;

public class FormDisplayPanel extends JPanel implements ActionListener
{
	private FormList forms;
	private QuestionList questions;
	private GUI gui;
	private boolean adminMode;
	private FormsInProgressList formsInProgress;
	
	
	private JPanel mainPanel = new JPanel();
	private JButton helpButton = new JButton("Help");
	
	// For the view table
	private String[] tableHeaders = new String[] {"ID","Title", "Description", "Main Skills Tested","Difficulty", "Percent Complete", "Times completed"}; // The headers for the table
	private String[][] formData = new String[0][0];
	private DefaultTableModel formTableModel = new DefaultTableModel(formData, tableHeaders);
	private JTable formTable = new JTable(formTableModel); // Create a table to hold the questions
	private JScrollPane formTableScrollPane = new JScrollPane(formTable); // Create a scroll pane
	
	// Sort buttons
	private JPanel buttonPanel; // To hold the buttons
	private JButton sortDifficultyButton = new JButton("Difficulty Sort"); // Button to sort by difficulty
	private JButton attemptButton = new JButton("Attempt Form"); // User presses this to attempt the selected question
	private JButton attemptUserWeaknessesFormButton = new JButton("Attempt form based on weaknesses"); // Button that the user can press to attempt a form based on their weaknesses
	private JButton deleteButton = new JButton("Delete Form"); // To delete the form
	// Filters
	private JPanel sortAndFilterPanel;
	private JPanel sortPanel;
	private JPanel difficultyFilterPanel; // To hold the sliders
	private JPanel typeFilterPanel;
	private JSlider difficultySlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 1); // Filter slider
	private JButton difficultyFilterButton = new JButton("Apply difficulty filter"); // JButton that will apply the filter
	private JCheckBox[] typeCheckBoxes; // Holds the check boxes for the types
	private JPanel typeCheckBoxPanel = new JPanel();
	private JButton typeFilterButton = new JButton("Apply main skills tested filter");
	
	private JButton resetButton = new JButton("Reset sorts and filters"); // To reset the sorts and filters
	
	private Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED); // Border style
	
	// Store which sorts and filters have been applied
	private boolean difficultySort = false;
	private boolean typeFilter = false;
	private boolean difficultyFilter = false;
	
	public FormDisplayPanel(FormList tempList, GUI tempGUI, QuestionList tempQuestions, FormsInProgressList tempFormsInProgress, boolean tempAdminMode) // Constructor
	{
		forms = tempList; // Store the form list
		gui = tempGUI;
		questions = tempQuestions;
		formsInProgress = tempFormsInProgress;
		adminMode = tempAdminMode;
		prepareGUI();
	}
	
	private void prepareGUI() // Makes the window
	{
		System.out.println("[INFO] <FORM_DISPLAY_PANEL> Running prepareGUI"); // Debug
			
		this.setLayout(new BorderLayout());
			
		GridBagLayout layout = new GridBagLayout(); // Create a new grid bag layout
		
		mainPanel.setLayout(layout); // Get the layout
		
		GridBagConstraints mainPanelConstraints = new GridBagConstraints();
		mainPanelConstraints.fill = GridBagConstraints.BOTH;

		mainPanelConstraints.weightx = 1;
		mainPanelConstraints.weighty = 1;
		mainPanelConstraints.gridx = 0;
		mainPanelConstraints.gridy = 0;

		mainPanelConstraints.gridheight = 4;

		mainPanelConstraints.insets = new Insets(5,5,5,5); // 5 px padding all around
		
		mainPanel.add(formTableScrollPane, mainPanelConstraints); // Add the table to the view
		
		sortAndFilterPanel = new JPanel(); // Create a new JPanel
		sortAndFilterPanel.setLayout(new GridBagLayout());
		
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Sort and Filter");
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		sortAndFilterPanel.setBorder(border); // Set the border
		
		GridBagConstraints sortAndFilterPanelConstraints = new GridBagConstraints();

		sortAndFilterPanelConstraints.fill = GridBagConstraints.BOTH;
		sortAndFilterPanelConstraints.insets = new Insets(5,5,5,5); // 5 px padding all around
		sortAndFilterPanelConstraints.gridx = 0;
		sortAndFilterPanelConstraints.gridy = 0;
		sortAndFilterPanelConstraints.weightx = 1;
		sortAndFilterPanelConstraints.weighty = 1; 

		prepareSortPanel();
		
		sortAndFilterPanel.add(sortPanel, sortAndFilterPanelConstraints);
		
		// Prepare the filters
		
		prepareDifficultyFilterPanel();
		
		sortAndFilterPanelConstraints.gridx = 1;
		sortAndFilterPanelConstraints.gridwidth = 2; // Span two columns
		
		sortAndFilterPanel.add(difficultyFilterPanel, sortAndFilterPanelConstraints);
		
		// Prepare the type filter
		
		prepareTypeFilterPanel();
		
		sortAndFilterPanelConstraints.gridx = 0;
		sortAndFilterPanelConstraints.gridy = 1;
		sortAndFilterPanelConstraints.gridwidth = 3; // Span three columns
		
		sortAndFilterPanel.add(typeFilterPanel, sortAndFilterPanelConstraints);
		
		resetButton.addActionListener(this);
		resetButton.setBackground(Color.RED);
		resetButton.setForeground(Color.WHITE);
		sortAndFilterPanelConstraints.gridy = 2;
		sortAndFilterPanelConstraints.gridwidth = 3; // Span three columns
		
		sortAndFilterPanel.add(resetButton, sortAndFilterPanelConstraints);
		
		attemptButton.addActionListener(this);
		attemptButton.setBackground(new Color(130,183,75));

		mainPanelConstraints.gridheight = 1;
		mainPanelConstraints.weightx = 0.1;
		
		mainPanelConstraints.gridx = 1;
		mainPanel.add(sortAndFilterPanel, mainPanelConstraints);
		
		mainPanelConstraints.weighty = 0.2;
		mainPanelConstraints.gridx = 1;
		mainPanelConstraints.gridy = 1;
		
		if (adminMode) // Add the delete question button if the user is an admin
		{
			deleteButton.addActionListener(this);
			deleteButton.setBackground(new Color(174,59,46));
			deleteButton.setForeground(Color.WHITE);
			mainPanel.add(deleteButton, mainPanelConstraints);
		}
		
		attemptUserWeaknessesFormButton.setBackground(new Color(139, 102, 153));
		attemptUserWeaknessesFormButton.addActionListener(this);
		mainPanelConstraints.gridy = 2;
		mainPanel.add(attemptUserWeaknessesFormButton, mainPanelConstraints);
		
		mainPanelConstraints.gridy = 3;
		mainPanel.add(attemptButton, mainPanelConstraints);
		
		// Hide the first column as it contains the id and we don't want that displayed to the user
		TableColumnModel tcm = formTable.getColumnModel();
		tcm.removeColumn(tcm.getColumn(0));
		tcm.getColumn(3).setCellRenderer(new WordWrapCellRenderer());
		
		populateTable(forms.getArray()); // Populate the table with the questions
	
		this.add(mainPanel, BorderLayout.CENTER);
		
		helpButton.addActionListener(this);
		
		this.add(helpButton, BorderLayout.NORTH);
		
		this.setVisible(true);
	}
	
	private void prepareSortPanel()
	{
		sortPanel = new JPanel();
		sortPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints sortPanelConstraints = new GridBagConstraints();
		sortPanelConstraints.fill = GridBagConstraints.BOTH;
		sortPanelConstraints.insets = new Insets(5,5,5,5); // 5 px padding all around
		sortPanelConstraints.gridx = 0;
		sortPanelConstraints.gridy = 0;
		sortPanelConstraints.weightx = 1;
		sortPanelConstraints.weighty = 1; 
		
		JLabel sortsLabel = new JLabel("Sorts", SwingConstants.CENTER);
		sortPanel.add(sortsLabel, sortPanelConstraints);
		sortPanelConstraints.gridy = 1;
		
		sortDifficultyButton.addActionListener(this);
		sortPanel.add(sortDifficultyButton, sortPanelConstraints);
	}
	
	private void prepareDifficultyFilterPanel()
	{
		// Prepare the difficulty filter
		difficultyFilterPanel = new JPanel();
		difficultyFilterPanel.setLayout(new GridBagLayout()); // Create a grid bag layout
		
		GridBagConstraints difficultyFilterPanelConstraints = new GridBagConstraints();
		difficultyFilterPanelConstraints.fill = GridBagConstraints.BOTH;

		difficultyFilterPanelConstraints.weightx = 1;
		difficultyFilterPanelConstraints.insets = new Insets(5,5,5,5); // 5 px padding all around
		difficultyFilterPanelConstraints.gridx = 0;
		difficultyFilterPanelConstraints.gridy = 0;
		difficultyFilterPanelConstraints.weightx = 1;
		difficultyFilterPanelConstraints.weighty = 1; 
		
		JLabel difficultyFilterLabel = new JLabel("Difficulty Filter", SwingConstants.CENTER);
		
		difficultyFilterPanel.add(difficultyFilterLabel, difficultyFilterPanelConstraints);
		difficultyFilterPanelConstraints.gridy += 1;
		
		difficultySlider.setMajorTickSpacing(1);
		difficultySlider.setPaintTicks(true); // Add the ticks
		difficultySlider.setPaintLabels(true);
		
		difficultyFilterButton.addActionListener(this);
		
		difficultyFilterPanel.add(difficultySlider, difficultyFilterPanelConstraints);
		difficultyFilterPanelConstraints.gridy += 1;
		
		difficultyFilterPanel.add(difficultyFilterButton, difficultyFilterPanelConstraints);
		difficultyFilterPanelConstraints.gridy += 1;
	}
	
	private void prepareTypeFilterPanel()
	{
		// Prepare the type filter
		
		prepareTypeCheckBoxes();

		typeFilterButton.addActionListener(this);
		
		typeFilterPanel = new JPanel();
		typeFilterPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints typeFilterPanelConstraints = new GridBagConstraints();
		typeFilterPanelConstraints.fill = GridBagConstraints.BOTH;
		typeFilterPanelConstraints.insets = new Insets(5,5,5,5); // 5 px padding all around
		typeFilterPanelConstraints.gridx = 0;
		typeFilterPanelConstraints.gridy = 0;
		typeFilterPanelConstraints.weightx = 1;
		typeFilterPanelConstraints.weighty = 1; 
		
		JLabel typeFilterLabel = new JLabel("Type filter", SwingConstants.CENTER);
		
		typeFilterPanelConstraints.gridx = 2; // Put it in the middle column
		typeFilterPanel.add(typeFilterLabel, typeFilterPanelConstraints);
		
		typeFilterPanelConstraints.gridx = 0;
		typeFilterPanelConstraints.gridy = 1;
		typeFilterPanelConstraints.gridwidth = 3; // Span 3 columns
		typeFilterPanel.add(typeCheckBoxPanel, typeFilterPanelConstraints);
		
		typeFilterPanelConstraints.gridy = 2;
		typeFilterPanel.add(typeFilterButton, typeFilterPanelConstraints);
	}

	private void prepareTypeCheckBoxes()
	{
		typeCheckBoxPanel.setLayout(new GridLayout(0,3)); // 3 rows infinite columns
		
		String[] questionTypes = questions.getTypes(); // Get the types

		typeCheckBoxes = new JCheckBox[questionTypes.length]; // Create an array large enough to store the check boxes

		for (int i = 0; i < typeCheckBoxes.length; i++) // For each type of question
		{
			typeCheckBoxes[i] = new JCheckBox(questionTypes[i]); // Create a checkbox with the correct name
			typeCheckBoxPanel.add(typeCheckBoxes[i]); // Add the check boxes to the check box panel 
		}
	}
	
	public void refreshTable() // Refreshes the table. Preserves sorts and filters
	{
		Form[] formData = forms.getArray();
		
		if (difficultySort)
		{
			forms.sortByDifficulty();
			formData = forms.getArray();
		}
		
		if (difficultyFilter && typeFilter) // If they selected both filters we need to find the intersection of the filters
		{
			Form[] difficulty = forms.filterByDifficulty(difficultySlider.getValue());
			Form[] type = forms.filterByType(getTypesSelected());
			
			Form[] intersection = new Form[forms.getArray().length]; // At most it could contain every question
			int nextIntersectionLocation = 0;
			
			for (Form fD : difficulty)
			{
				for (Form fT : type)
				{
					if (fD == fT) // If they are the same question
					{
						intersection[nextIntersectionLocation] = fT;
						nextIntersectionLocation++;
					}
				}
			}
			
			// Trim the array
			
			formData = new Form[nextIntersectionLocation];
			
			for (int i = 0; i < nextIntersectionLocation; i++)
			{
				formData[i] = intersection[i];
			}
		}
		else if (difficultyFilter)
		{
			formData = forms.filterByDifficulty(difficultySlider.getValue());
		}
		else if (typeFilter)
		{
			formData = forms.filterByType(getTypesSelected());
		}
		
		populateTable(formData);
	}
	
	private void clearCheckboxes()
	{
		for (JCheckBox checkbox : typeCheckBoxes)
		{
			checkbox.setSelected(false);
		}
	}
	
	private void resetTable()
	{
		clearCheckboxes();

		typeFilter = false;
		difficultySort = false;
		difficultyFilter = false;
		
		refreshTable();
		
	}
	
	private void populateTable(Form[] data) // Populates the table with data
	{
		
		System.out.println("[INFO] <FORM_DISPLAY_PANEL> Running populateTable"); // Debug
		
		formTableModel.setRowCount(0); // Start a zero rows
		
		for (int i =0; i < data.length; i++) // For each form in the array
		{
			if(data[i] != null) // If there is data
			{
				Form f = data[i]; // The current form. The name is f as it'll be called a large amount of time. f is to make the calls 
								  // less cluttered
								  
				String percentageComplete = "0%";
				String formID = f.getID();
				if (formsInProgress.isFormPresent(formID)) // If the form is partially completed / has already been attempted by the user
				{
					percentageComplete = formsInProgress.getByID(formID).getPercentComplete() + "%"; // Get the percentage that the form is complete, append %, and add it to the end of the array
				}
				
				// "ID","Title", "Description", "Main Skills Tested","Difficulty", "Percent Complete", "Times completed"
				// This is the column order
				
				// The default output is just a . between the skills
				// A , and a space looks better for outputting to the user
				String betterLookingMainskillsTested = f.mainSkillsTestedToString().replace(".", ", ");
				
				String[] formData = {f.getID(), f.getTitle(), f.getDescription(), betterLookingMainskillsTested, 
									f.getDifficulty() + "", percentageComplete, "PLACEHOLDER"}; // Convert the form to a String array
				
				formTableModel.addRow(formData); // Add the form to the table
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
			difficultySort = true;
			refreshTable();
		}
		else if (evt.getSource() == typeFilterButton)
		{
			System.out.println("[INFO] <FORM_DISPLAY_PANEL> typeFilterButton pressed"); // Debug
			typeFilter = true;
			refreshTable();
		}
		else if (evt.getSource() == difficultyFilterButton)
		{
			System.out.println("[INFO] <FORM_DISPLAY_PANEL> difficultyFilterButton pressed"); // Debug
			difficultyFilter = true;
			refreshTable();
		}
		else if (evt.getSource() == attemptButton)
		{
			System.out.println("[INFO] <FORM_DISPLAY_PANEL> attemptButton pressed"); // Debug
			int row = formTable.getSelectedRow();
			Form selectedForm = forms.getFormByID(formTable.getModel().getValueAt(row, 0).toString()); // Get the form id and get the form
			gui.openForm(selectedForm); // Open the form
		}
		else if (evt.getSource() == resetButton)
		{
			System.out.println("[INFO] <FORM_DISPLAY_PANEL> resetButton pressed"); // Debug
			resetTable();
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
	
	static class WordWrapCellRenderer extends JTextArea implements TableCellRenderer 
	{
		public WordWrapCellRenderer() 
		{
			setLineWrap(true);
			setWrapStyleWord(true);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
		{
			setText(value.toString());
			setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
			if (table.getRowHeight(row) != getPreferredSize().height) 
			{
				table.setRowHeight(row, getPreferredSize().height);
			}
			return this;
		}
	}

}