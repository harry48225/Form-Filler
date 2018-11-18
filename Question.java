import java.util.*;
import java.io.*;

public class Question implements Serializable
{
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
	
	public int getDifficulty() // Getter for difficulty
	{
		return difficulty; // Return the difficulty
	}
	
	public String getType() // Getter for type
	{
		return type; // Return the type
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String toString() // Returns a string of the attributes
	{
		return id + "," + difficulty + "," + type + "," + title; // Return a string of the attributes concatenated
	}
	
	public String[] toStringArray() // Returns a string array
	{
		return toString().split(","); // Return a string array of the attributes by splitting the output of to string at the comma
	}

	public String getID()
	{
		return id;
	}
	
}