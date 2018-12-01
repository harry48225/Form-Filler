import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class SelectQuestionsPanel extends JPanel implements ActionListener, TableColumnModelListener
{
	private QuestionList questions;

	private String[] tableHeaders = new String[] {"ID","Title", "Difficulty", "Type"}; // The headers for the table
	private String[][] questionData = new String[0][0];
	private DefaultTableModel questionTableModel = new DefaultTableModel(questionData, tableHeaders);
	private JTable questionTable = new JTable(questionTableModel); // Create a table to hold the questions
	private JScrollPane questionTableScrollPane = new JScrollPane(questionTable); // Create a scroll pane

	private JPanel buttonPanel = new JPanel();
	
	private JButton typeSortButton = new JButton("Type sort");
	private JButton difficultySortButton = new JButton("Difficulty sort");
	private JButton previewButton = new JButton("Preview");
	
	// Store which sorts and filters have been applied
	private boolean typeSort = false;
	private boolean difficultySort = false;
	private boolean typeFilter = false;
	private boolean difficultyFilter = false;
	
	public SelectQuestionsPanel(QuestionList tempQuestions)
	{
		questions = tempQuestions;
		prepareGUI();
	}
	
	private void prepareGUI()
	{
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		prepareTable();
		this.add(questionTableScrollPane);
		
		prepareButtonPanel();
		this.add(buttonPanel);
		
		this.setVisible(true);
	}
	
	public void addNewButton(JButton buttonToAdd) // Allows a button to be added to the button panel
	{
		buttonPanel.add(buttonToAdd);
		this.revalidate();
	}

	private void prepareButtonPanel()
	{
		
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		
		typeSortButton.setBackground(new Color(169,196,235));
		difficultySortButton.setBackground(new Color(169,196,235));
		previewButton.setBackground(new Color(169,196,235));
		
		typeSortButton.addActionListener(this);
		difficultySortButton.addActionListener(this);
		previewButton.addActionListener(this);
		
		typeSortButton.setMaximumSize(new Dimension(80, 40));
		difficultySortButton.setMaximumSize(new Dimension(80, 40));
		previewButton.setMaximumSize(new Dimension(80, 40));
		
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(typeSortButton);
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(difficultySortButton);
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(previewButton);
		buttonPanel.add(Box.createHorizontalGlue());
	}
	
	public void refreshTable() // Refreshes the table. Preserves sorts and filters
	{
		Question[] questionData = questions.getArray();
		
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
		
		populateTable(questionData);
	}
	
	private void populateTable(Question[] data) // Populates the table with data
	{
		
		System.out.println("[INFO] <SELECT_QUESTIONS_PANEL> Running populateTable"); // Debug
		
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
	
	private void prepareTable()
	{
		questionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only allow one row at a time to be selected
		// Hide the first column as it contains the id and we don't want that displayed to the user
		TableColumnModel tcm = questionTable.getColumnModel();

		tcm.removeColumn(tcm.getColumn(0));
		
		for (int i = 0; i < questionTable.getColumnCount(); i++)
		{
			tcm.getColumn(i).setCellRenderer(new WordWrapCellRenderer());
			tcm.getColumn(i).setHeaderRenderer(new WordWrapHeaderRenderer());
		}
		tcm.addColumnModelListener(this);
		
		populateTable(questions.getArray()); // Populate the table with the questions
	}
	
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == difficultySortButton)
		{
			System.out.println("[INFO] <SELECT_QUESTIONS_PANEL> difficultySortButton pressed"); // Debug
			difficultySort = true;
			typeSort = false;
			refreshTable();
		}
		else if (e.getSource() == typeSortButton)
		{
			System.out.println("[INFO] <SELECT_QUESTIONS_PANEL> typeSortButton pressed"); // Debug
			typeSort = true;
			difficultySort = false;
			refreshTable();
		}
		else if (e.getSource() == previewButton)
		{
			int row = questionTable.getSelectedRow();
			if (row != -1) // If they actually selected a row
			{
				openQuestionInWindow(questionTable.getModel().getValueAt(row, 0).toString()); // Get the id of the question in the selected row and open a window
			}
		}
	}
	
	private void openQuestionInWindow(String qID) // Opens a question to practise in a window
	{
		System.out.println("[INFO] <QUESTION_SELECTION_PANEL> Running openQuestionInWindow");
		
		JFrame questionFrame = new JFrame();
		questionFrame.setLayout(new GridLayout(0,1));
		questionFrame.setSize(300, 100);
		questionFrame.add(questions.getPanelByID(qID));
		questionFrame.setVisible(true);
	}
	
	public String getSelectedQuestionID()
	{
		int row = questionTable.getSelectedRow();
		return row == -1 ? null:questionTable.getModel().getValueAt(row, 0).toString(); // Get the id of the question in the selected row
	}
	
	private void resizeRows()
	{
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
		resizeRows();
	}
	public void columnAdded(TableColumnModelEvent e) {}
	
	public void columnRemoved(TableColumnModelEvent e) {}
	
	public void columnMoved(TableColumnModelEvent e) {}
	
	public void columnSelectionChanged(ListSelectionEvent e) {}
	
}