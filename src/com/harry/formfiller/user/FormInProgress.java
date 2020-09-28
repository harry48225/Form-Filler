package com.harry.formfiller.user;

import javax.swing.JPanel;

import com.harry.formfiller.gui.question.QuestionPanel;
import com.harry.formfiller.gui.question.component.HeaderPanel;
import com.harry.formfiller.gui.question.component.JSaveableComponent;

public class FormInProgress
{
	/* Stores a copy of the question panels in a form for a user's current attempt of the form.
		Allow's their entries into the form to be saved between system restarts */

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
		/* Loads a FormInProgress form its saveString */

		// The save string is a list of all of the question
		// panels in the form sepatrated by commas.
		// The list comprises of QuestionPanels and HeaderPanels
		// The save string also has the form id, the percentageComplete,
		// and the times Completed
		
		// It look like this
		// formID;percentComplete;timesCompleted~QuestionPanel,QuestionPanel,HeaderPanel
		
		// Extract all of the information
		String[] splitSaveString = saveString.split("~");
		
		String[] formInProgressData = splitSaveString[0].split(";");

		String[] componentStrings = splitSaveString[1].split(",");
		
		// Store the relevant data
		formID = formInProgressData[0];
		percentComplete = Integer.parseInt(formInProgressData[1]);
		timesCompleted = Integer.parseInt(formInProgressData[2]);
		

		// Load the questionPanels from file
		questionPanels = new JPanel[componentStrings.length];
		
		// Iterate over the saved question panels and load each one, or load the header if it's a header
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

	}

	public String toString()
	{
		/* Returns a string that fully describes the FormInProgress and can be used to recreate it */

		System.out.println("[INFO] <FORM_IN_PROGRESS> Running toString"); // Debug

		String outputString = formID + ";" + percentComplete + ";" + timesCompleted + "~"; // Produce the FormInProgress output string
		
		// Append all of the QuestionPanels and Headers to the output string
		for (JPanel panel : questionPanels)
		{
			JSaveableComponent c = (JSaveableComponent) panel;
			outputString += (c.toString() + ",");
		}
		
		outputString = outputString.substring(0, outputString.length() - 1); // Trim off the trailing ,
		
		return outputString;
	}
	
	/* Get and setter methods follow */

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
	
	
}