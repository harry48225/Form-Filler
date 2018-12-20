import java.io.*;
import java.util.*;

public class UserList
{
	private String databaseFileName = "users/UserDB.txt";
	private String credentialDatabaseName = "users/Credentials.txt";
	private String questionStatDatabaseName = "users/QuestionStats.txt";
	private String sensitiveDatabaseName = "users/userInfo.txt";
	
	private boolean decrypted = false;
	private String encryptionKey;
	
	public User[] userArray = new User[100];
	
	private int nextUserLocation = 0;
	
	public UserList()
	{
		loadDatabase();
	}
	
	public boolean isDecrypted()
	{
		return decrypted;
	}
	
	public void setKey(String newKey)
	{
		encryptionKey = newKey;
	}
	
	public User[] filterByFirstName(String firstname) // Gets all of the users with a specified first name
	{
		System.out.println("[INFO] <USER_LIST> Running filterByFirstname");
		
		User[] searchResults = new User[nextUserLocation]; // Create array large enough to store the matches

		int nextResultLocation = 0;

		for (int i = 0; i < nextUserLocation; i++) // For each user
		{
			
			if 	(userArray[i].getFirstName().equalsIgnoreCase(firstname)) // If it's a match
			{
				searchResults[nextResultLocation] = userArray[i]; // Store it in the search results array
				
				nextResultLocation++;
			}
		}

		// Trim the results array

		User[] searchResultsTrimmed = new User[nextResultLocation]; // Create a new array of the exact size required

		for (int i = 0; i < nextResultLocation; i++)
		{
			searchResultsTrimmed[i] = searchResults[i]; // Copy over the result
		}

		return searchResultsTrimmed;
	}
	
	public void removeUser(String userID) // Removes a user based on id
	{
		System.out.println("[INFO] <USER_LIST> Running removeUser");
		
		User[] newArray = new User[userArray.length]; // Create a new array of the required size
		
		int j = 0; // The location in newArray
		
		for (int i = 0; i < nextUserLocation; i++)
		{
			if (!userArray[i].getID().equals(userID)) // If the user isn't the one that we don't want
			{
				newArray[j] = userArray[i]; // Copy the user into the new array
				j++;
			}
		}
		
		userArray = newArray; // Overwrite the old array
		nextUserLocation--; // There is one less user in the array so a free spot has opened
	}
	
	public User getUserByID(String id) // Returns the User corresponding to an ID
	{
		//System.out.println("[INFO] <USER_LIST> Running getUserByID"); // Debug
		
		User result = null; // The user that was found
		
		for (int i = 0; i < nextUserLocation; i++) // Linear search
		{
			if (userArray[i].getID().equals(id)) // If we've found the user
			{
				result = userArray[i];
				
				break; // Stop searching
			}
		}
		
		return result;
		
	}
	
	public User getUserByUsername(String username) // Returns the User corresponding to a username
	{
		System.out.println("[INFO] <USER_LIST> Running getUserByUsername"); // Debug
		
		User result = null; // The user that was found
		
		for (int i = 0; i < nextUserLocation; i++) // Linear search
		{
			if (userArray[i].getUsername().equals(username)) // If we've found the user
			{
				result = userArray[i];
				
				break; // Stop searching
			}
		}
		
		return result;
	}
	
	private String generateId() // Randomly generates an 8 digit id
	{
		System.out.println("[INFO] <USER_LIST> Running generateId");
	
		String id = "U"; // The id
		Random r = new Random(); // Declare a random number generator
		
		for (int i = 0; i < 8; i++) // Do this 8 times
		{
			id += r.nextInt(10); // Append a random digit from 0 to 9
		}
		
		return id; 
	}
	
	public String getFreeID() // Gets a free id
	{
		System.out.println("[INFO] <USER_LIST> Running getFreeID"); // Debug
		Random r = new Random();
		
		String id = null; // The new id
		
		boolean unique = false; // Whether the id is unique
		
		while (!unique) // While a unique id has not been found
		{
			id = generateId(); // Generate an id
			
			if (getUserByID(id) == null) // If the id isn't taken
			{
				unique = true; // We've found a unique id
			}
		}
		
		return id; // Return the id
	}
	
	public User[] getArray()
	{
		return userArray;
	}
	
	public User[] getUsers() // Returns a trimmed version of the array
	{
		User[] trimmedArray = new User[nextUserLocation];
		
		for (int i = 0; i < nextUserLocation; i++)
		{
			trimmedArray[i] = userArray[i];
		}
		
		return trimmedArray;
	}
	
	private void writeCredentialDatabase()
	{
		System.out.println("[INFO] <USER_LIST> Running writeCredentialDatabase");
		
		try
		{
			FileWriter fw = new FileWriter(credentialDatabaseName);
			
			for (int i = 0; i < nextUserLocation; i++) // For each User in the array
			{
				fw.write(userArray[i].getCredentialString());
				
				fw.write("\r\n"); // Move onto a new line
			}
			
			fw.close(); // Close the file
		}
		catch (Exception e)
		{
			System.out.println("[ERROR] <USER_LIST> Error writing credential database to file " + e);
		}
	
	}
	
	public void writeSensitiveDatabase(String encryptionKey)
	{
		System.out.println("[INFO] <USER_LIST> Running writeSensitiveDatabase");
		
		Encrypter enc = new Encrypter();
		try
		{
			FileWriter fw = new FileWriter(sensitiveDatabaseName);
			
			for (int i = 0; i < nextUserLocation; i++) // For each User in the array
			{
				String userData = userArray[i].getSensitiveString();
				String encryptedUserData = enc.encode(userData, encryptionKey);
				fw.write(encryptedUserData);
				
				fw.write("\r\n"); // Move onto a new line
			}
			
			fw.close(); // Close the file
		}
		catch (Exception e)
		{
			System.out.println("[ERROR] <USER_LIST> Error writing sensitive database to file " + e);
		}
	
	}
	
	private void writeQuestionStatDatabase()
	{
		System.out.println("[INFO] <USER_LIST> Running writeQuestionStatDatabase");
		
		try
		{
			FileWriter fw = new FileWriter(questionStatDatabaseName);
			
			for (int i = 0; i < nextUserLocation; i++) // For each User in the array
			{
				fw.write(userArray[i].getID() + "||" + userArray[i].getQuestionStats().toString());
				
				fw.write("\r\n"); // Move onto a new line
			}
			
			fw.close(); // Close the file
		}
		catch (Exception e)
		{
			System.out.println("[ERROR] <USER_LIST> Error writing question stat database to file " + e);
		}
	}
	
	public void writeDatabase()
	{
		System.out.println("[INFO] <USER_LIST> Running writeDatabase");
		
		writeCredentialDatabase();
		writeQuestionStatDatabase();
		
		if (decrypted)
		{
			writeSensitiveDatabase(encryptionKey);
		}
	}
	
	private void loadCredentialDatabase() // Loads just the non sensitive information
	{
		System.out.println("[INFO] <USER_LIST> Running loadCredentialDatabase");
		
		nextUserLocation = 0; // Ensure that the users get added to the start of the array
		
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(credentialDatabaseName)); // Open the file for reading
			
			String line = br.readLine();
			
			while (line != null) // While there is data to read from the file
			{
				addUser(new User(line));
				
				line = br.readLine();
				
			}
			
			System.out.println("[INFO] <USER_LIST> " + nextUserLocation + " users loaded from file");
			
		}
		catch (Exception e)
		{
			System.out.println("[ERROR] <USER_LIST> Error loading credential database "+  e);
		}
	}
	
	public void loadSensitiveDatabase(String key) // Loads the sensitive information
	{
		System.out.println("[INFO] <USER_LIST> Running loadSensitiveDatabase");
		
		Encrypter enc = new Encrypter();
		
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(sensitiveDatabaseName)); // Open the file for reading
			
			String line = br.readLine();
			
			while (line != null) // While there is data to read from the file
			{
				
				String decryptedLine = enc.decode(line, key);
				String[] splitData = decryptedLine.split("\\|\\|");
				String userID = splitData[0];
				
				getUserByID(userID).addSensitiveInformation(splitData[1]);
				
				line = br.readLine();
				
			}
			
			encryptionKey = key;
			decrypted = true;
			
		}
		catch (Exception e)
		{
			System.out.println("[ERROR] <USER_LIST> Error loading sensitive database "+  e);
		}
	}
	
	private void loadQuestionStatDatabase() // Loads just the question stats
	{
		System.out.println("[INFO] <USER_LIST> Running loadQuestionStatDatabase");
		
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(questionStatDatabaseName)); // Open the file for reading
			
			String line = br.readLine();
			
			while (line != null) // While there is data to read from the file
			{
				String[] splitLine = line.split("\\|\\|"); // Split at ||
				
				QuestionStatList questionStats = new QuestionStatList(); // Empty question stat list
				
				if (splitLine.length > 1) // If there is a saved QuestionStatList
				{
					questionStats = new QuestionStatList(splitLine[1]); // Load the question stat list 
				}
				
				String userID = splitLine[0];
				getUserByID(userID).setQuestionStats(questionStats);
				
				line = br.readLine();
				
			}
			
		}
		catch (Exception e)
		{
			System.out.println("[ERROR] <USER_LIST> Error loading questionStat database "+  e);
		}
	}
	
	public void loadDatabase()
	{
		System.out.println("[INFO] <USER_LIST> Running loadDatabase");
		
		loadCredentialDatabase();
		loadQuestionStatDatabase();
		
	}
	
	public void addUser(User newUser)
	{
		//System.out.println("[INFO] <USER_LIST> Running addUser");
		
		userArray[nextUserLocation] = newUser;
		nextUserLocation++;
	}
	
	public void sortByFirstName()
	{
		System.out.println("[INFO] <USER_LIST> Running sortByFirstName"); // Debug
		
		boolean swapped = true; // Toggle that contains whether a value has been swapped
		
		while (swapped == true) // Until no more swaps are made
		{
			
			swapped = false; // Set swapped to false
			
			for (int i = 0; i < nextUserLocation-1; i++) // For all users but the last one
			{
				String user1FirstName = userArray[i].getFirstName();
				String user2FirstName = userArray[i+1].getFirstName();
				
				if (!user1FirstName.equals(user2FirstName) && !Utils.isBeforeInDictionary(user1FirstName, user2FirstName)) // If user 1 doesn't come before user 2
				{
					User temp = userArray[i+1]; // Store the value in a temp variable
					userArray[i + 1] = userArray[i]; // Swap the values
					userArray[i] = temp; // Swap
					
					swapped = true; // Set swapped to true as a swap was made
				}
			}
		}
	}

}