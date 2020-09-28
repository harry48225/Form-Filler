package com.harry.formfiller.user;

import java.time.LocalDate;

public class User
{
	/* All users in the system are stored as user objects.
		A user object stores all of the data to do with a user */
		
	private String id;
	
	private String username;
	private String password;
	
	// Sensitive info
	// Store as encrypted by default as the database isn't initally decrypted
	private final static String ENCRYPTED_STRING = "encrypted";
	private String firstName = ENCRYPTED_STRING;
	private String lastName = ENCRYPTED_STRING;
	private String dateOfBirth = ENCRYPTED_STRING;
	private String phoneNumber = ENCRYPTED_STRING;
	
	// Store whether the user is an admin user
	private boolean admin;
	
	private String[] sessionsPresentAt;
	
	private QuestionStatList questionStats;
	
	public User(String credentialString)
	{
		/* Loads all of the information stored in a user saveString */
		
		// The data is delimited by commas
		String[] splitData = credentialString.split(",");
		
		// Extract the data
		id = splitData[0];
		username = splitData[1];
		password = splitData[2];
		admin = Boolean.parseBoolean(splitData[3]);
		
		// Load the sessions present at array
		sessionsPresentAt = new String[0];
		
		if (splitData.length > 4) // If the sessions present at is present
		{
			sessionsPresentAt = splitData[4].split("\\."); // The sessions present at are delimited by . s
		}
	}
	
	public User(String tempId, String tempUsername, String tempPassword, String tempFirstName, 
					String tempLastName, String tempDateOfBirth, String tempPhoneNumber, boolean tempAdmin,
					String[] tempSessionsPresentAt, QuestionStatList tempQuestionStats)
	{
		/* Creates a new user with the attributes specified by the parameters */
		
		id = tempId;
		username = tempUsername;
		password = tempPassword;
		firstName = tempFirstName;
		lastName = tempLastName;
		dateOfBirth = tempDateOfBirth;
		phoneNumber = tempPhoneNumber;
		
		admin = tempAdmin;
		
		sessionsPresentAt = tempSessionsPresentAt;
		
		questionStats = tempQuestionStats;
	}
	
	public void addSensitiveInformation(String sensitiveInfoString)
	{
		/* Adds the loaded sensitive info to the user.
			This is called when the user database is decrypted */
			
		// The information is delimited by commas
		String[] splitData = sensitiveInfoString.split(",");
		
		firstName = splitData[0];
		lastName = splitData[1];
		dateOfBirth = splitData[2];
		phoneNumber = splitData[3];
	}
	
	public boolean isAdmin()
	{
		/* Returns whether or not the user is an admin */
		return admin;
	}
	
	public void addPresentToday()
	{
		/* Adds today's date to the user's sessions present at array */
		
		String rawToday = LocalDate.now().toString(); // Get today's date
		// By default its yyyy-mm-dd
		// We want dd-mm-yyyy
		String[] todayArray = rawToday.split("-");
		String today = todayArray[2] + "-" + todayArray[1] + "-" + todayArray[0];
		
		if (sessionsPresentAt.length >= 1) // If they've been to at least one session
		{
			String mostRecentSession = sessionsPresentAt[sessionsPresentAt.length - 1]; // Get the most recent session
			
			if (!mostRecentSession.equals(today)) // If the most recent session wasn't today
			{
				addSession(today); // Add today
			}
		}
		else
		{
			addSession(today);
		}
	}
	
	public void addSession(String sessionDate)
	{
		/* Adds a date to the user's sessions present at array. */
		
		String[] newSessions = new String[sessionsPresentAt.length + 1]; // Create a larger array to store the sessions
		
		for (int i = 0; i < sessionsPresentAt.length; i++)
		{
			newSessions[i] = sessionsPresentAt[i]; // Copy across the sessions
		}			
		
		newSessions[sessionsPresentAt.length] = sessionDate; // Add the new session at the end
		
		sessionsPresentAt = newSessions;
	}
	
	public String toString()
	{
		/* Returns a string containing all of the data about a user */
		return id + "," + username + "," + password + "," + firstName + "," + lastName + "," + dateOfBirth + "," + phoneNumber + "," + admin + "," + sessionsToString();
	}
	
	public String getCredentialString()
	{
		/* Returns just the username, password, admin status, and sessions the user has attended */
		return id + "," + username + "," + password + "," + admin + "," + sessionsToString();
	}
	
	public String getSensitiveString()
	{
		/* Returns the sensitive information stored about a user */
		return id + "||" + firstName + "," + lastName + "," + dateOfBirth + "," + phoneNumber;	
	}
	
	private String sessionsToString()
	{
		/* Converts the sessions array to string */
		
		String sessions = "";
		
		if (sessionsPresentAt.length > 0)
		{
			sessions = sessionsPresentAt[0]; // Start at the first session
			
			for (int i = 1; i < sessionsPresentAt.length; i++) // For every session (apart from the first)
			{
				sessions += "." + sessionsPresentAt[i]; // Append it to the string
			}
		}
		
		return sessions;
	}
	
	/* These methods that follow are general setter and getter methods */
	public String[] getSessionsAttended()
	{
		return sessionsPresentAt;
	}
	
	public String getID()
	{
		return id;
	}
	
	public String[] toStringArray()
	{
		return toString().split(",");
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public void setUsername(String newUsername)
	{
		username = newUsername;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public void setPassword(String newPassword)
	{
		password = newPassword;
	}
	
	public String getFirstName()
	{
		return firstName;
	}
	
	public void setFirstName(String newFirstName)
	{
		firstName = newFirstName;
	}
	
	public String getLastName()
	{
		return lastName;
	}
	
	public void setLastName(String newLastName)
	{
		lastName = newLastName;
	}
	
	public String getDateOfBirth()
	{
		return dateOfBirth;
	}
	
	public void setDateOfBirth(String newDateOfBirth)
	{
		dateOfBirth = newDateOfBirth;
	}
	
	public String getPhoneNumber()
	{
		return phoneNumber;
	}
	
	public void setPhoneNumber(String newPhoneNumber)
	{
		phoneNumber = newPhoneNumber;
	}
	
	public void setSessionsAttended(String[] newSessionsAttended)
	{
		sessionsPresentAt = newSessionsAttended;
	}
	
	public void setQuestionStats(QuestionStatList qStats)
	{
		questionStats = qStats;
	}
	
	public QuestionStatList getQuestionStats()
	{
		return questionStats;
	}
	
	public String getSessionsAttendedString()
	{
		return sessionsToString();
	}
}