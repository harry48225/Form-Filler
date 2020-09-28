package com.harry.formfiller.gui.question;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class QuestionPanelList
{
	/* Handles, manipulates, and stores all of the Question Panels in the system */
	
	private String databaseFileName = "questions/QuestionPanelDB.txt"; 
	
	public QuestionPanel[] panels = new QuestionPanel[100]; // To store the question panels
	
	private int nextQuestionPanelLocation = 0; // The location to store the next question panel
	
	public QuestionPanelList()
	{
		loadDatabase(); // Load the database
	}
	
	public void removeQuestionPanel(String questionID)
	{
		/* Removes a question panel from the database by questionID */
		
		System.out.println("[INFO] <QUESTION_PANEL_LIST> Running removeQuestionPanel");
		
		QuestionPanel[] newArray = new QuestionPanel[panels.length]; // Create a new array of the required size
		
		int j = 0; // The location in newArray
		
		// Copy all of the question panels apart from the one to delete to the new array
		for (int i = 0; i < nextQuestionPanelLocation; i++)
		{
			if (!panels[i].getQuestionID().equals(questionID)) // If the panel isn't the one to delete
			{
				newArray[j] = panels[i]; // Copy over the question panel
				j++;
			}
		}
		
		panels = newArray; // Overwrite the old array
		nextQuestionPanelLocation--; // There is one less questionPanel in the array so a free spot has opened
		
		writeDatabase(); // Save changes
	}
	
	public void writeDatabase()
	{
		/* Writes the question panel database to file */
		
		System.out.println("[INFO] <QUESTION_PANEL_LIST> Running writeDatabase");
	
		// Try to write the database
		try(FileWriter fw = new FileWriter(databaseFileName))
		{
			
			for (int i = 0; i < nextQuestionPanelLocation; i++) // For each Question in the array
			{
				String currentPositionQuestionData = panels[i].toString(); // Get the attribute string
				
				fw.write(currentPositionQuestionData); // Write the data
				
				fw.write("\r\n"); // Go to a new line
			}
			
			fw.close(); // Close the file
		}
		catch (IOException e)
		{
			System.out.println("[ERROR] <QUESTION_PANEL_LIST> Writing database " + e); // Print the error message
		}
	}
	
	public void loadDatabase()
	{
		/* Loads the question panels that are saved to file */
		
		System.out.println("[INFO] <QUESTION_PANEL_LIST> Running loadDatabase");
		
		nextQuestionPanelLocation = 0; // Start at the beginning of the array
		
		// Try to load the question panels from file
		try(BufferedReader br = new BufferedReader(new FileReader(databaseFileName))) // Open the database file
		{
			String line = br.readLine(); // Read line from the file
			
			while (line != null) // For each line with a question panel in it
			{
				addQuestionPanel(new QuestionPanel(line)); // Load the question panel
				
				line = br.readLine(); // Read the next line
			}
			
			br.close();
		}
		catch(Exception e)
		{
			System.out.println("[ERROR] <QUESTION_PANEL_LIST> Error loading database " + e); // Print the error message
			e.printStackTrace();
		}
		
		System.out.println("[INFO] <QUESTION_PANEL_LIST> " + nextQuestionPanelLocation + " QuestionPanels loaded from file");
	}
	
	public void addQuestionPanel(QuestionPanel tempQuestionPanel)
	{
		/* Adds a question panel to the list */
		
		System.out.println("[INFO] <QUESTION_PANEL_LIST> Running addQuestionPanel"); // Debug		
		
		// Search for the first panel panel with an id greater than it and insert it before that id
		// This ensures that the array remains in order - ascending
		
		boolean inserted = false;
		int questionIDNumber = tempQuestionPanel.getQuestionIDNumber(); // Get the id number of the question
		
		// Iterate over each question panel to find the first question panel with an id number greater than the current one
		for (int i = 0; i < nextQuestionPanelLocation; i++)
		{
			int questionIDNumberInArray = panels[i].getQuestionIDNumber();
			
			// If the id number of the panel in the array is greater than the one that we are adding
			if (questionIDNumberInArray > questionIDNumber)
			{
				nextQuestionPanelLocation++; // Increment the location
				
				// Insert the question before it in the array and shuffle all elements over
				QuestionPanel previous = panels[i];
				panels[i] = tempQuestionPanel;
				
				// Shuffle the question panels over
				for (int j = i+1; j < nextQuestionPanelLocation; j++)
				{
					QuestionPanel temp = panels[j];
					panels[j] = previous;
					previous = temp;
				}
				
				inserted = true;
				
				break;
			}	
		}
		
		// If there was no question panel with an id greater than it, add the new panel to the end
		if (!inserted)
		{
			panels[nextQuestionPanelLocation] = tempQuestionPanel;
			
			nextQuestionPanelLocation++;
		}
		
	}
	
	public QuestionPanel getByID(String questionID)
	{
		/* Performs a binary search to retrive a question panel by ID */
		
		System.out.println("[INFO] <QUESTION_PANEL_LIST> Running getByID");
		
		// Get the id number
		int questionIDNumber = Integer.parseInt(questionID.replace("Q",""));
		
		// Perform a binary search
		QuestionPanel result = binarySearch(questionIDNumber, 0, nextQuestionPanelLocation);
		
		// If a question panel was found return a deep copy of it, otherwise return null
		return result != null ? result.deepCopy() : null;
	}
	
	private QuestionPanel binarySearch(int questionIDNumber, int low, int high)
	{
		/* Performs a binary search to find a question panel */
		
		// Get the middle index
		int middle = (low + high)/2;
		
		// If we've passed the lowest or highest index the panel hasn't been found so return null
		if (high < low)
		{
			return null;
		}
		
		// Get the id of the middle question
		int middleQuestionIDNumber = panels[middle].getQuestionIDNumber();
		
		// If we've found the panel, return it
		if (middleQuestionIDNumber == questionIDNumber)
		{
			return panels[middle];
		}
		// If our id is greater than the middle, binary search with the upper half of the array
		else if (questionIDNumber > middleQuestionIDNumber)
		{
			return binarySearch(questionIDNumber, middle + 1, high);
		}
		// Otherwise binary search with the lower half of the array
		else
		{
			return binarySearch(questionIDNumber, low, middle -1);
		}
		
	}
}