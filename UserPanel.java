import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.*;

public class UserPanel extends JPanel implements ActionListener, TableColumnModelListener
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
	
	private JPanel actionPanel = new JPanel(); // Stores everything apart from the table
	
	// For searching through the users
	private JPanel searchPanel = new JPanel();
	private JLabel firstNameSearchLabel = new JLabel("First name to search for:");
	private JTextField firstNameSearchTextField = new JTextField();
	private JButton searchButton = new JButton("Search");
	
	private JButton addUserButton = new JButton("Add new user");
	private JButton editTableButton = new JButton("Edit table");
	private JButton deleteUserButton = new JButton("Delete user");
	private JButton saveButton = new JButton("Save changes");
	
	private JButton registerButton = new JButton("Take register");
	
	public UserPanel(UserList tempUserList)
	{
		users = tempUserList;
		
		prepareGUI();
	}
	
	private void prepareGUI()
	{
		System.out.println("[INFO] <USER_PANEL> Running prepareGUI");
		
		this.setLayout(new BorderLayout());
		
		prepareTable();
		this.add(userTableScrollPane, BorderLayout.CENTER); // Add the table
		
		prepareActionPanel();
		
		this.add(actionPanel, BorderLayout.SOUTH);
		
	}
	
	private void prepareActionPanel()
	{
		actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.LINE_AXIS));
		
		registerButton.addActionListener(this);
		registerButton.setBackground(new Color(169,196,235));
		actionPanel.add(registerButton);
		
		actionPanel.add(Box.createRigidArea(new Dimension(5,0)));
		
		prepareSearchPanel();
		actionPanel.add(searchPanel);
	
		actionPanel.add(Box.createRigidArea(new Dimension(5,0)));
		
		editTableButton.addActionListener(this);
		editTableButton.setBackground(new Color(169,196,235));
		actionPanel.add(editTableButton);

		actionPanel.add(Box.createRigidArea(new Dimension(5,0)));
		
		addUserButton.addActionListener(this);
		addUserButton.setBackground(new Color(169,196,235));
		actionPanel.add(addUserButton);

		actionPanel.add(Box.createRigidArea(new Dimension(5,0)));
		
		deleteUserButton.addActionListener(this);
		deleteUserButton.setBackground(new Color(169,196,235));
		actionPanel.add(deleteUserButton);
		
		actionPanel.add(Box.createRigidArea(new Dimension(5,0)));

		saveButton.addActionListener(this);
		saveButton.setBackground(new Color(169,196,235));
		actionPanel.add(saveButton);
		
	}
	private void prepareTable()
	{
		userTable.setDefaultEditor(Object.class, null); // Disable editing
		// Hide the first column as it contains the id and we don't want that displayed to the user
		TableColumnModel tcm = userTable.getColumnModel();
		tcm.removeColumn(tcm.getColumn(0));
		
		for (int i = 0; i < userTable.getColumnCount(); i++)
		{
			tcm.getColumn(i).setCellRenderer(new WordWrapCellRenderer());
			tcm.getColumn(i).setHeaderRenderer(new WordWrapHeaderRenderer());
		}
		tcm.addColumnModelListener(this);
		
		tcm.getColumn(0).setMaxWidth(120); // Username
		tcm.getColumn(0).setMaxWidth(120); // Username
		tcm.getColumn(1).setMaxWidth(120); // Password
		tcm.getColumn(2).setMaxWidth(120); // First Name
		tcm.getColumn(3).setMaxWidth(120); // Last Name
		tcm.getColumn(4).setMaxWidth(120); // Date of birth
		tcm.getColumn(5).setMaxWidth(120); // Phone Number
		
		tcm.getColumn(0).setMinWidth(120); // Username
		tcm.getColumn(0).setMinWidth(120); // Username
		tcm.getColumn(1).setMinWidth(120); // Password
		tcm.getColumn(2).setMinWidth(120); // First Name
		tcm.getColumn(3).setMinWidth(120); // Last Name
		tcm.getColumn(4).setMinWidth(120); // Date of birth
		tcm.getColumn(5).setMinWidth(120); // Phone Number
		//tcm.getColumn(6).setPreferredWidth(90); // Sessions Attended
		
		populateTable(users.getArray());
	}
	
	private void prepareSearchPanel() // Prepares the panel that allows the user to search through the users
	{
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.LINE_AXIS));
		
		searchPanel.add(firstNameSearchLabel);
		searchPanel.add(Box.createRigidArea(new Dimension(5,0)));
		searchPanel.add(firstNameSearchTextField);
		searchPanel.add(Box.createRigidArea(new Dimension(5,0)));
		
		searchButton.addActionListener(this);
		searchButton.setBackground(new Color(169,196,235));
		searchButton.setBackground(new Color(169,196,235));
		
		firstNameSearchTextField.addActionListener(this);
		
		searchPanel.add(searchButton);
	}
	
	private void populateTable(User[] data)
	{
		System.out.println("[INFO] <USER_PANEL> Running populateTable");
		
		userTableModel.setRowCount(0); // Start at the first row
		
		for (int i = 0; i < data.length; i++) // For each user in the array
		{
		
			// Validate the user name
			if (data[i] != null)
			{
				User u = data[i];
				String[] userData = new String[] {u.getID(), u.getUsername(), u.getPassword(), u.getFirstName(), u.getLastName(),
											   u.getDateOfBirth(), u.getPhoneNumber(), u.getSessionsAttendedString().replace(".", " ")};
				userTableModel.addRow(userData); // Add the user to the table
			}
		}
		
		resizeRows();
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
			
		users.addUser(new User(id,username,password,firstName,lastName,dateOfBirth,phoneNumber, false, new String[0], new QuestionStatList()));
		
		JOptionPane.showMessageDialog(null, "User added!");
		
		populateTable(users.getArray());
	}
	
	private void saveChanges()
	{
		userTable.setDefaultEditor(Object.class, null); // Disable editing
		
		// Go through each row of the table and update all of the user details
		// If there is an issue then create a dialog telling the user what is wrong
		for (int rowIndex = 0; rowIndex < userTable.getRowCount(); rowIndex++)
		{
			String userID = (String) userTable.getModel().getValueAt(rowIndex, 0);
			User u = users.getUserByID(userID);
			
			// Check username, date of birth, phone number, and sessions attended for errors.
			
			String newUsername = (String) userTable.getModel().getValueAt(rowIndex, 1);
			String newDateOfBirth = (String) userTable.getModel().getValueAt(rowIndex, 5);
			String newPhoneNumber = (String) userTable.getModel().getValueAt(rowIndex, 6);
			String newSessionsAttended = (String) userTable.getModel().getValueAt(rowIndex, 7);
			
			// Check if the username is already in use by a different user
			User otherUser = users.getUserByUsername(newUsername);
			if (otherUser != null && otherUser != u) // If the username is already taken
			{
				JOptionPane.showMessageDialog(null, u.getFirstName() + " username already in use.", "Error saving changes", JOptionPane.ERROR_MESSAGE);
				return; // Exit the method
			}
			
			if (!validateDate(newDateOfBirth))
			{
				JOptionPane.showMessageDialog(null, u.getFirstName() + ": Invaid date of birth", "Error saving changes", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (!validatePhoneNumber(newPhoneNumber))
			{
				JOptionPane.showMessageDialog(null, u.getFirstName() + ": Invaid phone number", "Error saving changes", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (!newSessionsAttended.isEmpty())
			{
				for (String session : newSessionsAttended.split(" "))
				{
					if (!validateDate(session)) // If one of the dates is incorrect
					{
						JOptionPane.showMessageDialog(null, u.getFirstName() + " " + session + ": Invaid date (Sessions attended)", "Error saving changes", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}
			
		}
		
		// We've not hit a return therefore everything must have been correct therefore
		// write the changes to file.
		users.writeDatabase();
		
	}
	
	private boolean validatePhoneNumber(String phoneNumber)
	{
		return Pattern.matches("^(((\\+44\\s?\\d{4}|\\(?0\\d{4}\\)?)\\s?\\d{3}\\s?\\d{3})|((\\+44\\s?\\d{3}|\\(?0\\d{3}\\)?)\\s?\\d{3}\\s?\\d{4})|((\\+44\\s?\\d{2}|\\(?0\\d{2}\\)?)\\s?\\d{4}\\s?\\d{4}))(\\s?\\#(\\d{4}|\\d{3}))?$", phoneNumber);
		
	}
	
	private boolean validateDate(String date)
	{
		// Checks if a date is in yyyy-mm-dd
		boolean pass = true;
		
		if (date.length() != 10)
		{
			pass = false;
		}
		
		String[] splitDate = date.split("-");
		
		try
		{
			int day = Integer.parseInt(splitDate[0]);
			int month = Integer.parseInt(splitDate[1]);
			int year = Integer.parseInt(splitDate[2]);
			
			if (day > 31 || month > 12 || year < 1900)
			{
				pass = false;
			}
		}
		catch (NumberFormatException e)
		{
			pass = false;
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			pass = false;
		}
		// The date is 10 characters long, has 2 - in it and the year is 4 digits and the month and days are 2 digits and they are valid months and days
		return pass;
		
	}
	/*
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
	*/
	
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
	
	private void editTable()
	{
		userTable.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField())); // Disable editing
	}
	
	private void deleteUser()
	{
			int row = userTable.getSelectedRow();
			if (row != -1) // If they actually selected a row
			{
				String selectedID = (String) userTable.getModel().getValueAt(row, 0); // Get the userID
				users.removeUser(selectedID); // Delete the user that the ID corresponds to.
				populateTable(users.getArray());
			}
	}
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == addUserButton)
		{
			System.out.println("[INFO] <USER_PANEL> addUserButton pressed");
			
			createNewUser();
		}
		else if (evt.getSource() == editTableButton)
		{
			System.out.println("[INFO] <USER_PANEL> editTableButton pressed");
			editTable();
		}
		else if (evt.getSource() == deleteUserButton)
		{
			System.out.println("[INFO] <USER_PANEL> deleteUserButton pressed");
			deleteUser();
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
		else if (evt.getSource() == firstNameSearchTextField)
		{
			searchButton.doClick();
		}
		else if (evt.getSource() == saveButton)
		{
			saveChanges();
		}
	}
	
	private void resizeRows()
	{
		for (int row = 0; row < userTable.getRowCount(); row ++)
		{
			int requiredHeight = 0;
			
			// Go through the columns and get the largest height of all of the components
			for (int col = 0; col < userTable.getColumnCount(); col++)
			{
				TableCellRenderer cellRenderer = userTable.getCellRenderer(row, col);
				Component c = userTable.prepareRenderer(cellRenderer, row, col);
				
				int preferredHeight = c.getPreferredSize().height;
				
				if (preferredHeight > requiredHeight)
				{
					requiredHeight = preferredHeight;
				}

			}
			
			// Set the height of the row to that height if that's not already the height
			if (userTable.getRowHeight(row) != requiredHeight)
			{
				userTable.setRowHeight(row, requiredHeight);
			}
		}
	}
	
	public void columnMarginChanged(ChangeEvent e)
	{
		resizeRows();
	}
	public void columnAdded(TableColumnModelEvent e) {}
	
	public void columnRemoved(TableColumnModelEvent e) {}
	
	public void columnMoved(TableColumnModelEvent e) {}
	
	public void columnSelectionChanged(ListSelectionEvent e) {}
}