import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

public class MainMenuPanel extends JPanel implements ActionListener
{
	private boolean adminMode;
	private User user;
	private GUI gui;
	private FormsInProgressList formsInProgress;
	private FormList forms;
		
	private JPanel usernamePanel;
	private JLabel usernameLabel;
	
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
	
	public void update() // Should be called when a user selects the tab and updates the last form attempted section
	{
		System.out.println("[INFO] <MAIN_MENU_PANEL> Running update");
		
		if (!adminMode)
		{
			updateContinueFormLabel();
		}
	}
	
	private void prepareGUI()
	{
		System.out.println("[INFO] <MAIN_MENU_PANEL> Running prepareGUI");
		
		this.setLayout(new BorderLayout());
		
		prepareUsernamePanel();
		
		this.add(usernamePanel, BorderLayout.NORTH);
			
		if (adminMode)
		{
			prepareAdminMode();
		}
		else
		{
			prepareUserMode();
		}
		
		
		this.add(mainPanel, BorderLayout.CENTER);
		
		prepareExitButtonPanel();
		
		this.add(exitButtonPanel, BorderLayout.SOUTH);
	}
	
	private void prepareUserMode()
	{
		System.out.println("[INFO] <MAIN_MENU_PANEL> Running prepareUserMode");
		
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));
		
		prepareButtonNavigationPanel();
		prepareContinueFormPanel();
		
		buttonNavigationPanel.setPreferredSize(new Dimension(400,600));
		buttonNavigationPanel.setMaximumSize(new Dimension(600,800));
		
		mainPanel.add(Box.createHorizontalGlue());
		mainPanel.add(buttonNavigationPanel);
		mainPanel.add(Box.createHorizontalGlue());
		
		mainPanel.add(continueFormPanel);
		
		mainPanel.add(Box.createHorizontalGlue());
	}
	
	private void prepareContinueFormPanel()
	{
		continueFormPanel.setLayout(new BorderLayout());
		
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Continue?");
		Font currentFont = border.getTitleFont();
		border.setTitleFont(currentFont.deriveFont(Font.BOLD, 16)); // Make the font larger and bold
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		continueFormPanel.setBorder(border); // Set the border
		
		updateContinueFormLabel();
		
		
		JPanel continueFormLabelPanel = new JPanel();
		continueFormLabelPanel.setLayout(new BoxLayout(continueFormLabelPanel, BoxLayout.LINE_AXIS));
		
		continueFormLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		continueFormLabelPanel.add(Box.createHorizontalStrut(15));
		continueFormLabelPanel.add(Box.createHorizontalGlue());
		continueFormLabelPanel.add(continueFormLabel);
		continueFormLabelPanel.add(Box.createHorizontalGlue());
		continueFormLabelPanel.add(Box.createHorizontalStrut(15));
		
		continueFormPanel.add(continueFormLabelPanel, BorderLayout.CENTER);
			
		resumeFormButton.addActionListener(this);
		resumeFormButton.setBackground(new Color(130,183,75));
	
		continueFormPanel.add(resumeFormButton, BorderLayout.SOUTH);
		
		continueFormPanel.setPreferredSize(new Dimension(400,400));
		continueFormPanel.setMaximumSize(new Dimension(600,300));
	}
	
	private void updateContinueFormLabel()
	{
		String mostRecentFormID = formsInProgress.getMostRecentFormID();
		
		if (mostRecentFormID != null && forms.getFormByID(mostRecentFormID) != null)
		{
			resumeFormButton.setEnabled(true);
			
			Form mostRecentForm = forms.getFormByID(mostRecentFormID); // Get the form object associated with the form id
			
			String formTitle = mostRecentForm.getTitle();
			String formDescription = mostRecentForm.getDescription();
			continueFormLabel.setText("<html><center><strong>Last form attempted:<br><br>" + formTitle + "</strong><br><br>Description:<br>" + formDescription + "</center></html>");
		}
		else
		{
			resumeFormButton.setEnabled(false);
			continueFormLabel.setText("<html><center>No form has been attempted.<br><br><br>Try attempting a form!</center></html>");
		}
		
	}
	
	private void prepareAdminMode()
	{
		System.out.println("[INFO] <MAIN_MENU_PANEL> Running prepareAdminMode");
		
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));
		
		JPanel adminPanel = new JPanel();
		
		adminPanel.setLayout(new BoxLayout(adminPanel, BoxLayout.PAGE_AXIS));
		
		prepareButtonNavigationPanel();
		
		adminPanel.add(buttonNavigationPanel);
		
		prepareTakeRegisterButton();
		
		adminPanel.add(Box.createVerticalGlue());
		adminPanel.add(Box.createVerticalStrut(20));
		
		adminPanel.add(takeRegisterButtonPanel);
		
		adminPanel.add(Box.createVerticalStrut(20));
		
		adminPanel.setPreferredSize(new Dimension(900, 500));
		adminPanel.setMaximumSize(new Dimension(1200, 600));

		mainPanel.add(Box.createHorizontalGlue());
		mainPanel.add(adminPanel);
		mainPanel.add(Box.createHorizontalGlue());
		
	}
	
	private void prepareTakeRegisterButton()
	{
		takeRegisterButton.addActionListener(this);
		takeRegisterButton.setBackground(new Color(130,183,75));
		
		takeRegisterButtonPanel.setLayout(new GridLayout(1,1));
		takeRegisterButtonPanel.add(takeRegisterButton);
		takeRegisterButtonPanel.setPreferredSize(new Dimension(1200, 50));
		takeRegisterButtonPanel.setMaximumSize(new Dimension(1200, 50));
	}
	
	private void prepareExitButtonPanel()
	{
		exitButtonPanel.setLayout(new BoxLayout(exitButtonPanel, BoxLayout.LINE_AXIS));
		
		exitButton.addActionListener(this);
		exitButton.setBackground(new Color(174,59,46));
		exitButton.setForeground(Color.WHITE);
		
		exitButtonPanel.add(Box.createHorizontalGlue());
		exitButtonPanel.add(exitButton);
	}
	
	private void prepareButtonNavigationPanel()
	{
		buttonNavigationPanel = new JPanel();
		
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Navigation");
		Font currentFont = border.getTitleFont();
		border.setTitleFont(currentFont.deriveFont(Font.BOLD, 16)); // Make the font larger and bold
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		buttonNavigationPanel.setBorder(border); // Set the border
		
		buttonNavigationPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints buttonNavigationPanelConstraints = new GridBagConstraints();
		buttonNavigationPanelConstraints.fill = GridBagConstraints.BOTH;
		buttonNavigationPanelConstraints.insets = new Insets(10,10,10,10); // 10 px padding all around
		buttonNavigationPanelConstraints.gridx = 0;
		buttonNavigationPanelConstraints.gridy = 0;
		buttonNavigationPanelConstraints.weightx = 1;
		buttonNavigationPanelConstraints.weighty = 1; 
		
		prepareButtons();
		
		// Question buttons
		buttonNavigationPanel.add(viewQuestionsButton, buttonNavigationPanelConstraints);
		
		if (adminMode)
		{
			buttonNavigationPanelConstraints.gridx = 1;
			buttonNavigationPanel.add(createQuestionsButton, buttonNavigationPanelConstraints);
		}
		
		buttonNavigationPanelConstraints.gridy += 1;
		buttonNavigationPanelConstraints.gridx = 0;
		
		// Form buttons
		buttonNavigationPanel.add(viewFormsButton, buttonNavigationPanelConstraints);
		
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
		
		if (adminMode)
		{
			buttonNavigationPanel.add(viewUsersButton, buttonNavigationPanelConstraints);
		}
		
	}
	
	private void prepareButtons()
	{
		for (JButton button : navigationButtons)
		{
			button.addActionListener(this);
			button.setBackground(new Color(169,196,235));
		}
	}
	
	private void prepareUsernamePanel()
	{
		usernamePanel = new JPanel();
		usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.PAGE_AXIS));
		
		usernamePanel.add(Box.createVerticalStrut(20));
		
		usernameLabel = new JLabel("Welcome " + user.getUsername(), SwingConstants.CENTER);
		usernameLabel.setPreferredSize(new Dimension(10000,50));
		usernameLabel.setMaximumSize(new Dimension(10000,50));
		Font currentFont = usernameLabel.getFont();
		usernameLabel.setFont(currentFont.deriveFont(Font.BOLD, 32)); // Make the font larger and bold
		
		usernamePanel.add(usernameLabel);
		
		usernamePanel.add(Box.createVerticalStrut(50));
	}
	
	public void actionPerformed(ActionEvent evt)
	{
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
			System.out.println("[INFO] <MAIN_MENU_PANEL> resumeFormButton pressed");
			String mostRecentFormID = formsInProgress.getMostRecentFormID();
			Form mostRecentForm = forms.getFormByID(mostRecentFormID);
			
			gui.openForm(mostRecentForm);
		}
		
	}
}