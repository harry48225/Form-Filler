import javax.swing.*;
import java.awt.*;
import java.io.*;

public class FormInProgress implements Serializable // This object get serialized
{
	private String formID;
	
	private int percentComplete;
	private int timesCompleted;
	
	private QuestionPanel[] questionPanels;
	
	public FormInProgress(String tempFormID, int tempPercentComplete, QuestionPanel[] tempQuestionPanels, int tempTimesCompleted)
	{
		formID = tempFormID;
		percentComplete = tempPercentComplete;
		questionPanels = tempQuestionPanels;
		timesCompleted = tempTimesCompleted;
	}
	
	public void setPercentComplete(int newPercentComplete)
	{
		percentComplete = newPercentComplete;
	}
	
	public void addTimesCompleted()
	{
		timesCompleted++;
	}
	
	public int getPercentComplete()
	{
		return percentComplete;
	}
	
	public int getTimesCompleted()
	{
		return timesCompleted;
	}
	
	public QuestionPanel[] getQuestionPanels()
	{
		return questionPanels;
	}
	
	public String getFormID()
	{
		return formID;
	}
	
}