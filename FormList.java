import java.io.*;
import java.util.*;

public class FormList
{
	private String databaseFileName = "forms/FormDB.txt"; // The filename of the forms database
	
	private Form[] formArray = new Form[100]; // Store 100 Forms
	
	private int nextFormLocation = 0; // The location to store the next form
	
	public FormList()
	{
		loadDatabase(); // Load the database from file
	}
	
	public Form getFormByID(String id) // Returns the form corresponding to an ID
	{
		System.out.println("[INFO] <FORM_LIST> Running getFormByID"); // Debug
		
		Form result = null; // The form that was found
		
		for (int i = 0; i < nextFormLocation; i++) // Linear search
		{
			if (formArray[i].getID().equals(id)) // If we've found the form
			{
				result = formArray[i];
				
				break; // Stop searching
			}
		}
		
		return result;
		
	}
	
	public void loadDatabase() // Loads the forms that are saved in the database
	{
		System.out.println("[INFO] <FORM_LIST> Running loadDatabase"); // Debug
		
		nextFormLocation = 0; // Start at the beginning of the array
		
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(databaseFileName)); // Open the database file name
			
			String line = br.readLine(); // Read the line from the database
			
			while (line != null) // While there is still data to load from the file
			{
				addForm(new Form(line)); // Load the form and add it to the array
				
				line = br.readLine(); // Read the next line
			}
		}
		catch  (Exception e)
		{
			System.out.println("[ERROR] <FORM_LIST> Error loading database" + e);
		}
		
		System.out.println("[INFO] <FORM_LIST> " + nextFormLocation + " Forms loaded from file");
	}
	

	
	public void sortByDifficulty() // Bubble sort to sort by difficulty
	{
		System.out.println("[INFO] <FORM_LIST> Running sortByDifficulty"); // Debug
		
		boolean swapped = true; // Toggle that contains whether a value has been swapped
		
		while (swapped == true) // Until no more swaps are made
		{
			
			swapped = false; // Set swapped to false
			
			for (int i = 0; i < nextFormLocation-1; i++) // For all forms but the last one
			{
				// Ascending sort, lowest - highest
				if (formArray[i].getDifficulty() > formArray[i+1].getDifficulty()) // Check to see if it's greater than the one after
				{
					Form temp = formArray[i+1]; // Store the value in a temp variable
					formArray[i + 1] = formArray[i]; // Swap the values
					formArray[i] = temp; // Swap
					
					swapped = true; // Set swapped to true as a swap was made
				}
			}
		}
	}

	public Form[] filterByType(String[] searchTypes) // Filters by the types present in the array
	{
		Form[] searchResults = new Form[nextFormLocation]; // Create array large enough to store the matches

		int nextResultLocation = 0;

		for (int i = 0; i < nextFormLocation; i++) // For each form
		{
			String[] formTypes = formArray[i].getTypes(); // Get the types
			
			int matches = 0; // The number of matches between searchTypes and formTypes

			for (String searchType : searchTypes) // For each search type
			{
				for (String formType : formTypes) // For each type of the form
				{
					if (formType.equals(searchType)) // If they are the same
					{
						matches++;
						break; // Move on to the next search type
					}
				}
			}

			if (matches == searchTypes.length) // If there has been a match for every search type i.e. a complete match
			{
				searchResults[nextResultLocation] = formArray[i]; // Store the form in the results array
				nextResultLocation++;
			}
		}

		// Trim the results array

		Form[] searchResultsTrimmed = new Form[nextResultLocation]; // Create a new array of the exact size required

		for (int i = 0; i < nextResultLocation; i++)
		{
			searchResultsTrimmed[i] = searchResults[i]; // Copy over the result
		}

		return searchResultsTrimmed;
	}
	
	public Form[] filterByDifficulty(int difficulty)
	{
		System.out.println("[INFO] <FORM_LIST> Running filterByDifficulty");
		
		// Linear search
		
		Form[] results = new Form[nextFormLocation]; // Store the results of the search
		int nextResultLocation = 0;
		
		for (int i = 0; i < nextFormLocation; i++) // Iterate over the forms
		{
			if (formArray[i].getDifficulty() == difficulty) // If it's of the correct difficulty
			{
				results[nextResultLocation] = formArray[i]; // Copy the form into the results array
				nextResultLocation++;
			}
		}
		
		Form[] trimmedResults = new Form[nextResultLocation]; // Create a new array of the correct size
		
		for (int i = 0; i < nextResultLocation; i++)
		{
			trimmedResults[i] = results[i];
		}
		
		return trimmedResults; // Return the results
	}
	
	public void writeDatabase() // Writes the form data to file
	{
		System.out.println("[INFO] <FORM_LIST> Running writeDatabase");
		
		try
		{
			FileWriter fw = new FileWriter(databaseFileName); // Declare a new file writer
			
			for (int i = 0; i < nextFormLocation; i++) // For each form in the array
			{
				fw.write(formArray[i].toString() + "\r\n"); // Write the form data and go to a new line
			}
			
			fw.close(); // Close the file
			
		}
		catch (Exception e)
		{
			System.out.println("[ERROR] <FORM_LIST> Error writing database to file" + e);
			e.printStackTrace();
		}

	}
	
	public void addForm(Form tempForm) // Adds a form to the program
	{
		//System.out.println("[INFO] <FORM_LIST> Running addForm"); // Debug
		
		formArray[nextFormLocation] = tempForm; // Add the form to the next free location
		
		nextFormLocation++;
	}
	
	public void removeForm(String formID) // Removes a form from the database
	{
		System.out.println("[INFO] <FORM_LIST> Running removeForm");
		
		Form[] newArray = new Form[formArray.length]; // Create a new array of the required size
		
		int j = 0; // The location in newArray
		
		for (int i = 0; i < nextFormLocation; i++)
		{
			if (!formArray[i].getID().equals(formID)) // If the form isn't the one that we don't want
			{
				newArray[j] = formArray[i];
				j++;
			}
		}
		
		formArray = newArray; // Overwrite the old array
		nextFormLocation--; // There is one less form in the array so a free spot has opened
		
		writeDatabase();
	}
	
	public Form[] getArray()
	{
		return formArray;
	}
	
	public Form[] getTrimmedArray() // Returns a version of the array with no null elements
	{
		Form[] trimmedArray = new Form[nextFormLocation]; // Create an array just large enough to store all the elements
		
		for (int i = 0; i < nextFormLocation; i++) // Iterate over each element
		{
			trimmedArray[i] = formArray[i]; // Copy it to the trimmed array
		}
		
		return trimmedArray;
	}
	public String getFreeID() // Gets a free id
	{
		System.out.println("[INFO] <FORM_LIST> Running getFreeID"); // Debug
		Random r = new Random();
		
		String id = null; // The new id
		
		boolean unique = false; // Whether the id is unique
		
		while (!unique) // While a unique id has not been found
		{
			id = generateId(); // Generate an id
			
			if (getFormByID(id) == null) // If the id isn't taken
			{
				unique = true; // We've found a unique id
			}
		}
		
		return id; // Return the id
	}
	
	private String generateId() // Randomly generates an 8 digit id
	{
		System.out.println("[INFO] <FORM_LIST> Running generateId");
	
		String id = "F"; // The id
		Random r = new Random(); // Declare a random number generator
		
		for (int i = 0; i < 8; i++) // Do this 8 times
		{
			id += r.nextInt(10); // Append a random digit from 0 to 9
		}
		
		return id; 
	}
}