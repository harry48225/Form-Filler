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
	private JButton[] navigationButtons = {viewQuestionsButton, createQuestionsButton, viewFormsButton, createFormsButton, importExportButton, viewStatisticsButton,
										   viewUsersButton};
	
	private JButton takeRegisterButton = new JButton("Take register");
	
	private JButton exitButton = new JButton("Exit button");
	
	private Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED); // Border style
	
	public MainMenuPanel(User tempUser, GUI tempGUI)
	{
		user = tempUser;
		gui = tempGUI;
		
		adminMode = user.isAdmin();
		
		prepareGUI();
	}
	
	private void prepareGUI()
	{
		System.out.println("[INFO] <MAIN_MENU_PANEL> Running prepareGUI");
		
		this.setLayout(new BorderLayout());
		
		prepareUsernamePanel();
		
		this.add(usernamePanel, BorderLayout.NORTH);
		
		prepareButtonNavigationPanel();
		
		this.add(buttonNavigationPanel, BorderLayout.CENTER);
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
		buttonNavigationPanelConstraints.gridx = 1;
		buttonNavigationPanel.add(createQuestionsButton, buttonNavigationPanelConstraints);
		buttonNavigationPanelConstraints.gridy += 1;
		buttonNavigationPanelConstraints.gridx = 0;
		
		// Form buttons
		buttonNavigationPanel.add(viewFormsButton, buttonNavigationPanelConstraints);
		buttonNavigationPanelConstraints.gridx = 1;
		buttonNavigationPanel.add(createFormsButton, buttonNavigationPanelConstraints);
		buttonNavigationPanelConstraints.gridy += 1;
		buttonNavigationPanelConstraints.gridx = 0;
		
		// Import export button
		buttonNavigationPanelConstraints.gridwidth = 2;
		buttonNavigationPanel.add(importExportButton, buttonNavigationPanelConstraints);
		buttonNavigationPanelConstraints.gridy += 1;
		buttonNavigationPanelConstraints.gridwidth = 1;
		
		// View statistics buttons
		buttonNavigationPanel.add(viewStatisticsButton, buttonNavigationPanelConstraints);
		buttonNavigationPanelConstraints.gridx = 1;
		buttonNavigationPanel.add(viewUsersButton, buttonNavigationPanelConstraints);
		
		
		
		
		
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
		
		
	}
}