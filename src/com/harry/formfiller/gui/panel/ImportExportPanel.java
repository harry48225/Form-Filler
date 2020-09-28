package com.harry.formfiller.gui.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.harry.formfiller.form.ExportedForm;
import com.harry.formfiller.form.Form;
import com.harry.formfiller.form.FormList;
import com.harry.formfiller.gui.question.QuestionPanel;
import com.harry.formfiller.question.ExportedQuestion;
import com.harry.formfiller.question.Question;
import com.harry.formfiller.question.QuestionList;

public class ImportExportPanel extends JPanel implements ActionListener, Helper
{
	/* This class displays a panel that allows the user to 
	import and export questions and forms form the system. */

	private final String HELP_STRING = "This is the import export panel. You can use this panel to import and export questions or forms. To export, a question/form select it in the table and press export. To import, press import and select the question/form using the file browser.";
	
	private transient QuestionList questions;
	private transient FormList forms;
	
	// Allows the user to select files
	private JFileChooser fileChooser = new JFileChooser();
	
	private JButton importQuestionButton = new JButton("Import question");
	private JButton exportQuestionButton = new JButton("Export");
	
	private JButton importFormButton = new JButton("Import form");
	private JButton exportFormButton = new JButton("Export");
	
	private JPanel importExportQuestionsPanel = new JPanel();
	private JPanel importExportFormPanel = new JPanel();
	
	private SelectQuestionsPanel questionSelectionPanel;
	private SelectFormsPanel formSelectionPanel;
	
	private transient Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED); // Border style
	
	public ImportExportPanel(QuestionList tempQuestions, FormList tempForms)
	{
		questions = tempQuestions;
		forms = tempForms;
		
		prepareGUI();
	}
	
	public String getHelpString()
	{
		/* Returns the help string */
		return HELP_STRING;
	}
	
	public void refreshTables()
	{
		/* Refreshes the form and question tables */
		
		questionSelectionPanel.refreshTable();
		formSelectionPanel.refreshTable();
	}
	
	private void prepareGUI()
	{
		/* Prepares the main panel and adds the question and form selection panels to it */
		
		this.setLayout(new GridLayout(0,2)); // 2 columns
		
		prepareImportExportQuestionsPanel();
		this.add(importExportQuestionsPanel);
		
		prepareImportExportFormPanel();
		this.add(importExportFormPanel);
	}
	
	private void prepareImportExportFormPanel()
	{
		/* Prepares the import and export forms panel */
		
		importExportFormPanel.setLayout(new BoxLayout(importExportFormPanel, BoxLayout.PAGE_AXIS));
		
		// Create the forms border
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Forms");
		Font currentFont = border.getTitleFont();
		border.setTitleFont(currentFont.deriveFont(Font.BOLD, 16)); // Make the font larger and bold
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		importExportFormPanel.setBorder(border); // Set the border
		
		// Add a 10px vertical space
		importExportFormPanel.add(Box.createRigidArea(new Dimension(0,10)));
		
		// Add the import button
		importFormButton.addActionListener(this);
		importFormButton.setBackground(new Color(130,183,75)); // Green
		importFormButton.setForeground(Color.WHITE); // Make the text white
		importFormButton.setMaximumSize(new Dimension(100000, 40)); // Very large width so that it fills all horizontal space
		importFormButton.setMinimumSize(new Dimension(100000, 20));
		importExportFormPanel.add(importFormButton);
		
		// Add a 10px vertical space
		importExportFormPanel.add(Box.createRigidArea(new Dimension(0,10)));
		
		// Add the selection panel
		formSelectionPanel = new SelectFormsPanel(forms,questions);
		importExportFormPanel.add(formSelectionPanel);
		
		// Add a 10px vertical space
		importExportFormPanel.add(Box.createRigidArea(new Dimension(0,10)));
		
		// Add the export button
		exportFormButton.addActionListener(this);
		exportFormButton.setBackground(new Color(169,196,235)); // Blue
		exportFormButton.setMaximumSize(new Dimension(100000, 40)); // Very large width so that it fills all horizontal space
		exportFormButton.setMinimumSize(new Dimension(100000, 20));
		importExportFormPanel.add(exportFormButton);
	}
	
	private void prepareImportExportQuestionsPanel()
	{
		/* Prepares the panel that allows the user to import and export questions */
		
		importExportQuestionsPanel.setLayout(new BoxLayout(importExportQuestionsPanel, BoxLayout.PAGE_AXIS));
		
		// Create the question border
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Questions");
		Font currentFont = border.getTitleFont();
		border.setTitleFont(currentFont.deriveFont(Font.BOLD, 16)); // Make the font larger and bold
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		importExportQuestionsPanel.setBorder(border); // Set the border
		
		
		importExportQuestionsPanel.add(Box.createRigidArea(new Dimension(0,10)));
		
		// Add the import button
		importQuestionButton.addActionListener(this);
		importQuestionButton.setBackground(new Color(130,183,75)); // Green
		importQuestionButton.setForeground(Color.WHITE); // Make the text colour white
		importQuestionButton.setMaximumSize(new Dimension(100000, 40)); // Very large width so that it fills all horizontal space
		importQuestionButton.setMinimumSize(new Dimension(100000, 20));
		importExportQuestionsPanel.add(importQuestionButton);
		
		// Create 10px of vertical space
		importExportQuestionsPanel.add(Box.createRigidArea(new Dimension(0,10)));
		
		// Add the selection panel
		questionSelectionPanel = new SelectQuestionsPanel(questions);
		importExportQuestionsPanel.add(questionSelectionPanel);
		
		// Create 10px of vertical space
		importExportQuestionsPanel.add(Box.createRigidArea(new Dimension(0,10)));
		
		// Add the export button
		exportQuestionButton.addActionListener(this);
		exportQuestionButton.setBackground(new Color(169,196,235)); // Blue
		exportQuestionButton.setMaximumSize(new Dimension(100000, 40)); // Very large width so that it fills all horizontal space
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
			// Gets the id of the question that the user selected and allows them to export it
			System.out.println("[INFO] <IMPORT_EXPORT_PANEL> exportQuestionButton pressed"); // Debug
			String selectedQuestionID = questionSelectionPanel.getSelectedQuestionID();
			if (selectedQuestionID != null)
			{
				exportQuestion(selectedQuestionID);
			}
		}
		else if (evt.getSource() == exportFormButton)
		{
			// Gets the id of the form that the user selected and allows them to export it
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
		/* Opens a file chooser and allows the user to select a question to 
			import and then imports it */
		System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Running importQuestion");
		
		// Create a new filter to only allow question files to be selected
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Exported Questions", "question");
		
		fileChooser.setFileFilter(filter);
		
		// Open the file chooser
		int result = fileChooser.showOpenDialog(this);
		
		// If they pressed the cancel option cancel the import process
		if (result == JFileChooser.CANCEL_OPTION)
		{
			System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Importing question cancelled");
			return;
		}
		
		ExportedQuestion questionImported = null;
		String filePath = fileChooser.getSelectedFile().getAbsolutePath();
		
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) // Open the database file
		{	
			questionImported = new ExportedQuestion(br.readLine()); // Read the ExportedQuestion from the file
			
			br.close();
		}
		catch(Exception e)
		{
			System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Error importing question " + e); // Output the error
		}
		
		// Add the question to the database overwriting the old one (if it exists)
		
		if (questionImported != null)
		{
			String questionID = questionImported.getQuestion().getID();
			
			if (questions.getQuestionByID(questionID) != null)
			{
				questions.removeQuestion(questionID);
			}
			
			questions.addQuestion(questionImported.getQuestion(), questionImported.getQuestionPanel()); // Add the imported question to the database
			
			questions.writeDatabase();
			
			System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Question imported!");
			JOptionPane.showMessageDialog(this, "Question imported!");
			
			refreshTables();
		}
	}
	
	private void importForm()
	{
		/* Opens a file chooser and allows the user to select a form to 
			import and then imports it */
			
		System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Running importForm");
		
		// Create a filter that only allows form files.
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Exported Forms", "form");
		
		fileChooser.setFileFilter(filter);
		
		// Open the filter chooser
		int result = fileChooser.showOpenDialog(this);
		
		// If they pressed cancel, cancel the process
		if (result == JFileChooser.CANCEL_OPTION)
		{
			System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Importing form cancelled");
			return;
		}
		
		ExportedForm formImported = null;
		
		// Read the data from the file and import the form
		String filePath = fileChooser.getSelectedFile().getAbsolutePath();
						
		
		try(BufferedReader br = new BufferedReader(new FileReader(filePath))) // Open the database file
		{
			formImported = new ExportedForm(br.readLine()); // Read the ExportedForm from the file
			
			br.close();
		}
		catch(Exception e)
		{
			System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Error importing form " + e); // Output the error
		}

		if (formImported != null)
		{
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
			JOptionPane.showMessageDialog(this, "Form imported!");
			
			refreshTables();
		}
	}
	
	private void exportQuestion(String questionID)
	{
		/* Takes a question ID and exports the question that corresponds with it */
		System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Running exportQuestion");
		
		Question q = questions.getQuestionByID(questionID); // Get the question
		QuestionPanel qP = questions.getPanelByID(questionID); // Get the question panel
		
		ExportedQuestion questionToExport = new ExportedQuestion(q, qP); // Create a new ExportedQuestion object
		
		// Create a new file with the correct name and make it automatically selected.
		// This fills in the file name in the save dialog
		fileChooser.setSelectedFile(new File(q.getTitle()));
		
		
		// Only allow the user to save files with the extension form
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Exported Questions", "question");
		
		fileChooser.setFileFilter(filter);
		
		// Get the user to choose a place to save
		int result = fileChooser.showSaveDialog(this);
		
		// Cancel exporting the question if the user pressed cancel
		if (result == JFileChooser.CANCEL_OPTION)
		{
			System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Exporting question cancelled");
			return;
		}
		
		// Get the file path that they selected
		String filePath = fileChooser.getSelectedFile().getAbsolutePath();
		
		if (!filePath.endsWith(".question")) // If it doesn't end with question
		{
			filePath += ".question"; // Add the question file type
		}
		
		// Export the question
		try (FileWriter fw = new FileWriter(filePath))
		{
			
			fw.write(questionToExport.toString()); // Serialize the question
			
			fw.close();
		}
		catch (IOException e)
		{
			System.out.println("[ERROR] <IMPORT_EXPORT_PANEL> Error exporting question to file " + e); // Output the error
		}
		
		System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Question exported!");
		// Tell the user that the question has been exported
		JOptionPane.showMessageDialog(this, "Question exported!");
	}
	
	private void exportForm(String formID)
	{
		/* Takes a form ID and exports the form that corresponds with it */
		System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Running exportForm");
		Form form = forms.getFormByID(formID);
		
		// Only allow the user to save with the extension form
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Exported Forms", "form");
		
		fileChooser.setFileFilter(filter);
		
		// Create a new file with the correct name and make it automatically selected.
		// This fills in the file name in the save dialog
		fileChooser.setSelectedFile(new File(form.getTitle()));
		// Get the user to choose a place to save
		int result = fileChooser.showSaveDialog(this);
		
		// Cancel exporting the form if the user pressed cancel
		if (result == JFileChooser.CANCEL_OPTION)
		{
			System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Exporting form cancelled");
			return;
		}
		
		
		String[] questionIDs = form.getQuestionIDs();
		
		ExportedQuestion[] exportedQuestions = new ExportedQuestion[questionIDs.length]; // Create an array to store the exported question objects
		int nextExportedQuestionsLocation  = 0;
		
		// Fill the exportedQuestions array with ExportedQuestion objects
		// corresponding to each question in the form.
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
		
		try (FileWriter fw = new FileWriter(filePath)) // Write the exported form object to file
		{
			
			fw.write(formToExport.toString()); // Write the form to file
			
			fw.close();
		}
		catch (IOException e)
		{
			System.out.println("[ERROR] <IMPORT_EXPORT_PANEL> Error exporting form to file " + e); // Output the error
		}
		
		System.out.println("[INFO] <IMPORT_EXPORT_PANEL> Form exported!");
		// Tell the user that the form was successfully exported
		JOptionPane.showMessageDialog(this, "Form exported!");
	}
}