import components.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import java.io.*;

import java.net.URL;

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
		
		System.out.println(formsInProgress);
		
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
	
	private List<Image> getIcons()
	{
		List<Image> images = new ArrayList<Image>();
		
		images.add(new ImageIcon("icons/icon-12.png").getImage());
		images.add(new ImageIcon("icons/icon-16.png").getImage());
		images.add(new ImageIcon("icons/icon-24.png").getImage());
		images.add(new ImageIcon("icons/icon-48.png").getImage());
		images.add(new ImageIcon("icons/icon-96.png").getImage());
		images.add(new ImageIcon("icons/icon-240.png").getImage());
		
		return images;
	}
	private void prepareGUI()
	{
		this.setTitle("Form Filler");
		
		this.setIconImages(getIcons());
		
		this.setSize(1200,700);
		this.setLocation(500,200);
		this.setMinimumSize(new Dimension(900,600));
		this.setLayout(new GridLayout(1,1)); // Only 1 row and 1 column as it'll only store panels
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		tabs.add("Main menu", new MainMenuPanel(currentUser, this));
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
		JPanel[] formComponents;
		
		System.out.println("[INFO] <GUI> Running openForm"); // Debug
		
		if (formsInProgress.isFormPresent(f.getID())) // If the form is in the formsInProgressList
		{
			FormInProgress fP = formsInProgress.getByID(f.getID());
			// If the form is not fully completed
			if (fP.getPercentComplete() != 100)
			{
				System.out.println("[INFO] <GUI> Loading saved form");
			}
			else // We should reset the form in progress
			{
				System.out.println("[INFO] <GUI> Resetting form");
				
				fP.setFormComponents(buildFormComponentArray(f));
			}
			
			formComponents = formsInProgress.getByID(f.getID()).getQuestionPanels(); // Get the saved formQuestionPanels
		}
		else // The form is not in the forms in progress list
		{
			System.out.println("[INFO] <GUI> Starting new form");
			
			formComponents = buildFormComponentArray(f);
			
			formsInProgress.addFormInProgress(new FormInProgress(f.getID(), 0, formComponents, 0)); // Add the form in progress
			
		}
		
		new FormDisplayer(f, formComponents, currentUser, users, this, questions); // Open the form
		
	}
	
	private JPanel[] buildFormComponentArray(Form f)
	{
		JPanel[] formComponents;
		// Load the question panels from the questionPanels database
		String[] questionIDs = f.getQuestionIDs();
		
		formComponents = new JPanel[questionIDs.length];
		
		for (int i = 0; i < formComponents.length; i++)
		{
			// Determine whether the component is a question or a header
			boolean isAQuestion = questions.getQuestionByID(questionIDs[i]) != null;
			
			if (isAQuestion)
			{
				JPanel questionPanel = questions.getPanelByID(questionIDs[i]);
				formComponents[i]  = questionPanel; // Add the question panel to the array
			}
			else // It's a header
			{
				JPanel headerPanel = new HeaderPanel(questionIDs[i]);

				formComponents[i] = headerPanel; // Add the header to the page
			}
		}
		
		return formComponents;
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
		
		System.out.println(f);
		System.out.println(percentComplete);
		
		FormInProgress fP = formsInProgress.getByID(f.getID());
		fP.setPercentComplete(percentComplete);
		
		if (percentComplete == 100) // If the user fully completed the form
		{
			fP.addTimesCompleted();
		}
		
		formsInProgress.writeDatabase();
		System.out.println("[INFO] <GUI> Form saved"); // Debug
		
		refreshFormTab();
		
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
		else if (selectedComponent instanceof ImportExportPanel)
		{
			ImportExportPanel Iep = (ImportExportPanel) selectedComponent;
			Iep.refreshTables(); // Refresh the tables.
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
	
	private void refreshFormTab() // Refreshes the form tab
	{
		// Search through the tabs to find the one that the one
		// that is the form display panel
		
		for (int i = 0; i < tabs.getTabCount(); i++) // For each tab
		{
			Component currentTab = tabs.getComponentAt(i);
			if (currentTab instanceof FormDisplayPanel)
			{
				FormDisplayPanel fDP = (FormDisplayPanel) currentTab;
				fDP.refreshTable();
				
				break;
			}
		}
	}
	
	public void setSelectedTab(String tabToSelect)
	{
		System.out.println("[INFO] <GUI> Running setSelectedTab");
		
		// Search through the tabs to find the one that the one
		// that is of the the class that matches the parameter
		
		for (int i = 0; i < tabs.getTabCount(); i++) // For each tab
		{
			Component currentTab = tabs.getComponentAt(i);
			if (currentTab.getClass().getName().equals(tabToSelect))
			{
				tabs.setSelectedIndex(i);
			}
		}
	}
	
	public void openRegister()
	{
		new Register(users);
	}
}