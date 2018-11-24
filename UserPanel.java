import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

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
	private JButton editUserButton = new JButton("Edit user");
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
		
		editUserButton.addActionListener(this);
		editUserButton.setBackground(new Color(169,196,235));
		actionPanel.add(editUserButton);

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
		// Hide the first column as it contains the id and we don't want that displayed to the user
		TableColumnModel tcm = userTable.getColumnModel();
		tcm.removeColumn(tcm.getColumn(0));
		
		for (int i = 0; i < userTable.getColumnCount(); i++)
		{
			tcm.getColumn(i).setCellRenderer(new WordWrapCellRenderer());
			tcm.getColumn(i).setHeaderRenderer(new WordWrapHeaderRenderer());
		}
		tcm.addColumnModelListener(this);
		
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
			
		users.addUser(new User(id,username,password,firstName,lastName,dateOfBirth,phoneNumber, false, new String[0], new QuestionStatList()));
		
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