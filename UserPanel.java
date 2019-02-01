import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.*;

import java.awt.print.*;

public class UserPanel extends JPanel implements ActionListener, TableColumnModelListener, Printable
{
	private UserList users;
	private GUI gui;
	private QuestionList questions;
	
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
	private JButton produceReportsButton = new JButton("Produce report(s)");
	
	private JButton registerButton = new JButton("Take register");
	
	private JPanel[] reportPanels;
	
	public UserPanel(UserList tempUserList, GUI tempGUI, QuestionList tempQuestions)
	{
		users = tempUserList;
		gui = tempGUI;
		questions = tempQuestions;
		
		prepareGUI();
	}
	
	public void refresh()
	{
		populateTable(users.getArray());
		
		refreshButtons();
	}
	
	private void refreshButtons() // Enables the buttons if the databse has been decrypted
	{
		if (users.isDecrypted())
		{
			registerButton.setEnabled(true);
			searchButton.setEnabled(true);
			produceReportsButton.setEnabled(true);
			
			addUserButton.setEnabled(true);
			editTableButton.setEnabled(true);
			deleteUserButton.setEnabled(true);
			saveButton.setEnabled(true);
		}
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
		registerButton.setEnabled(false);
		actionPanel.add(registerButton);
		
		actionPanel.add(Box.createRigidArea(new Dimension(5,0)));
		
		produceReportsButton.addActionListener(this);
		produceReportsButton.setBackground(new Color(169,196,235));
		produceReportsButton.setEnabled(false);
		actionPanel.add(produceReportsButton);
		
		actionPanel.add(Box.createRigidArea(new Dimension(5,0)));
		
		prepareSearchPanel();
		actionPanel.add(searchPanel);
	
		actionPanel.add(Box.createRigidArea(new Dimension(5,0)));
		
		editTableButton.addActionListener(this);
		editTableButton.setBackground(new Color(169,196,235));
		editTableButton.setEnabled(false);
		actionPanel.add(editTableButton);

		actionPanel.add(Box.createRigidArea(new Dimension(5,0)));
		
		addUserButton.addActionListener(this);
		addUserButton.setBackground(new Color(169,196,235));
		addUserButton.setEnabled(false);
		actionPanel.add(addUserButton);

		actionPanel.add(Box.createRigidArea(new Dimension(5,0)));
		
		deleteUserButton.addActionListener(this);
		deleteUserButton.setBackground(new Color(169,196,235));
		deleteUserButton.setEnabled(false);
		actionPanel.add(deleteUserButton);
		
		actionPanel.add(Box.createRigidArea(new Dimension(5,0)));

		saveButton.addActionListener(this);
		saveButton.setBackground(new Color(169,196,235));
		saveButton.setEnabled(false);
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
		searchButton.setEnabled(false);
		
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
		
		if (username == null)
		{
			return;
		}
		
		String password = JOptionPane.showInputDialog(this,"Please enter a password");
		
		if (password == null)
		{
			return;
		}
		
		String firstName = JOptionPane.showInputDialog(this,"Please enter your first name");
		
		if (firstName == null)
		{
			return;
		}
		
		String lastName = JOptionPane.showInputDialog(this,"Please enter your last name");
		
		if (lastName == null)
		{
			return;
		}
		
		String dateOfBirth = JOptionPane.showInputDialog(this,"Please enter your date of birth as dd-mm-yyyy");
		
		if (dateOfBirth == null)
		{
			return;
		}
		
		String phoneNumber = JOptionPane.showInputDialog(this,"Please enter your phone number");
			
		if (phoneNumber == null)
		{
			return;
		}
		
		users.addUser(new User(id,username,password,firstName,lastName,dateOfBirth,phoneNumber, false, new String[0], new QuestionStatList()));
		
		JOptionPane.showMessageDialog(this, "User added!");
		
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
			String newPassword = (String) userTable.getModel().getValueAt(rowIndex, 2);
			String newFirstName = (String) userTable.getModel().getValueAt(rowIndex, 3);
			String newLastName = (String) userTable.getModel().getValueAt(rowIndex, 4);
			String newDateOfBirth = (String) userTable.getModel().getValueAt(rowIndex, 5);
			String newPhoneNumber = (String) userTable.getModel().getValueAt(rowIndex, 6);
			String newSessionsAttended = (String) userTable.getModel().getValueAt(rowIndex, 7);
			
			// Check if the username is already in use by a different user
			User otherUser = users.getUserByUsername(newUsername);
			if (otherUser != null && otherUser != u) // If the username is already taken
			{
				JOptionPane.showMessageDialog(this, u.getFirstName() + " username already in use.", "Error saving changes", JOptionPane.ERROR_MESSAGE);
				return; // Exit the method
			}
			
			if (newPassword.contains(","))
			{
				JOptionPane.showMessageDialog(this, u.getFirstName() + " please remove commas from password.", "Error saving changes", JOptionPane.ERROR_MESSAGE);
				return; // Exit the method
			}
			
			if (!validateDate(newDateOfBirth))
			{
				JOptionPane.showMessageDialog(this, u.getFirstName() + ": Invaid date of birth", "Error saving changes", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (!validatePhoneNumber(newPhoneNumber))
			{
				JOptionPane.showMessageDialog(this, u.getFirstName() + ": Invaid phone number", "Error saving changes", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (!newSessionsAttended.isEmpty())
			{
				for (String session : newSessionsAttended.split(" "))
				{
					if (!validateDate(session)) // If one of the dates is incorrect
					{
						JOptionPane.showMessageDialog(this, u.getFirstName() + " " + session + ": Invaid date (Sessions attended)", "Error saving changes", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}
			
			// commas and || are not allowed as they could break the database therefore we should remove them.
			String[] userData = {newUsername, newPassword, newFirstName, newLastName, newDateOfBirth, newPhoneNumber, newSessionsAttended};
			for (int i = 0; i < userData.length; i++)
			{
				userData[i] = userData[i].replace(",","").replaceAll("\\|", "");
			}
			
			String[] sessionsAttendedArray = new String[0];
			if (userData[6].length() > 5) // If there is a date
			{
				sessionsAttendedArray = userData[6].split(" ");
			}
			u.setUsername(userData[0]);
			u.setPassword(userData[1]);
			u.setFirstName(userData[2]);
			u.setLastName(userData[3]);
			u.setDateOfBirth(userData[4]);
			u.setPhoneNumber(userData[5]);
			u.setSessionsAttended(sessionsAttendedArray);
			
		}
		
		// We've not hit a return therefore everything must have been correct therefore
		// write the changes to file.
		
		// Ask if they want to change the encryption key
		int changeKey = JOptionPane.showConfirmDialog(this, "Would you like to change the encryption key?", "Change key?", JOptionPane.YES_NO_OPTION);
		
		if (changeKey == 0)
		{
			String newKey = JOptionPane.showInputDialog(this, "Please enter a new key", "Key?");
			
			if (newKey != null && !newKey.isEmpty())
			{
				users.setKey(newKey);
			}
		}
		
		users.writeDatabase();
		
		JOptionPane.showMessageDialog(this, "Database saved!");
		
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
	
	public String getUsername() // Gets a unique username from the user
	{
		String username = "";
		
		boolean success = false; 
			
		while (!success) // While a unique username hasn't been chosen yet
		{
			username = JOptionPane.showInputDialog(this, "Please enter a username");
			
			if (users.getUserByUsername(username) == null) // If a unique username has been chosen
			{
				success = true;
			}
			else
			{
				JOptionPane.showMessageDialog(this, "Username already taken!", "Username taken", JOptionPane.ERROR_MESSAGE);
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
			
			if (firstNameSearchTextField.getText().trim().isEmpty()) // If they aren't searching for a user repopulate the table with all users
			{
				populateTable(users.getArray());
			}
			else
			{
				populateTable(users.filterByFirstName(firstNameSearchTextField.getText()));
			}
		}
		else if (evt.getSource() == registerButton)
		{
			System.out.println("[INFO] <USER_PANEL> registerButton pressed");
			
			gui.openRegister();
			
			refresh();
		}
		else if (evt.getSource() == produceReportsButton)
		{
			System.out.println("[INFO] <USER_PANEL> produceReportsButton pressed");
			
			if (userTable.getSelectedRows().length > 0)
			{
				System.out.println(userTable.getSelectedRows().length);
				runPrint();
			}
		}
		else if (evt.getSource() == firstNameSearchTextField)
		{
			searchButton.doClick();
		}
		else if (evt.getSource() == saveButton)
		{
			if (users.isDecrypted())
			{
				saveChanges();
			}
		}
	}
	
	private void produceReports()
	{
		int[] selectedRows = userTable.getSelectedRows();
		reportPanels = new JPanel[selectedRows.length];
		
		for (int i = 0; i < selectedRows.length; i++)
		{
			int row = selectedRows[i];
			
			String selectedID = (String) userTable.getModel().getValueAt(row, 0); // Get the userID
			User u = users.getUserByID(selectedID);
			QuestionStatList questionStats = u.getQuestionStats();
			String[][] reportData = questionStats.produceReport(questions);
			
			reportPanels[i] = new Report(reportData, u.getUsername());
		}
	}
	
	public int print(Graphics g, PageFormat pf, int page) throws PrinterException 
	{
		if (page < reportPanels.length) 
		{
			JPanel report = reportPanels[page];
			
			JFrame reportFrame = new JFrame(); // Holds the report but is invisible
			reportFrame.setSize(600,800);
			reportFrame.add(report);
			reportFrame.repaint();
			reportFrame.pack();
			report.setVisible(true);
			
			Graphics2D g2d = (Graphics2D)g;
			g2d.translate(pf.getImageableX(), pf.getImageableY());
			double scaleFactor = pf.getImageableWidth()/report.getWidth();
			g2d.scale(scaleFactor, scaleFactor);

			// Print the reportTablePanel
			report.printAll(g);

			return PAGE_EXISTS;
		}
		else
		{
			System.out.println("[INFO] <USER_PANEL> Printed reports");
			return NO_SUCH_PAGE;
		}

	}
	
	private void runPrint()
	{
		System.out.println("[INFO] <USER_PANEL> Running runPrint");
		
		produceReports();
		
		PrinterJob job = PrinterJob.getPrinterJob();
		
		PageFormat pf = job.defaultPage();
		pf.setOrientation(PageFormat.PORTRAIT); // Make the print job portrait
		
		job.setPrintable(this, pf);
		
		boolean doPrint = job.printDialog();
		
		if (doPrint)
		{
			try
			{
				job.print();
			}
			catch (PrinterException e)
			{
				System.out.println("[ERROR] <USER_PANEL> Error printing " + e);
			}
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