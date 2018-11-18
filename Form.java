import java.util.*;
import java.io.*;

public class Form implements Serializable
{
	private String id; // ID unique to each form
	
	private String[] questions; // Stores the ids of the questions that are in the form
	
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
	}
	
	public String[] getQuestionIDs() // Returns the question array
	{
		return questions;
	}
	
	private String questionArrayToString() // Converts the question array to string
	{
		String arrayAsString = "";
		
		for (String q : questions)
		{
			arrayAsString += q + ".";
		}
		
		arrayAsString = arrayAsString.substring(0, arrayAsString.length() - 1); // Get rid of trailing .
		
		return arrayAsString;
		
	}
	
	private String mainSkillsTestedToString() // Converts the skills array to string
	{
		String arrayAsString = "";
		
		for (String s : mainSkillsTested)
		{
			arrayAsString += s + ".";
		}
		
		arrayAsString = arrayAsString.substring(0, arrayAsString.length() - 1); // Get rid of trailing .
		
		return arrayAsString;
	}
	
	public String toString() // Outputs attributes as String
	{
		return id + ","  + questionArrayToString() + "," + description + "," + mainSkillsTestedToString() + "," + difficulty + "," + title; 
	}
	
	public String getID()
	{
		return id;
	}
	
	public String getTitle()
	{
		return title;
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
		
		public FormBuilder add(String questionID) // Add a question to the form
		{
			System.out.println("[INFO] <FORM_BUILDER> Running add"); // Debug
			
			questions[nextQuestionLocation] = questionID; // Add the question
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
			}
			
			return this;
		}
		
		public Form build() // Builds a form
		{
			System.out.println("[INFO] <FORM_BUILDER> Running build");
			
			trimArray(); // Trim the array

			mainSkillsTested = new String[questions.length];

			for (int i = 0; i < questions.length; i++)
			{
				mainSkillsTested[i] = questionList.getQuestionByID(questions[i]).getType(); // Store the type of the question in the main skills tested array
			}
			
			return new Form(this);
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