package com.harry.formfiller.question;

import java.io.Serializable;

public class Question implements Serializable
{
	/* All questions in the system are stored as Question objects,
		this stores all of the non-graphical information about a question. */
	
	private String id;
	
	private int difficulty = -1; // The difficulty of the question 1 - 10
	private String type = ""; // The type of the question
	private String title = ""; // The title of the question 
	
	public Question(String tempID, int tempDiff, String tempType, String tempTitle) // Constructor
	{
		id = tempID; // Store the ID
		difficulty = tempDiff; // Store the difficulty
		type = tempType; // Store the type
		title = tempTitle; // Store the title
	}
	
	public Question(String saveString)
	{
		/* Loads a question from its saveString */
		
		// All of the information in a question's save string is delimited by a ,
		String[] splitQuestionData = saveString.split(",");
		
		id = splitQuestionData[0];
		difficulty = Integer.parseInt(splitQuestionData[1]); // Convert the first part to an integer
		type = splitQuestionData[2]; // Get the type
		title = splitQuestionData[3];

	}
	
	public int getDifficulty()
	{
		/* Returns the question's difficulty */
		return difficulty; // Return the difficulty
	}
	
	public String getType()
	{
		/* Returns the question's type */
		return type; // Return the type
	}
	
	public String getTitle()
	{
		/* Returns the question's title */
		return title; // Return the title
	}
	
	public String toString()
	{
		/* Returns a string of the attributes that can be used to recreate the question */

		return id + "," + difficulty + "," + type + "," + title; // Return a string of the attributes concatenated
	}
	
	public String[] toStringArray()
	{
		/* Returns a string array of the question's attributes */

		return toString().split(","); // Return a string array of the attributes by splitting the output of to string at the comma
	}

	public String getID()
	{
		/* Return the question's id */
		return id; // Return the id
	}
	
}