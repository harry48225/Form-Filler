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

public class GUI extends JFrame implements ChangeListener, ActionListener// Main GUI class
{
	private QuestionList questions; // The question list
	
	private FormList forms;
	
	private UserList users;
	
	private User currentUser;
	
	private FormsInProgressList formsInProgress;
	
	private JTabbedPane tabs = new JTabbedPane(); // To store the different sections of the program
	
	private List<Image> icons;
	
	private JPanel helpPanel = new JPanel();
	
	private JButton helpButton = new JButton("?");
	
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
		
		getIcons();
		
		new LoginFrame(users, icons, this);
		
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
	
	public void login(User u)
	{
		currentUser = u;
		
		currentUser.addPresentToday(); // Log the user's attendance
			
		users.writeDatabase(); // Write the database to file
		
		formsInProgress = new FormsInProgressList(currentUser.getUsername());
		
		System.out.println(formsInProgress);
		
		prepareGUI();
	}
	private void getIcons()
	{
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
		this.setTitle("Form Filler");
		
		this.setIconImages(icons);
		this.setSize(1200,700);
		this.setMinimumSize(new Dimension(900,600));
		this.setLayout(new GridLayout(1,1));
		this.setLocationRelativeTo(null); // Center it
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new OverlayLayout(mainPanel));
		
		tabs.add("Main menu", new MainMenuPanel(currentUser, this, formsInProgress, forms));
		tabs.add("View Questions", new QuestionDisplayPanel(questions, this, currentUser.isAdmin()));
		tabs.add("View Forms", new FormDisplayPanel(forms, this, questions, formsInProgress,currentUser.isAdmin()));
		
		if (currentUser.isAdmin())
		{
			tabs.add("Create questions", new QuestionCreationPanel(questions, this));
			tabs.add("Create forms", new FormCreationPanel(questions, forms, this));
			tabs.add("Users", new UserPanel(users, this, questions));
		}
		tabs.add("Statistics", new StatisticsPanel(currentUser, questions, icons));
		tabs.add("Import and Export", new ImportExportPanel(questions, forms));
		
		tabs.addChangeListener(this);
		
		tabs.setAlignmentX(1.0f); // Right align
        tabs.setAlignmentY(0.0f); // Left align
		Font currentFont = tabs.getFont();
		tabs.setFont(currentFont.deriveFont(Font.BOLD, 16)); // Make the font larger and bold
		
		prepareHelpPanel();
		
		helpPanel.setAlignmentX(1.0f); // Right align
        helpPanel.setAlignmentY(0.0f); // Left align
		
		mainPanel.add(helpPanel);
		mainPanel.add(tabs);
		
		this.add(mainPanel);
		
		this.setVisible(true);
	}
	
	private void prepareHelpPanel()
	{
		helpPanel.setLayout(new BoxLayout(helpPanel, BoxLayout.LINE_AXIS)); // Horizontal box layout
		//helpPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		helpPanel.add(Box.createHorizontalGlue()); // Will take up horizontal space and force 
		
		
		Font currentFont = helpButton.getFont();
		helpButton.setFont(currentFont.deriveFont(Font.BOLD, 14)); // Make the font larger and bold
		
		helpButton.setBackground(new Color(0,102,204));
		helpButton.setForeground(Color.WHITE);
		
		helpButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5)); // 5 thick black border
		
		helpButton.setMinimumSize(new Dimension(100,30));
		helpButton.setPreferredSize(new Dimension(100,30));
		
		helpButton.addActionListener(this);
		helpButton.setFocusPainted(false); // Disable the border around the text when the button is focused
		
		helpPanel.setMinimumSize(new Dimension(100,30));
		helpPanel.setPreferredSize(new Dimension(100,30));
		helpPanel.setOpaque(false); // Make the panel transparent
		
		helpPanel.add(helpButton);
	}
	
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
			
			FormInProgress inProgress = formsInProgress.getByID(f.getID());
			formComponents = inProgress.getQuestionPanels(); // Get the saved formQuestionPanels
		}
		else // The form is not in the forms in progress list
		{
			System.out.println("[INFO] <GUI> Starting new form");
			
			formComponents = buildFormComponentArray(f);
			
			formsInProgress.addFormInProgress(new FormInProgress(f.getID(), 0, formComponents, 0)); // Add the form in progress
			
		}
		
		formsInProgress.setMostRecentAttempted(f.getID());
		new FormDisplayer(f, formComponents, currentUser, users, this, questions, icons); // Open the form
		
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
			
			JOptionPane.showMessageDialog(this, ta, "Help", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	private void decryptUserdatabase()
	{
		String key = JOptionPane.showInputDialog(this, "Please enter a key to decrypt the database");
		if (key != null)
		{
			users.loadSensitiveDatabase(key);
			
			if (!users.isDecrypted()) // If the database was not successfully decrypted
			{
				JOptionPane.showMessageDialog(this, "<html><center>Error decrypting the database.<br>This is most likely due to an incorrect decryption key</center></html>", "Decryption error", JOptionPane.ERROR_MESSAGE);
			}
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
				fDP.refresh();
				
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
		// Need to make sure that the database is decrypted first otherwise
		// all of the names will be encrypted
		
		if (!users.isDecrypted())
		{
			decryptUserdatabase();
			
		}
		
		if (users.isDecrypted()) // If the decryption was successful
		{
			new Register(users, icons);
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
}