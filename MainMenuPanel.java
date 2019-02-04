import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

import java.time.*;
import java.util.Date;

public class MainMenuPanel extends JPanel implements ActionListener, Helper
{
	/* This is a panel that displays a main menu to the user/admin */
	
	private final String HELP_STRING = "This is the main menu. You can navigate to other areas of the system from here. You can also resume attempting the most recent form that you attempted. Your attendance is dispalyed on the right.";
	
	private boolean adminMode;
	private User user;
	private GUI gui;
	private FormsInProgressList formsInProgress;
	private FormList forms;
		
	private JPanel usernamePanel;
	private JLabel usernameLabel;
	
	// Components for navigation
	private JPanel buttonNavigationPanel;
	private JButton viewQuestionsButton = new JButton("View questions");
	private JButton createQuestionsButton = new JButton("Create questions");
	private JButton viewFormsButton = new JButton("View forms");
	private JButton createFormsButton = new JButton("Create forms");
	private JButton importExportButton = new JButton("Import/Export");
	private JButton viewStatisticsButton = new JButton("View statistics");
	private JButton viewUsersButton = new JButton("View users");
	private JButton[] navigationButtons = {viewQuestionsButton, createQuestionsButton, viewFormsButton, 
										   createFormsButton, importExportButton, viewStatisticsButton,
										   viewUsersButton};
	
	private JPanel takeRegisterButtonPanel = new JPanel();
	private JButton takeRegisterButton = new JButton("Take register");
	
	private JButton exitButton = new JButton("Exit");
	private JPanel exitButtonPanel = new JPanel();
	
	private JPanel continueFormPanel = new JPanel();
	private JLabel continueFormLabel = new JLabel();
	private JButton resumeFormButton = new JButton("Resume form");
	
	private JPanel attendancePanel = new JPanel();
	private JLabel attendanceLabel = new JLabel();

	private JPanel mainPanel = new JPanel();
	
	private Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED); // Border style
	
	public MainMenuPanel(User tempUser, GUI tempGUI, FormsInProgressList tempFormsInProgress, FormList tempForms)
	{
		user = tempUser;
		gui = tempGUI;
		formsInProgress = tempFormsInProgress;
		forms = tempForms;
		
		adminMode = user.isAdmin();
		
		prepareGUI();
	}
	
	public String getHelpString()
	{
		/* Returns the help string */
		return HELP_STRING;
	}
	
	public void update()
	{
		/*  Should be called when a user selects the tab and updates the last form attempted section with the most recent form attempted*/
		
		System.out.println("[INFO] <MAIN_MENU_PANEL> Running update");
		
		if (!adminMode) // If the user isn't an admin
		{
			updateContinueFormLabel();
			updateAttendanceLabel();
		}
	}
	
	private void prepareGUI()
	{
		/* Prepares the panel for display */
		
		System.out.println("[INFO] <MAIN_MENU_PANEL> Running prepareGUI");
		
		this.setLayout(new BorderLayout());
		
		prepareUsernamePanel();
		
		// Add the username panel at the top of the panel
		this.add(usernamePanel, BorderLayout.NORTH);
			
		// Show the admin panel if the user is an admin
		if (adminMode)
		{
			prepareAdminMode();
		}
		else // Otherwise show the user panel
		{
			prepareUserMode();
		}
		
		// Add the panel to the center of the main menu panel
		this.add(mainPanel, BorderLayout.CENTER);
		
		// Add the exit button to the bottom of the panel
		prepareExitButtonPanel();
		
		this.add(exitButtonPanel, BorderLayout.SOUTH);
	}
	
	private void prepareAttendancePanel()
	{
		/* Prepares the attendance display */
		
		attendancePanel.setLayout(new GridLayout(1,1));
		
		// Create an attendance border
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Attendance");
		
		Font currentFont = border.getTitleFont();
		border.setTitleFont(currentFont.deriveFont(Font.BOLD, 16)); // Make the font larger and bold
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		attendancePanel.setBorder(border); // Set the border
		
		// Make the panel the correct size
		attendancePanel.setMinimumSize(new Dimension(200,200));
		attendancePanel.setPreferredSize(new Dimension(300,300));
		attendancePanel.setMaximumSize(new Dimension(300,300));
		
		updateAttendanceLabel();
		
		// Center the attendance label both horizontally and vertically
		attendanceLabel.setHorizontalAlignment(SwingConstants.CENTER);
		attendanceLabel.setVerticalAlignment(SwingConstants.CENTER);
		
		currentFont = attendanceLabel.getFont();
		attendanceLabel.setFont(currentFont.deriveFont(Font.BOLD, 32)); // Make the font larger and bold
		
		attendancePanel.add(attendanceLabel);
	}
	
	private void updateAttendanceLabel()
	{
		/* Works out what percentage of the past sessions over the last 5 weeks the user has been present */
		
		long DAY_IN_MS = 1000 * 60 * 60 * 24;
		Date fiveWeeksAgo = new Date(System.currentTimeMillis() - (36 * DAY_IN_MS)); // Calculate the date five weeks ago
		
		int numberAttended = 0;
		
		// Iterate through the sessions that the user has attended and count the numbe that were in the last 5 weeks
		for (String session : user.getSessionsAttended()) // For each session
		{
			// Extract the day, month, and year
			String[] splitSession = session.split("-");
			int day = Integer.parseInt(splitSession[0]);
			int month = Integer.parseInt(splitSession[1]);
			int year = Integer.parseInt(splitSession[2]);
			Date sessionDate = new Date(year, month, day);
			
			if (sessionDate.after(fiveWeeksAgo)) // If the date is after 5 weeks ago
			{
				numberAttended++;
			}
		}
		
		// If if the number attended is greater than 5 set it to 5 to prevent percentages greater than 100%
		if (numberAttended > 5)
		{
			numberAttended = 5;
		}
		
		int percentage = (numberAttended * 100)/5; // Calcuate the percentage of sessions attended in the past 5 weeks
		
		// Set the text of the label to the percentage of sessions attended
		attendanceLabel.setText(percentage + "%");
	}
	
	private void prepareUserMode()
	{
		/* Prepares the panel for a logged in user */
		
		System.out.println("[INFO] <MAIN_MENU_PANEL> Running prepareUserMode");
		
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS)); // Horizontal box layout
		
		prepareButtonNavigationPanel();
		prepareContinueFormPanel();
		
		// Make the button navigation panel the correct size
		buttonNavigationPanel.setPreferredSize(new Dimension(400,300));
		buttonNavigationPanel.setMaximumSize(new Dimension(600,400));
		mainPanel.add(Box.createHorizontalGlue());
		
		// Add the panels to the main panel with horizontal glue between them to fill the horizontal space
		mainPanel.add(buttonNavigationPanel);
		mainPanel.add(Box.createHorizontalGlue());
		
		mainPanel.add(continueFormPanel);
		mainPanel.add(Box.createHorizontalGlue());
		
		prepareAttendancePanel();
		mainPanel.add(attendancePanel);
		mainPanel.add(Box.createHorizontalGlue());
		
		
		mainPanel.add(Box.createHorizontalGlue());
	}
	
	private void prepareContinueFormPanel()
	{
		/* Prepares the panel that allows the user to resume the form that they most recently attempted */
		
		continueFormPanel.setLayout(new BorderLayout());
		
		// Create the border
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Continue?");
		Font currentFont = border.getTitleFont();
		border.setTitleFont(currentFont.deriveFont(Font.BOLD, 16)); // Make the font larger and bold
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		continueFormPanel.setBorder(border); // Set the border
		
		updateContinueFormLabel();
		
		
		JPanel continueFormLabelPanel = new JPanel();
		continueFormLabelPanel.setLayout(new BoxLayout(continueFormLabelPanel, BoxLayout.LINE_AXIS)); // Horizontal box layout
		
		// Add the continue form label to the center of the panel
		continueFormLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		// 15px padding top and bottom and then horizontal glue to fill the empty space
		continueFormLabelPanel.add(Box.createHorizontalStrut(15));
		continueFormLabelPanel.add(Box.createHorizontalGlue());
		continueFormLabelPanel.add(continueFormLabel);
		continueFormLabelPanel.add(Box.createHorizontalGlue());
		continueFormLabelPanel.add(Box.createHorizontalStrut(15));
		
		continueFormPanel.add(continueFormLabelPanel, BorderLayout.CENTER);
			
		resumeFormButton.addActionListener(this);
		resumeFormButton.setBackground(new Color(130,183,75)); // Green
	
		// Add the resume form button to the bottom
		continueFormPanel.add(resumeFormButton, BorderLayout.SOUTH);
		
		// Make the panel the correct size
		continueFormPanel.setPreferredSize(new Dimension(400,200));
		continueFormPanel.setMaximumSize(new Dimension(600,300));
	}
	
	private void updateContinueFormLabel()
	{
		/* Updates the continue form label with information about the form that the user has most recently attempted */
		
		String mostRecentFormID = formsInProgress.getMostRecentFormID();
		
		// If they have attempted a form recently and that form is still in the database
		if (mostRecentFormID != null && forms.getFormByID(mostRecentFormID) != null)
		{
			// Enable the resume form button
			resumeFormButton.setEnabled(true);
			
			// Get the title and description and add it to the label
			Form mostRecentForm = forms.getFormByID(mostRecentFormID); // Get the form object associated with the form id
			
			String formTitle = mostRecentForm.getTitle();
			String formDescription = mostRecentForm.getDescription();
			continueFormLabel.setText("<html><center><strong>Last form attempted:<br><br>" + formTitle + "</strong><br><br>Description:<br>" + formDescription + "</center></html>");
		}
		else // They have not recently attempted a form
		{
			// Disable the button and set the label text to tell the user that they need to first attempt a form
			resumeFormButton.setEnabled(false);
			continueFormLabel.setText("<html><center>No form has been attempted.<br><br><br>Try attempting a form!</center></html>");
		}
		
	}
	
	private void prepareAdminMode()
	{
		/* Prepares the admin main menu panel */
		
		System.out.println("[INFO] <MAIN_MENU_PANEL> Running prepareAdminMode");
		
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS)); // Horizontal box layout
		
		JPanel adminPanel = new JPanel();
		
		adminPanel.setLayout(new BoxLayout(adminPanel, BoxLayout.PAGE_AXIS)); // Vertical box layout
		
		// Prepare the panels and add them
		prepareButtonNavigationPanel();
		
		adminPanel.add(buttonNavigationPanel);
		
		prepareTakeRegisterButton();
		
		// Vertical glue to fill the empty vertical space
		adminPanel.add(Box.createVerticalGlue());
		adminPanel.add(Box.createVerticalStrut(20)); // 20px vertical padding
		
		adminPanel.add(takeRegisterButtonPanel);
		
		adminPanel.add(Box.createVerticalStrut(20));
		
		// Make the panel the correct size
		adminPanel.setPreferredSize(new Dimension(900, 500));
		adminPanel.setMaximumSize(new Dimension(1200, 600));

		// Add horizontal glue either side of the panel to center it and then add it to the main panel
		mainPanel.add(Box.createHorizontalGlue());
		mainPanel.add(adminPanel);
		mainPanel.add(Box.createHorizontalGlue());
		
	}
	
	private void prepareTakeRegisterButton()
	{
		/* Prepares the button that allows the admin to take a register */
		
		takeRegisterButton.addActionListener(this);
		takeRegisterButton.setBackground(new Color(130,183,75)); // Green
		
		takeRegisterButtonPanel.setLayout(new GridLayout(1,1));
		takeRegisterButtonPanel.add(takeRegisterButton);
		
		// Make the button the correct size
		takeRegisterButtonPanel.setPreferredSize(new Dimension(1200, 50));
		takeRegisterButtonPanel.setMaximumSize(new Dimension(1200, 50));
	}
	
	private void prepareExitButtonPanel()
	{
		/* Prepares the exit button and the panel that contains it */
		
		exitButtonPanel.setLayout(new BoxLayout(exitButtonPanel, BoxLayout.LINE_AXIS)); // Horizontal box layout
		
		exitButton.addActionListener(this);
		exitButton.setBackground(new Color(174,59,46)); // Red
		exitButton.setForeground(Color.WHITE); // White text
		
		// Adding horizontal glue on the left forces the button to the far right
		exitButtonPanel.add(Box.createHorizontalGlue());
		exitButtonPanel.add(exitButton);
	}
	
	private void prepareButtonNavigationPanel()
	{
		/* Prepares the grid of buttons that allows the user / admin to access all areas of the system */
		
		buttonNavigationPanel = new JPanel();
		
		// Create a titled border
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Navigation");
		Font currentFont = border.getTitleFont();
		border.setTitleFont(currentFont.deriveFont(Font.BOLD, 16)); // Make the font larger and bold
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		buttonNavigationPanel.setBorder(border); // Set the border
		
		buttonNavigationPanel.setLayout(new GridBagLayout());
		
		// Prepare the grid bag layout constraints
		GridBagConstraints buttonNavigationPanelConstraints = new GridBagConstraints();
		buttonNavigationPanelConstraints.fill = GridBagConstraints.BOTH;
		buttonNavigationPanelConstraints.insets = new Insets(10,10,10,10); // 10 px padding all around
		buttonNavigationPanelConstraints.gridx = 0;
		buttonNavigationPanelConstraints.gridy = 0;
		buttonNavigationPanelConstraints.weightx = 1;
		buttonNavigationPanelConstraints.weighty = 1; 
		
		// Prepare the buttons
		prepareButtons();
		
		// Question buttons
		buttonNavigationPanel.add(viewQuestionsButton, buttonNavigationPanelConstraints);
		
		// Add the create questions button if the user is an admin
		if (adminMode)
		{
			buttonNavigationPanelConstraints.gridx = 1;
			buttonNavigationPanel.add(createQuestionsButton, buttonNavigationPanelConstraints);
		}
		
		buttonNavigationPanelConstraints.gridy += 1;
		buttonNavigationPanelConstraints.gridx = 0;
		
		// Form buttons
		buttonNavigationPanel.add(viewFormsButton, buttonNavigationPanelConstraints);
		
		// Add the create forms button if the user is an admin
		if (adminMode)
		{
			buttonNavigationPanelConstraints.gridx = 1;
			buttonNavigationPanel.add(createFormsButton, buttonNavigationPanelConstraints);
		}
		buttonNavigationPanelConstraints.gridy += 1;
		buttonNavigationPanelConstraints.gridx = 0;
		
		// Import export button
		if (adminMode)
		{
			buttonNavigationPanelConstraints.gridwidth = 2;
		}
		
		buttonNavigationPanel.add(importExportButton, buttonNavigationPanelConstraints);
		buttonNavigationPanelConstraints.gridy += 1;
		buttonNavigationPanelConstraints.gridwidth = 1;
		
		// View statistics buttons
		buttonNavigationPanel.add(viewStatisticsButton, buttonNavigationPanelConstraints);
		buttonNavigationPanelConstraints.gridx = 1;
		
		// Add the view users button if the user is an admin
		if (adminMode)
		{
			buttonNavigationPanel.add(viewUsersButton, buttonNavigationPanelConstraints);
		}
		
	}
	
	private void prepareButtons()
	{
		/* Gives each navigation button an actionlistener and makes them the correct colour */
		
		for (JButton button : navigationButtons)
		{
			button.addActionListener(this);
			button.setBackground(new Color(169,196,235)); // Blue
		}
	}
	
	private void prepareUsernamePanel()
	{
		/* Prepares the label and panel at the top of the main menu panel that welcomes the user to the system */
		
		usernamePanel = new JPanel();
		usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.PAGE_AXIS)); // Vertical box layout
		
		usernamePanel.add(Box.createVerticalStrut(20)); // 20px vertical padding
		
		// Create a very long, horizontally centered label that welcomes the user to the system
		usernameLabel = new JLabel("Welcome " + user.getUsername(), SwingConstants.CENTER);
		usernameLabel.setPreferredSize(new Dimension(10000,50)); // Very large width so that it fills all available horizontal space
		usernameLabel.setMaximumSize(new Dimension(10000,50));
		Font currentFont = usernameLabel.getFont();
		usernameLabel.setFont(currentFont.deriveFont(Font.BOLD, 32)); // Make the font larger and bold
		
		usernamePanel.add(usernameLabel);
		
		usernamePanel.add(Box.createVerticalStrut(50)); // 50px vertical padding
	}
	
	public void actionPerformed(ActionEvent evt)
	{
		// Navigates to the correct tab depending on which button was pressed
		if (evt.getSource() == viewQuestionsButton)
		{
			gui.setSelectedTab("QuestionDisplayPanel");
		}
		else if (evt.getSource() == createQuestionsButton)
		{
			gui.setSelectedTab("QuestionCreationPanel");
		}
		else if (evt.getSource() == viewFormsButton)
		{
			gui.setSelectedTab("FormDisplayPanel");
		}
		else if (evt.getSource() == createFormsButton)
		{
			gui.setSelectedTab("FormCreationPanel");
		}
		else if (evt.getSource() == importExportButton)
		{
			gui.setSelectedTab("ImportExportPanel");
		}
		else if (evt.getSource() == viewStatisticsButton)
		{
			gui.setSelectedTab("StatisticsPanel");
		}
		else if (evt.getSource() == viewUsersButton)
		{
			gui.setSelectedTab("UserPanel");
		}
		else if (evt.getSource() == exitButton)
		{
			System.out.println("[INFO] <MAIN_MENU_PANEL> exitButton pressed");
			
			System.out.println("[INFO] <MAIN_MENU_PANEL> Terminating program");
			System.exit(0); // Quit the program
		}
		else if (evt.getSource() == takeRegisterButton)
		{
			System.out.println("[INFO] <MAIN_MENU_PANEL> takeRegisterButton pressed");
			
			gui.openRegister();
		}
		else if (evt.getSource() == resumeFormButton)
		{
			/* Gets the most recent form attempted and opens it for the user to resume attempting */
			System.out.println("[INFO] <MAIN_MENU_PANEL> resumeFormButton pressed");
			String mostRecentFormID = formsInProgress.getMostRecentFormID();
			Form mostRecentForm = forms.getFormByID(mostRecentFormID);
			
			gui.openForm(mostRecentForm);
		}
		
	}
}