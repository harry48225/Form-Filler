package com.harry.formfiller.user;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FormsInProgressList
{
	/* Stores and manipulates all of the formsInProgress objects */
	
	private String databaseFileName = "FormsInProgessDB.txt";
	private String databasePath;
	private FormInProgress[] formsInProgressArray = new FormInProgress[100];
	
	private String mostRecentForm; // Holds the ID of the most recently attempted form
	
	private int nextFormInProgressLocation = 0;
	
	public FormsInProgressList(String username)
	{
		/* Loads the forms in progress list for a given username */
		
		databasePath = "formsInProgress/" + username; // Create the path
		loadDatabase();
	}
	
	public FormInProgress[] getArray()
	{
		/* Returns the array */
		return formsInProgressArray;
	}
	
	public boolean isFormPresent(String searchFormID)
	{
		/* Checks to see if a form is in the array */
		
		return getByID(searchFormID) != null; // Returns true if when getting the form it doesn't return null i.e. the form is present
	}
	
	public FormInProgress getByID(String searchID)
	{
		/* Returns a FormInProgress object by id. Performs a linear search */
		
		FormInProgress result = null;
		
		for (int i = 0; i < nextFormInProgressLocation; i++) // For each formInProgress in the array
		{
			FormInProgress form = formsInProgressArray[i];
			
			if (form.getFormID().equals(searchID)) // If the correct form has been found
			{
				result = form;
				break;
			}
		}
		
		return result;
		
	}
	
	public void setMostRecentAttempted(String formID)
	{
		/* Sets which form is the most recently attempted form */
		
		mostRecentForm = formID;
	}
	
	public String getMostRecentFormID()
	{
		/* Gets the id of the form that the user most recently attempted */
		
		return mostRecentForm;
	}
	
	public void addFormInProgress(FormInProgress formInProgressToAdd)
	{
		/* Adds a form in progress object to the list */
		
		formsInProgressArray[nextFormInProgressLocation] = formInProgressToAdd; // Add the form at the next free location
		
		nextFormInProgressLocation++; 
	}
	
	public void writeDatabase()
	{
		/* Writes the FormInProgress objects to the user's FormsInProgress database */
		
		System.out.println("[INFO] <FORMS_IN_PROGRESS_LIST> Running writeDatabase");
		
		// It can't certain that the directories exist
		File directory = new File(databasePath);
		
		if (!directory.exists()) // If the directory doesn't exist
		{
			directory.mkdirs(); // Make any required directories
		}
		
		try (FileWriter fw = new FileWriter(databasePath + "/" + databaseFileName)) // Open a filter writer
		{
			
			
			// Write the most recent form id to the top of the file
			fw.write(mostRecentForm + "\r\n");
			
			
			// Write each form to the file
			for (int i = 0; i < nextFormInProgressLocation; i++) // For each form in the array
			{
				String currentPositionFormData = formsInProgressArray[i].toString(); // Get the attribute string
				
				fw.write(currentPositionFormData + "\r\n"); // Write the data

			}
			
			fw.close(); // Close the file
			
		}
		catch (IOException e)
		{
			System.out.println("[ERROR] <FORMS_IN_PROGRESS_LIST> Writing database " + e); // Print the error message
			e.printStackTrace();
		}
	}
	
	private void loadDatabase()
	{
		/* Loads a user's form in progress database from file */
		
		System.out.println("[INFO] <FORMS_IN_PROGRESS_LIST> Running loadDatabase");
		
		nextFormInProgressLocation = 0;
		
		try(BufferedReader br = new BufferedReader(new FileReader(databasePath + "/" + databaseFileName))) // Open the database file name
		{

			String line = br.readLine(); // Read the line from the database
			
			// This first line is the id of the most recent form that the user has attempted
			mostRecentForm = line;
			
			line = br.readLine();
			
			// Load each form from each line of the database
			while (line != null) // While there is still data to load from the file
			{
				
				addFormInProgress(new FormInProgress(line));
				
				line = br.readLine(); // Read the next line
			}
			
			br.close();
		}
		catch(Exception e)
		{
			System.out.println("[ERROR] <FORMS_IN_PROGRESS_LIST> Error loading database most likely user hasn't attempted a form yet" + e); // Print the error message
		}
		
		System.out.println("[INFO] <FORMS_IN_PROGRESS_LIST> " + nextFormInProgressLocation + " formsInProgress loaded from file");
	}
}