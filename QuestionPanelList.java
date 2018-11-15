import java.io.*;

public class QuestionPanelList
{
	private String databaseFileName = "questions/QuestionPanelDB.ser"; 
	
	public QuestionPanel[] panels = new QuestionPanel[100]; // To store the question panels
	
	private int nextQuestionPanelLocation = 0; // The location to store the next question panel
	
	public QuestionPanelList()
	{
		loadDatabase(); // Load the database
	}
	
	public void removeQuestionPanel(String questionID)
	{
		System.out.println("[INFO] <QUESTION_PANEL_LIST> Running removeQuestionPanel");
		
		QuestionPanel[] newArray = new QuestionPanel[panels.length]; // Create a new array of the required size
		
		int j = 0; // The location in newArray
		
		for (int i = 0; i < nextQuestionPanelLocation; i++)
		{
			if (!panels[i].getQuestionID().equals(questionID)) // If the panel isn't the one that we don't want
			{
				newArray[j] = panels[i]; // Copy over the question panel
				j++;
			}
		}
		
		panels = newArray; // Overwrite the old array
		nextQuestionPanelLocation--; // There is one less questionPanel in the array so a free spot has opened
		
		writeDatabase(); // Save changes
	}
	
	public void writeDatabase()
	{
		System.out.println("[INFO] <QUESTION_PANEL_LIST> Running writeDatabase");
	
		try
		{
			FileOutputStream fileOut = new FileOutputStream(databaseFileName); // Create a file output stream with the correct path to the output file
			ObjectOutputStream out = new ObjectOutputStream(fileOut); // Create an object output stream from the file output stream
			
			out.writeObject(panels); // Write all of the panels to file
			
			out.close();
		}
		catch (IOException e)
		{
			System.out.println("[ERROR] <QUESTION_PANEL_LIST> Writing database " + e); // Print the error message
		}
	}
	
	public void loadDatabase() // Loads the question panels that are saved to file
	{
		System.out.println("[INFO] <QUESTION_PANEL_LIST> Running loadDatabase");
		
		try
		{
			FileInputStream fileIn = new FileInputStream(databaseFileName); // Create an input stream with the correct database
			ObjectInputStream in = new ObjectInputStream(fileIn); // Create an object input stream
			
			panels = (QuestionPanel[]) in.readObject(); // Read the QuestionPanel array from the file and store it
			
			in.close(); // Close the file
			
			fileIn.close();
		}
		catch(Exception e)
		{
			System.out.println("[ERROR] <QUESTION_PANEL_LIST> Error loading database " + e); // Print the error message
		}
		
		// Now we need to determine the number of panels in the question panel array to
		// avoid overwriting and to correctly set nextQuestionPanelLocation
		
		nextQuestionPanelLocation = 0;
		
		for (QuestionPanel qP : panels)
		{	
			if (qP != null) // If the space in the array is occupied
			{
				nextQuestionPanelLocation++; // Increment the nextQuestionPanelLocation as the space is taken
			}
			else // Space is free
			{
				break; // Stop incrementing
			}
		}
		
		System.out.println("[INFO] <QUESTION_PANEL_LIST> " + nextQuestionPanelLocation + " QuestionPanels loaded from file");
	}
	
	public void addQuestionPanel(QuestionPanel tempQuestionPanel)
	{
		System.out.println("[INFO] <QUESTION_PANEL_LIST> Running addQuestionPanel"); // Debug
		
		panels[nextQuestionPanelLocation] = tempQuestionPanel; // Add the question to the next free space in the array
		
		nextQuestionPanelLocation++; // Increment the location
	}
	
	public QuestionPanel getByID(String questionID)
	{
		QuestionPanel result = null;
		
		System.out.println("[INFO] <QUESTION_PANEL_LIST> Running getByID");
		
		for (int i = 0; i < nextQuestionPanelLocation; i++) // For each question panel in the database
		{
			if (panels[i].getQuestionID().equals(questionID)) // If we've found the correct question panel
			{
				result = panels[i];
				break; // Stop searching
			}
		}
		
		return result;
	}
	
}