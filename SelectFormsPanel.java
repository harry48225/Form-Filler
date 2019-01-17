import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

import components.*;

public class SelectFormsPanel extends JPanel implements ActionListener, TableColumnModelListener
{
	private FormList forms;
	private QuestionList questions;
	
	private String[] tableHeaders = new String[] {"ID","Title", "Description", 
												  "Main Skills Tested",
												  "Difficulty"};
	private String[][] formData = new String[0][0];
	private DefaultTableModel formTableModel = new DefaultTableModel(formData, tableHeaders);
	private JTable formTable = new JTable(formTableModel); // Create a table to hold the questions
	private JScrollPane formTableScrollPane = new JScrollPane(formTable); // Create a scroll pane

	private JPanel buttonPanel = new JPanel();
	
	private JButton typeSortButton = new JButton("Type sort");
	private JButton difficultySortButton = new JButton("Difficulty sort");
	private JButton previewButton = new JButton("Preview");
	
	// Store which sorts and filters have been applied
	private boolean typeSort = false;
	private boolean difficultySort = false;
	private boolean typeFilter = false;
	private boolean difficultyFilter = false;
	
	public SelectFormsPanel(FormList tempForms, QuestionList tempQuestions)
	{
		forms = tempForms;
		questions = tempQuestions;
		prepareGUI();
	}
	
	private void prepareGUI()
	{
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		prepareTable();
		this.add(formTableScrollPane);
		
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

		difficultySortButton.setBackground(new Color(169,196,235));
		previewButton.setBackground(new Color(169,196,235));
		
		difficultySortButton.addActionListener(this);
		previewButton.addActionListener(this);
		
		typeSortButton.setMaximumSize(new Dimension(80, 40));
		difficultySortButton.setMaximumSize(new Dimension(80, 40));
		previewButton.setMaximumSize(new Dimension(80, 40));

		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(difficultySortButton);
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(previewButton);
		buttonPanel.add(Box.createHorizontalGlue());
	}
	
	public void refreshTable() // Refreshes the table. Preserves sorts and filters
	{
		Form[] formData = forms.getArray();
		
		if (difficultySort)
		{
			forms.sortByDifficulty();
			formData = forms.getArray();
		}
		
		populateTable(formData);
	}
	
	private void populateTable(Form[] data) // Populates the table with data
	{
		
		System.out.println("[INFO] <SELECT_FORMS_PANEL> Running populateTable"); // Debug
		
		formTableModel.setRowCount(0); // Start a zero rows
		
		for (int i =0; i < data.length; i++) // For each question in the array
		{
			if(data[i] != null) // If there is data
			{
				Form f = data[i];
				
				// "ID","Title", "Description", "Main Skills Tested","Difficulty"
				// This is the column order
				
				// The default output is just a . between the skills
				// A , and a space looks better for outputting to the user
				String betterLookingMainskillsTested = f.mainSkillsTestedToString().replace(".", ", ");
				
				String[] formData = {f.getID(), f.getTitle(), f.getDescription(), betterLookingMainskillsTested, 
									 f.getDifficulty() + ""}; // Convert the form to a String array
				
				formTableModel.addRow(formData); // Add the form to the table
			}
		}
		
		resizeRows();
	}
	
	private void prepareTable()
	{
		formTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only allow one row at a time to be selected
		formTable.setDefaultEditor(Object.class, null); // Disable editing
		// Hide the first column as it contains the id and we don't want that displayed to the user
		TableColumnModel tcm = formTable.getColumnModel();

		tcm.removeColumn(tcm.getColumn(0));
		
		for (int i = 0; i < formTable.getColumnCount(); i++)
		{
			tcm.getColumn(i).setCellRenderer(new WordWrapCellRenderer());
			tcm.getColumn(i).setHeaderRenderer(new WordWrapHeaderRenderer());
		}
		tcm.addColumnModelListener(this);
		
		populateTable(forms.getArray()); // Populate the table with the questions
	}
	
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == difficultySortButton)
		{
			System.out.println("[INFO] <SELECT_FORMS_PANEL> difficultySortButton pressed"); // Debug
			difficultySort = true;
			typeSort = false;
			refreshTable();
		}
		else if (e.getSource() == typeSortButton)
		{
			System.out.println("[INFO] <SELECT_FORMS_PANEL> typeSortButton pressed"); // Debug
			typeSort = true;
			difficultySort = false;
			refreshTable();
		}
		else if (e.getSource() == previewButton)
		{
			int row = formTable.getSelectedRow();
			if (row != -1) // If they actually selected a row
			{
				openFormInWindow(formTable.getModel().getValueAt(row, 0).toString()); // Get the id of the question in the selected row and open a window
			}
		}
	}
	
	private void openFormInWindow(String formID) // Opens a form to preview in a window
		{
			System.out.println("[INFO] <SELECT_FORMS_PANEL> Running openFormInWindow");
			
			Form selectedForm = forms.getFormByID(formID);
			
			JPanel formPanel = new JPanel();
			formPanel.setLayout(new GridLayout(0,1));
			int width = 400;
			int height = selectedForm.getQuestionIDs().length * 100;
			formPanel.setSize(width, height);
			formPanel.setMaximumSize(new Dimension(width, height));
			formPanel.setPreferredSize(new Dimension(width, height));
			
			for (String question : selectedForm.getQuestionIDs()) // For each question id in the form
			{
				Question q = questions.getQuestionByID(question);
				if (q != null) // If it's a question not a header
				{
					formPanel.add(questions.getPanelByID(question)); // Add the question to the frame
				}
				else
				{
					formPanel.add(new HeaderPanel(question)); // Add the header
				}
			}
			JScrollPane formScroller = new JScrollPane(formPanel);
			height = height > 600 ? 600 : height;
			width += 25;
			formScroller.setSize(width, height);
			formScroller.setMaximumSize(new Dimension(width, height));
			formScroller.setPreferredSize(new Dimension(width, height));
			formScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			JOptionPane.showMessageDialog(this, formScroller, selectedForm.getTitle() + " Preview", JOptionPane.PLAIN_MESSAGE);
		}
		
	
	public String getSelectedFormID()
	{
		int row = formTable.getSelectedRow();
		return row == -1 ? null:formTable.getModel().getValueAt(row, 0).toString(); // Get the id of the question in the selected row
	}
	
	private void resizeRows()
	{
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
		resizeRows();
	}
	public void columnAdded(TableColumnModelEvent e) {}
	
	public void columnRemoved(TableColumnModelEvent e) {}
	
	public void columnMoved(TableColumnModelEvent e) {}
	
	public void columnSelectionChanged(ListSelectionEvent e) {}
	
}