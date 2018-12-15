import java.io.*;

public class ExportedForm 
{

	private ExportedQuestion[] questions; // All of the questions in the form
	
	private Form form; // The actual form
	
	public ExportedForm(Form tempForm, ExportedQuestion[] tempQuestions)
	{
		form = tempForm;
		questions = tempQuestions;
	}
	
	public ExportedForm(String saveString)
	{
		String[] splitSaveString = saveString.split("%%%");
			
		form = new Form(splitSaveString[0]); // Load the form from file
		
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
		return questions;
	}
	
	public Form getForm()
	{
		return form;
	}
	
	private String formToString()
	{
		return form.toString();
	}
	
	public String toString()
	{
		String outputString = formToString() + "%%%"; // %%% delimits between the form in questions
		
		for (ExportedQuestion eQ : questions)
		{
			outputString += (eQ.toString() + "==="); // === delimits between each question
		}
		
		outputString = outputString.substring(0, outputString.length() - 3); // Trim off the trailing +++
		
		return outputString;
	}
		
}