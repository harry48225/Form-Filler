import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.table.*;

import java.io.*;

public class ImportExportPanel extends JPanel implements ActionListener
{
	QuestionList questions;
	FormList forms;
	
	private JFileChooser fileChooser = new JFileChooser();
	
	private JButton importQuestionButton = new JButton("Import question");
	
	private JButton importFormButton = new JButton("Import form");
	
	public ImportExportPanel(QuestionList tempQuestions, FormList tempForms)
	{
		questions = tempQuestions;
		forms = tempForms;
		
		prepareGUI();
	}
	
	private void prepareGUI()
	{
		this.setLayout(new GridLayout(0,2));
		
		this.add(new QuestionSelectionPanel(questions)); // Add a new question selection panel
		
		importQuestionButton.addActionListener(this);
		this.add(importQuestionButton);
		
		this.add(new FormSelectionPanel(forms, questions));
		
		importFormButton.addActionListener(this);
		this.add(importFormButton);
	}
	
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == importQuestionButton)
		{
			System.out.println("[INFO] <IMPORT_EXPORT_PANEL> importQuestionButton pressed");
			
			importQuestion();
		}
		else if (evt.getSource() == importFormButton)
		{
			System.out.println("[INFO] <IMPORT_EXPORT_PANEL> importFormButton pressed");
			
			importForm();
		}
	}
	
	private void importQuestion()
	{
		System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Running importQuestion");
		
		fileChooser.showOpenDialog(this);
		
		ExportedQuestion questionImported = null;
		
		try
		{
			FileInputStream fileIn = new FileInputStream(fileChooser.getSelectedFile()); // Open an input stream at the file that the user selected
			ObjectInputStream in = new ObjectInputStream(fileIn); // Create an object input stream
			
			questionImported = (ExportedQuestion) in.readObject(); // Read the ExportedQuestion from the file
			
			in.close(); // Close the file
			
			fileIn.close();
		}
		catch(Exception e)
		{
			System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Error importing question " + e); // Output the error
		}
		
		questions.addQuestion(questionImported.getQuestion(), questionImported.getQuestionPanel()); // Add the imported question to the database
		
		questions.writeDatabase();
		
		System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Question imported!");
	}
	
	private void importForm()
	{
		System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Running importForm");
		
		fileChooser.showOpenDialog(this);
		
		ExportedForm formImported = null;
		
		try
		{
			FileInputStream fileIn = new FileInputStream(fileChooser.getSelectedFile()); // Open an input stream at the file that the user selected
			ObjectInputStream in = new ObjectInputStream(fileIn); // Create an object input stream
			
			formImported = (ExportedForm) in.readObject(); // Read the ExportedForm from the file
			
			in.close(); // Close the file
			
			fileIn.close();
		}
		catch(Exception e)
		{
			System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Error importing form " + e); // Output the error
		}
		
		// Add the questions to the question database if they are not present
		
		for (ExportedQuestion importedQuestion : formImported.getQuestions()) // For each question that was exported with the form
		{
			String importedQuestionID = importedQuestion.getQuestion().getID(); // Get the id of the question
			
			if (questions.getQuestionByID(importedQuestionID) == null) // If the question isn't present in the question list
			{
				questions.addQuestion(importedQuestion.getQuestion(), importedQuestion.getQuestionPanel()); // Add the imported question to the question list
			}			
		}
		
		forms.addForm(formImported.getForm()); // Add the form to the form list
		
		questions.writeDatabase();
		forms.writeDatabase();
		
		System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Form imported!");
	}
	
	private void exportQuestion(String questionID)
	{
		System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Running exportQuestion");
		
		Question q = questions.getQuestionByID(questionID); // Get the question
		QuestionPanel qP = questions.getPanelByID(questionID); // Get the question panel
		
		ExportedQuestion questionToExport = new ExportedQuestion(q, qP); // Create a new ExportedQuestion object
		
		fileChooser.setSelectedFile(new File("export.ser"));
		fileChooser.showSaveDialog(this);
		
		try
		{
			FileOutputStream fileOut = new FileOutputStream(fileChooser.getSelectedFile()); // Open a new output stream at file selected from the save dialog
			ObjectOutputStream out = new ObjectOutputStream(fileOut); // Create an object output stream
			
			out.writeObject(questionToExport); // Serialize the question
			
			out.close();
		}
		catch (IOException e)
		{
			System.out.println("[ERROR] <IMPORT_EXPORT_PANEL> Error exporting form to file " + e); // Output the error
		}
		
		System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Question exported!");
		
	}
	
	private void exportForm(String formID)
	{
		System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Running exportForm");
		
		Form form = forms.getFormByID(formID);
		
		String[] questionIDs = form.getQuestionIDs();
		
		ExportedQuestion[] exportedQuestions = new ExportedQuestion[questionIDs.length]; // Create an array to store the exported question objects
		
		for (int i = 0; i < questionIDs.length; i++) // For each question in the form
		{
			String questionID = questionIDs[i];
			
			Question q = questions.getQuestionByID(questionID); // Get the question
			QuestionPanel qP = questions.getPanelByID(questionID); // Get the question panel
		
			ExportedQuestion questionToExport = new ExportedQuestion(q, qP); // Create a new ExportedQuestion object
			
			exportedQuestions[i] = questionToExport; // Add the question to export to the array
		}
		
		ExportedForm formToExport = new ExportedForm(form, exportedQuestions); // Add the questions and the form to an exported form object
		
		// Get the user to select a place to save the form
		fileChooser.setSelectedFile(new File("export.ser"));
		fileChooser.showSaveDialog(this);
		
		try
		{
			FileOutputStream fileOut = new FileOutputStream(fileChooser.getSelectedFile()); // Open a new output stream at file selected from the save dialog
			ObjectOutputStream out = new ObjectOutputStream(fileOut); // Create an object output stream
			
			out.writeObject(formToExport); // Serialize the form
			
			out.close();
		}
		catch (IOException e)
		{
			System.out.println("[ERROR] <IMPORT_EXPORT_PANEL> Error exporting form to file " + e); // Output the error
		}
		
		System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Form exported!");
	}
	
	private class FormSelectionPanel extends JPanel implements ActionListener
	{
		private FormList forms;
		private QuestionList questions;
		
		// For the view table
		private String[] tableHeaders = new String[] {"ID","Questions","Description","Main Skills Tested","Difficulty"}; // The headers for the table
		private String[][] formData = new String[0][0];
		private DefaultTableModel formTableModel = new DefaultTableModel(formData, tableHeaders);
		private JTable formTable = new JTable(formTableModel); // Create a table to hold the questions
		private JScrollPane formTableScrollPane = new JScrollPane(formTable); // Create a scroll pane
		
		// Sort buttons
		private JPanel buttonPanel; // To hold the buttons
		private JButton sortDifficultyButton = new JButton("Difficulty Sort"); // Button to sort by difficultly
		private JButton attemptButton = new JButton("Preview Form"); // User presses this to view the selected form
		private JButton exportButton = new JButton("Export"); // When pressed exports the form
		
		
		
		public FormSelectionPanel(FormList tempForms, QuestionList tempQuestions) // Constructor
		{
			forms = tempForms; // Store the form list
			questions = tempQuestions;
				
			prepareGUI();
		}
		
		private void prepareGUI() // Makes the window
		{
			System.out.println("[INFO] <FORM_SELECTION_PANEL> Running prepareGUI"); // Debug
				
				
			GridLayout layout = new GridLayout(1,2); // Create a new grid layout
				
			this.setLayout(layout); // Get the layout
				
			this.add(formTableScrollPane); // Add the table to the view
			
			// Add action listeners 
			sortDifficultyButton.addActionListener(this);
			attemptButton.addActionListener(this);
			exportButton.addActionListener(this);
			
			buttonPanel = new JPanel(); // Create a new JPanel
			buttonPanel.setLayout(new GridLayout(0,1)); // Create a grid layout with infinite rows and 1 column
			buttonPanel.add(sortDifficultyButton);
			buttonPanel.add(attemptButton);
			buttonPanel.add(exportButton);
			 
			this.add(buttonPanel);
			
			// Hide the first column as it contains the id and we don't want that displayed to the user
			TableColumnModel tcm = formTable.getColumnModel();
			tcm.removeColumn(tcm.getColumn(0));
			
			populateTable(forms.getArray()); // Populate the table with the questions
		}
		
		private void populateTable(Form[] data) // Populates the table with data
		{
			
			System.out.println("[INFO] <FORM_SELECTION_PANEL> Running populateTable"); // Debug
			
			formTableModel.setRowCount(0); // Start a zero rows
			
			for (int i =0; i < data.length; i++) // For each form in the array
			{
				if(data[i] != null) // If there is data
				{
					String[] form = data[i].toStringArray(); // Convert the form to a String array
					formTableModel.addRow(form); // Add the form to the table
				}
			}
		}
		
		private void openFormInWindow(String formID) // Opens a form to preview in a window
		{
			System.out.println("[INFO] <FORM_SELECTION_PANEL> Running openFormInWindow");
			
			Form selectedForm = forms.getFormByID(formID);
			
			JFrame formFrame = new JFrame();
			formFrame.setLayout(new GridLayout(0,1));
			formFrame.setSize(300, 300);
			
			for (String question : selectedForm.getQuestionIDs()) // For each question id in the form
			{
				formFrame.add(questions.getPanelByID(question)); // Add the question to the frame
			}
			
			formFrame.setVisible(true);
		}
		
		public void actionPerformed(ActionEvent evt)
		{
			
			if (evt.getSource() == sortDifficultyButton)
			{
				System.out.println("[INFO] <FORM_SELECTION_PANEL> sortDifficultyButton pressed"); // Debug
				forms.sortByDifficulty(); // Sort the list by type
				populateTable(forms.getArray());
			}
			else if (evt.getSource() == attemptButton)
			{
				System.out.println("[INFO] <FORM_SELECTION_PANEL> attemptButton pressed"); // Debug
				int row = formTable.getSelectedRow();
				String formID = formTable.getModel().getValueAt(row, 0).toString();
				openFormInWindow(formID);
			}
			else if (evt.getSource() == exportButton)
			{
				System.out.println("[INFO] <FORM_SELECTION_PANEL> exportButton pressed"); // Debug
				
				int row = formTable.getSelectedRow();
				String formID = (String) formTable.getModel().getValueAt(row, 0) + ""; // Get the id of the form in the selected row
				ImportExportPanel.this.exportForm(formID); // Call the export form method.
			}
		}
	
	}
	
	private class QuestionSelectionPanel extends JPanel implements ActionListener
	{
		private QuestionList questions;
		
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
		private JButton attemptButton = new JButton("Preview Question"); // User presses this to view the selected question
		private JButton exportButton = new JButton("Export"); // When pressed exports the question
		
		
		
		public QuestionSelectionPanel(QuestionList tempList) // Constructor
		{
			questions = tempList; // Store the question list
				
			prepareGUI();
		}
		
		private void prepareGUI() // Makes the window
		{
			System.out.println("[INFO] <QUESTION_SELECTION_PANEL> Running prepareGUI"); // Debug
				
				
			GridLayout layout = new GridLayout(1,2); // Create a new grid layout
				
			this.setLayout(layout); // Get the layout
				
			this.add(questionTableScrollPane); // Add the table to the view
			
			// Add action listeners 
			sortDifficultyButton.addActionListener(this);
			sortTypeButton.addActionListener(this);
			attemptButton.addActionListener(this);
			exportButton.addActionListener(this);
			
			buttonPanel = new JPanel(); // Create a new JPanel
			buttonPanel.setLayout(new GridLayout(0,1)); // Create a grid layout with infinite rows and 1 column
			buttonPanel.add(sortDifficultyButton);
			buttonPanel.add(sortTypeButton);
			buttonPanel.add(attemptButton);
			buttonPanel.add(exportButton);
			 
			this.add(buttonPanel);
			
			// Hide the first column as it contains the id and we don't want that displayed to the user
			TableColumnModel tcm = questionTable.getColumnModel();
			tcm.removeColumn(tcm.getColumn(0));
			
			populateTable(questions.getArray()); // Populate the table with the questions
		}
		
		private void populateTable(Question[] data) // Populates the table with data
		{
			
			System.out.println("[INFO] <QUESTION_SELECTION_PANEL> Running populateTable"); // Debug
			
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
		
		private void openQuestionInWindow(String qID) // Opens a question to practise in a window
		{
			System.out.println("[INFO] <QUESTION_SELECTION_PANEL> Running openQuestionInWindow");
			
			JFrame questionFrame = new JFrame();
			questionFrame.setLayout(new GridLayout(0,1));
			questionFrame.setSize(300, 100);
			questionFrame.add(questions.getPanelByID(qID));
			questionFrame.setVisible(true);
		}
		
		public void actionPerformed(ActionEvent evt)
		{
			
			if (evt.getSource() == sortDifficultyButton)
			{
				System.out.println("[INFO] <QUESTION_SELECTION_PANEL> sortDifficultyButton pressed"); // Debug
				questions.sortByDifficulty(); // Sort the list by type
				populateTable(questions.getArray());
			}
			else if (evt.getSource() == sortTypeButton)
			{
				System.out.println("[INFO] <QUESTION_SELECTION_PANEL> sortTypeButton pressed"); // Debug
				questions.sortByType();
				populateTable(questions.getArray());
			}
			else if (evt.getSource() == attemptButton)
			{
				System.out.println("[INFO] <QUESTION_SELECTION_PANEL> attemptButton pressed"); // Debug
				int row = questionTable.getSelectedRow();
				openQuestionInWindow(questionTable.getModel().getValueAt(row, 0).toString()); // Get the id of the question in the selected row and open a window
			}
			else if (evt.getSource() == exportButton)
			{
				System.out.println("[INFO] <QUESTION_SELECTION_PANEL> exportButton pressed"); // Debug
				
				int row = questionTable.getSelectedRow();
				String selectedQuestionID = (String) questionTable.getModel().getValueAt(row, 0) + ""; // Get the id of the question in the selected row
				ImportExportPanel.this.exportQuestion(selectedQuestionID); // Call the export question method.
			}
		}
	
	}
}