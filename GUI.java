import components.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import java.io.*;

import java.net.URL;

import javax.swing.border.Border;

public class GUI extends JFrame implements ChangeListener, ActionListener, WindowListener// Main GUI class
{
	/* The main gui class, contains all of the other panels */
	
	private QuestionList questions; // The question list
	
	private FormList forms;
	
	private UserList users;
	
	private User currentUser;
	
	private FormsInProgressList formsInProgress;
	
	private JTabbedPane tabs = new JTabbedPane(); // To store the different sections of the program
	
	private List<Image> icons; // The icons that the window can have
	
	private JPanel helpPanel = new JPanel();
	
	private JButton helpButton = new JButton("?");
	
	public GUI() // Constructor
	{
		setup(); // Run the setup method
	}	
	
	private void setup()
	{
		/* Prepares the question, forms, and user list */
		
		System.out.println("[INFO] <GUI> Running setup"); // Debug
		
		
		JWindow window = new JWindow();
		window.getContentPane().add(
			new JLabel("", new ImageIcon("icons/icon-240.png"), SwingConstants.CENTER));
		window.setBounds(500, 350, 300, 200);
		window.setVisible(true);
		window.setLocationRelativeTo(null); // Center it
	
		// Load the questions and the forms
		questions = new QuestionList(); // Create a new question list
		
		forms = new FormList();

		users = new UserList();
		
		getIcons();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		window.setVisible(false);
		window.dispose();
		
		new LoginFrame(users, icons, this); // Show the login dialog		
		
	}
	
	private User getUser()
	{
		/* Debug method, allows programmer to login as a user by selecting them from a list rather than entering the username and password */
		
		String[] usernames = new String[users.getUsers().length];
		
		// Add each user's username to the username list
		for (int i = 0; i < usernames.length; i++)
		{
			usernames[i] = users.getUsers()[i].getUsername();
		}
		
		// Ask the user to select a user to login as
		return users.getUserByUsername((String)JOptionPane.showInputDialog(
                    null,
                    "Please select a user",
                    "User?",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    usernames,
                    usernames[0])); // Show the user a dropdown with all the current users
	}
	
	public void login(User u)
	{
		/* Logs in a user */
		
		currentUser = u;
		
		currentUser.addPresentToday(); // Log the user's attendance
			
		users.writeDatabase(); // Write the database to file
		
		// Load the user's forms in progress list
		formsInProgress = new FormsInProgressList(currentUser.getUsername());
		
		prepareGUI();
	}
	private void getIcons()
	{
		/* Loads the window icons */
		List<Image> images = new ArrayList<Image>();
		
		images.add(new ImageIcon("icons/icon-12.png").getImage());
		images.add(new ImageIcon("icons/icon-16.png").getImage());
		images.add(new ImageIcon("icons/icon-24.png").getImage());
		images.add(new ImageIcon("icons/icon-48.png").getImage());
		images.add(new ImageIcon("icons/icon-96.png").getImage());
		images.add(new ImageIcon("icons/icon-240.png").getImage());
		
		icons = images;
	}
	
	private void prepareGUI()
	{
		/* Prepares the gui */
		
		this.setTitle("Form Filler");
		
		// Setup the window
		this.setIconImages(icons);
		this.setSize(1200,700);
		this.setMinimumSize(new Dimension(900,600));
		this.setLayout(new GridLayout(1,1));
		this.setLocationRelativeTo(null); // Center it
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Create a main panel to hold the help panel and tabs
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new OverlayLayout(mainPanel)); // Allow the help button to be placed on top
		
		// Add the tabs
		tabs.add("Main menu", new MainMenuPanel(currentUser, this, formsInProgress, forms));
		tabs.add("View Questions", new QuestionDisplayPanel(questions, this, currentUser.isAdmin()));
		tabs.add("View Forms", new FormDisplayPanel(forms, this, questions, formsInProgress,currentUser.isAdmin()));
		
		// Add the admin tabs
		if (currentUser.isAdmin())
		{
			tabs.add("Create questions", new QuestionCreationPanel(questions, this));
			tabs.add("Create forms", new FormCreationPanel(questions, forms, this));
			tabs.add("Users", new UserPanel(users, this, questions));
		}
		
		// Add the rest of the tabs
		tabs.add("Statistics", new StatisticsPanel(currentUser, questions, icons));
		tabs.add("Import and Export", new ImportExportPanel(questions, forms));
		
		// Add a change listener so that we can detect when tab focus changes
		tabs.addChangeListener(this);
		
		tabs.setAlignmentX(1.0f); // Right align
        tabs.setAlignmentY(0.0f); // Top align
		Font currentFont = tabs.getFont();
		tabs.setFont(currentFont.deriveFont(Font.BOLD, 16)); // Make the font larger and bold
		
		prepareHelpPanel();
		
		helpPanel.setAlignmentX(1.0f); // Right align
        helpPanel.setAlignmentY(0.0f); // Top align
		
		mainPanel.add(helpPanel);
		mainPanel.add(tabs);
		
		this.add(mainPanel);
		
		this.setVisible(true);
	}
	
	private void prepareHelpPanel()
	{
		/* Prepares the presistent help button */
		helpPanel.setLayout(new BoxLayout(helpPanel, BoxLayout.LINE_AXIS)); // Horizontal box layout
		
		helpPanel.add(Box.createHorizontalGlue()); // Will take up horizontal space and force the button to the far right of the window
		
		
		Font currentFont = helpButton.getFont();
		helpButton.setFont(currentFont.deriveFont(Font.BOLD, 14)); // Make the font larger and bold
		
		helpButton.setBackground(new Color(0,102,204)); // Blue
		helpButton.setForeground(Color.WHITE); // Make the text white
		
		helpButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5)); // 5 thick black border
		
		// Make it the correct size
		helpButton.setMinimumSize(new Dimension(100,30));
		helpButton.setPreferredSize(new Dimension(100,30));
		
		helpButton.addActionListener(this);
		helpButton.setFocusPainted(false); // Disable the border around the text when the button is focused
		
		// Make the panel the correct size
		helpPanel.setMinimumSize(new Dimension(100,30));
		helpPanel.setPreferredSize(new Dimension(100,30));
		helpPanel.setOpaque(false); // Make the panel transparent
		
		helpPanel.add(helpButton);
	}
	
	public void openForm(Form f)
	{
		/* Takes a form as a parameter and opens it for attempting */
		
		JPanel[] formComponents;
		
		System.out.println("[INFO] <GUI> Running openForm"); // Debug
		
		if (formsInProgress.isFormPresent(f.getID())) // If the form is in the formsInProgressList
		{
			// Get the forminprogress object coressponding to the form
			
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
			
			formComponents = fP.getQuestionPanels(); // Get the saved formQuestionPanels
		}
		else // The form is not in the forms in progress list
		{
			System.out.println("[INFO] <GUI> Starting new form");
			
			formComponents = buildFormComponentArray(f);
			
			formsInProgress.addFormInProgress(new FormInProgress(f.getID(), 0, formComponents, 0)); // Add the form in progress
			
		}
		
		// Set the form as the most recently attempted form
		formsInProgress.setMostRecentAttempted(f.getID());
		
		new FormDisplayer(f, formComponents, currentUser, users, this, questions, icons); // Open the form
		
	}
	
	private JPanel[] buildFormComponentArray(Form f)
	{
		/* Creates an array that contains all of the components in a form */
		
		JPanel[] formComponents;
		
		String[] questionIDs = f.getQuestionIDs();
		
		formComponents = new JPanel[questionIDs.length];
		
		// Load the question panels from the questionPanels database
		for (int i = 0; i < formComponents.length; i++)
		{
			// Determine whether the component is a question or a header by looking it up in the question database, if there is a match it's a question
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
		/* Creates a form that contains the questions that the user struggles with the most */
		
		String[] questionIDs = currentUser.getQuestionStats().getQuestionsStruggleTheMost(questions); // Get the questions that the user struggles with the most
		
		Form.FormBuilder questionForm = new Form.FormBuilder("USER_WEAKNESSES", questions);
		
		for (String questionID : questionIDs) // Add each question to the form and make it required
		{
			questionForm = questionForm.add(questionID, true);
		}
		
		openForm(questionForm.build()); // Open the form to be attempted
	}
	
	public void saveForm(Form f, int percentComplete)
	{
		/* Saves a form */
		
		System.out.println("[INFO] <GUI> Running saveForm"); // Debug
		
		FormInProgress fP = formsInProgress.getByID(f.getID());
		fP.setPercentComplete(percentComplete);
		
		if (percentComplete == 100) // If the user fully completed the form increment the number of times that the user has attempted the form
		{
			fP.addTimesCompleted();
		}
		
		formsInProgress.writeDatabase();
		System.out.println("[INFO] <GUI> Form saved"); // Debug
		
		refreshFormTab();
		
	}
	
	public void stateChanged(ChangeEvent changeEvent)
	{
		/* Refreshs the tab that the user just clicked on so that it has the most recent information */
		
		// Get the component in the tab that the user just selected
		JTabbedPane sourcePane = (JTabbedPane) changeEvent.getSource();
		Component selectedComponent = sourcePane.getSelectedComponent();
		
		// Work out which type of panel they selected and refresh it
		
		if (selectedComponent instanceof QuestionDisplayPanel)
		{
			QuestionDisplayPanel qDP = (QuestionDisplayPanel) selectedComponent;
			qDP.refresh(); // Refresh the tab
		}
		else if (selectedComponent instanceof FormDisplayPanel)
		{
			FormDisplayPanel fDP = (FormDisplayPanel) selectedComponent;
			fDP.refresh(); // Refresh the tab
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
		else if (selectedComponent instanceof MainMenuPanel)
		{
			MainMenuPanel mmP = (MainMenuPanel) selectedComponent;
			mmP.update();
		}
		else if (selectedComponent instanceof UserPanel)
		{
			UserPanel uP = (UserPanel) selectedComponent;
			
			if (!users.isDecrypted()) // If the database hasn't been decrypted yet
			{	
				decryptUserdatabase();
			}
			
			uP.refresh();
		}
		
		// If they tab that they selected has help available, enable the help button,
		// otherwise disable it
		if (selectedComponent instanceof Helper)
		{
			helpButton.setEnabled(true);
		}
		else
		{
			helpButton.setEnabled(false);
		}
		
	}
	
	private void showHelp()
	{
		/* Shows a popup with the help string provided by the selected component */
		
		Component selectedTab = tabs.getSelectedComponent();
		
		if (selectedTab instanceof Helper) // If it implements the helper interface
		{
			Helper helperTab = (Helper) selectedTab;
			
			// Setup a JTextArea to contain the help string so that
			// the text is automatically wrapped.
			JTextArea ta = new JTextArea(helperTab.getHelpString(), 1, 20);
			ta.setWrapStyleWord(true);
			ta.setLineWrap(true);
			ta.setOpaque(false);
			ta.setBorder(null);
			ta.setEditable(false);
			ta.setFocusable(false);
			
			Font currentFont = ta.getFont();
			ta.setFont(currentFont.deriveFont(Font.PLAIN, 14)); // Make the font larger
			
			// Work out the perfect size for the text area
			// Width is always 300.
			// 20 columns so number of rows should be number of chars / 20
			
			int length = helperTab.getHelpString().toCharArray().length;
			
			int numberOfRows = length / 20;
			
			int height = numberOfRows * 11;
			
			ta.setMinimumSize(new Dimension(300,height));
			ta.setPreferredSize(new Dimension(300,height));
			
			// Show the help dialog
			JOptionPane.showMessageDialog(this, ta, "Help", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	private void decryptUserdatabase()
	{
		/* Asks the user for a key to decrypt the user database and decrypts it if they entered the correct key */
		
		boolean encrypted = true;
		
		while (encrypted)
		{
		
			String key = JOptionPane.showInputDialog(this, "Please enter a key to decrypt the database");
			if (key != null)
			{
				users.loadSensitiveDatabase(key);
				
				if (!users.isDecrypted()) // If the database was not successfully decrypted
				{
					// Show an error message
					JOptionPane.showMessageDialog(this, "<html><center>Error decrypting the database.<br>This is most likely due to an incorrect decryption key</center></html>", "Decryption error", JOptionPane.ERROR_MESSAGE);
					
					int reply = JOptionPane.showConfirmDialog(this, "Would you like to retry?", "Retry", JOptionPane.YES_NO_OPTION);
					
					if (reply == JOptionPane.NO_OPTION)
					{
						break;
					}					
				}
				else
				{
					encrypted = false;
				}
			}
		}
		
	}
	
	public void resetTab(JComponent componentToReset)
	{
		/* Resets a tab by replacing the component in the tab with a brand new one */
		
		// Get the index of the component passed in as the parameter
		int index = tabs.indexOfComponent(componentToReset);
		
		// Work out what it is and reset it.
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
		if (componentToReset instanceof QuestionDisplayPanel)
		{
			// Add a new question display panel at the index of the old one
			tabs.setComponentAt(index, new QuestionDisplayPanel(questions, this, currentUser.isAdmin()));
		}
		if (componentToReset instanceof FormDisplayPanel)
		{
			// Add a new form display panel at the index of the old one
			tabs.setComponentAt(index, new FormDisplayPanel(forms, this, questions, formsInProgress,currentUser.isAdmin()));
		}
	}
	
	private void refreshFormTab()
	{
		/* Refreshs the form tab */
		
		
		// Search through the tabs to find the one that the one
		// that is the form display panel
		
		for (int i = 0; i < tabs.getTabCount(); i++) // For each tab
		{
			Component currentTab = tabs.getComponentAt(i);
			if (currentTab instanceof FormDisplayPanel) // Check if it's the form display panel
			{
				FormDisplayPanel fDP = (FormDisplayPanel) currentTab;
				fDP.refresh();
				
				break;
			}
		}
	}
	
	public void setSelectedTab(String tabToSelect)
	{
		/* Sets a tab which has the same class as the text of the string as selected */
		
		System.out.println("[INFO] <GUI> Running setSelectedTab");
		
		// Search through the tabs to find the one that the one
		// that is of the the class that matches the parameter
		
		for (int i = 0; i < tabs.getTabCount(); i++) // For each tab
		{
			Component currentTab = tabs.getComponentAt(i);
			if (currentTab.getClass().getName().equals(tabToSelect)) // If the class is the same as the text of the string
			{
				tabs.setSelectedIndex(i);
			}
		}
	}
	
	public void openRegister()
	{
		/* Opens the register for the admin to fill in */
		
		// Need to make sure that the database is decrypted first otherwise
		// all of the names will be encrypted
		
		if (!users.isDecrypted()) // If the database isn't decrypted ask the user to decrypt it
		{
			decryptUserdatabase();
			
		}
		
		if (users.isDecrypted()) // If the decryption was successful
		{
			Register r = new Register(users, icons);
			r.addWindowListener(this);
		}
		
	}
	
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == helpButton)
		{
			System.out.println("[INFO] <GUI> helpButton pressed");
			
			showHelp();
		}
	}
	
	/* These methods need to be implemented for this class to be an window listener */
	public void windowActivated(WindowEvent e) {}  
	public void windowClosing(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	
	public void windowClosed(WindowEvent e)
	{
		/* This will be run when the register is closed therefore we should refresh the usertab if it's visible */
		
		Component selectedTab = tabs.getSelectedComponent();
		
		if (selectedTab instanceof UserPanel)
		{
			UserPanel uP = (UserPanel) selectedTab;
			uP.refresh();
		}
	}
}