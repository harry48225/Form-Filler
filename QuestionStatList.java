import java.util.*;

public class QuestionStatList // Every user has one and it stores the QuestionStat objects
{
	// This list class works a little differently. It doesn't actually manipulate text files itself.

	private HashMap<String, QuestionStat> questionStatMap = new HashMap<String, QuestionStat>();
	
	public QuestionStatList()
	{
		return;
	}
	
	public QuestionStatList(String loadedDatabase)
	{
		// The database is delimited by |.
		
		String[] questionStats = loadedDatabase.split("\\|"); // \\ to escape
		
		for (String questionStatString : questionStats) // For each string
		{
			add(new QuestionStat(questionStatString.split(","))); // Add the questionStat to the database
		}
		
	}
	
	public String[] getIDArray() // Returns an array containing all of the ids of the questions
	{
		return questionStatMap.keySet().toArray(new String[0]);
	}
	
	public String[] getQuestionsStruggleTheMost(QuestionList questions) // Returns a list of questions that the user sturggles the most on
	{
		QuestionStat[] questionsToSort = questionStatMap.values().toArray(new QuestionStat[0]); // Create a new questionStat array of just the right size

		// Bubble sort based on the average number of attempts taken to correct an error highest to lowest
		
		boolean swapped = true; // Toggle that contains whether a value has been swapped
		
		while (swapped == true) // Until no more swaps are made
		{
			
			swapped = false; // Set swapped to false
			
			for (int i = 0; i < questionsToSort.length -1; i++) // For all questions but the last one
			{
				// Descending sort, highest - lowest
				if (questionsToSort[i].getAverageNumberOfAttemptsNeededToCorrect() < questionsToSort[i+1].getAverageNumberOfAttemptsNeededToCorrect()) // Check to see if it's less than the one after
				{
					QuestionStat temp = questionsToSort[i+1]; // Store the value in a temp variable
					questionsToSort[i + 1] = questionsToSort[i]; // Swap the values
					questionsToSort[i] = temp; // Swap
					
					swapped = true; // Set swapped to true as a swap was made
				}
			}
		}
		
		// Array should now be in order highest to lowest
		
		// Some question stats may not have questions attached to them
		
		String[] questionIDs = new String[3]; // Store the 3 hardest questions
		int nextQuestionIDLocation = 0;
		
		for (int i = 0; i < questionsToSort.length; i++) // For each question
		{
			String questionID = questionsToSort[i].getID();
			
			if (questions.getQuestionByID(questionID) != null) // If the question exists
			{
				questionIDs[nextQuestionIDLocation] = questionID; // Store the question id in the array
				nextQuestionIDLocation ++;
				
				if (nextQuestionIDLocation > 2) // If the array is full
				{
					break; // Break as no need to add any more questions
				}
			}
		}
		
		return questionIDs; // Return the array
		
	}
	
	public String[][] produceReport(QuestionList questions) // Produces a report
	{
		System.out.println("[INFO] <QUESTION_STAT_LIST> Running produceReport");
		
		String[] types = questions.getTypes(); // Get all the types of questions
		
		String[][] data = new String[types.length][]; // Create a new 2d array a row for each type
		
		for (int i = 0; i < types.length; i++) // For each type of question
		{
			String currentType = types[i]; // Get the current type
			
			int totalTimesFailedValidation = 0; // Start from zero	
	
	
			// For calculating averages
			int numberOfQuestions = 0;
			int totalNumberOfAttemptsNeededToCorrect = 0; // Store the total number of attempts needed to correct
			long totalTimeTakenToComplete = 0;
			
			for (QuestionStat currentQuestionStat : questionStatMap.values()) // For each question stat
			{
				Question currentQuestion = questions.getQuestionByID(currentQuestionStat.getID()); // Get the question that it corresponds to
				
				if (currentQuestion != null) // If a question was found and the question stat wasn't part of a deleted question
				{
					String questionType = currentQuestion.getType(); // Get the type of the current question
					
					if (questionType.equals(currentType)) // If the question is of the correct type
					{
						numberOfQuestions++; // Increment the number of questions
						totalTimesFailedValidation += currentQuestionStat.getTimesFailedValidation(); // Add the number of times failed validation to the total
						totalNumberOfAttemptsNeededToCorrect += currentQuestionStat.getAverageNumberOfAttemptsNeededToCorrect();
						totalTimeTakenToComplete += currentQuestionStat.getAverageTimeTakenToComplete();
						
					}
				}
			}
			
			String[] rowData = new String[4]; // Array to store the data to do with this type of question 
			
			rowData[0] = currentType; // Store the question type
			
			if (numberOfQuestions > 0) // If the question type has been attempted at least once
			{
				int averageNumberOfAttemptsToCorrect = totalNumberOfAttemptsNeededToCorrect / numberOfQuestions; // Calculate the average
				long averageTimeTakenToComplete = totalTimeTakenToComplete / numberOfQuestions;
				//System.out.println(currentType + " Times failed validation: " + totalTimesFailedValidation + " Average # attempts to correct: "  + averageNumberOfAttemptsToCorrect + " Average time: " + averageTimeTakenToComplete);
				
				// Add the data to the array
				rowData[1] = totalNumberOfAttemptsNeededToCorrect + "";
				rowData[2] = averageNumberOfAttemptsToCorrect + "";
				rowData[3] = averageTimeTakenToComplete + "";
				
			}
			else // The question hasn't been attempted at least one
			{
				// Store a blank row
				rowData[1] = "";
				rowData[2] = "";
				rowData[3] = "";
			}
			
			data[i] = rowData; // Store the data from the row
		}	
		
		return data;
	}
	
	// There should be no add method only getters. And if the question isn't present it gets created automatically.
	private void add(QuestionStat newQStat) // Private as should only be used when loading the database
	{
		questionStatMap.put(newQStat.getID(), newQStat);
	}
	
	public QuestionStat getQuestionStatByID(String searchID)
	{
		questionStatMap.putIfAbsent(searchID, new QuestionStat(searchID));
		
		return questionStatMap.get(searchID);		
	}
	
	
	public String toString() // Returns a long string of all the questions stats object delimited by |
	{
		String outputString = "";
		
		for (QuestionStat q : questionStatMap.values())
		{
			outputString += (q.toString() + "|");
		}
		
		return outputString;
	}
	
}