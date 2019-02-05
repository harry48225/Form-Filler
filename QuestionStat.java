public class QuestionStat
{
	/* Every user has one of these for each question and it stores the stats about how they're performing on this question */

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
		/* Loads a question stat from it's split saveString */
	
		// Extract all of the data
		questionID = loadedData[0];
		timesFailedValidation = Integer.parseInt(loadedData[1]);
		numberOfAttemptsNeededToCorrect = loadNumberOfAttemptsNeededToCorrectArray(loadedData[2]);
		timeTakenToComplete = loadTimeTakenToCompleteArray(loadedData[3]);
		numberOfSuccessfulAttempts = Integer.parseInt(loadedData[4]);
	}
	
	private void initaliseArrays()
	{	
		/* Initalises the arrays that store the data about the user's attempts of the questions by setting all of the values to -1 */

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
		/* Returns the total number of times that the user has attempted the question */

		return numberOfSuccessfulAttempts + timesFailedValidation;
	}
	
	public int getNumberOfAttempts()
	{
		/* Returns the number of times the user has completed the question */

		return numberOfSuccessfulAttempts;
	}
	
	
	public int getTimesFailedValidation()
	{
		/* Returns the number of times that the user has failed the validation check of the question */
		return timesFailedValidation;
	}
	
	public int[] getNumberOfAttemptsNeededToCorrect()
	{
		/* Returns the array that contains the number of attempts it took the user to recently successfully complete the question */

		return numberOfAttemptsNeededToCorrect;
	}
	
	public int getAverageNumberOfAttemptsNeededToCorrect()
	{
		/* Returns the average number of attempts it takes the user to correct an error they've made while attempting the question */

		int number = 0; // Store the number of number of attempts - to calculate the average
		int total = 0;
		
		// Update the number and total for each attempt
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
		/* Returns the array of most recent times that it took the user to complete the question */
		return timeTakenToComplete;
	}
	
	public long getAverageTimeTakenToComplete()
	{
		/* Returns the average time that it takes the user to complete the question */

		int number = 0; // Store the number of times - to calculate the average
		long total = 0;
		
		// Update the number and total for each attempt
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
	
	public void addFailedValidation()
	{
		/* Increments the times failed validation counter */
		timesFailedValidation++;
	}
	
	public void addAttempt()
	{
		/* Increments the number of attempts counter */
		numberOfSuccessfulAttempts++;
	}
	
	public String getID()
	{
		/* Returns the id of the question that the question stat corresponds to */
		return questionID;
	}
	
	public String toString()
	{
		/* Returns a string that fully describes the question stat and can be used to recreate it */
		return questionID + "," + timesFailedValidation + "," + numberOfAttemptsNeededToCorrectToString() + "," + timeTakenToCompleteToString() + "," + numberOfSuccessfulAttempts;
	}
	
	private String timeTakenToCompleteToString()
	{
		/* Converts the time taken to complete array to a string delimited by . s */

		String output = timeTakenToComplete[0] + ""; // Take the first element and convert it to a string
			
		// Append each time to the output string
		for (int i = 1; i < timeTakenToComplete.length; i++)
		{
			output += "." + timeTakenToComplete[i];
		}
		
		return output;
	}
	
	public void addTimeTakenToComplete(long timeTaken)
	{
		/* Adds the time its taken the user to complete the question to the array */

		// The array operates FIFO - first in first out.
		// Therefore all of the data items need to be copied one along and then the latest data stored at the front
		
		// Create a new array of the same size
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
		/* Loads the time taken to complete array from its string */
		
		// The times are delimited by . so split at . to get an array of times
		String[] stringTimes = loadedString.split("\\.");
		
		// Create a new long array
		long[] outputArray = new long[stringTimes.length]; // Create a long array of the same size
			
		// Convert each string to a long and copy to the output array
		for (int i = 0; i < outputArray.length; i++)
		{
			outputArray[i] = Long.parseLong(stringTimes[i]); // Convert each element to a long
		}
		
		return outputArray;
	}
	
	private String numberOfAttemptsNeededToCorrectToString()
	{
		/* Converts the number of attempts array to a string */

		String output = numberOfAttemptsNeededToCorrect[0] + ""; // Take the first element and convert it to a string
			
		// Append each attempt to the output string
		for (int i = 1; i < numberOfAttemptsNeededToCorrect.length; i++)
		{
			output += "." + numberOfAttemptsNeededToCorrect[i];
		}
		
		return output;
	}
	
	public void addNumberOfAttemptsNeededToCorrect(int numberOfSuccessfulAttempts)
	{
		/* Adds the number of attempts it's taken a user to correct an error to the array */

		// The array operates FIFO - first in first out.
		// Therefore all of the data items need to be copied one along and then the latest data stored at the front
		
		// Create a new array of the same size
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
		/* Loads the number of attempts needed to correct array from its string */
		
		// The attempts are delimited by . so splitting at each . returns an array of attempts
		String[] stringAttempts = loadedString.split("\\.");
		
		int[] outputArray = new int[stringAttempts.length]; // Create an int array of the same size
		
		// Convert each string to an int and copy it to the output array
		for (int i = 0; i < outputArray.length; i++)
		{
			outputArray[i] = Integer.parseInt(stringAttempts[i]); // Convert each element to an int
		}
		
		return outputArray;
	}
	
}