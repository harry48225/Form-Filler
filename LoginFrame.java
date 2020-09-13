import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class LoginFrame extends JFrame implements ActionListener
{
	/* This is a login window */
	
	private UserList users;
	private GUI gui;
	private List<Image> icons;
	
	private JLabel usernameLabel = new JLabel("Username:");
	private JLabel passwordLabel = new JLabel("Password:");
	
	private JTextField usernameField = new JTextField();
	private JPasswordField passwordField = new JPasswordField();
	
	private JButton loginButton = new JButton("Login");
	
	public LoginFrame(UserList tempUsers, List<Image> tempIcons, GUI tempGUI)
	{
		users = tempUsers;
		icons = tempIcons;
		gui = tempGUI;
		prepareGUI();
	}
	
	private void prepareGUI()
	{
		/* Prepares the window for display */
		System.out.println("[INFO] <LOGIN_FRAME> Running prepareGUI");
		this.setLayout(new GridLayout(1,1));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel mainPanel = new JPanel();
		
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS)); // Vertical box layout
		
		
		// Make the labels the correct size
		usernameLabel.setMaximumSize(new Dimension(100,10));
		usernameLabel.setPreferredSize(new Dimension(100,10));
		usernameLabel.setMinimumSize(new Dimension(100,10));
		
		passwordLabel.setMaximumSize(new Dimension(100,10));
		passwordLabel.setPreferredSize(new Dimension(100,10));
		passwordLabel.setMinimumSize(new Dimension(100,10));
		
		// Create the username panel that contains the username label and field
		JPanel usernamePanel = new JPanel();
		usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.LINE_AXIS)); // Horizontal box layout
		
		// Add the label and field with 10px horizontal padding between them
		usernamePanel.add(Box.createHorizontalStrut(10));
		usernamePanel.add(usernameLabel);
		usernamePanel.add(Box.createHorizontalStrut(10));
		usernamePanel.add(usernameField);
		usernamePanel.add(Box.createHorizontalStrut(10));
		
		// Create the password panel that contains the password field and label
		JPanel passwordPanel = new JPanel();
		
		// Add an actionlistener to the password field so that presses to the enter key are detected
		passwordField.addActionListener(this);
		passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.LINE_AXIS)); // Horizontal box layout
		
		// Add the label and field with 10px horizontal padding between them
		passwordPanel.add(Box.createHorizontalStrut(10));
		passwordPanel.add(passwordLabel);
		passwordPanel.add(Box.createHorizontalStrut(10));
		passwordPanel.add(passwordField);
		passwordPanel.add(Box.createHorizontalStrut(10));
		
		// Create the panel to hold the login button
		JPanel loginPanel = new JPanel();
		loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.LINE_AXIS)); // Horizontal box layout
		
		loginButton.setBackground(new Color(169,196,235)); // Blue
		loginButton.addActionListener(this);
		
		// Add the login button with horizontal glue each side to center it
		loginPanel.add(Box.createHorizontalGlue());
		loginPanel.add(loginButton);
		loginPanel.add(Box.createHorizontalGlue());
	
		// Add each panel to the main panel with 20px vertical padding between them
		mainPanel.add(Box.createVerticalStrut(20));
		mainPanel.add(usernamePanel);
		mainPanel.add(Box.createVerticalStrut(20));
		mainPanel.add(passwordPanel);
		mainPanel.add(Box.createVerticalStrut(20));
		mainPanel.add(loginPanel);
		mainPanel.add(Box.createVerticalStrut(10));
		
		this.add(mainPanel);
		this.setSize(300,200); // Set the size
		this.setLocationRelativeTo(null); // Center it
		this.setTitle("Login");
		this.setResizable(false);
		this.setIconImages(icons); // Set the form filler icon
		this.setVisible(true);
	}
	
	private void validateLogin()
	{
		/* Logs in a user if they entered the correct credentials */
		
		if (checkCredentials()) // If they entered valid credentials
		{
			gui.login(users.getUserByUsername(usernameField.getText())); // Login the user
			
			// Destroy and close this window
			setVisible(false);
			setVisible(false);
			dispose();
		}
		else
		{
			// Tell them that the username / password is incorrect
			JOptionPane.showMessageDialog(this, "username/password incorrect", "Invalid login", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private boolean checkCredentials()
	{
		/* Validates the credentials that the user has entered */
		
		boolean pass = true;
		
		User u = users.getUserByUsername(usernameField.getText()); // Get the user that corresponds to the username that the user entered
	
		if (u != null) // If the username is valid
		{
			String password = u.getPassword(); // Get the correct password for the user
			String enteredPassword = new String(passwordField.getPassword()); // Get the password that the user has entered
			
			if (!password.equals(enteredPassword)) // If the passwords don't match
			{
				pass = false;
			}
		}
		else // The username is invalid
		{
			pass = false;
		}
		
		return pass;
	}
	
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == loginButton)
		{
			System.out.println("[INFO] <LOGIN_FRAME> loginButton pressed");
			
			validateLogin();
		}
		else if (evt.getSource() == passwordField)
		{
			// If the user presses enter in the password field, click the login button
			loginButton.doClick();
		}
	}
}