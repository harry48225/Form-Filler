import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.filechooser.*;

import javax.swing.table.*;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

import java.io.*;

public class ImportExportPanel extends JPanel implements ActionListener
{
	QuestionList questions;
	FormList forms;
	
	private JFileChooser fileChooser = new JFileChooser();
	
	private JButton importQuestionButton = new JButton("Import question");
	private JButton exportQuestionButton = new JButton("Export");
	
	private JButton importFormButton = new JButton("Import form");
	private JButton exportFormButton = new JButton("Export");
	
	private JPanel importExportQuestionsPanel = new JPanel();
	private JPanel importExportFormPanel = new JPanel();
	
	private SelectQuestionsPanel questionSelectionPanel;
	private SelectFormsPanel formSelectionPanel;
	
	private Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED); // Border style
	
	public ImportExportPanel(QuestionList tempQuestions, FormList tempForms)
	{
		questions = tempQuestions;
		forms = tempForms;
		
		prepareGUI();
	}
	
	public void refreshTables()
	{
		questionSelectionPanel.refreshTable();
		formSelectionPanel.refreshTable();
	}
	
	private void prepareGUI()
	{
		this.setLayout(new GridLayout(0,2)); // 2 columns
		
		prepareImportExportQuestionsPanel();
		this.add(importExportQuestionsPanel);
		
		prepareImportExportFormPanel();
		this.add(importExportFormPanel);
	}
	
	private void prepareImportExportFormPanel()
	{
		importExportFormPanel.setLayout(new BoxLayout(importExportFormPanel, BoxLayout.PAGE_AXIS));
		
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Forms");
		Font currentFont = border.getTitleFont();
		border.setTitleFont(currentFont.deriveFont(Font.BOLD, 16)); // Make the font larger and bold
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		importExportFormPanel.setBorder(border); // Set the border
		
		importExportFormPanel.add(Box.createRigidArea(new Dimension(0,10)));
		
		// Add the import button
		importFormButton.addActionListener(this);
		importFormButton.setBackground(new Color(130,183,75));
		importFormButton.setForeground(Color.WHITE);
		importFormButton.setMaximumSize(new Dimension(100000, 40));
		importFormButton.setMinimumSize(new Dimension(100000, 20));
		importExportFormPanel.add(importFormButton);
		
		importExportFormPanel.add(Box.createRigidArea(new Dimension(0,10)));
		
		// Add the selection panel
		formSelectionPanel = new SelectFormsPanel(forms,questions);
		importExportFormPanel.add(formSelectionPanel);
		
		importExportFormPanel.add(Box.createRigidArea(new Dimension(0,10)));
		
		// Add the export button
		exportFormButton.addActionListener(this);
		exportFormButton.setBackground(new Color(169,196,235));
		exportFormButton.setMaximumSize(new Dimension(100000, 40));
		exportFormButton.setMinimumSize(new Dimension(100000, 20));
		importExportFormPanel.add(exportFormButton);
	}
	
	private void prepareImportExportQuestionsPanel()
	{
		importExportQuestionsPanel.setLayout(new BoxLayout(importExportQuestionsPanel, BoxLayout.PAGE_AXIS));
		
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Questions");
		Font currentFont = border.getTitleFont();
		border.setTitleFont(currentFont.deriveFont(Font.BOLD, 16)); // Make the font larger and bold
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		importExportQuestionsPanel.setBorder(border); // Set the border
		
		importExportQuestionsPanel.add(Box.createRigidArea(new Dimension(0,10)));
		
		// Add the import button
		importQuestionButton.addActionListener(this);
		importQuestionButton.setBackground(new Color(130,183,75));
		importQuestionButton.setForeground(Color.WHITE);
		importQuestionButton.setMaximumSize(new Dimension(100000, 40));
		importQuestionButton.setMinimumSize(new Dimension(100000, 20));
		importExportQuestionsPanel.add(importQuestionButton);
		
		importExportQuestionsPanel.add(Box.createRigidArea(new Dimension(0,10)));
		
		// Add the selection panel
		questionSelectionPanel = new SelectQuestionsPanel(questions);
		importExportQuestionsPanel.add(questionSelectionPanel);
		
		importExportQuestionsPanel.add(Box.createRigidArea(new Dimension(0,10)));
		
		// Add the export button
		exportQuestionButton.addActionListener(this);
		exportQuestionButton.setBackground(new Color(169,196,235));
		exportQuestionButton.setMaximumSize(new Dimension(100000, 40));
		exportQuestionButton.setMinimumSize(new Dimension(100000, 20));
		importExportQuestionsPanel.add(exportQuestionButton);
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
		else if (evt.getSource() == exportQuestionButton)
		{
			System.out.println("[INFO] <IMPORT_EXPORT_PANEL> exportQuestionButton pressed"); // Debug
			String selectedQuestionID = questionSelectionPanel.getSelectedQuestionID();
			if (selectedQuestionID != null)
			{
				exportQuestion(selectedQuestionID);
			}
		}
		else if (evt.getSource() == exportFormButton)
		{
			System.out.println("[INFO] <IMPORT_EXPORT_PANEL> exportFormButton pressed"); // Debug
			String selectedFormID = formSelectionPanel.getSelectedFormID();
			if (selectedFormID != null)
			{
				exportForm(selectedFormID);
			}
		}
	}
	
	private void importQuestion()
	{
		System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Running importQuestion");
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Exported Questions", "question");
		
		fileChooser.setFileFilter(filter);
		
		int result = fileChooser.showOpenDialog(this);
		
		if (result == JFileChooser.CANCEL_OPTION)
		{
			System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Importing question cancelled");
			return;
		}
		
		ExportedQuestion questionImported = null;
		
		try
		{
			String filePath = fileChooser.getSelectedFile().getAbsolutePath();
			
			BufferedReader br = new BufferedReader(new FileReader(filePath)); // Open the database file
			
			
			questionImported = new ExportedQuestion(br.readLine()); // Read the ExportedQuestion from the file
		}
		catch(Exception e)
		{
			System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Error importing question " + e); // Output the error
		}
		
		// Add the question to the database overwriting the old one (if it exists)
		
		String questionID = questionImported.getQuestion().getID();
		
		if (questions.getQuestionByID(questionID) != null)
		{
			questions.removeQuestion(questionID);
		}
		
		questions.addQuestion(questionImported.getQuestion(), questionImported.getQuestionPanel()); // Add the imported question to the database
		
		questions.writeDatabase();
		
		System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Question imported!");
		JOptionPane.showMessageDialog(null, "Question imported!");
		
		refreshTables();
	}
	
	private void importForm()
	{
		System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Running importForm");
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Exported Forms", "form");
		
		fileChooser.setFileFilter(filter);
		
		int result = fileChooser.showOpenDialog(this);
		
		if (result == JFileChooser.CANCEL_OPTION)
		{
			System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Importing form cancelled");
			return;
		}
		
		ExportedForm formImported = null;
		
		try
		{
			String filePath = fileChooser.getSelectedFile().getAbsolutePath();
			
			BufferedReader br = new BufferedReader(new FileReader(filePath)); // Open the database file
			
			
			formImported = new ExportedForm(br.readLine()); // Read the ExportedForm from the file
		}
		catch(Exception e)
		{
			System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Error importing form " + e); // Output the error
		}

		// Add the questions to the question database if they're not already in it
		
		for (ExportedQuestion importedQuestion : formImported.getQuestions()) // For each question that was exported with the form
		{
			if (importedQuestion != null) // If the question isn't null
			{
				String importedQuestionID = importedQuestion.getQuestion().getID(); // Get the id of the question
				
				if (questions.getQuestionByID(importedQuestionID) == null) // If the question isn't present in the question list
				{
					questions.addQuestion(importedQuestion.getQuestion(), importedQuestion.getQuestionPanel()); // Add the imported question to the question list
				}
			}			
		}
		
		// Add the form to the database overwriting the old one (if it exists)
		
		String formID = formImported.getForm().getID();
		
		if (forms.getFormByID(formID) != null)
		{
			forms.removeForm(formID);
		}
		
		forms.addForm(formImported.getForm()); // Add the form to the form list
		
		questions.writeDatabase();
		forms.writeDatabase();
		
		System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Form imported!");
		JOptionPane.showMessageDialog(null, "Form imported!");
		
		refreshTables();
	}
	
	private void exportQuestion(String questionID)
	{
		System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Running exportQuestion");
		
		Question q = questions.getQuestionByID(questionID); // Get the question
		QuestionPanel qP = questions.getPanelByID(questionID); // Get the question panel
		
		ExportedQuestion questionToExport = new ExportedQuestion(q, qP); // Create a new ExportedQuestion object
		
		fileChooser.setSelectedFile(new File(q.getTitle()));
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Exported Questions", "question");
		
		fileChooser.setFileFilter(filter);
		
		int result = fileChooser.showSaveDialog(this);
		
		if (result == JFileChooser.CANCEL_OPTION)
		{
			System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Exporting question cancelled");
			return;
		}
		
		String filePath = fileChooser.getSelectedFile().getAbsolutePath();
		
		if (!filePath.endsWith(".question")) // If it doesn't end with question
		{
			filePath += ".question"; // Add the question file type
		}
		
		try
		{
			FileWriter fw = new FileWriter(filePath);
			
			fw.write(questionToExport.toString()); // Serialize the question
			
			fw.close();
		}
		catch (IOException e)
		{
			System.out.println("[ERROR] <IMPORT_EXPORT_PANEL> Error exporting question to file " + e); // Output the error
		}
		
		System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Question exported!");
		JOptionPane.showMessageDialog(null, "Question exported!");
	}
	
	private void exportForm(String formID)
	{
		System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Running exportForm");
		Form form = forms.getFormByID(formID);
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Exported Forms", "form");
		
		fileChooser.setFileFilter(filter);
		
		// Get the user to select a place to save the form
		fileChooser.setSelectedFile(new File(form.getTitle()));
		int result = fileChooser.showSaveDialog(this);
		
		if (result == JFileChooser.CANCEL_OPTION)
		{
			System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Exporting form cancelled");
			return;
		}
		
		
		String[] questionIDs = form.getQuestionIDs();
		
		ExportedQuestion[] exportedQuestions = new ExportedQuestion[questionIDs.length]; // Create an array to store the exported question objects
		int nextExportedQuestionsLocation  = 0;
		
		for (int i = 0; i < questionIDs.length; i++) // For each question in the form
		{
			String questionID = questionIDs[i];
			
			if (questions.getQuestionByID(questionID) != null) // Only export if it's a question not a header
			{
				Question q = questions.getQuestionByID(questionID); // Get the question
				QuestionPanel qP = questions.getPanelByID(questionID); // Get the question panel
			
				ExportedQuestion questionToExport = new ExportedQuestion(q, qP); // Create a new ExportedQuestion object
				
				exportedQuestions[nextExportedQuestionsLocation] = questionToExport; // Add the question to export to the array
				nextExportedQuestionsLocation++;
			}
		}
		
		// Now we need to trim the array so that there aren't any null elements
		ExportedQuestion[] trimmedExportedQuestions = new ExportedQuestion[nextExportedQuestionsLocation];
		
		for (int i = 0; i < trimmedExportedQuestions.length; i++)
		{
			trimmedExportedQuestions[i] = exportedQuestions[i];
		}
		
		ExportedForm formToExport = new ExportedForm(form, trimmedExportedQuestions); // Add the questions and the form to an exported form object
		
		String filePath = fileChooser.getSelectedFile().getAbsolutePath();
		
		if (!filePath.endsWith(".form")) // If it doesn't end with form
		{
			filePath += ".form"; // Add the form file type
		}
		
		try
		{
			
			FileWriter fw = new FileWriter(filePath);
			
			fw.write(formToExport.toString()); // Write the form to file
			
			fw.close();
		}
		catch (IOException e)
		{
			System.out.println("[ERROR] <IMPORT_EXPORT_PANEL> Error exporting form to file " + e); // Output the error
		}
		
		System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Form exported!");
		JOptionPane.showMessageDialog(null, "Form exported!");
	}
}