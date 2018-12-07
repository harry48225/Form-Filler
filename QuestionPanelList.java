import java.io.*;

public class QuestionPanelList
{
	private String databaseFileName = "questions/QuestionPanelDB.txt"; 
	
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
			FileWriter fw = new FileWriter(databaseFileName);
			
			for (int i = 0; i < nextQuestionPanelLocation; i++) // For each Question in the array
			{
				String currentPositionQuestionData = panels[i].toString(); // Get the attribute string
				
				fw.write(currentPositionQuestionData); // Write the data
				
				fw.write("\r\n"); // Go to a new line
			}
			
			fw.close(); // Close the file
		}
		catch (IOException e)
		{
			System.out.println("[ERROR] <QUESTION_PANEL_LIST> Writing database " + e); // Print the error message
		}
	}
	
	public void loadDatabase() // Loads the question panels that are saved to file
	{
		System.out.println("[INFO] <QUESTION_PANEL_LIST> Running loadDatabase");
		
		nextQuestionPanelLocation = 0; // Start at the beginning of the array
		
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(databaseFileName)); // Open the database file
			
			String line = br.readLine(); // Read line from the file
			
			while (line != null)
			{
				addQuestionPanel(new QuestionPanel(line)); // Load the question panel
				
				line = br.readLine();
			}
		}
		catch(Exception e)
		{
			System.out.println("[ERROR] <QUESTION_PANEL_LIST> Error loading database " + e); // Print the error message
			e.printStackTrace();
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
		
		return result.clone();
	}
	
}