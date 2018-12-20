import java.time.*;

public class User
{
	private String id;
	
	private String username;
	private String password;
	
	// Sensitive info
	private String firstName = "encrypted";
	private String lastName = "encrypted";
	private String dateOfBirth = "encrypted";
	private String phoneNumber = "encrypted";
	
	private boolean admin;
	
	private String[] sessionsPresentAt;
	
	private QuestionStatList questionStats;
	
	public User(String credentialString)
	{
	
		String[] splitData = credentialString.split(",");
		
		id = splitData[0];
		username = splitData[1];
		password = splitData[2];
		admin = Boolean.parseBoolean(splitData[3]);
		
		sessionsPresentAt = new String[0];
		
		if (splitData.length > 4) // If the sessions present at is present
		{
			sessionsPresentAt = splitData[4].split("\\.");
		}
	}
	
	public User(String tempId, String tempUsername, String tempPassword, String tempFirstName, 
					String tempLastName, String tempDateOfBirth, String tempPhoneNumber, boolean tempAdmin,
					String[] tempSessionsPresentAt, QuestionStatList tempQuestionStats)
	{
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
		String[] splitData = sensitiveInfoString.split(",");
		
		firstName = splitData[0];
		lastName = splitData[1];
		dateOfBirth = splitData[2];
		phoneNumber = splitData[3];
	}
	
	public boolean isAdmin()
	{
		return admin;
	}
	
	public void addPresentToday() // Adds today's date to the user's sessions present at array
	{
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
	
	public void addSession(String sessionDate) // Adds a date to the user's sessions array.
	{
		String[] newSessions = new String[sessionsPresentAt.length + 1]; // Create a larger array to store the sessions
		
		for (int i = 0; i < sessionsPresentAt.length; i++)
		{
			newSessions[i] = sessionsPresentAt[i]; // Copy across the sessions
		}			
		
		newSessions[sessionsPresentAt.length] = sessionDate; // Add the new session at the end
		
		sessionsPresentAt = newSessions;
	}
	
	public String[] getSessionsAttended()
	{
		return sessionsPresentAt;
	}
	
	public String getID()
	{
		return id;
	}
	
	public String toString()
	{
		return id + "," + username + "," + password + "," + firstName + "," + lastName + "," + dateOfBirth + "," + phoneNumber + "," + admin + "," + sessionsToString();
	}
	
	public String getCredentialString()
	{
		return id + "," + username + "," + password + "," + admin + "," + sessionsToString();
	}
	
	public String getSensitiveString()
	{
		return id + "||" + firstName + "," + lastName + "," + dateOfBirth + "," + phoneNumber;
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
	private String sessionsToString() // Converts the sessions array to string
	{
		String sessions = "";
		
		if (sessionsPresentAt.length > 0)
		{
			sessions = sessionsPresentAt[0]; // Start at the first one
			
			for (int i = 1; i < sessionsPresentAt.length; i++) // For every other session
			{
				sessions += "." + sessionsPresentAt[i];
			}
		}
		
		return sessions;
	}
}