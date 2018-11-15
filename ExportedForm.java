import java.io.*;

public class ExportedForm implements Serializable
{
	private ExportedQuestion[] questions; // All of the questions in the form
	
	private Form form; // The actual form
	
	public ExportedForm(Form tempForm, ExportedQuestion[] tempQuestions)
	{
		form = tempForm;
		questions = tempQuestions;
	}
		
	public ExportedQuestion[] getQuestions()
	{
		return questions;
	}
	
	public Form getForm()
	{
		return form;
	}
		
}