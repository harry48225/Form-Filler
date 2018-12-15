import java.io.*;

public class ExportedQuestion
{
	private Question question; // The question and question panel
	private QuestionPanel questionPanel;
	
	public ExportedQuestion(Question tempQuestion, QuestionPanel tempQuestionPanel)
	{
		question = tempQuestion;
		questionPanel = tempQuestionPanel;
	}
	
	public ExportedQuestion(String saveString)
	{
		String[] splitSaveString = saveString.split("###");
		String questionString = splitSaveString[0];
		String questionPanelString = splitSaveString[1];
		
		question = new Question(questionString);
		questionPanel = new QuestionPanel(questionPanelString);
	}
	
	public Question getQuestion()
	{
		return question;
	}
	
	public QuestionPanel getQuestionPanel()
	{
		return questionPanel;
	}
	
	public String toString()
	{
		return question.toString() + "###" + questionPanel.toString();
	}
	
}