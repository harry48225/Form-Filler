import javax.swing.*;
import java.awt.*;
import java.io.*;

public class FormInProgress implements Serializable // This object get serialized
{
	private String formID;
	
	private int percentComplete;
	
	private QuestionPanel[] questionPanels;
	
	public FormInProgress(String tempFormID, int tempPercentComplete, QuestionPanel[] tempQuestionPanels)
	{
		formID = tempFormID;
		 percentComplete = tempPercentComplete;
		 questionPanels = tempQuestionPanels;
	}
	
	public void setPercentComplete(int newPercentComplete)
	{
		percentComplete = newPercentComplete;
	}
	
	public int getPercentComplete()
	{
		return percentComplete;
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