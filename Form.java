import java.util.*;
import java.io.*;

public class Form implements Serializable
{
	private String id; // ID unique to each form
	
	private String[] questions; // Stores the ids of the questions that are in the form
	private Boolean[] requiredQuestions; // Stores whether each question is required or not
	
	private String title;
	
	private String description;
	
	private String[] mainSkillsTested;
	
	private int difficulty;
	
	public Form(FormBuilder builder) // Takes the builder
	{
		id = builder.id; // Store the id
		questions = builder.questions; // Get the questions added
		title = builder.title;
		description = builder.description;
		mainSkillsTested = builder.mainSkillsTested;
		difficulty = builder.difficulty;
		requiredQuestions = builder.requiredQuestions;
	}
	
	public Form(String loadedDataFromFile) // Loads a form from the data from the file
	{
		String[] data = loadedDataFromFile.split(","); // Split at the commas
	
		id = data[0]; // The first item is the id
		questions = data[1].split("\\."); // Questions are delimited by a .
		description = data[2];
		mainSkillsTested = data[3].split("\\.");
		difficulty = Integer.parseInt(data[4]);
		title = data[5];
		requiredQuestions = loadRequiredQuestions(data[6].split("\\."));
	}
	
	public Boolean isQuestionRequired(String questionID)
	{
		Boolean required = false;
		
		for (int i = 0; i < questions.length; i ++ )
		{
			if (questions[i].equals(questionID)) {required = requiredQuestions[i];}	
		}
		
		return required;
	}
	
	public Boolean[] loadRequiredQuestions(String[] requiredQuestionsStringArray)
	{
		Boolean[] outputArray = new Boolean[requiredQuestionsStringArray.length];
		
		for (int i = 0; i < requiredQuestionsStringArray.length; i++)
		{
			outputArray[i] = Boolean.parseBoolean(requiredQuestionsStringArray[i]);
		}
		
		return outputArray;
	}
	
	public String[] getQuestionIDs() // Returns the question array
	{
		return questions;
	}
	
	private String arrayToString(Object[] array) // Converts an array to string
	{
		String arrayAsString = "";
		
		for (Object o : array)
		{
			arrayAsString += o + ".";
		}
		
		if (arrayAsString != "")
		{
			arrayAsString = arrayAsString.substring(0, arrayAsString.length() - 1); // Get rid of trailing .
		}
		
		return arrayAsString;
		
	}
	
	public String mainSkillsTestedToString()
	{
		return arrayToString(mainSkillsTested);
	}
	
	public String toString() // Outputs attributes as String
	{
		return id + ","  + arrayToString(questions) + "," 
				+ description + "," + arrayToString(mainSkillsTested) 
				+ "," + difficulty + "," + title + "," + arrayToString(requiredQuestions); 
	}
	
	public String getID()
	{
		return id;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public int getDifficulty()
	{
		return difficulty;
	}

	public String[] getTypes()
	{
		return mainSkillsTested;
	}
	
	public String[] toStringArray() // Outputs attributes as string array
	{
		return toString().split(",");
	}


	public static class FormBuilder // Simplifies the creation of forms
	{
		private final String id; // Must be set in constructor
		private final QuestionList questionList;

		private String title;
		private String description;
		private String[] mainSkillsTested;
		private int difficulty; 
		
		private String[] questions = new String[50]; // Store 50 questions
		private Boolean[] requiredQuestions = new Boolean[50];
		
		private int nextQuestionLocation = 0;
		
		public FormBuilder(String tempID, QuestionList tempQuestions)
		{
			id = tempID;
			questionList = tempQuestions;
		}
		
		public FormBuilder setFinalDetails(String tempTitle, String tempDescription, int tempDifficulty)
		{
			title = tempTitle;
			description = tempDescription;
			difficulty = tempDifficulty;
			
			return this;
		}
		
		public FormBuilder add(String questionID, Boolean required) // Add a question to the form
		{
			System.out.println("[INFO] <FORM_BUILDER> Running add"); // Debug
			
			// Add the question
			questions[nextQuestionLocation] = questionID;
			requiredQuestions[nextQuestionLocation] = required;
			nextQuestionLocation++;
			
			return this;
		}
		
		public FormBuilder remove(String questionID) // Removes a question from the form
		{
			System.out.println("[INFO] <FORM_BUILDER> Running remove"); // Debug
			
			String[] newArray = new String[questions.length]; // Create a new array of the required size
		
			int j = 0; // The location in newArray
			
			for (int i = 0; i < nextQuestionLocation; i++)
			{
				if (!questions[i].equals(questionID)) // If the question isn't the one that we don't want
				{
					newArray[j] = questions[i];
					j++;
				}
			}
		
			questions = newArray; // Overwrite the old array
			nextQuestionLocation--; // There is one less question in the array so a free spot has opened
			
			return this;
		}
		
		public FormBuilder moveUp(String questionID) // Moves a question up
		{
			System.out.println("[INFO] <FORM_BUILDER> Running moveUp"); // Debug
			int questionLocation = -1;
			
			for (int i = 0; i < nextQuestionLocation; i++) // Iterate over the questions
			{
				if (questions[i].equals(questionID)) // If we've found the required question
				{
					questionLocation = i;
					break;
				}
			}
			
			if (questionLocation > 0) // If there is room to move it up
			{
				// Swap the questions
				String temp = questions[questionLocation - 1]; // Store the question above it
				questions[questionLocation - 1] = questions[questionLocation]; // Move it up
				questions[questionLocation] = temp; // Move the one above it into the now free space
				
				boolean tempRequired = requiredQuestions[questionLocation-1];
				requiredQuestions[questionLocation-1] = requiredQuestions[questionLocation];
				requiredQuestions[questionLocation] = tempRequired;
			}
			
			return this;
		}
		
		public FormBuilder moveDown(String questionID) // Moves a question down
		{
			System.out.println("[INFO] <FORM_BUILDER> Running moveDown"); // Debug
			int questionLocation = -1;
			
			for (int i = 0; i < nextQuestionLocation; i++) // Iterate over the questions
			{
				if (questions[i].equals(questionID)) // If we've found the required question
				{
					questionLocation = i;
					break;
				}
			}
			
			if (questionLocation < nextQuestionLocation - 1) // If there is room to move it down
			{
				// Swap the questions
				String temp = questions[questionLocation + 1]; // Store the question below it
				questions[questionLocation + 1] = questions[questionLocation]; // Move it down
				questions[questionLocation] = temp; // Move the one below it into the now free space
				
				boolean tempRequired = requiredQuestions[questionLocation+1];
				requiredQuestions[questionLocation+1] = requiredQuestions[questionLocation];
				requiredQuestions[questionLocation] = tempRequired;
			}
			
			return this;
		}
		public FormBuilder setRequired(String questionID, Boolean newRequiredStatus)
		{
			System.out.println("[INFO] <FORM_BUILDER> Running setRequired");
			
			for (int i = 0; i < nextQuestionLocation; i++) // Iterate over the questions
			{
				if (questions[i].equals(questionID)) // If we've found the required question
				{
					requiredQuestions[i] = newRequiredStatus;
					break;
				}
			}
			return this;
		}
		
		public Form build() // Builds a form
		{
			System.out.println("[INFO] <FORM_BUILDER> Running build");
			
			trimArray(); // Trim the array
			trimRequiredQuestionsArray(); // Trim the requried questions array
			
			getMainSkillsTested();
			
			return new Form(this);
		}
		
		private void getMainSkillsTested() // Gets the main skills tested
		{
			int nextUnTrimmedLocation = 0;
			String[] unTrimmedMainSkillsTested = new String[questions.length];
			
			for (int i = 0; i < questions.length; i++)
			{
				Question question = questionList.getQuestionByID(questions[i]); // Get the question
				if (question != null) // If the question isn't null i.e. it's a question not a header
				{
					unTrimmedMainSkillsTested[nextUnTrimmedLocation] = question.getType();
					nextUnTrimmedLocation++;
				}
			}
			
			if (nextUnTrimmedLocation != questions.length) // If there was at least one header in the form there will be null values we need to trim off
			{
				mainSkillsTested = new String[nextUnTrimmedLocation];
				
				for (int i = 0; i < nextUnTrimmedLocation; i++)
				{
					mainSkillsTested[i] = unTrimmedMainSkillsTested[i];
				}
			}
			else
			{
				mainSkillsTested = unTrimmedMainSkillsTested;
			}
		}
		
		private void trimRequiredQuestionsArray() // Trims the array to the correct length so that there are no null elements
		{
			System.out.println("[INFO] <FORM_BUILDER> Running trimRequiredQuestionsArray");
		
			Boolean[] newArray = new Boolean[nextQuestionLocation]; // Create a new array of the correct size
			
			for (int i = 0; i < nextQuestionLocation; i++) // For each object in the array
			{
				newArray[i] = requiredQuestions[i]; // Copy the object
			}
			
			requiredQuestions = newArray; // Store the new trimmed array in questions
		}
		
		private void trimArray() // Trims the array to the correct length so that there are no null elements
		{
			System.out.println("[INFO] <FORM_BUILDER> Running trimArray");
		
			String[] newArray = new String[nextQuestionLocation]; // Create a new array of the correct size
			
			for (int i = 0; i < nextQuestionLocation; i++) // For each object in the array
			{
				newArray[i] = questions[i]; // Copy the object
			}
			
			questions = newArray; // Store the new trimmed array in questions
		}
	}
}