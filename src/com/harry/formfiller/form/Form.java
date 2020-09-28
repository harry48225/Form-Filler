package com.harry.formfiller.form;

import java.io.Serializable;

import com.harry.formfiller.gui.question.component.StringEscaper;
import com.harry.formfiller.question.Question;
import com.harry.formfiller.question.QuestionList;

public class Form implements Serializable
{
	/* All forms in the system are stored as form objects */
	
	private String id; // ID unique to each form
	
	private String[] questions; // Stores the ids of the questions that are in the form and any header text
	private Boolean[] requiredQuestions; // Stores whether each question is required or not
	
	// General information about the form
	private String title;
	
	private String description;
	
	private String[] mainSkillsTested;
	
	private int difficulty;
	
	public Form(FormBuilder builder)
	{
		/* Takes the builder and extracts all information to create a new form object */
		
		id = builder.id; // Store the id
		questions = builder.questions; // Get the questions added
		title = builder.title;
		description = builder.description;
		mainSkillsTested = builder.mainSkillsTested;
		difficulty = builder.difficulty;
		requiredQuestions = builder.requiredQuestions;
	}
	
	public Form(String loadedDataFromFile)
	{
		/* Loads a form from the data from the file */
		
		String[] data = loadedDataFromFile.split(","); // Split at the commas
	
		id = data[0]; // The first item is the id
		questions = data[1].split("\\."); // Questions are delimited by a .
		description = StringEscaper.unescape(data[2]); // Load the description and unescape it
		mainSkillsTested = data[3].split("\\."); // Skills are separated by .
		difficulty = Integer.parseInt(data[4]);
		title = data[5];
		requiredQuestions = loadRequiredQuestions(data[6].split("\\.")); // Required questions are separated by .
	}
	
	public Boolean isQuestionRequired(String questionID)
	{
		/* Takes a question id as input and returns whether the question is a required question in the form */
		Boolean required = false;
		
		// Search through the question list to find the index that the question is at in the form and look it up in the required questions array.
		for (int i = 0; i < questions.length; i ++ )
		{
			if (questions[i].equals(questionID)) {required = requiredQuestions[i];}	
		}
		
		return required;
	}
	
	public Boolean[] loadRequiredQuestions(String[] requiredQuestionsStringArray)
	{
		/* Takes the string array loaded from file and converts it to a boolean array */
		
		Boolean[] outputArray = new Boolean[requiredQuestionsStringArray.length];
		
		for (int i = 0; i < requiredQuestionsStringArray.length; i++)
		{
			outputArray[i] = Boolean.parseBoolean(requiredQuestionsStringArray[i]);
		}
		
		return outputArray;
	}
	
	public String[] getQuestionIDs()
	{
		/* Returns the question array */
		return questions;
	}
	
	private String arrayToString(Object[] array)
	{
		/* Converts an array to stri///ng */
		
		String arrayAsString = "";
		
		for   ( Object o : array)
		{
			arrayAsString += o + ".";
		}
		
		if (!arrayAsString.equals(""))
		{
			arrayAsString = arrayAsString.substring(0, arrayAsString.length() - 1); // Get rid of trailing .
		}
		
		return arrayAsString;
		
	}
	
	public String mainSkillsTestedToString()
	{
		// Returns the main skills tested as a string
		return arrayToString(mainSkillsTested);
	}
	
	public String toString()
	{
		/* Outputs attributes as an escaped String */
		
		return id + ","  + arrayToString(questions) + "," 
				+ StringEscaper.escape(description) + "," + arrayToString(mainSkillsTested) 
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
	
	public String[] toStringArray()
	{
		/* Outputs attributes as string array */
		
		String[] output = toString().split(",");
		output[2] = StringEscaper.unescape(output[2]); // Unescape the description so that it's displayed correctly to the user.
		return output;
	}


	public static class FormBuilder // Simplifies the creation of forms - allows forms to be created asynchonusly
	{
		private final String id; // Must be set in constructor
		private final QuestionList questionList;

		private String title = "";
		private String description = "";
		private String[] mainSkillsTested = new String[0];
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
			/* Takes the title, decription, and difficulty of the form and stores them */
			title = tempTitle;
			description = tempDescription;
			difficulty = tempDifficulty;
			
			return this;
		}
		
		public FormBuilder add(String questionID, Boolean required)
		{
			/* Add a question to the form */
			
			System.out.println("[INFO] <FORM_BUILDER> Running add"); // Debug
			
			// Add the question
			questions[nextQuestionLocation] = questionID;
			requiredQuestions[nextQuestionLocation] = required;
			nextQuestionLocation++;
			
			return this;
		}
		
		public FormBuilder remove(String questionID)
		{
			/* Removes a question from the form */
			
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
		
		public FormBuilder moveUp(String questionID)
		{
			/* Moves a question up in the form */
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
				
				// Swap the required data too
				boolean tempRequired = requiredQuestions[questionLocation-1];
				requiredQuestions[questionLocation-1] = requiredQuestions[questionLocation];
				requiredQuestions[questionLocation] = tempRequired;
			}
			
			return this;
		}
		
		public FormBuilder moveDown(String questionID)
		{
			/* Moves a question down in the form */
			
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
				
				// Swap the required data too
				boolean tempRequired = requiredQuestions[questionLocation+1];
				requiredQuestions[questionLocation+1] = requiredQuestions[questionLocation];
				requiredQuestions[questionLocation] = tempRequired;
			}
			
			return this;
		}
		public FormBuilder setRequired(String questionID, Boolean newRequiredStatus)
		{
			/* Takes a question id and a boolean and sets whether the question is required in the form */
			
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
		
		public Form build()
		{
			/* Builds a form */
			
			System.out.println("[INFO] <FORM_BUILDER> Running build");
			
			trimArray(); // Trim the array
			trimRequiredQuestionsArray(); // Trim the requried questions array
			
			getMainSkillsTested();
			
			return new Form(this);
		}
		
		private void getMainSkillsTested()
		{
			/* Gets the main skills tested */
			
			int nextUntrimmedLocation = 0;
			String[] unTrimmedMainSkillsTested = new String[questions.length];
			
			for (int i = 0; i < questions.length; i++)
			{
				Question question = questionList.getQuestionByID(questions[i]); // Get the question
				if (question != null) // If the question isn't null i.e. it's a question not a header
				{
					unTrimmedMainSkillsTested[nextUntrimmedLocation] = question.getType();
					nextUntrimmedLocation++;
				}
			}
			
			if (nextUntrimmedLocation != questions.length) // If there was at least one header in the form there will be null values we need to trim off
			{
				mainSkillsTested = new String[nextUntrimmedLocation];
				
 				for (int i = 0; i < nextUntrimmedLocation; i++)
	 			{
					mainSkillsTested[i] = unTrimmedMainSkillsTested[i];
				}
			}
			else
			{
				mainSkillsTested = unTrimmedMainSkillsTested;
			}
		}
		
		private void trimRequiredQuestionsArray()
		{
			/* Trims the array to the correct length so that there are no null elements */
			
			System.out.println("[INFO] <FORM_BUILDER> Running trimRequiredQuestionsArray");
		
			Boolean[] newArray = new Boolean[nextQuestionLocation]; // Create a new array of the correct size
			
			for (int i = 0; i < nextQuestionLocation; i++) // For each object in the array
			{
				newArray[i] = requiredQuestions[i]; // Copy the object
			}
			
			requiredQuestions =  newArray; // Store the new trimmed array in questions
		}
		
		private void trimArray()
		{
			/* Trims the array to the correct length so that there are no null elements */
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