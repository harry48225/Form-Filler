import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class UserPanel extends JPanel implements ActionListener
{
	private UserList users;
	
	// For the view table
	private String[] tableHeaders = new String[] {"ID",  "Username", "Password", "First Name",
																		"Last Name", "Date of Birth", "Phone Number",
																		"Sessions attended"}; // The headers for the table
	private String[][] userData = new String[0][0];
	private DefaultTableModel userTableModel = new DefaultTableModel(userData, tableHeaders);
	private JTable userTable = new JTable(userTableModel); // Create a table to hold the users
	private JScrollPane userTableScrollPane = new JScrollPane(userTable); // Create a scroll pane
	
	// For searching through the users
	private JPanel searchPanel = new JPanel();
	private JLabel firstNameSearchLabel = new JLabel("First name to search for");
	private JTextField firstNameSearchTextField = new JTextField();
	private JButton searchButton = new JButton("Search");
	
	private JButton addUserButton = new JButton("Add new user");
	private JButton editUserButton = new JButton("Edit user");
	private JButton deleteUserButton = new JButton("Delete user");
	
	private JButton registerButton = new JButton("Take register");
	
	public UserPanel(UserList tempUserList)
	{
		users = tempUserList;
		
		prepareGUI();
	}
	
	private void prepareGUI()
	{
		System.out.println("[INFO] <USER_PANEL> Running prepareGUI");
		
		this.setLayout(new GridLayout(0,1));
		
		this.add(userTableScrollPane); // Add the table
		
		// Hide the first column as it contains the id and we don't want that displayed to the user
		TableColumnModel tcm = userTable.getColumnModel();
		tcm.removeColumn(tcm.getColumn(0));
		
		populateTable(users.getArray());
		
		prepareSearchPanel();
		
		this.add(searchPanel);
		
		addUserButton.addActionListener(this);
		this.add(addUserButton);
		
		editUserButton.addActionListener(this);
		this.add(editUserButton);
		
		deleteUserButton.addActionListener(this);
		this.add(deleteUserButton);
		
		registerButton.addActionListener(this);
		this.add(registerButton);
	}
	
	private void prepareSearchPanel() // Prepares the panel that allows the user to search through the users
	{
		searchPanel.setLayout(new GridLayout(0,1));
		
		searchPanel.add(firstNameSearchLabel);
		searchPanel.add(firstNameSearchTextField);
		
		searchButton.addActionListener(this);
		
		searchPanel.add(searchButton);
	}
	
	private void populateTable(User[] data)
	{
		System.out.println("[INFO] <USER_PANEL> Running populateTable");
		
		userTableModel.setRowCount(0); // Start at the first row
		
		for (int i = 0; i < data.length; i++) // For each user in the array
		{
			if (data[i] != null)
			{
				String[] user = data[i].toStringArray(); // Convert the user to a String array
				userTableModel.addRow(user); // Add the user to the table
			}
		}
		
	}
	
	private void createNewUser() // Lets the user create a new user
	{
		String id = users.getFreeID();
		
		String username = getUsername();
		
		String password = JOptionPane.showInputDialog("Please enter a password");
		
		String firstName = JOptionPane.showInputDialog("Please enter your first name");
		String lastName = JOptionPane.showInputDialog("Please enter your last name");
		String dateOfBirth = JOptionPane.showInputDialog("Please enter your date of birth as dd/mm/yyyy");
		String phoneNumber = JOptionPane.showInputDialog("Please enter your phone number");
			
		users.addUser(new User(id,username,password,firstName,lastName,dateOfBirth,phoneNumber, new String[0], new QuestionStatList()));
		
		users.writeDatabase();
		
		JOptionPane.showMessageDialog(null, "User added!");
	}
	
	private void editUser(User userToEdit) // Lets the user edit a user's details
	{
		// Show the input dialogs used when creating a user, however populate them with the user's info
		String username = JOptionPane.showInputDialog(null, "Please enter a username", userToEdit.getUsername());
		
		String password = JOptionPane.showInputDialog(null, "Please enter a password", userToEdit.getPassword());
		
		String firstName = JOptionPane.showInputDialog(null, "Please enter your first name", userToEdit.getFirstName());
		String lastName = JOptionPane.showInputDialog(null, "Please enter your last name", userToEdit.getLastName());
		String dateOfBirth = JOptionPane.showInputDialog(null, "Please enter your date of birth as dd/mm/yyyy", userToEdit.getDateOfBirth());
		String phoneNumber = JOptionPane.showInputDialog(null, "Please enter your phone number", userToEdit.getPhoneNumber());
		String sessionsAttendedString = JOptionPane.showInputDialog(null, "Please enter the sessions attended as dd-mm-yyyy separated by commas", userToEdit.getSessionsAttendedString());
	
		String[] sessionsAttended = new String[0]; // Empty string array
		
		if (sessionsAttendedString.length() > 0) // If there are sessions
		{
			sessionsAttended = sessionsAttendedString.split(","); // Get the sessions.
		}
	
		// Update the user
		userToEdit.setUsername(username);
		userToEdit.setPassword(password);
		userToEdit.setFirstName(firstName);
		userToEdit.setLastName(lastName);
		userToEdit.setDateOfBirth(dateOfBirth);
		userToEdit.setPhoneNumber(phoneNumber);
		userToEdit.setSessionsAttended(sessionsAttended);
		
		users.writeDatabase();
	}
	
	public String getUsername() // Gets a unique username from the user
	{
		String username = "";
		
		boolean success = false; 
			
		while (!success) // While a unique username hasn't been chosen yet
		{
			username = JOptionPane.showInputDialog("Please enter a username");
			
			if (users.getUserByUsername(username) == null) // If a unique username has been chosen
			{
				success = true;
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Username already taken!", "Username taken", JOptionPane.ERROR_MESSAGE);
			}
		}	
		
		return username;
	}
	
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == addUserButton)
		{
			System.out.println("[INFO] <USER_PANEL> addUserButton pressed");
			
			createNewUser();
		}
		else if (evt.getSource() == editUserButton)
		{
			System.out.println("[INFO] <USER_PANEL> editUserButton pressed");
			int row = userTable.getSelectedRow();
			String selectedID = (String) userTable.getModel().getValueAt(row, 0); // Get the userID
			User selectedUser = users.getUserByID(selectedID); // Get the user that has been selected
			editUser(selectedUser);
		}
		else if (evt.getSource() == deleteUserButton)
		{
			System.out.println("[INFO] <USER_PANEL> deleteUserButton pressed");
			int row = userTable.getSelectedRow();
			String selectedID = (String) userTable.getModel().getValueAt(row, 0); // Get the userID
			users.removeUser(selectedID); // Delete the user that the ID corresponds to.
		}
		else if (evt.getSource() == searchButton)
		{
			System.out.println("[INFO] <USER_PANEL> searchButton pressed");
			
			populateTable(users.filterByFirstName(firstNameSearchTextField.getText()));
		}
		else if (evt.getSource() == registerButton)
		{
			System.out.println("[INFO] <USER_PANEL> registerButton pressed");
			
			new Register(users);
		}
	}
}