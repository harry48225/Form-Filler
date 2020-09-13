import java.io.*;

public class ExportedForm 
{
	/* This class stores and form and all of the questions that are 
		in it so that it can be exported as a single package */
			
	private ExportedQuestion[] questions; // All of the questions in the form
	
	private Form form; // The actual form
	
	public ExportedForm(Form tempForm, ExportedQuestion[] tempQuestions)
	{
		form = tempForm;
		questions = tempQuestions;
	}
	
	public ExportedForm(String saveString)
	{
		/* Load an exported form from the saveString */
		
		// Save string looks like
		// <form>%%%<question>===<question>===etc.
		
		String[] splitSaveString = saveString.split("%%%");
			
		form = new Form(splitSaveString[0]); // Load the form from file
			
		// Split the questions
		String[] exportedQuestions = splitSaveString[1].split("===");
		
		questions = new ExportedQuestion[exportedQuestions.length];
		
		// Load each exported question object from file
		for (int i = 0; i < questions.length; i++)
		{
			questions[i] = new ExportedQuestion(exportedQuestions[i]);
		}
		
		
	}
		
	public ExportedQuestion[] getQuestions()
	{
		/* Returns the questions that were exported with the form,
			as ExportedQuestion objects*/
		
		return questions;
	}
	
	public Form getForm()
	{
		/* Returns the form that was exported */
		return form;
	}
	
	private String formToString()
	{
		/* Returns a string that fully describes the form and can be used to recreate it */
		return form.toString();
	}
	
	public String toString()
	{
		/* Returns a string that fully describes the exported form along with all of the questions in it,
			that can be used to recreate the form and the questions */

		String outputString = formToString() + "%%%"; // %%% delimits between the form in questions
		
		// Append each question to the string with === to delimit them
		for (ExportedQuestion eQ : questions)
		{
			outputString += (eQ.toString() + "==="); // === delimits between each question
		}
		
		outputString = outputString.substring(0, outputString.length() - 3); // Trim off the trailing +++
		
		return outputString;
	}
		
}