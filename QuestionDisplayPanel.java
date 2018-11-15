import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class QuestionDisplayPanel extends JPanel implements ActionListener
{
	private QuestionList questions;
	private GUI gui;
	
	private JPanel mainPanel = new JPanel(); // Holds all of the components apart from the help button
	private JButton helpButton = new JButton("Help");
	// For the view table
	private String[] tableHeaders = new String[] {"ID","Difficulty", "Type"}; // The headers for the table
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
	private JPanel filterPanel; // To hold the sliders
	private JSlider difficultySlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 1); // Filter slider
	private JButton difficultyFilterButton = new JButton("Filter by difficulty"); // JButton that will apply the filter
	private JRadioButton[] typeRadioButtons; // Holds the radio buttons for the types
	private JPanel typeRadioButtonPanel = new JPanel();
	private ButtonGroup typeRadioButtonGroup = new ButtonGroup();
	private JButton typeFilterButton = new JButton("Filter by type");
	
	public QuestionDisplayPanel(QuestionList tempList, GUI tempGUI) // Constructor
	{
		questions = tempList; // Store the question list
		gui = tempGUI;
		
		prepareGUI();
	}
	
	private void prepareGUI() // Makes the window
	{
		System.out.println("[INFO] <QUESTION_DISPLAY_PANEL> Running prepareGUI"); // Debug
			
		this.setLayout(new BorderLayout());
			
		GridLayout layout = new GridLayout(1,2); // Create a new grid layout
			
		mainPanel.setLayout(layout); // Get the layout
			
		mainPanel.add(questionTableScrollPane); // Add the table to the view
		
		// Add action listeners 
		sortDifficultyButton.addActionListener(this);
		sortTypeButton.addActionListener(this);
		attemptButton.addActionListener(this);
		deleteButton.addActionListener(this);
		
		buttonPanel = new JPanel(); // Create a new JPanel
		buttonPanel.setLayout(new GridLayout(4,1)); // Create a grid layout with 4 rows and 1 column
		buttonPanel.add(sortDifficultyButton);
		buttonPanel.add(sortTypeButton);
		buttonPanel.add(attemptButton);
		buttonPanel.add(deleteButton);
		 
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

		prepareTypeRadioButtons();

		typeFilterButton.addActionListener(this);

		filterPanel.add(typeRadioButtonPanel);
		filterPanel.add(typeFilterButton);
		
		mainPanel.add(filterPanel);
		
		// Hide the first column as it contains the id and we don't want that displayed to the user
		TableColumnModel tcm = questionTable.getColumnModel();
		tcm.removeColumn(tcm.getColumn(0));
		
		populateTable(questions.getArray()); // Populate the table with the questions
	
		this.add(mainPanel, BorderLayout.CENTER);
		
		helpButton.addActionListener(this);
		
		this.add(helpButton, BorderLayout.NORTH);
		
		this.setVisible(true);
	}
	
	private void prepareTypeRadioButtons()
	{
		typeRadioButtonPanel.setLayout(new GridLayout(0,4)); // 4 rows infinite columns
		
		String[] questionTypes = questions.getTypes(); // Get the types

		typeRadioButtons = new JRadioButton[questionTypes.length]; // Create an array large enough to store the radio buttons

		for (int i = 0; i < typeRadioButtons.length; i++) // For each type of question
		{
			typeRadioButtons[i] = new JRadioButton(questionTypes[i]); // Create a radio button with the correct name
			typeRadioButtonPanel.add(typeRadioButtons[i]); // Add the check boxes to the check box panel 
			typeRadioButtonGroup.add(typeRadioButtons[i]);
		}
	}
	
	
	private void populateTable(Question[] data) // Populates the table with data
	{
		
		System.out.println("[INFO] <QUESTION_DISPLAY_PANEL> Running populateTable"); // Debug
		
		questionTableModel.setRowCount(0); // Start a zero rows
		
		for (int i =0; i < data.length; i++) // For each question in the array
		{
			if(data[i] != null) // If there is data
			{
				String[] question = data[i].toStringArray(); // Convert the question to a String array
				questionTableModel.addRow(question); // Add the question to the table
			}
		}
	}
	
	private void openQuestionInWindow(String questionID) // Opens a question to practise in a window
	{
		System.out.println("[INFO] <QUESTION_DISPLAY_PANEL> Running openQuestionInWindow");
		
		// Create a form that has only the question in it. Set the id of the question as the id of the form too
		Form questionForm = new Form.FormBuilder(questionID, questions).add(questionID).build();
		
		gui.openForm(questionForm); // Open the form
	}
	
	private String getTypeSelected() // Gets which type radio button is selected
	{
		String typeSelected = "";
		
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
	
	public void actionPerformed(ActionEvent evt)
	{
		
		if (evt.getSource() == sortDifficultyButton)
		{
			System.out.println("[INFO] <QUESTION_DISPLAY_PANEL> sortDifficultyButton pressed"); // Debug
			questions.sortByDifficulty(); // Sort the list by type
			populateTable(questions.getArray());
		}
		else if (evt.getSource() == sortTypeButton)
		{
			System.out.println("[INFO] <QUESTION_DISPLAY_PANEL> sortTypeButton pressed"); // Debug
			questions.sortByType();
			populateTable(questions.getArray());
		}
		else if (evt.getSource() == typeFilterButton)
		{
			System.out.println("[INFO] <QUESTION_DISPLAY_PANEL> typeFilterButton pressed"); // Debug
			populateTable(questions.filterByType(getTypeSelected()));
		}
		else if (evt.getSource() == difficultyFilterButton)
		{
			System.out.println("[INFO] <QUESTION_DISPLAY_PANEL> difficultyFilterButton pressed"); // Debug
			populateTable(questions.filterByDifficulty(difficultySlider.getValue()));
		}
		else if (evt.getSource() == attemptButton)
		{
			System.out.println("[INFO] <QUESTION_DISPLAY_PANEL> attemptButton pressed"); // Debug
			int row = questionTable.getSelectedRow();
			String selectedQuestion = questionTable.getModel().getValueAt(row, 0).toString(); // Get the id of the question that the user selected
			openQuestionInWindow(questionTable.getModel().getValueAt(row, 0).toString()); // Get the id of the question in the selected row and open a window
		}
		else if (evt.getSource() == deleteButton)
		{
			/*
			System.out.println("[INFO] <QUESTION_DISPLAY_PANEL> deleteButton pressed"); // Debug
			int row = questionTable.getSelectedRow();
			String selectedQuestion = questionTable.getModel().getValueAt(row, 0).toString(); // Get the id of the question that the user selected
			questions.removeQuestion(selectedQuestion); // Delete the question
			*/
		}
		else if (evt.getSource() == helpButton)
		{
			System.out.println("[INFO] <QUESTION_DISPLAY_PANEL> helpButton pressed"); // Debug
			JOptionPane.showMessageDialog(null, "This is the view questions screen. From here you can select questions and attempt them by \r\n selecting them in the table and pressing the attempt button. You can \r\n filter and sort questions by using the buttons on the right.");
		}
	}

}