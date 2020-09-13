import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

import java.util.*;

public class QuestionDisplayPanel extends JPanel implements ActionListener, TableColumnModelListener, Helper
{
	/* This is a panel which displays questions to the user and allows them to sort and search them, and attempt them*/
	
	private final String HELP_STRING = "This is the view questions screen. From here you can select questions and attempt them by selecting them in the table and pressing the attempt button. You can filter and sort questions by using the buttons on the right.";
	
	private QuestionList questions;
	private GUI gui;
	private boolean adminMode;
	
	private JPanel mainPanel = new JPanel(); // Holds all of the components apart from the help button
	// For the view table
	private String[] tableHeaders = new String[] {"ID","Title", "Difficulty", "Type"}; // The headers for the table
	private String[][] questionData = new String[0][0];
	private DefaultTableModel questionTableModel = new DefaultTableModel(questionData, tableHeaders);
	private JTable questionTable = new JTable(questionTableModel); // Create a table to hold the questions
	private JScrollPane questionTableScrollPane = new JScrollPane(questionTable); // Create a scroll pane
	
	// Sort buttons
	private JPanel buttonPanel; // To hold the buttons
	private JButton sortDifficultyButton = new JButton("Difficulty Sort"); // Button to sort by difficulty
	private JButton sortTypeButton = new JButton("Type Sort"); // Sorts by type
	private JButton attemptButton = new JButton("Attempt Question"); // User presses this to attempt the selected question
	private JButton deleteButton = new JButton("Delete Question"); // To delete the question
	// Filters
	private JPanel sortAndFilterPanel;
	private JPanel sortPanel;
	private JPanel difficultyFilterPanel; // To hold the sliders
	private JPanel typeFilterPanel;
	private JSlider difficultySlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 1); // Filter slider
	private JButton difficultyFilterButton = new JButton("Apply difficulty filter"); // JButton that will apply the filter
	private JRadioButton[] typeRadioButtons; // Holds the radio buttons for the types
	private JPanel typeRadioButtonPanel = new JPanel();
	private ButtonGroup typeRadioButtonGroup = new ButtonGroup();
	private JButton typeFilterButton = new JButton("Apply type filter");
	
	private JButton resetButton = new JButton("Reset sorts and filters"); // To reset the sorts and filters
	
	private Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED); // Border style
	
	// Store which sorts and filters have been applied
	private boolean typeSort = false;
	private boolean difficultySort = false;
	private boolean typeFilter = false;
	private boolean difficultyFilter = false;
	
	public QuestionDisplayPanel(QuestionList tempList, GUI tempGUI, boolean tempAdminMode) // Constructor
	{
		questions = tempList; // Store the question list
		gui = tempGUI;
		adminMode = tempAdminMode;
		
		prepareGUI();
	}
	
	public String getHelpString()
	{
		/* Returns the help string associated with this panel */
		return HELP_STRING;
	}
	
	private void prepareGUI()
	{
		/* Prepares the panel to be displayed */
		
		System.out.println("[INFO] <QUESTION_DISPLAY_PANEL> Running prepareGUI"); // Debug
			
		this.setLayout(new BorderLayout());
			
		GridBagLayout layout = new GridBagLayout(); // Create a new grid bag layout
		
		mainPanel.setLayout(layout); // Set the layout
		
		GridBagConstraints mainPanelConstraints = new GridBagConstraints();
		mainPanelConstraints.fill = GridBagConstraints.BOTH; // Make components fill empty space to the sides and top and bottom
	
		// Setup the constraints
		mainPanelConstraints.weightx = 1;
		mainPanelConstraints.weighty = 1;
		mainPanelConstraints.gridx = 0;
		mainPanelConstraints.gridy = 0;

		mainPanelConstraints.gridheight = 3; // Make the component take up 3 rows

		mainPanelConstraints.insets = new Insets(5,5,5,5); // 5 px padding all around
		
		mainPanel.add(questionTableScrollPane, mainPanelConstraints); // Add the table to the view
		
		
		// Prepare the sort and filter panel
		sortAndFilterPanel = new JPanel(); // Create a new JPanel
		sortAndFilterPanel.setLayout(new GridBagLayout());
		
		// Prepare the border and its title
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Sort and Filter");
		Font currentFont = border.getTitleFont();
		border.setTitleFont(currentFont.deriveFont(Font.BOLD, 18)); // Make the font larger and bold
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		sortAndFilterPanel.setBorder(border); // Set the border
		
		
		// Setup a new set of constraints for the sort and filter panel
		GridBagConstraints sortAndFilterPanelConstraints = new GridBagConstraints();

		sortAndFilterPanelConstraints.fill = GridBagConstraints.BOTH; // Fill horizontal and vertical
		sortAndFilterPanelConstraints.insets = new Insets(5,5,5,5); // 5 px padding all around
		sortAndFilterPanelConstraints.gridx = 0;
		sortAndFilterPanelConstraints.gridy = 0;
		sortAndFilterPanelConstraints.weightx = 1;
		sortAndFilterPanelConstraints.weighty = 1; 

		prepareSortPanel();
		
		sortAndFilterPanel.add(sortPanel, sortAndFilterPanelConstraints); // Add the sort panel to the sort and filter panel
		
		// Prepare the filters
		
		prepareDifficultyFilterPanel();
		
		sortAndFilterPanelConstraints.gridx = 1;
		sortAndFilterPanelConstraints.gridwidth = 2; // Span two columns
		
		sortAndFilterPanel.add(difficultyFilterPanel, sortAndFilterPanelConstraints); // Add the difficulty panel to the sort and filter panel
		
		// Prepare the type filter
		
		prepareTypeFilterPanel();
		
		sortAndFilterPanelConstraints.gridx = 0;
		sortAndFilterPanelConstraints.gridy = 1;
		sortAndFilterPanelConstraints.gridwidth = 3; // Span three columns
		
		sortAndFilterPanel.add(typeFilterPanel, sortAndFilterPanelConstraints); // Add the type filter panel to the sort and filter panel
		
		// Prepare the reset sorts and filters button
		resetButton.addActionListener(this);
		resetButton.setBackground(new Color(255,127,127)); // Make the button red
		resetButton.setForeground(Color.WHITE); // Make the text white
		sortAndFilterPanelConstraints.gridy = 2;
		sortAndFilterPanelConstraints.gridwidth = 3; // Span three columns
		
		sortAndFilterPanel.add(resetButton, sortAndFilterPanelConstraints); // Add the reset button to the sort and filter panel
		
		attemptButton.addActionListener(this);
		attemptButton.setBackground(new Color(130,183,75)); // Make it green

		mainPanelConstraints.gridheight = 1;
		mainPanelConstraints.weightx = 0.1; // This component shouldn't take up much more horizontal space when the window is resized
		
		mainPanelConstraints.gridx = 1;
		mainPanel.add(sortAndFilterPanel, mainPanelConstraints);
		
		mainPanelConstraints.weighty = 0.2;
		mainPanelConstraints.gridx = 1;
		mainPanelConstraints.gridy = 1;
		
		if (adminMode) // Add the delete question button if the user is an admin
		{
			deleteButton.addActionListener(this);
			deleteButton.setBackground(new Color(174,59,46)); // Make it red
			deleteButton.setForeground(Color.WHITE); // Make the text white
			mainPanel.add(deleteButton, mainPanelConstraints);
		}
		
		mainPanelConstraints.gridy = 2;
		mainPanel.add(attemptButton, mainPanelConstraints);
		
		prepareTable();
		
		populateTable(questions.getArray()); // Populate the table with the questions
	
		this.add(mainPanel, BorderLayout.CENTER);
		
		this.setVisible(true);
	}
	
	private void prepareTable()
	{
		/* Prepares the table to display the questions */
		
		questionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only allow one row at a time to be selected
		questionTable.setDefaultEditor(Object.class, null); // Disable editing
		
		// Make double clicking on a row open that question to be attempted.
		questionTable.addMouseListener(new MouseAdapter() {
							public void mousePressed(MouseEvent mouseEvent) {
								JTable table =(JTable) mouseEvent.getSource();
								if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
									attemptButton.doClick();
								}
							}
						});
		
		// Hide the first column as it contains the id and we don't want that displayed to the user
		TableColumnModel tcm = questionTable.getColumnModel();
		tcm.removeColumn(tcm.getColumn(0));
		
		// Give each column the word wrap renderer so that the heights of the rows are automatically adjusted so that all text fits.
		for (int i = 0; i < questionTable.getColumnCount(); i++)
		{
			tcm.getColumn(i).setCellRenderer(new WordWrapCellRenderer());
			tcm.getColumn(i).setHeaderRenderer(new WordWrapHeaderRenderer());
		}
		tcm.addColumnModelListener(this); // Add a listener so that we can detect when the columns are resized
		
		populateTable(questions.getArray()); // Populate the table with the questions
	}
	
	private void prepareSortPanel()
	{
		/* Prepares the panel that contains the sort buttons */
		
		// Create the panel
		sortPanel = new JPanel();
		sortPanel.setLayout(new GridBagLayout());
		
		// Prepare the grid bag constraints
		GridBagConstraints sortPanelConstraints = new GridBagConstraints();
		sortPanelConstraints.fill = GridBagConstraints.BOTH;
		sortPanelConstraints.insets = new Insets(5,5,5,5); // 5 px padding all around
		sortPanelConstraints.gridx = 0;
		sortPanelConstraints.gridy = 0;
		sortPanelConstraints.weightx = 1;
		sortPanelConstraints.weighty = 1; 
		
		// Create the sorts label with bold size 17 font
		JLabel sortsLabel = new JLabel("Sorts", SwingConstants.CENTER);
		Font currentFont = sortsLabel.getFont();
		sortsLabel.setFont(currentFont.deriveFont(Font.BOLD, 17)); // Make the font larger and bold
		sortPanel.add(sortsLabel, sortPanelConstraints);
		
		// Prepare the sort buttons
		sortPanelConstraints.gridy = 1;
		sortDifficultyButton.addActionListener(this);
		sortDifficultyButton.setBackground(new Color(169,196,235)); // Blue
		sortPanel.add(sortDifficultyButton, sortPanelConstraints);
		
		sortPanelConstraints.gridy = 2;
		sortTypeButton.addActionListener(this);
		sortTypeButton.setBackground(new Color(169,196,235)); // Blue
		sortPanel.add(sortTypeButton, sortPanelConstraints);
	}
	
	private void prepareDifficultyFilterPanel()
	{
		/* Prepares the panel that contains the difficulty filter */
		
		// Prepare the difficulty filter
		difficultyFilterPanel = new JPanel();
		difficultyFilterPanel.setLayout(new GridBagLayout()); // Create a grid bag layout
		
		// Prepare the gridbag constraints
		GridBagConstraints difficultyFilterPanelConstraints = new GridBagConstraints();
		difficultyFilterPanelConstraints.fill = GridBagConstraints.BOTH;

		difficultyFilterPanelConstraints.weightx = 1;
		difficultyFilterPanelConstraints.insets = new Insets(5,5,5,5); // 5 px padding all around
		difficultyFilterPanelConstraints.gridx = 0;
		difficultyFilterPanelConstraints.gridy = 0;
		difficultyFilterPanelConstraints.weightx = 1;
		difficultyFilterPanelConstraints.weighty = 1; 
		
		// Create the difficulty filter label with bold size 17 text
		JLabel difficultyFilterLabel = new JLabel("Difficulty Filter", SwingConstants.CENTER);
		Font currentFont = difficultyFilterLabel.getFont();
		difficultyFilterLabel.setFont(currentFont.deriveFont(Font.BOLD, 17)); // Make the font larger and bold
		
		difficultyFilterPanel.add(difficultyFilterLabel, difficultyFilterPanelConstraints);
		difficultyFilterPanelConstraints.gridy += 1;
		
		// Prepare the difficulty slider
		difficultySlider.setMajorTickSpacing(1);
		difficultySlider.setPaintTicks(true); // Add the ticks
		difficultySlider.setPaintLabels(true);
		
		difficultyFilterButton.addActionListener(this);
		difficultyFilterButton.setBackground(new Color(169,196,235));
		
		difficultyFilterPanel.add(difficultySlider, difficultyFilterPanelConstraints);
		difficultyFilterPanelConstraints.gridy += 1;
		
		difficultyFilterPanel.add(difficultyFilterButton, difficultyFilterPanelConstraints);
		difficultyFilterPanelConstraints.gridy += 1;
	}
	
	private void prepareTypeFilterPanel()
	{
		/*  Prepare the type filter panel */
		
		prepareTypeRadioButtons();
	
		// Prepare the type filter button
		typeFilterButton.addActionListener(this);
		typeFilterButton.setBackground(new Color(169,196,235)); // Blue
		
		typeFilterPanel = new JPanel();
		typeFilterPanel.setLayout(new GridBagLayout());
		
		// Prepare the grid bag constraints
		GridBagConstraints typeFilterPanelConstraints = new GridBagConstraints();
		typeFilterPanelConstraints.fill = GridBagConstraints.BOTH;
		typeFilterPanelConstraints.insets = new Insets(5,5,5,5); // 5 px padding all around
		typeFilterPanelConstraints.gridx = 0;
		typeFilterPanelConstraints.gridy = 0;
		typeFilterPanelConstraints.weightx = 1;
		typeFilterPanelConstraints.weighty = 1; 
		
		// Prepare the label with bold size 17 text
		JLabel typeFilterLabel = new JLabel("Type filter", SwingConstants.CENTER);
		Font currentFont = typeFilterLabel.getFont();
		typeFilterLabel.setFont(currentFont.deriveFont(Font.BOLD, 17)); // Make the font larger and bold
		
		typeFilterPanelConstraints.gridx = 2; // Put it in the middle column
		typeFilterPanel.add(typeFilterLabel, typeFilterPanelConstraints);
		
		typeFilterPanelConstraints.gridx = 0;
		typeFilterPanelConstraints.gridy = 1;
		typeFilterPanelConstraints.gridwidth = 3; // Span 3 columns
		typeFilterPanel.add(typeRadioButtonPanel, typeFilterPanelConstraints);
		
		typeFilterPanelConstraints.gridy = 2;
		typeFilterPanel.add(typeFilterButton, typeFilterPanelConstraints);
	}
	
	public void refresh()
	{
		/* Refreshes the tab with the most recent information */
		refreshTable();
		
		refreshTypeFilterButtons();
	}
	
	private void refreshTypeFilterButtons()
	{
		/* Calls the reset tab method if new types have been added to the system so that new radio buttons are added for those types to filter by */
		if (typeRadioButtons.length != questions.getTypes().length)
		{
			System.out.println("[INFO] <QUESTION_DISPLAY_PANEL> Types have changed, resetting panel");
			gui.resetTab(this); // Reset the tab.
		}
	}
	
	private void refreshTable()
	{
		/* Refreshes the table. Preserves sorts and filters */
		
		Question[] questionData = questions.getArray(); // Get the most recent question data
		
		
		// If they have selected a sort / filter apply it
		
		if (typeSort)
		{
			questions.sortByType();
			questionData = questions.getArray();
		}
		if (difficultySort)
		{
			questions.sortByDifficulty();
			questionData = questions.getArray();
		}
		
		if (difficultyFilter && typeFilter) // If they selected both filters we need to find the intersection of the filters
		{
			Question[] difficulty = questions.filterByDifficulty(difficultySlider.getValue());
			Question[] type = questions.filterByType(getTypeSelected());
			
			Question[] intersection = new Question[questions.getArray().length]; // At most it could contain every question
			int nextIntersectionLocation = 0;
			
			// Look through the questions in the arrays and add them to the intersection array if they are in both arrays
			for (Question qD : difficulty)
			{
				for (Question qT : type)
				{
					if (qD == qT) // If they are the same question
					{
						intersection[nextIntersectionLocation] = qT;
						nextIntersectionLocation++;
					}
				}
			}
			
			// Trim the array
			
			questionData = new Question[nextIntersectionLocation];
			
			for (int i = 0; i < nextIntersectionLocation; i++)
			{
				questionData[i] = intersection[i];
			}
		}
		else if (difficultyFilter)
		{
			questionData = questions.filterByDifficulty(difficultySlider.getValue());
		}
		else if (typeFilter)
		{
			questionData = questions.filterByType(getTypeSelected());
		}
		
		populateTable(questionData); // Populate the table with the results of the sorts/filters
	}
	
	private void prepareTypeRadioButtons()
	{
		/* Creates a radio button for each type of question in the system */
		
		typeRadioButtonPanel = new JPanel();
		typeRadioButtonPanel.setLayout(new GridLayout(0,3)); // 3 rows infinite columns
		
		String[] questionTypes = questions.getTypes(); // Get the types

		typeRadioButtons = new JRadioButton[questionTypes.length]; // Create an array large enough to store the radio buttons

		for (int i = 0; i < typeRadioButtons.length; i++) // For each type of question
		{
			typeRadioButtons[i] = new JRadioButton(questionTypes[i]); // Create a radio button with the correct name
			typeRadioButtonPanel.add(typeRadioButtons[i]); // Add the check boxes to the check box panel 
			typeRadioButtonGroup.add(typeRadioButtons[i]);
		}
	}
	
	
	private void populateTable(Question[] data)
	{
		/* Populates the table with data */
		
		System.out.println("[INFO] <QUESTION_DISPLAY_PANEL> Running populateTable"); // Debug
		
		questionTableModel.setRowCount(0); // Start a zero rows
		
		for (int i =0; i < data.length; i++) // For each question in the array
		{
			if(data[i] != null) // If there is data
			{
				Question currentQuestion = data[i];
				
				String[] question = {currentQuestion.getID(), currentQuestion.getTitle(), 
									 currentQuestion.getDifficulty() + "", currentQuestion.getType()}; // Convert the question to a String array
				questionTableModel.addRow(question); // Add the question to the table
			}
		}
		
		resizeRows();
	}
	
	private void openQuestionInWindow(String questionID)
	{
		/* Opens a question to practise in a window */
		
		System.out.println("[INFO] <QUESTION_DISPLAY_PANEL> Running openQuestionInWindow");
		
		// Create a form that has only the question in it. Set the id of the question as the id of the form too
		Form questionForm = new Form.FormBuilder(questionID, questions).add(questionID, true).build();
		
		gui.openForm(questionForm); // Open the form
	}
	
	private String getTypeSelected()
	{
		/*  Gets which type radio button is selected */
		String typeSelected = "";
		
		// For each radio button
		for (JRadioButton rB : typeRadioButtons)
		{
			if (rB.isSelected()) //  If the button is selected
			{
				typeSelected = rB.getText();
				break;
			}
		}

		return typeSelected;
	}
	
	private void resetTable()
	{
		/* Resets the table by setting all of the sorts and filters to false. */
		
		typeRadioButtonGroup.clearSelection();
		
		typeSort = false;
		typeFilter = false;
		difficultySort = false;
		difficultyFilter = false;
		
		refreshTable();
		
	}
	
	public void actionPerformed(ActionEvent evt)
	{
		
		if (evt.getSource() == sortDifficultyButton)
		{
			System.out.println("[INFO] <QUESTION_DISPLAY_PANEL> sortDifficultyButton pressed"); // Debug
			
			// Disable type sort and enable difficulty sort and then refresh the table
			typeSort = false;
			difficultySort = true;
			refreshTable();
		}
		else if (evt.getSource() == sortTypeButton)
		{
			System.out.println("[INFO] <QUESTION_DISPLAY_PANEL> sortTypeButton pressed"); // Debug
			
			// Disable difficulty sort and enable type sort and then refresh the table
			typeSort = true;
			difficultySort = false;
			refreshTable();
		}
		else if (evt.getSource() == typeFilterButton)
		{
			System.out.println("[INFO] <QUESTION_DISPLAY_PANEL> typeFilterButton pressed"); // Debug
			
			// Enable the type filter and refresh the table
			typeFilter = true;
			refreshTable();
		}
		else if (evt.getSource() == difficultyFilterButton)
		{
			System.out.println("[INFO] <QUESTION_DISPLAY_PANEL> difficultyFilterButton pressed"); // Debug
			// Enable the difficulty filter and refresh the table
			difficultyFilter = true;
			refreshTable();
		}
		else if (evt.getSource() == attemptButton)
		{
			System.out.println("[INFO] <QUESTION_DISPLAY_PANEL> attemptButton pressed"); // Debug
			
			// Get the index of the selected row
			int row = questionTable.getSelectedRow();
			
			if (row != -1) // If a question has been selected
			{
				String selectedQuestion = questionTable.getModel().getValueAt(row, 0).toString(); // Get the id of the question that the user selected
				openQuestionInWindow(questionTable.getModel().getValueAt(row, 0).toString()); // Get the id of the question in the selected row and open a window
			}
		}
		else if (evt.getSource() == deleteButton)
		{
			System.out.println("[INFO] <QUESTION_DISPLAY_PANEL> deleteButton pressed"); // Debug
			
			int row = questionTable.getSelectedRow();
			
			if (row != -1) // If a row was actually selected
			{
				String selectedQuestionID = questionTable.getModel().getValueAt(row, 0).toString(); // Get the id of the question that the user selected
				Question selectedQuestion = questions.getQuestionByID(selectedQuestionID);
				int delete = JOptionPane.showConfirmDialog(this, "Are you sure that you want to delete \"" + selectedQuestion.getTitle() + "\"?" , "Are you sure?", JOptionPane.YES_NO_OPTION); // Confirm the delete
				
				if (delete == 0) // If they pressed yes
				{
					questions.removeQuestion(selectedQuestionID); // Delete the question
				}
				
				refreshTable();
			}
			
		}
		else if (evt.getSource() == resetButton)
		{
			System.out.println("[INFO] <QUESTION_DISPLAY_PANEL> resetButton pressed"); // Debug
			resetTable();
		}
	}
	
	private void resizeRows()
	{
		// Adjusts the sizes of the rows so that all of the text can be seen.
		for (int row = 0; row < questionTable.getRowCount(); row ++)
		{
			int requiredHeight = 0;
			
			// Go through the columns and get the largest height of all of the components
			for (int col = 0; col < questionTable.getColumnCount(); col++)
			{
				TableCellRenderer cellRenderer = questionTable.getCellRenderer(row, col);
				Component c = questionTable.prepareRenderer(cellRenderer, row, col);
				
				int preferredHeight = c.getPreferredSize().height;
				
				if (preferredHeight > requiredHeight)
				{
					requiredHeight = preferredHeight;
				}

			}
			
			// Set the height of the row to that height if that's not already the height
			if (questionTable.getRowHeight(row) != requiredHeight)
			{
				questionTable.setRowHeight(row, requiredHeight);
			}
		}
	}
	
	public void columnMarginChanged(ChangeEvent e)
	{
		/* If the width of the columns has been changed, resize the rows */
		resizeRows();
	}
	public void columnAdded(TableColumnModelEvent e) {}
	
	public void columnRemoved(TableColumnModelEvent e) {}
	
	public void columnMoved(TableColumnModelEvent e) {}
	
	public void columnSelectionChanged(ListSelectionEvent e) {}

}