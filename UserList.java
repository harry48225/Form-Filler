import java.io.*;
import java.util.*;

public class UserList
{
	private String databaseFileName = "users/UserDB.txt";
	
	public User[] userArray = new User[100];
	
	private int nextUserLocation = 0;
	
	public UserList()
	{
		loadDatabase();
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
		System.out.println("[INFO] <USER_LIST> Running getUserByID"); // Debug
		
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
	
	public void writeDatabase()
	{
		System.out.println("[INFO] <USER_LIST> Running writeDatabase");
		
		try
		{
			FileWriter fw = new FileWriter(databaseFileName);
			
			for (int i = 0; i < nextUserLocation; i++) // For each User in the array
			{
				fw.write(userArray[i].toString() + "||" + userArray[i].getQuestionStats().toString());
				
				fw.write("\r\n"); // Move onto a new line
			}
			
			fw.close(); // Close the file
		}
		catch (Exception e)
		{
			System.out.println("[ERROR] <USER_LIST> Error writing database to file " + e); // Out the error
		}
	}
	
	public void loadDatabase()
	{
		System.out.println("[INFO] <USER_LIST> Running loadDatabase");
		
		nextUserLocation = 0; // Ensure that the users get added to the start of the array
		
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(databaseFileName)); // Open the file for reading
			
			String line = br.readLine();
			
			while (line != null) // While there is data to read from the file
			{
				
				String[] splitData = line.split("\\|\\|"); // Separate into the user data and the QuestionStatList
				
				String[] splitUserData = splitData[0].split(",");
				
				String id = splitUserData[0];
				String username = splitUserData[1];
				String password = splitUserData[2];
				String firstName = splitUserData[3];
				String lastName = splitUserData[4];
				String dateOfBirth = splitUserData[5];
				String phoneNumber = splitUserData[6];
				boolean admin = Boolean.parseBoolean(splitUserData[7]);
				
				String[] sessionsPresentAt = new String[0];
				
				if (splitUserData.length > 8) // If this data is present in the file.
				{
					sessionsPresentAt = splitUserData[8].split("\\.");
				}
				
				QuestionStatList questionStats = new QuestionStatList(); // Empty question stat list
				
				
				if (splitData.length > 1) // If there is a saved QuestionStatList
				{
					questionStats = new QuestionStatList(splitData[1]); // Load the question stat list 
				}
				
				addUser(new User(id, username, password, firstName, lastName, dateOfBirth, phoneNumber, admin, sessionsPresentAt, questionStats));
				
				line = br.readLine();
				
			}
			
			System.out.println("[INFO] <USER_LIST> " + nextUserLocation + " users loaded from file");
			
		}
		catch (Exception e)
		{
			System.out.println("[ERROR] <USER_LIST> Error loading database "+  e);
		}
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
				if (!Utils.isBeforeInDictionary(userArray[i].getFirstName(), userArray[i+1].getFirstName())) // If user 1 doesn't come before user 2
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