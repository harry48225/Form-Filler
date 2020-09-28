package com.harry.formfiller.gui.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.harry.formfiller.form.Form;
import com.harry.formfiller.form.FormList;
import com.harry.formfiller.gui.WordWrapCellRenderer;
import com.harry.formfiller.gui.WordWrapHeaderRenderer;
import com.harry.formfiller.gui.question.component.HeaderPanel;
import com.harry.formfiller.question.Question;
import com.harry.formfiller.question.QuestionList;

public class SelectFormsPanel extends JPanel implements ActionListener, TableColumnModelListener
{
	/* Provides a table and sorts that allow the user to select a form */
	
	private transient FormList forms;
	private transient QuestionList questions;
	
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
	
	private boolean difficultySort = false;
	public SelectFormsPanel(FormList tempForms, QuestionList tempQuestions)
	{
		forms = tempForms;
		questions = tempQuestions;
		prepareGUI();
	}
	
	private void prepareGUI()
	{
		/* Prepares the panel */
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		prepareTable();
		this.add(formTableScrollPane);
		
		prepareButtonPanel();
		this.add(buttonPanel);
		
		this.setVisible(true);
	}
	
	public void addNewButton(JButton buttonToAdd) // Allows a button to be added to the button panel
	{
		/* Allows a new button to be added to the panel, useful for context specific buttons for the different places the panel is incremented */
		buttonPanel.add(buttonToAdd);
		this.revalidate();
	}

	private void prepareButtonPanel()
	{
		/* Prepares the panel that contains all of the buttons */
		
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS)); // Horizontal box layout

		difficultySortButton.setBackground(new Color(169,196,235)); // Blue
		previewButton.setBackground(new Color(169,196,235)); // Blue
		
		difficultySortButton.addActionListener(this);
		previewButton.addActionListener(this);
		
		// Make the buttons the correct size
		typeSortButton.setMaximumSize(new Dimension(80, 40));
		difficultySortButton.setMaximumSize(new Dimension(80, 40));
		previewButton.setMaximumSize(new Dimension(80, 40));

		// Add the buttons with horizontal glue between them to fill the empty space
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(difficultySortButton);
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(previewButton);
		buttonPanel.add(Box.createHorizontalGlue());
	}
	
	public void refreshTable()
	{
		/* Refreshes the table. Preserves sorts and filters */
		Form[] latestFormData = forms.getArray();
		
		// If the difficulty sort is applied, perform a difficulty sort
		if (difficultySort)
		{
			forms.sortByDifficulty();
			latestFormData = forms.getArray();
		}
		
		populateTable(latestFormData);
	}
	
	private void populateTable(Form[] data)
	{
		/* Populates the table with data */
		System.out.println("[INFO] <SELECT_FORMS_PANEL> Running populateTable"); // Debug
		
		
		formTableModel.setRowCount(0); // Start a zero rows
		
		// Fills the table with all of the forms passed in as a parameter
		for (int i =0; i < data.length; i++) // For each form in the array
		{
			if(data[i] != null) // If there is data
			{
				Form f = data[i];
				
				// "ID","Title", "Description", "Main Skills Tested","Difficulty"
				// This is the column order
				
				// The default output is just a . between the skills
				// A , and a space looks better for outputting to the user
				String betterLookingMainskillsTested = f.mainSkillsTestedToString().replace(".", ", ");
				
				String[] latestFormData = {f.getID(), f.getTitle(), f.getDescription(), betterLookingMainskillsTested, 
									 f.getDifficulty() + ""}; // Convert the form to a String array
				
				formTableModel.addRow(latestFormData); // Add the form to the table
			}
		}
		
		resizeRows();
	}
	
	private void prepareTable()
	{
		/* Prepares the table for display */
		
		formTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only allow one row at a time to be selected
		formTable.setDefaultEditor(Object.class, null); // Disable editing
		
		
		// Hide the first column as it contains the id and we don't want that displayed to the user
		TableColumnModel tcm = formTable.getColumnModel();
		tcm.removeColumn(tcm.getColumn(0));
		
		// Set the cell renderer for each column to the word wrap renderer
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
			
			// Apply the difficulty sort and remove the type sort and then refresh the table
			difficultySort = true;
			refreshTable();
		}
		else if (e.getSource() == typeSortButton)
		{
			System.out.println("[INFO] <SELECT_FORMS_PANEL> typeSortButton pressed"); // Debug
			
			difficultySort = false;
			refreshTable();
		}
		else if (e.getSource() == previewButton)
		{
			// Opens the form that the user selected to preview
			
			int row = formTable.getSelectedRow();
			if (row != -1) // If they actually selected a row
			{
				openFormInWindow(formTable.getModel().getValueAt(row, 0).toString()); // Get the id of the question in the selected row and open a window
			}
		}
	}
	
	private void openFormInWindow(String formID)
	{
		/* Opens a form to preview in a window */
		System.out.println("[INFO] <SELECT_FORMS_PANEL> Running openFormInWindow");
		
		Form selectedForm = forms.getFormByID(formID); // Get the form object corresponding to the selected form
		
		JPanel formPanel = new JPanel();
		formPanel.setLayout(new GridLayout(0,1));
	

		int width = 400;
		int height = selectedForm.getQuestionIDs().length * 110; // Give each question 110px of vertical space
		formPanel.setSize(width, height);
		formPanel.setMaximumSize(new Dimension(width, height));
		formPanel.setPreferredSize(new Dimension(width, height));
		
		// Add each question/header to the form preview
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
		
		// Add a scrollpane so that the form preview can be scrolled
		JScrollPane formScroller = new JScrollPane(formPanel);
		
		// Set the size of the scroll pane correctly
		height = height > 600 ? 620 : height; // If the height is over 600 set it to 620
		height += 20; // Add a little extra height so that the scroll bar doesn't appear if the height is less than 600
		width += 25;
		formScroller.setSize(width, height);
		formScroller.setMaximumSize(new Dimension(width, height));
		formScroller.setPreferredSize(new Dimension(width, height));
		
		// Show the form preview to the user
		JOptionPane.showMessageDialog(this, formScroller, selectedForm.getTitle() + " Preview", JOptionPane.PLAIN_MESSAGE);
	}
		
	
	public String getSelectedFormID()
	{
		/* Returns the id of the form that the user has selected in the table */
		int row = formTable.getSelectedRow();
		
		return row == -1 ? null:formTable.getModel().getValueAt(row, 0).toString(); // Get the id of the question in the selected row or null if no row is selected
	}
	
	private void resizeRows()
	{
		/* Resizes the rows of the table so that all of the text can be read */
		
		// Resize each row
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
		/* Resize the rows if a column has changed size */
		resizeRows();
	}
	public void columnAdded(TableColumnModelEvent e) {/* required for column listener */}
	
	public void columnRemoved(TableColumnModelEvent e) {/* required for column listener */}
	
	public void columnMoved(TableColumnModelEvent e) {/* required for column listener */}
	
	public void columnSelectionChanged(ListSelectionEvent e) {/* required for column listener */}
	
}