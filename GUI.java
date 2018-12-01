import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GUI extends JFrame implements ChangeListener// Main GUI class
{
	private QuestionList questions; // The question list
	
	private FormList forms;
	
	private UserList users;
	
	private User currentUser;
	
	private FormsInProgressList formsInProgress;
	
	private JTabbedPane tabs = new JTabbedPane(); // To store the different sections of the program
	
	public GUI() // Constructor
	{
		setup(); // Run the setup method
	}	
	
	private void setup()
	{
		System.out.println("[INFO] <GUI> Running setup"); // Debug
		
		//this.addWindowListener(this);
		
		questions = new QuestionList(); // Create a new question list
		
		forms = new FormList();
		
		users = new UserList();
		
		currentUser = getUser();
		
		currentUser.addPresentToday(); // Log the user's attendance
			
		users.writeDatabase(); // Write the database to file
		
		formsInProgress = new FormsInProgressList(currentUser.getUsername());
		
		prepareGUI();
		
	}
	
	private User getUser()
	{
		String[] usernames = new String[users.getUsers().length];
		
		for (int i = 0; i < usernames.length; i++)
		{
			usernames[i] = users.getUsers()[i].getUsername();
		}
		
		return users.getUserByUsername((String)JOptionPane.showInputDialog(
                    null,
                    "Please select a user",
                    "User?",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    usernames,
                    usernames[0])); // Show the user a dropdown with all the current users
	}
	
	private void prepareGUI()
	{
		this.setTitle("Form Filler");
		
		this.setSize(1200,700);
		this.setLocation(500,200);
		this.setMinimumSize(new Dimension(900,600));
		this.setLayout(new GridLayout(1,1)); // Only 1 row and 1 column as it'll only store panels
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		tabs.add("View Questions", new QuestionDisplayPanel(questions, this, currentUser.isAdmin()));
		tabs.add("View Forms", new FormDisplayPanel(forms, this, questions, formsInProgress,currentUser.isAdmin()));
		tabs.add("Create questions", new QuestionCreationPanel(questions, this));
		tabs.add("Create forms", new FormCreationPanel(questions, forms, this));
		tabs.add("Users", new UserPanel(users));
		tabs.add("Statistics", new StatisticsPanel(currentUser, questions));
		tabs.add("Import and Export", new ImportExportPanel(questions, forms));
		
		tabs.addChangeListener(this);
		
		this.add(tabs);
		
		this.setVisible(true);
	}
	
	// public void openQuestion() Good idea?
	
	public void openForm(Form f) // Takes a form and opens it for attempting
	{
		QuestionPanel[] formQuestionPanels;
		
		System.out.println("[INFO] <GUI> Running openForm"); // Debug
		
		if (formsInProgress.isFormPresent(f.getID())) // If the form is in the formsInProgressList
		{
			System.out.println("[INFO] <GUI> Loading saved form");
			formQuestionPanels = formsInProgress.getByID(f.getID()).getQuestionPanels(); // Get the saved formQuestionPanels
		}
		else // The form is not in the forms in progress list
		{
			System.out.println("[INFO] <GUI> Starting new form");
			// Load the question panels from the questionPanels database
			String[] questionIDs = f.getQuestionIDs();
			
			formQuestionPanels = new QuestionPanel[questionIDs.length];
			
			for (int i = 0; i < formQuestionPanels.length; i++)
			{
				formQuestionPanels[i] = questions.getPanelByID(questionIDs[i]); // Add the relevant question panel to the formQuestionPanels array
			}
			
			formsInProgress.addFormInProgress(new FormInProgress(f.getID(), 0, formQuestionPanels)); // Add the form in progress
			
		}
		
		new FormDisplayer(f, formQuestionPanels, currentUser, users, this); // Open the form
		
	}
	
	public void attemptFormFromUserWeaknesses() 
	{
		String[] questionIDs = currentUser.getQuestionStats().getQuestionsStruggleTheMost(questions); // Get the questions that the user struggles with the most
		
		Form.FormBuilder questionForm = new Form.FormBuilder("USER_WEAKNESSES", questions);
		
		for (String questionID : questionIDs) // Add each question to the form
		{
			questionForm = questionForm.add(questionID, true);
		}
		
		openForm(questionForm.build()); // Open the form to be attempted
	}
	
	public void saveForm(Form f, int percentComplete)
	{
		System.out.println("[INFO] <GUI> Running saveForm"); // Debug
		formsInProgress.getByID(f.getID()).setPercentComplete(percentComplete);
		formsInProgress.writeDatabase();
		System.out.println("[INFO] <GUI> Form saved"); // Debug
		
	}
	
	public void stateChanged(ChangeEvent changeEvent)
	{
		JTabbedPane sourcePane = (JTabbedPane) changeEvent.getSource();
		Component selectedComponent = sourcePane.getSelectedComponent();
		
		if (selectedComponent instanceof QuestionDisplayPanel)
		{
			QuestionDisplayPanel qDP = (QuestionDisplayPanel) selectedComponent;
			 qDP.refreshTable(); // Refresh the table
		}
		else if (selectedComponent instanceof FormDisplayPanel)
		{
			FormDisplayPanel fDP = (FormDisplayPanel) selectedComponent;
			fDP.refreshTable(); // Refresh the table
		}
		else if (selectedComponent instanceof StatisticsPanel)
		{
			StatisticsPanel sP = (StatisticsPanel) selectedComponent;
			sP.refresh(); // Refresh the panel
		}
		else if (selectedComponent instanceof FormCreationPanel)
		{
			FormCreationPanel fC = (FormCreationPanel) selectedComponent;
			fC.refreshTable(); // Refresh the table.
		}
		
	}
	
	public void resetTab(JComponent componentToReset) // Creates a new component in the old ones place used for resetting the question creation panel
	{
		int index = tabs.indexOfComponent(componentToReset);
		
		if (componentToReset instanceof QuestionCreationPanel)
		{
			// Add a new question creation panel at the index of the old one
			tabs.setComponentAt(index, new QuestionCreationPanel(questions, this));
		}
		if (componentToReset instanceof FormCreationPanel)
		{
			// Add a new form creation panel at the index of the old one
			tabs.setComponentAt(index, new FormCreationPanel(questions, forms, this));
		}
	}
}