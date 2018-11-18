import java.io.*;
import java.util.*;

public class QuestionList
{

	private String typesFileName = "questions/TypesDB.txt";
	
	private String[] types; // The types that a question could be
		
	private String databaseFileName = "questions/QuestionDB.txt"; // The filename of the question database
	
	private Question[] questionArray = new Question[100]; // Store 100 Questions
	
	private int nextQuestionLocation = 0; // The location to store the next Question

	private QuestionPanelList panels; // The question panel list - all the GUI elements
	
	public QuestionList() // Constructor
	{
		loadDatabase(); // Load the database from file
		loadTypes();
		panels = new QuestionPanelList(); // Create a new question panel list
	}
	
	public QuestionPanel getPanel(Question q) // Returns the Panel corresponding to the question
	{
		System.out.println("[INFO] <QUESTION_LIST> Running getPanel"); // Debug
		
		return getPanelByID(q.getID());
	}
	
	public void removeQuestion(String questionID)
	{
		System.out.println("[INFO] <QUESTION_LIST> Running removeQuestion");
		
		Question[] newArray = new Question[questionArray.length]; // Create a new array of the required size
		
		int j = 0; // The location in newArray
		
		for (int i = 0; i < nextQuestionLocation; i++)
		{
			if (!questionArray[i].getID().equals(questionID)) // If the question isn't the one that we don't want
			{
				newArray[j] = questionArray[i];
				j++;
			}
		}
		
		questionArray = newArray; // Overwrite the old array
		nextQuestionLocation--; // There is one less question in the array so a free spot has opened
		
		writeDatabase();
		
		panels.removeQuestionPanel(questionID); // Remove it from the question panel database also
	}
	
	public Question getQuestionByID(String id) // Returns an a question that corresponds to the id
	{
		//System.out.println("[INFO] <QUESTION_LIST> Running getQuestionByID"); // Debug
		
		Question result = null;
		
		for (Question q : questionArray) // For each item in the question array
		{
			if (q == null) // If we've reached the end of the populated section of the array
			{
				break; // Stop searching
			}
			
			if (q.getID().equals(id)) // If the id of the question matches the search id
			{
				result = q;
				
				break; // Stop searching
			}
		}
		
		return result; // Return the result
	}
	
	public QuestionPanel getPanelByID(String id) // Returns the panel corresponding to a question id
	{
		System.out.println("[INFO] <QUESTION_LIST> Running getPanelByID"); // Debug
		
		return panels.getByID(id);
	}
	
	public void loadDatabase() // Loads the questions that are saved in the database
	{
		System.out.println("[INFO] <QUESTION_LIST> Running loadDatabase"); // Debug
 		
		nextQuestionLocation = 0; // Start at the beginning of the array
		
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(databaseFileName)); // Open the database file
			
			String line = br.readLine(); // Read line from the file
			
			while (line != null) // While there is still data to load from the file
			{
				String[] splitQuestionData = line.split(","); // Split the data into its parts
				
				String id = splitQuestionData[0];
				int difficulty = Integer.parseInt(splitQuestionData[1]); // Convert the first part to an integer
				String type = splitQuestionData[2]; // Get the type
				String title = splitQuestionData[3];
				Question tempRead = new Question(id, difficulty, type, title); // Create a new question with the data read from the file
				
				addQuestion(tempRead); // Add the question to the array
				
				line = br.readLine(); // Read the next line of the file
			}
		}
		catch (Exception e)
		{
			System.out.println("[ERROR] <QUESTION_LIST> Error loading database " + e); // Error message
		}
		
		System.out.println("[INFO] <QUESTION_LIST> " + nextQuestionLocation + " Questions loaded from file");
	}
	
	public void writeDatabase() // Writes the questions to file
	{
		System.out.println("[INFO] <QUESTION_LIST> Running writeDatabase"); // Debug
		
		try
		{
			FileWriter fw = new FileWriter(databaseFileName); // Declare a new file writer
			
			for (int i = 0; i < nextQuestionLocation; i++) // For each Question in the array
			{
				String currentPositionUserData = questionArray[i].toString(); // Get the attribute string
				
				fw.write(currentPositionUserData); // Write the data
				
				fw.write("\r\n"); // Go to a new line
			}
			
			fw.close(); // Close the file
			
		}
		catch (Exception e)
		{
			System.out.println("[ERROR] <QUESTION_LIST> Error writing database to file " + e); // Output the error
		}
		
		
		// Save the question panels too
		panels.writeDatabase();
	}
	
	public void sortByDifficulty() // Bubble sort to sort by difficulty
	{
		System.out.println("[INFO] <QUESTION_LIST> Running sortByDifficulty"); // Debug
		
		boolean swapped = true; // Toggle that contains whether a value has been swapped
		
		while (swapped == true) // Until no more swaps are made
		{
			
			swapped = false; // Set swapped to false
			
			for (int i = 0; i < nextQuestionLocation-1; i++) // For all questions but the last one
			{
				// Ascending sort, lowest - highest
				if (questionArray[i].getDifficulty() > questionArray[i+1].getDifficulty()) // Check to see if it's greater than the one after
				{
					Question temp = questionArray[i+1]; // Store the value in a temp variable
					questionArray[i + 1] = questionArray[i]; // Swap the values
					questionArray[i] = temp; // Swap
					
					swapped = true; // Set swapped to true as a swap was made
				}
			}
		}
	}
	
	public void sortByType() // Bubble sort to sort by type
	{
		System.out.println("[INFO] <QUESTION_LIST> Running sortByType"); // Debug
		
		boolean swapped = true; // Toggle that contains whether a value has been swapped
		
		while (swapped == true) // Until no more swaps are made
		{
			
			swapped = false; // Set swapped to false
			
			for (int i = 0; i < nextQuestionLocation-1; i++) // For all questions but the last one
			{
				// Ascending sort, a - z
				char questionOneFirstLetter = questionArray[i].getType().charAt(0); // Get the first character of the type
				char questionTwoFirstLetter = questionArray[i+1].getType().charAt(0); // Get the first character of the type
				
				if ((int) questionOneFirstLetter > (int) questionTwoFirstLetter) // Cast to int and compare
				{
					Question temp = questionArray[i+1]; // Store the value in a temp variable
					questionArray[i + 1] = questionArray[i]; // Swap the values
					questionArray[i] = temp; // Swap
					
					swapped = true; // Set swapped to true as a swap was made
				}
			}
		}
	}
	
	public Question[] filterByType(String searchType) // Finds all the questions of the search type
	{
		System.out.println("[INFO] <QUESTION_LIST> Running filterByType");
		
		Question[] searchResults = new Question[nextQuestionLocation]; // Create array large enough to store the matches

		int nextResultLocation = 0;

		for (int i = 0; i < nextQuestionLocation; i++) // For each question
		{
			
			if 	(questionArray[i].getType().equals(searchType)) // If it's a match
			{
				searchResults[nextResultLocation] = questionArray[i]; // Store it in the search results array
				
				nextResultLocation++;
			}
		}

		// Trim the results array

		Question[] searchResultsTrimmed = new Question[nextResultLocation]; // Create a new array of the exact size required

		for (int i = 0; i < nextResultLocation; i++)
		{
			searchResultsTrimmed[i] = searchResults[i]; // Copy over the result
		}

		return searchResultsTrimmed;
	}
	
	public Question[] filterByDifficulty(int difficulty)
	{
		System.out.println("[INFO] <QUESTION_LIST> Running filterByDifficulty");
		
		// Linear search
		
		Question[] results = new Question[nextQuestionLocation]; // Store the results of the search
		int nextResultLocation = 0;
		
		for (int i = 0; i < nextQuestionLocation; i++) // Iterate over the questions
		{
			if (questionArray[i].getDifficulty() == difficulty) // If it's of the correct difficulty
			{
				results[nextResultLocation] = questionArray[i]; // Copy the question into the results array
				nextResultLocation++;
			}
		}
		
		Question[] trimmedResults = new Question[nextResultLocation]; // Create a new array of the correct size
		
		for (int i = 0; i < nextResultLocation; i++)
		{
			trimmedResults[i] = results[i];
		}
		
		return trimmedResults; // Return the results
	}
	
	public void printQuestions() // To print the questions
	{
		System.out.println("[INFO] <QUESTION_LIST> Running printQuestions"); // Debug
		
		for (int i = 0; i < nextQuestionLocation; i++) // For each question in the array
		{
			System.out.println(questionArray[i].toString()); // Print the attributes of each question
		}	
	}
	
	public void addQuestion(Question tempQuestion) // Adds a question to the program
	{
		//System.out.println("[INFO] <QUESTION_LIST> Running addQuestion"); // Debug
		
		questionArray[nextQuestionLocation] = tempQuestion; // Add the question to the array at the next free position
		
		nextQuestionLocation++; // Increment the location
	}
	
	public void addQuestion(Question tempQuestion, QuestionPanel tempQuestionPanel) // Adds a question and question panel to the program
	{
		addQuestion(tempQuestion); // Add the question
		panels.addQuestionPanel(tempQuestionPanel); // Add the question panel to the database
		
		System.out.println("[INFO] <QUESTION_LIST> Successfully added question and questionPanel");
	}
	
	public String getFreeID() // Gets a free id
	{
		Random r = new Random();
		
		String id = null; // The new id
		
		boolean unique = false; // Whether the id is unique
		
		while (!unique) // While a unique id has not been found
		{
			id = generateId(); // Generate an id
			
			if (getQuestionByID(id) == null) // If the id isn't taken
			{
				unique = true; // We've found a unique id
			}
		}
		
		return id; // Return the id
	}
	
	private String generateId() // Randomly generates an 8 digit id
	{
		System.out.println("[INFO] <QUESTION_LIST> Running generateId");
	
		String id = "Q"; // The id
		Random r = new Random(); // Declare a random number generator
		
		for (int i = 0; i < 8; i++) // Do this 8 times
		{
			id += r.nextInt(10); // Append a random digit from 0 to 9
		}
		
		return id; 
	}
	
	public Question[] getArray() // Returns the question array
	{
		return questionArray; // Return the array
	}
	
	private void loadTypes() // Loads the types from file
	{
		System.out.println("[INFO] <QUESTION_LIST> Running loadTypes"); // Debug
		
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(typesFileName)); // Open the database file name
			
			String line = br.readLine(); // Read the line from the database
			
			types = line.split(",");
		}
		catch  (Exception e)
		{
			System.out.println("[ERROR] <QUESTION_LIST> Error loading types database" + e);
		}
		
		System.out.println("[INFO] <QUESTION_LIST> " + types.length + " types loaded from file");
	}
	
	public void writeTypes() // Writes the types list to file
	{
		System.out.println("[INFO] <QUESTION_LIST> Running writeTypes");
		
		try
		{
			FileWriter fw = new FileWriter(typesFileName); // Declare a new file writer
			
			fw.write(typesArrayToString());
			
			fw.close(); // Close the file
			
		}
		catch (Exception e)
		{
			System.out.println("[ERROR] <QUESTION_LIST> Error writing types to file" + e);
		}
	}
	
	private String typesArrayToString() // Converts the skills array to string
	{
		String arrayAsString = "";
		
		for (String t : types)
		{
			arrayAsString += t + ",";
		}
		
		arrayAsString = arrayAsString.substring(0, arrayAsString.length() - 1); // Get rid of trailing .
		
		return arrayAsString;
		
	}
	
	public String[] getTypes()
	{
		return types;
	}

	public void addType(String newType) // Adds a new type to the type database
	{
		System.out.println("[INFO] <QUESTION_LIST> Running addType"); // Debug

		String[] newTypes = new String[types.length + 1]; // Make a longer string array to store the new type

		for (int i = 0; i <= types.length; i++)
		{
			if (i == types.length) // If it's the last item in the array
			{
				newTypes[i] = newType; // Add the new type
			}
			else
			{
				newTypes[i] = types[i]; // Copy across the old types
			}
		}

		types = newTypes;

		writeTypes(); // Save the changes
	}
	
}