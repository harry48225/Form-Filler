import java.io.*;

public class FormsInProgressList
{
	private String databaseFileName = "FormsInProgessDB.txt";
	private String databasePath;
	private FormInProgress[] formsInProgressArray = new FormInProgress[100];
	
	private int nextFormInProgressLocation = 0;
	
	public FormsInProgressList(String username)
	{
		databasePath = "formsInProgress/" + username; // Create the path
		loadDatabase();
	}
	
	public FormInProgress[] getArray()
	{
		return formsInProgressArray;
	}
	
	public boolean isFormPresent(String searchFormID) // Checks to see if a form is in the array
	{
		return getByID(searchFormID) != null; // Returns true if when getting the form it doesn't return null i.e. the form is present
	}
	
	public FormInProgress getByID(String searchID)
	{
		FormInProgress result = null;
		
		for (int i = 0; i < nextFormInProgressLocation; i++) // For each formInProgress in the array
		{
			FormInProgress form = formsInProgressArray[i];
			
			if (form.getFormID().equals(searchID)) // If the correct form has been found
			{
				result = form;
				break;
			}
		}
		
		return result;
		
	}
	
	public void addFormInProgress(FormInProgress formInProgressToAdd)
	{
		formsInProgressArray[nextFormInProgressLocation] = formInProgressToAdd; // Add the form at the next free location
		
		nextFormInProgressLocation++; 
	}
	
	public void writeDatabase()
	{
		System.out.println("[INFO] <FORMS_IN_PROGRESS_LIST> Running writeDatabase");
		
		// It can't certain that the directories exist
		File directory = new File(databasePath);
		
		if (!directory.exists()) // If the directory doesn't exist
		{
			directory.mkdirs(); // Make any required directories
		}
		
		try
		{
			FileWriter fw = new FileWriter(databaseFileName);
			
			for (int i = 0; i < nextFormInProgressLocation; i++) // For each form in the array
			{
				String currentPositionFormData = formsInProgressArray[i].toString(); // Get the attribute string
				
				fw.write(currentPositionFormData); // Write the data
				
				fw.write("\r\n"); // Go to a new line
			}
			
			fw.close(); // Close the file
		}
		catch (IOException e)
		{
			System.out.println("[ERROR] <FORMS_IN_PROGRESS_LIST> Writing database " + e); // Print the error message
			e.printStackTrace();
		}
	}
	
	private void loadDatabase()
	{
		/*
		System.out.println("[INFO] <FORMS_IN_PROGRESS_LIST> Running loadDatabase");
		
		try
		{
			FileInputStream fileIn = new FileInputStream(databasePath + "/" + databaseFileName); // Create an input stream with the correct class
			ObjectInputStream in = new ObjectInputStream(fileIn); // Create an object input stream
			
			formsInProgressArray = (FormInProgress[]) in.readObject(); // Read the formsInProgress array from the file and store it
			
			in.close(); // Close the file
			
			fileIn.close();
		}
		catch(Exception e)
		{
			System.out.println("[ERROR] <FORMS_IN_PROGRESS_LIST> Error loading database " + e); // Print the error message
			e.printStackTrace();
		}
		
		// Now we need to determine the number of formsInProgress objects in the array to
		// avoid overwriting and to correctly set nextFormInProgressLocation
		
		nextFormInProgressLocation = 0;
		
		for (FormInProgress fP : formsInProgressArray)
		{	
			if (fP != null) // If the space in the array is occupied
			{
				nextFormInProgressLocation++; // Increment the nextFormInProgressLocation as the space is taken
			}
			else // Space is free
			{
				break; // Stop incrementing
			}
		}
		
		System.out.println("[INFO] <FORMS_IN_PROGRESS_LIST> " + nextFormInProgressLocation + " formsInProgress loaded from file");
		*/
	}
}