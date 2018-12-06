import components.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class FormInProgress implements Serializable // This object get serialized
{
	private String formID;
	
	private int percentComplete;
	private int timesCompleted;
	
	private JPanel[] questionPanels; // Also contains the JPanels which contain the headers
	
	public FormInProgress(String tempFormID, int tempPercentComplete, JPanel[] tempQuestionPanels, int tempTimesCompleted)
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
	
	public JPanel[] getQuestionPanels()
	{
		return questionPanels;
	}
	
	public String getFormID()
	{
		return formID;
	}
	
	public String toString()
	{
		String outputString = "";
		
		for (JPanel panel : questionPanels)
		{
			JSaveableComponent c = (JSaveableComponent) panel;
			outputString += (c.toString() + ",");
		}
		
		outputString = outputString.substring(0, outputString.length() - 1); // Trim off the trailing ,
		
		return outputString;
	}
	
}