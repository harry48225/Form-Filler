public class QuestionStat // Every user has one of these for each question and it stores the stats about how they're performing on this question
{
	private String questionID; // The question that it's linked to
	
	private int timesFailedValidation = 0; // The number of times that the user has failed the validation check for the question
	private int numberOfSuccessfulAttempts = 0; // The number of times that the user has successfully attempted the question
	private int[] numberOfAttemptsNeededToCorrect = new int[5]; // The number of times that it's taken the user to correct a error that they've made in the question
	private long[] timeTakenToComplete = new long[5]; // The length of time in seconds that it has taken the user to complete the question in the past.
	
	public QuestionStat(String tempQuestionID)
	{
		questionID = tempQuestionID;
		
		initaliseArrays();
	}
	
	public QuestionStat(String[] loadedData)
	{
		questionID = loadedData[0];
		timesFailedValidation = Integer.parseInt(loadedData[1]);
		numberOfAttemptsNeededToCorrect = loadNumberOfAttemptsNeededToCorrectArray(loadedData[2]);
		timeTakenToComplete = loadTimeTakenToCompleteArray(loadedData[3]);
		numberOfSuccessfulAttempts = Integer.parseInt(loadedData[4]);
	}
	
	private void initaliseArrays()
	{	
		
		// Add rogue values
		
		// Set all the values in the array to -1
		for (int i = 0; i < numberOfAttemptsNeededToCorrect.length; i++)
		{
			numberOfAttemptsNeededToCorrect[i] = -1;
		}
		
		// Set all the values in the array to -1
		for (int i = 0; i < timeTakenToComplete.length; i++)
		{
			timeTakenToComplete[i] = -1;
		}
		
	}
	
	public int getTotalNumberOfAttempts()
	{
		return numberOfSuccessfulAttempts + timesFailedValidation;
	}
	
	public int getNumberOfAttempts()
	{
		return numberOfSuccessfulAttempts;
	}
	
	
	public int getTimesFailedValidation()
	{
		return timesFailedValidation;
	}
	
	public int[] getNumberOfAttemptsNeededToCorrect()
	{
		return numberOfAttemptsNeededToCorrect;
	}
	
	public int getAverageNumberOfAttemptsNeededToCorrect()
	{
		int number = 0; // Store the number of number of attempts - to calculate the average
		int total = 0;
		
		for (int attemptNeededToCorrect : numberOfAttemptsNeededToCorrect) // For each number in the array
		{
			if (attemptNeededToCorrect > 0) // If it's not null data
			{
				number++; // Increment the number of items in the average
				total += attemptNeededToCorrect; // Add the number to the total
			}
		}
		
		int average = 0; // Initial average
		
		if (number > 0) // Check that there was data in the array and that it won't be a division by 0
		{
			average = total / number; // Calculate the average
		}
		return average; // Return the average
	}
	
	public long[] getTimeTakenToComplete()
	{
		return timeTakenToComplete;
	}
	
	public long getAverageTimeTakenToComplete()
	{
		int number = 0; // Store the number of times - to calculate the average
		long total = 0;
		
		for (long time : timeTakenToComplete) // For each time in the array
		{
			if (time > 0) // If it's not null data
			{
				number++; // Increment the number of items in the average
				total += time; // Add the time to the total
			}
		}
		
		long average = 0; // Initial average
		
		if (number > 0) // Check that there was data in the array and that it won't be a division by 0
		{
			average = total / number; // Calculate the average
		}
		
		return average; // Return the average
	}
	
	public void addFailedValidation() // Increments the times failed validation counter
	{
		timesFailedValidation++;
	}
	
	public void addAttempt() // Increments the number of attempts counter
	{
		numberOfSuccessfulAttempts++;
	}
	
	public String getID()
	{
		return questionID;
	}
	
	public String toString()
	{
		return questionID + "," + timesFailedValidation + "," + numberOfAttemptsNeededToCorrectToString() + "," + timeTakenToCompleteToString() + "," + numberOfSuccessfulAttempts;
	}
	
	private String timeTakenToCompleteToString()
	{
		String output = timeTakenToComplete[0] + ""; // Take the first element and convert it to a string
		
		for (int i = 1; i < timeTakenToComplete.length; i++)
		{
			output += "." + timeTakenToComplete[i];
		}
		
		return output;
	}
	
	public void addTimeTakenToComplete(long timeTaken) // Adds the time is taken the user to complete the question to the array
	{
		// The array operates FIFO - first in first out.
		// Therefore all of the data items need to be copied one along and then the latest data stored at the front
		
		long[] newArray = new long[timeTakenToComplete.length];
		
		// Shuffle them all one across
		
		for (int i = 1; i < timeTakenToComplete.length; i++)
		{
			newArray[i] = timeTakenToComplete[i-1]; // Copy them across one further along than they were before - the oldest item gets discarded
		}
		
		newArray[0] = timeTaken; // Add the latest data at the start
		
		timeTakenToComplete = newArray; // Overwrite the old array
	}
	
	private long[] loadTimeTakenToCompleteArray(String loadedString)
	{
		String[] stringTimes = loadedString.split("\\.");
		
		long[] outputArray = new long[stringTimes.length]; // Create an int array of the same size
		
		for (int i = 0; i < outputArray.length; i++)
		{
			outputArray[i] = Long.parseLong(stringTimes[i]); // Convert each element to an int
		}
		
		return outputArray;
	}
	
	private String numberOfAttemptsNeededToCorrectToString()
	{
		String output = numberOfAttemptsNeededToCorrect[0] + ""; // Take the first element and convert it to a string
		
		for (int i = 1; i < numberOfAttemptsNeededToCorrect.length; i++)
		{
			output += "." + numberOfAttemptsNeededToCorrect[i];
		}
		
		return output;
	}
	
	public void addNumberOfAttemptsNeededToCorrect(int numberOfSuccessfulAttempts) // Adds the number of attempts it's taken a user to correct an error to the array
	{
		// The array operates FIFO - first in first out.
		// Therefore all of the data items need to be copied one along and then the latest data stored at the front
		
		int[] newArray = new int[numberOfAttemptsNeededToCorrect.length];
		
		// Shuffle them all one across
		
		for (int i = 1; i < numberOfAttemptsNeededToCorrect.length; i++)
		{
			newArray[i] = numberOfAttemptsNeededToCorrect[i-1]; // Copy them across one further along than they were before - the oldest item gets discarded
		}
		
		newArray[0] = numberOfSuccessfulAttempts; // Add the latest data at the start
		
		numberOfAttemptsNeededToCorrect = newArray; // Overwrite the old array
	}
	
	private int[] loadNumberOfAttemptsNeededToCorrectArray(String loadedString)
	{
		String[] stringAttempts = loadedString.split("\\.");
		
		int[] outputArray = new int[stringAttempts.length]; // Create an int array of the same size
		
		for (int i = 0; i < outputArray.length; i++)
		{
			outputArray[i] = Integer.parseInt(stringAttempts[i]); // Convert each element to an int
		}
		
		return outputArray;
	}
	
}