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
	
	public FormInProgress(String saveString)
	{
		// The save string is a list of all of the question
		// panels in the form sepatrated by commas.
		// The list comprises of QuestionPanels and HeaderPanels
		// The save string also has the form id, the percentageComplete,
		// and the times Completed
		
		// It look like this
		// formID;percentComplete;timesCompleted~QuestionPanel,QuestionPanel,HeaderPanel
		
		String[] splitSaveString = saveString.split("~");
		
		String[] formInProgressData = splitSaveString[0].split(";");
		System.out.println(splitSaveString[1]);
		String[] componentStrings = splitSaveString[1].split(",");
		
		formID = formInProgressData[0];
		percentComplete = Integer.parseInt(formInProgressData[1]);
		timesCompleted = Integer.parseInt(formInProgressData[2]);
		
		questionPanels = new JPanel[componentStrings.length];
		
		for (int i = 0; i < componentStrings.length; i++)
		{
			// If it contains a $ it's a question panel
			if (componentStrings[i].contains("$"))
			{
				questionPanels[i] = new QuestionPanel(componentStrings[i]);
			}
			else
			{
				questionPanels[i] = new HeaderPanel(componentStrings[i]);
			}
		}
		
		System.out.println(questionPanels.length);
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
	
	public void setFormComponents(JPanel[] newComponents) // Allows the question panel array to be overwritten with blank questions
	{
		questionPanels = newComponents;
	}
	
	public String getFormID()
	{
		return formID;
	}
	
	public String toString()
	{
		System.out.println("[INFO] <FORM_IN_PROGRESS> Running toString"); // Debug
		String outputString = formID + ";" + percentComplete + ";" + timesCompleted + "~";
		
		for (JPanel panel : questionPanels)
		{
			JSaveableComponent c = (JSaveableComponent) panel;
			outputString += (c.toString() + ",");
		}
		
		outputString = outputString.substring(0, outputString.length() - 1); // Trim off the trailing ,
		
		System.out.println("OS: " + outputString);
		
		return outputString;
	}
	
}