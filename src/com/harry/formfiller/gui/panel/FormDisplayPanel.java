package com.harry.formfiller.gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.harry.formfiller.form.Form;
import com.harry.formfiller.form.FormList;
import com.harry.formfiller.gui.GUI;
import com.harry.formfiller.gui.WordWrapCellRenderer;
import com.harry.formfiller.gui.WordWrapHeaderRenderer;
import com.harry.formfiller.question.QuestionList;
import com.harry.formfiller.user.FormInProgress;
import com.harry.formfiller.user.FormsInProgressList;

public class FormDisplayPanel extends JPanel implements ActionListener, TableColumnModelListener, Helper
{
	/* This is a panel that displays all of the forms in the system to the user and allows them to: search and sort them, and to attempt them. */
	
	private final String HELP_STRING = "This is the form display panel from here you can attempt a form by selecting one from the table and pressing attempt. By pressing attempt form based on weaknesses you can attempt a form based on your weak areas. You can filter and sort forms using the buttons on the right.";
	
	private transient FormList forms;
	private transient QuestionList questions;
	private GUI gui;
	private boolean adminMode;
	private transient FormsInProgressList formsInProgress;
	
	
	private JPanel mainPanel = new JPanel();
	
	// For the view table
	private String[] tableHeaders = new String[] {"ID","Title", "Description", 
												  "Main Skills Tested",
												  "Difficulty", "Percent Complete",
												  "Times Completed"}; // The headers for the table
	private String[][] formData = new String[0][0];
	private DefaultTableModel formTableModel = new DefaultTableModel(formData, tableHeaders);
	private JTable formTable = new JTable(formTableModel); // Create a table to hold the questions
	private JScrollPane formTableScrollPane = new JScrollPane(formTable); // Create a scroll pane
	
	private JButton sortDifficultyButton = new JButton("Difficulty Sort"); // Button to sort by difficulty
	private JButton attemptButton = new JButton("Attempt Form"); // User presses this to attempt the selected question
	private JButton attemptUserWeaknessesFormButton = new JButton("Attempt form based on weaknesses"); // Button that the user can press to attempt a form based on their weaknesses
	private JButton deleteButton = new JButton("Delete Form"); // To delete the form

	// Filters
	private JPanel sortAndFilterPanel;
	private JPanel sortPanel;
	private JPanel difficultyFilterPanel; // To hold the sliders
	private JPanel typeFilterPanel;
	private JSlider difficultySlider = new JSlider(SwingConstants.HORIZONTAL, 1, 10, 1); // Filter slider
	private JButton difficultyFilterButton = new JButton("Apply difficulty filter"); // JButton that will apply the filter
	private JCheckBox[] typeCheckBoxes; // Holds the check boxes for the types
	private JPanel typeCheckBoxPanel = new JPanel();
	private JButton typeFilterButton = new JButton("Apply main skills tested filter");
	
	// Stores the filter icon
	private ImageIcon filterIcon;
	private JLabel filterIconLabel;
	
	private JButton resetButton = new JButton("Reset sorts and filters"); // To reset the sorts and filters
	
	private transient Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED); // Border style
	
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
	
	public String getHelpString()
	{
		/* Returns the help string */
		return HELP_STRING;
	}
	
	private void prepareGUI()
	{
		/* Prepares the panel for display */
		
		System.out.println("[INFO] <FORM_DISPLAY_PANEL> Running prepareGUI"); // Debug
			
		this.setLayout(new BorderLayout());
			
		GridBagLayout layout = new GridBagLayout(); // Create a new grid bag layout
		
		mainPanel.setLayout(layout); // Get the layout
		
		
		// Prepare the constraints
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
		Font currentFont = border.getTitleFont();
		border.setTitleFont(currentFont.deriveFont(Font.BOLD, 18)); // Make the font larger and bold
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		sortAndFilterPanel.setBorder(border); // Set the border
		
		
		// Prepare constraints for the sort and filter panel
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
		resetButton.setBackground(new Color(255,127,127)); // Make the button red
		resetButton.setForeground(Color.WHITE); // Make the text white
		sortAndFilterPanelConstraints.gridy = 2;
		sortAndFilterPanelConstraints.gridwidth = 3; // Span three columns
		
		sortAndFilterPanel.add(resetButton, sortAndFilterPanelConstraints);
		
		attemptButton.addActionListener(this);
		attemptButton.setBackground(new Color(130,183,75)); // Make it green

	
		mainPanelConstraints.gridheight = 1;
		mainPanelConstraints.weightx = 0.1;
		
		mainPanelConstraints.gridx = 2;
		mainPanel.add(sortAndFilterPanel, mainPanelConstraints);
		
		mainPanelConstraints.weighty = 0.2;
		mainPanelConstraints.gridx = 2;
		mainPanelConstraints.gridy = 1;
		
		if (adminMode) // Add the delete question button if the user is an admin
		{
			deleteButton.addActionListener(this);
			deleteButton.setBackground(new Color(174,59,46)); // Make it red
			deleteButton.setForeground(Color.WHITE); // Make the text white
			mainPanel.add(deleteButton, mainPanelConstraints);
		}
		
		attemptUserWeaknessesFormButton.setBackground(new Color(139, 102, 153)); // Make it purple
		attemptUserWeaknessesFormButton.addActionListener(this);
		
		mainPanelConstraints.gridy = 2;
		mainPanel.add(attemptUserWeaknessesFormButton, mainPanelConstraints);
		
		mainPanelConstraints.gridy = 3;
		mainPanel.add(attemptButton, mainPanelConstraints);
	
		prepareTable();
		
		this.add(mainPanel, BorderLayout.CENTER);
		
		this.setVisible(true);
	}
	
	private void prepareTable()
	{
		/* Prepares the table that the forms are displayed in */
		formTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only allow one row at a time to be selected
		formTable.setDefaultEditor(Object.class, null); // Disable editing
		
		
		// Make double clicking on a row open that question to be attempted.
		formTable.addMouseListener(new MouseAdapter() {
							@Override
							public void mousePressed(MouseEvent mouseEvent) {
								JTable table =(JTable) mouseEvent.getSource();
								if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
									attemptButton.doClick();
								}
							}
						});
		
		
		// Hide the first column as it contains the id and we don't want that displayed to the user
		TableColumnModel tcm = formTable.getColumnModel();
		tcm.removeColumn(tcm.getColumn(0));
		
		// Add the wordwrap renderer to each of the columns in the table
		for (int i = 0; i < formTable.getColumnCount(); i++)
		{
			tcm.getColumn(i).setCellRenderer(new WordWrapCellRenderer());
			tcm.getColumn(i).setHeaderRenderer(new WordWrapHeaderRenderer());
		}
		
		// Add a listener so that we can detect when a column changes size.
		tcm.addColumnModelListener(this);
		
		// Make the Title column as small as possible
		tcm.getColumn(0).setMaxWidth(120);
		tcm.getColumn(0).setPreferredWidth(90);
		
		
		// Fix the difficulty, percentage complete, and time completed columns to the required size
		tcm.getColumn(3).setMaxWidth(90);
		
		tcm.getColumn(4).setMaxWidth(90);
		
		tcm.getColumn(5).setMaxWidth(100);
		
		populateTable(forms.getArray()); // Populate the table with the questions
	}
		
	private void prepareSortPanel()
	{
		/* Sets up the sort panel, this contains the sort buttons as well as the filter icon */
		
		// Setup the filter icon
		// Even though this is the sorts panel and not a filter panel we are putting 
		// the icon here because this is the place that looks
		// best and most closely matches the design.
		filterIcon = new ImageIcon(this.getClass().getResource("/icons/filter.png"));
		filterIcon = new ImageIcon(filterIcon.getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT)); // Make the icon smaller
		filterIconLabel = new JLabel("");
		filterIconLabel.setIcon(filterIcon);
		filterIconLabel.setVisible(false); // Make it invisble by default
		
		sortPanel = new JPanel();
		sortPanel.setLayout(new GridBagLayout());
		
		// Prepare the gridbag layout constraints
		GridBagConstraints sortPanelConstraints = new GridBagConstraints();
		sortPanelConstraints.fill = GridBagConstraints.BOTH;
		sortPanelConstraints.insets = new Insets(5,5,5,5); // 5 px padding all around
		sortPanelConstraints.gridx = 0;
		sortPanelConstraints.gridy = 0;
		sortPanelConstraints.weightx = 1;
		sortPanelConstraints.weighty = 1; 
		
		
		// Make the sorts label
		JLabel sortsLabel = new JLabel("Sorts", SwingConstants.CENTER);
		Font currentFont = sortsLabel.getFont();
		sortsLabel.setFont(currentFont.deriveFont(Font.BOLD, 17)); // Make the font larger and bold
		
		sortPanel.add(sortsLabel, sortPanelConstraints);
		
		sortPanel.add(filterIconLabel, sortPanelConstraints);
		sortPanelConstraints.gridy = 1;
		
		sortDifficultyButton.addActionListener(this);
		sortDifficultyButton.setBackground(new Color(169,196,235)); // Make it blue
		sortPanel.add(sortDifficultyButton, sortPanelConstraints);
	}
	
	private void prepareDifficultyFilterPanel()
	{
		/* Prepares the difficulty filter panel and slider */
		
		// Prepare the difficulty filter
		difficultyFilterPanel = new JPanel();
		difficultyFilterPanel.setLayout(new GridBagLayout()); // Create a grid bag layout
		
		
		// Prepare the gridbag constraints
		GridBagConstraints difficultyFilterPanelConstraints = new GridBagConstraints();
		difficultyFilterPanelConstraints.fill = GridBagConstraints.BOTH; // Fill all available horizontal and vertical space

		difficultyFilterPanelConstraints.weightx = 1;
		difficultyFilterPanelConstraints.insets = new Insets(5,5,5,5); // 5 px padding all around
		difficultyFilterPanelConstraints.gridx = 0;
		difficultyFilterPanelConstraints.gridy = 0;
		difficultyFilterPanelConstraints.weightx = 1;
		difficultyFilterPanelConstraints.weighty = 1; 
		
		// Create the label
		JLabel difficultyFilterLabel = new JLabel("Difficulty Filter", SwingConstants.CENTER);
		Font currentFont = difficultyFilterLabel.getFont();
		difficultyFilterLabel.setFont(currentFont.deriveFont(Font.BOLD, 17)); // Make the font larger and bold
		
		difficultyFilterPanel.add(difficultyFilterLabel, difficultyFilterPanelConstraints);
		difficultyFilterPanelConstraints.gridy += 1;
		
		difficultySlider.setMajorTickSpacing(1);
		difficultySlider.setPaintTicks(true); // Add the ticks
		difficultySlider.setPaintLabels(true); // Add the labels
		
		difficultyFilterButton.addActionListener(this);
		difficultyFilterButton.setBackground(new Color(169,196,235)); // Make it blue
		
		difficultyFilterPanel.add(difficultySlider, difficultyFilterPanelConstraints);
		difficultyFilterPanelConstraints.gridy += 1;
		
		difficultyFilterPanel.add(difficultyFilterButton, difficultyFilterPanelConstraints);
		difficultyFilterPanelConstraints.gridy += 1;
	}
	
	private void prepareTypeFilterPanel()
	{
		/* Prepare the type filter panel and checkboxes */
		
		prepareTypeCheckBoxes();

		typeFilterButton.addActionListener(this);
		typeFilterButton.setBackground(new Color(169,196,235)); // Make it blue
		
		typeFilterPanel = new JPanel();
		typeFilterPanel.setLayout(new GridBagLayout());
		
		// Setup the constraints
		GridBagConstraints typeFilterPanelConstraints = new GridBagConstraints();
		typeFilterPanelConstraints.fill = GridBagConstraints.BOTH;
		typeFilterPanelConstraints.insets = new Insets(5,5,5,5); // 5 px padding all around
		typeFilterPanelConstraints.gridx = 0;
		typeFilterPanelConstraints.gridy = 0;
		typeFilterPanelConstraints.weightx = 1;
		typeFilterPanelConstraints.weighty = 1; 
		
		// Create the label
		JLabel typeFilterLabel = new JLabel("Type filter", SwingConstants.CENTER);
		Font currentFont = typeFilterLabel.getFont();
		typeFilterLabel.setFont(currentFont.deriveFont(Font.BOLD, 17)); // Make the font larger and bold
		
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
		/* Prepares the type checkboxes that the user can use to filter */
		
		typeCheckBoxPanel.setLayout(new GridLayout(0,3)); // 3 rows infinite columns
		
		String[] questionTypes = questions.getTypes(); // Get the types

		typeCheckBoxes = new JCheckBox[questionTypes.length]; // Create an array large enough to store the check boxes

		for (int i = 0; i < typeCheckBoxes.length; i++) // For each type of question
		{
			typeCheckBoxes[i] = new JCheckBox(questionTypes[i]); // Create a checkbox with the correct name
			typeCheckBoxPanel.add(typeCheckBoxes[i]); // Add the check boxes to the check box panel 
		}
	}
	
	public void refresh()
	{
		/* Refreshes the table and the type filter checkboxes */
		refreshTable();
		
		refreshTypeFilterButtons();
	}
	
	private void refreshTypeFilterButtons()
	{
		/* Refreshs the type filter checkboxes by resetting the tab */
		
		if (typeCheckBoxes.length != questions.getTypes().length)
		{
			System.out.println("[INFO] <FORM_DISPLAY_PANEL> Types have changed, resetting panel");
			gui.resetTab(this); // Reset the tab.
		}
	}	
	
	private void refreshTable()
	{
		/* Refreshes the table. Preserves sorts and filters */
		
		Form[] latestFormData = forms.getArray();
		
		// Difficulty sort the forms if the difficulty sort is applied
		if (difficultySort)
		{
			forms.sortByDifficulty();
			latestFormData = forms.getArray();
		}
		
		if (difficultyFilter && typeFilter) // If they selected both filters we need to find the intersection of the filters
		{
			Form[] difficulty = forms.filterByDifficulty(difficultySlider.getValue());
			Form[] type = forms.filterByType(getTypesSelected());
			
			Form[] intersection = new Form[forms.getArray().length]; // At most it could contain every question
			int nextIntersectionLocation = 0;
			
			// Add each form that is in both arrays to the intersection array
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
			
			latestFormData = new Form[nextIntersectionLocation];
			
			for (int i = 0; i < nextIntersectionLocation; i++)
			{
				latestFormData[i] = intersection[i];
			}
		}
		else if (difficultyFilter) // If just the difficulty filter is applied, perform the filter
		{
			latestFormData = forms.filterByDifficulty(difficultySlider.getValue());
		}
		else if (typeFilter) // If just the type filter is applied, perform the filter
		{
			latestFormData = forms.filterByType(getTypesSelected());
		}
		
		// Show the sort icon if a sort has been applied
		if (difficultyFilter || typeFilter)
		{
			filterIconLabel.setVisible(true);
		}
		else
		{
			filterIconLabel.setVisible(false);
		}
		
		populateTable(latestFormData);
	}
	
	private void clearCheckboxes()
	{
		/* Unselects all of the type filter checkboxes */
		
		// Iterate over all of the checkboxes and deselect each one
		for (JCheckBox checkbox : typeCheckBoxes)
		{
			checkbox.setSelected(false);
		}
	}
	
	private void resetTable()
	{
		/* Disables all of the sorts and filters and then refreshes the table */
		
		clearCheckboxes();

		typeFilter = false;
		difficultySort = false;
		difficultyFilter = false;
		
		refreshTable();
		
	}
	
	private void populateTable(Form[] data)
	{
		/* Populates the table with the form data */
		
		System.out.println("[INFO] <FORM_DISPLAY_PANEL> Running populateTable"); // Debug
		
		formTableModel.setRowCount(0); // Start at zero rows
		
		for (int i =0; i < data.length; i++) // For each form in the array
		{
			if(data[i] != null) // If there is data
			{
				Form f = data[i]; // The current form. The name is f as it'll be called a large amount of time. f is to make the calls less cluttered
								  
				String percentageComplete = "0%";
				String timesCompleted = "0";
				
				String formID = f.getID();
				if (formsInProgress.isFormPresent(formID)) // If the form is partially completed / has already been attempted by the user
				{
					FormInProgress fP = formsInProgress.getByID(formID);
					percentageComplete = fP.getPercentComplete() + "%"; // Get the percentage that the form is complete, append %, and add it to the end of the array
					timesCompleted = fP.getTimesCompleted() + ""; // Get the times completed and convert to string
				}
				
				// "ID","Title", "Description", "Main Skills Tested","Difficulty", "Percent Complete", "Times completed"
				// This is the column order
				
				// The default output is just a . between the skills
				// A , and a space looks better for outputting to the user
				String betterLookingMainskillsTested = f.mainSkillsTestedToString().replace(".", ", ");
				
				String[] latestFormData = {f.getID(), f.getTitle(), f.getDescription(), betterLookingMainskillsTested, 
									f.getDifficulty() + "", percentageComplete, timesCompleted}; // Convert the form to a String array
				
				formTableModel.addRow(latestFormData); // Add the form to the table
			}
		}
		
		resizeRows();
	}

	private String[] getTypesSelected()
	{
		/* Gets which type checkboxes are selected */
		
		String[] typesSelected = new String[questions.getTypes().length]; // Create an array of the correct size

		int nextLocation = 0;
		
		// Iterate over the type checkboxes and add the ones that are selected to the array
		for (JCheckBox cB : typeCheckBoxes)
		{
			if (cB.isSelected()) //  If the box is checked
			{
				typesSelected[nextLocation] = cB.getText();
				nextLocation++;
			}
		}

		// Trim the array
		
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
			if (row != -1) // If a row was actually selected
			{
				Form selectedForm = forms.getFormByID(formTable.getModel().getValueAt(row, 0).toString()); // Get the form id and get the form
				gui.openForm(selectedForm); // Open the form
			}
		}
		else if (evt.getSource() == resetButton)
		{
			System.out.println("[INFO] <FORM_DISPLAY_PANEL> resetButton pressed"); // Debug
			resetTable();
		}
		else if (evt.getSource() == deleteButton)
		{
			
			System.out.println("[INFO] <FORM_DISPLAY_PANEL> deleteButton pressed"); // Debug
			int row = formTable.getSelectedRow();
			
			if (row != -1) // If a row was actually selected
			{
				String  selectedFormID = formTable.getModel().getValueAt(row, 0).toString(); // Get the form id
				String formTitle = formTable.getModel().getValueAt(row, 1).toString(); // Get the form title
				
				int delete = JOptionPane.showConfirmDialog(this, "Are you sure that you want to delete \"" + formTitle + "\"?" , "Are you sure?", JOptionPane.YES_NO_OPTION); // Confirm the delete
				
				if (delete == 0) // If they pressed yes
				{
					forms.removeForm(selectedFormID); // Remove the form
					forms.writeDatabase();
				}
				
				refreshTable();
			}
		}
		else if (evt.getSource() == attemptUserWeaknessesFormButton)
		{
			System.out.println("[INFO] <FORM_DISPLAY_PANEL> attemptUserWeaknessesFormButton pressed");
			gui.attemptFormFromUserWeaknesses();
		}
	}
	
	private void resizeRows()
	{
		/* Resizes the rows based on the column width to ensure that all of the text can be read */
		
		for (int row = 0; row < formTable.getRowCount(); row ++)
		{
			int requiredHeight = 0;
			
			// Go through the columns and get the largest height of all of the components
			for (int col = 0; col < formTable.getColumnCount(); col++)
			{
				TableCellRenderer cellRenderer = formTable.getCellRenderer(row, col);
				Component c = formTable.prepareRenderer(cellRenderer, row, col);
				
				int preferredHeight = c.getPreferredSize().height;
				
				if (preferredHeight > requiredHeight)
				{
					requiredHeight = preferredHeight;
				}

			}
			
			// Set the height of the row to that height if that's not already the height
			if (formTable.getRowHeight(row) != requiredHeight)
			{
				formTable.setRowHeight(row, requiredHeight);
			}
		}
	}
	
	public void columnMarginChanged(ChangeEvent e)
	{
		/* Resize the rows if a column has been resized */
		resizeRows();
	}
	
	/* These methods need to be implemented for this class to be a column listener */
	public void columnAdded(TableColumnModelEvent e) {/* required for column listener */}
	
	public void columnRemoved(TableColumnModelEvent e) {/* required for column listener */}
	
	public void columnMoved(TableColumnModelEvent e) {/* required for column listener */}
	
	public void columnSelectionChanged(ListSelectionEvent e) {/* required for column listener */}

}