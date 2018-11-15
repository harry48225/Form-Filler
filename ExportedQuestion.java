import java.io.*;

public class ExportedQuestion implements Serializable
{
	private Question question; // The question and question panel
	private QuestionPanel questionPanel;
	
	public ExportedQuestion(Question tempQuestion, QuestionPanel tempQuestionPanel)
	{
		question = tempQuestion;
		questionPanel = tempQuestionPanel;
	}
	
	public Question getQuestion()
	{
		return question;
	}
	
	public QuestionPanel getQuestionPanel()
	{
		return questionPanel;
	}
}