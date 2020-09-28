package com.harry.formfiller.user;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

import com.harry.formfiller.util.Encrypter;
import com.harry.formfiller.util.Util;

public class UserList
{
	/* This list class stores and manipulates all of the user objects in the system */
	
	private String credentialDatabaseName = "users/Credentials.txt";
	private String questionStatDatabaseName = "users/QuestionStats.txt";
	private String sensitiveDatabaseName = "users/userInfo.txt";
	
	private boolean decrypted = false;
	private String encryptionKey;
	
	private Random r = new Random(); // Declare a random number generator
	
	public User[] userArray = new User[100];
	
	private int nextUserLocation = 0;
	
	public UserList()
	{
		loadDatabase();
	}
	
	public boolean isDecrypted()
	{
		/* Returns whether the database is decrypted */
		return decrypted;
	}
	
	public void setKey(String newKey)
	{
		/* Sets a new encryption key */
		encryptionKey = newKey;
	}
	
	public User[] filterByFirstName(String firstname)
	{
		/* Returns an array of all the users with a specified first name */
		
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
	
	public void removeUser(String userID)
	{
		/* Removes a user based on id */
		
		System.out.println("[INFO] <USER_LIST> Running removeUser");
		
		User[] newArray = new User[userArray.length]; // Create a new array of the required size
		
		int j = 0; // The location in newArray
		
		// Copy over all of the users to the new array apart from the one to delete
		for (int i = 0; i < nextUserLocation; i++)
		{
			if (!userArray[i].getID().equals(userID)) // If the user isn't the one to delete
			{
				newArray[j] = userArray[i]; // Copy the user into the new array
				j++;
			}
		}
		
		userArray = newArray; // Overwrite the old array
		nextUserLocation--; // There is one less user in the array so a free spot has opened
	}
	
	public User getUserByID(String id)
	{
		/* Returns the User corresponding to an ID */
		
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
	
	public User getUserByUsername(String username)
	{
		/* Returns the User corresponding to a username */
		
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
	
	private String generateId()
	{
		/* Randomly generates an 8 digit user id */
		
		System.out.println("[INFO] <USER_LIST> Running generateId");
	
		String id = "U"; // The id
		
		for (int i = 0; i < 8; i++) // Do this 8 times
		{
			id += r.nextInt(10); // Append a random digit from 0 to 9
		}
		
		return id; 
	}
	
	public String getFreeID()
	{
		/* Gets a free user id */
		
		System.out.println("[INFO] <USER_LIST> Running getFreeID"); // Debug
		
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
		/* Returns the user array */
		
		return userArray;
	}
	
	public User[] getUsers()
	{
		/* Returns a trimmed version of the user array */
		
		User[] trimmedArray = new User[nextUserLocation];
		
		// Copy over all of the users to the trimmed array
		for (int i = 0; i < nextUserLocation; i++)
		{
			trimmedArray[i] = userArray[i];
		}
		
		return trimmedArray;
	}
	
	private void writeCredentialDatabase()
	{
		/* Writes just the credential database to file */
		
		System.out.println("[INFO] <USER_LIST> Running writeCredentialDatabase");
		
		try(FileWriter fw = new FileWriter(credentialDatabaseName))
		{			
			for (int i = 0; i < nextUserLocation; i++) // For each User in the array
			{
				fw.write(userArray[i].getCredentialString()); // Write their credential string to file
				
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
		/* Writes just the sensitive user database to file and encrypts it with the encryption key */
		
		System.out.println("[INFO] <USER_LIST> Running writeSensitiveDatabase");
		
		Encrypter enc = new Encrypter(); // Create a new encrypter
		
		try(FileWriter fw = new FileWriter(sensitiveDatabaseName))
		{	
			for (int i = 0; i < nextUserLocation; i++) // For each User in the array
			{
				// Get their sensitive data, encrypt it, and then write it to file
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
		/* Writes just the question stat database to file */
		
		System.out.println("[INFO] <USER_LIST> Running writeQuestionStatDatabase");
		
		try(FileWriter fw = new FileWriter(questionStatDatabaseName))
		{	
			for (int i = 0; i < nextUserLocation; i++) // For each User in the array
			{
				// Write the user id and their question stat database
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
		/* Writes all of the user databases to file */
		
		System.out.println("[INFO] <USER_LIST> Running writeDatabase");
		
		writeCredentialDatabase();
		writeQuestionStatDatabase();
		
		// Only write the sensitive database if it's decrypted
		if (decrypted)
		{
			writeSensitiveDatabase(encryptionKey);
		}
	}
	
	private void loadCredentialDatabase()
	{
		/* Loads just the non sensitive, general, information about the users */
		
		System.out.println("[INFO] <USER_LIST> Running loadCredentialDatabase");
		
		nextUserLocation = 0; // Ensure that the users get added to the start of the array
		
		try(BufferedReader br = new BufferedReader(new FileReader(credentialDatabaseName))) // Open the file for reading
		{
			String line = br.readLine();
			
			while (line != null) // While there is data to read from the file
			{
				addUser(new User(line)); // Load each user from file
				
				line = br.readLine(); // Read the next line
				
			}
			
			System.out.println("[INFO] <USER_LIST> " + nextUserLocation + " users loaded from file");
			
			br.close();
			
		}
		catch (Exception e)
		{
			System.out.println("[ERROR] <USER_LIST> Error loading credential database "+  e);
		}
	}
	
	public void loadSensitiveDatabase(String key)
	{
		/* Loads the sensitive information and decrypts it with the key passed as a parameter */
		
		System.out.println("[INFO] <USER_LIST> Running loadSensitiveDatabase");
		
		Encrypter enc = new Encrypter(); // Create a new encrypter
		
		try(BufferedReader br = new BufferedReader(new FileReader(sensitiveDatabaseName))) // Open the file for reading
		{
			String line = br.readLine();
			
			while (line != null) // While there is data to read from the file
			{
				
				// Decode the line and load the user id
				String decryptedLine = enc.decode(line, key);
				String[] splitData = decryptedLine.split("\\|\\|");
				String userID = splitData[0];
				
				// Add the data to the correct user in the list
				getUserByID(userID).addSensitiveInformation(splitData[1]);
				
				line = br.readLine();
				
			}
			
			// Store the key and that the decryption was successful
			encryptionKey = key;
			decrypted = true;
			
			br.close();
		}
		catch (Exception e)
		{
			System.out.println("[ERROR] <USER_LIST> Error loading sensitive database "+  e);
		}
	}
	
	private void loadQuestionStatDatabase()
	{
		/* Loads just the question stats */
		
		System.out.println("[INFO] <USER_LIST> Running loadQuestionStatDatabase");
		
		try(BufferedReader br = new BufferedReader(new FileReader(questionStatDatabaseName))) // Open the file for reading
		{	
			String line = br.readLine();
			
			while (line != null) // While there is data to read from the file
			{
				String[] splitLine = line.split("\\|\\|"); // Split at ||
	 			
				QuestionStatList questionStats = new QuestionStatList(); // Empty question stat list
				
				if (splitLine.length > 1) // If there is a saved QuestionStatList
				{
					questionStats = new QuestionStatList(splitLine[1]); // Load the question stat list 
				}
				
				// Get the user id and add the loaded question stats to the correct user
				String userID = splitLine[0];
				getUserByID(userID).setQuestionStats(questionStats);
				
				line = br.readLine();
				
			}
			
			br.close();
			
		}
		catch (Exception e)
		{
			System.out.println("[ERROR] <USER_LIST> Error loading questionStat database "+  e);
		}
	}
	
	public void loadDatabase()
	{
		/* Loads the non-encrypted databases */
		
		System.out.println("[INFO] <USER_LIST> Running loadDatabase");
		
		loadCredentialDatabase();
		loadQuestionStatDatabase();
		
	}
	
	public void addUser(User newUser)
	{
		/* Adds a user to the user list */
		
		userArray[nextUserLocation] = newUser;
		nextUserLocation++;
	}
	
	public void sortByFirstName()
	{
		/* Performs a bubble sort to sort the users into alphabetical order */
		
		System.out.println("[INFO] <USER_LIST> Running sortByFirstName"); // Debug
		
		boolean swapped = true; // Toggle that contains whether a value has been swapped
		
		while (swapped == true) // Until no more swaps are made
		{
			
			swapped = false; // Set swapped to false
			
			for (int i = 0; i < nextUserLocation-1; i++) // For all users but the last one
			{
				String user1FirstName = userArray[i].getFirstName();
				String user2FirstName = userArray[i+1].getFirstName();
				
				if (!user1FirstName.equals(user2FirstName) && !Util.isBeforeInDictionary(user1FirstName, user2FirstName)) // If user 1 doesn't come before user 2
				{
					// Swap the users
					
					User temp = userArray[i+1]; // Store the value in a temp variable
					userArray[i + 1] = userArray[i]; // Swap the values
					userArray[i] = temp; // Swap
					
					swapped = true; // Set swapped to true as a swap was made
				}
			}
		}
	}

}