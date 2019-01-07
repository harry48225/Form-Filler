import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class LoginFrame extends JFrame implements ActionListener
{
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
		System.out.println("[INFO] <LOGIN_FRAME> Running prepareGUI");
		this.setLayout(new GridLayout(1,1));

		JPanel mainPanel = new JPanel();
		
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		
		usernameLabel.setMaximumSize(new Dimension(100,10));
		usernameLabel.setPreferredSize(new Dimension(100,10));
		usernameLabel.setMinimumSize(new Dimension(100,10));
		
		passwordLabel.setMaximumSize(new Dimension(100,10));
		passwordLabel.setPreferredSize(new Dimension(100,10));
		passwordLabel.setMinimumSize(new Dimension(100,10));
		
		JPanel usernamePanel = new JPanel();
		usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.LINE_AXIS));
		usernamePanel.add(Box.createHorizontalStrut(10));
		usernamePanel.add(usernameLabel);
		usernamePanel.add(Box.createHorizontalStrut(10));
		usernamePanel.add(usernameField);
		usernamePanel.add(Box.createHorizontalStrut(10));
		
		
		JPanel passwordPanel = new JPanel();
		
		passwordField.addActionListener(this);
		passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.LINE_AXIS));
		passwordPanel.add(Box.createHorizontalStrut(10));
		passwordPanel.add(passwordLabel);
		passwordPanel.add(Box.createHorizontalStrut(10));
		passwordPanel.add(passwordField);
		passwordPanel.add(Box.createHorizontalStrut(10));
		
		
		JPanel loginPanel = new JPanel();
		loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.LINE_AXIS));
		
		loginButton.setBackground(new Color(169,196,235));
		loginButton.addActionListener(this);
		
		loginPanel.add(Box.createHorizontalGlue());
		loginPanel.add(loginButton);
		loginPanel.add(Box.createHorizontalGlue());
	
		mainPanel.add(Box.createVerticalStrut(20));
		mainPanel.add(usernamePanel);
		mainPanel.add(Box.createVerticalStrut(20));
		mainPanel.add(passwordPanel);
		mainPanel.add(Box.createVerticalStrut(20));
		mainPanel.add(loginPanel);
		mainPanel.add(Box.createVerticalStrut(10));
		
		this.add(mainPanel);
		this.setSize(300,200);
		this.setLocationRelativeTo(null); // Center it
		this.setTitle("Login");
		this.setResizable(false);
		this.setIconImages(icons);
		this.setVisible(true);
	}
	
	private void validateLogin()
	{
		if (checkCredentials()) // If tbey entered valid credentials
		{
			gui.login(users.getUserByUsername(usernameField.getText()));
			
			setVisible(false);
			setVisible(false);
			dispose();
		}
		else
		{
			JOptionPane.showMessageDialog(this, "username/password incorrect", "Invalid login", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private boolean checkCredentials()
	{
		boolean pass = true;
		
		User u = users.getUserByUsername(usernameField.getText());
	
		if (u != null) // If the username is valid
		{
			String password = u.getPassword();
			String enteredPassword = new String(passwordField.getPassword());
			
			if (!password.equals(enteredPassword)) // If the passwords don't match
			{
				pass = false;
			}
		}
		else
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
			loginButton.doClick();
		}
	}
}