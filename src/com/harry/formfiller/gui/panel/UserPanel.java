package com.harry.formfiller.gui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.harry.formfiller.gui.GUI;
import com.harry.formfiller.gui.Report;
import com.harry.formfiller.gui.WordWrapCellRenderer;
import com.harry.formfiller.gui.WordWrapHeaderRenderer;
import com.harry.formfiller.question.QuestionList;
import com.harry.formfiller.user.QuestionStatList;
import com.harry.formfiller.user.User;
import com.harry.formfiller.user.UserList;

public class UserPanel extends JPanel implements ActionListener, TableColumnModelListener, Printable
{
	/* 
		This is a panel that the admin can use to view and edit the information stored about users
		They can also add new users to the system and delete them. They can also produce multiple reports
		and take a register.
	*/
	
	private static final String ERROR_SAVING_CHANGES = "Error saving changes";
	// System data
	private transient UserList users;
	private GUI gui;
	private transient QuestionList questions;
	
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
	
	// The rest of the action buttons at the bottom of the panel
	private JButton addUserButton = new JButton("Add new user");
	private JButton editTableButton = new JButton("Edit table");
	private JButton deleteUserButton = new JButton("Delete user");
	private JButton saveButton = new JButton("Save changes");
	private JButton produceReportsButton = new JButton("Produce report(s)");
	
	private JButton registerButton = new JButton("Take register");
	
	private boolean edited = false;
	
	private JPanel[] reportPanels; // Stores all of the reports that have been generated.
	
	public UserPanel(UserList tempUserList, GUI tempGUI, QuestionList tempQuestions)
	{
		users = tempUserList;
		gui = tempGUI;
		questions = tempQuestions;
		
		prepareGUI();
	}
	
	public void refresh()
	{
		/* Refreshes the panel with up to date information form the user list. */
		populateTable(users.getArray());
		
		refreshButtons();
	}
	
	private void refreshButtons()
	{
		/* Enables the buttons if the database has been decrypted */
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
		/* Prepares the panel */
		System.out.println("[INFO] <USER_PANEL> Running prepareGUI");
		
		this.setLayout(new BorderLayout());
		
		prepareTable();
		
		this.add(userTableScrollPane, BorderLayout.CENTER); // Add the table
		
		prepareActionPanel();
		
		this.add(actionPanel, BorderLayout.SOUTH);
		
	}
	
	private void prepareActionPanel()
	{
		/* 
			Prepares all of the buttons at the bottom of the panel 
			each button has its background set to the correct colour and then
			is disabled. (They are enabled after the database has been decrypted)
			They are added to the actionPanel with 5pt space between them.
		*/
		actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.LINE_AXIS));
		
		registerButton.addActionListener(this);
		registerButton.setBackground(new Color(169,196,235)); // Blue
		registerButton.setEnabled(false);
		actionPanel.add(registerButton);
		
		actionPanel.add(Box.createRigidArea(new Dimension(5,0)));
		
		produceReportsButton.addActionListener(this);
		produceReportsButton.setBackground(new Color(169,196,235));
		produceReportsButton.setEnabled(false);
		actionPanel.add(produceReportsButton);
		
		actionPanel.add(Box.createRigidArea(new Dimension(5,0)));
		
		// Adds the first name search text field and button
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
		/* Prepares the table to display the user data */
		
		userTable.setDefaultEditor(Object.class, null); // Disable editing
		
		// Hide the first column as it contains the id and we don't want that displayed to the user
		TableColumnModel tcm = userTable.getColumnModel();
		tcm.removeColumn(tcm.getColumn(0));
		
		// Set the renderer for each column of the table to be the wordwrap renderer so that the
		// contents of the cells are automatically wrapped.
		
		for (int i = 0; i < userTable.getColumnCount(); i++)
		{
			tcm.getColumn(i).setCellRenderer(new WordWrapCellRenderer());
			tcm.getColumn(i).setHeaderRenderer(new WordWrapHeaderRenderer());
		}
		// Add a column model listener so that we can detect when the columns are resized.
		tcm.addColumnModelListener(this);
		
		// Set the correct widths for all of the columns so that they are displayed correctly.
		tcm.getColumn(0).setMaxWidth(120); // Username
		tcm.getColumn(1).setMaxWidth(120); // Password
		tcm.getColumn(2).setMaxWidth(120); // First Name
		tcm.getColumn(3).setMaxWidth(120); // Last Name
		tcm.getColumn(4).setMaxWidth(120); // Date of birth
		tcm.getColumn(5).setMaxWidth(120); // Phone Number
		
		tcm.getColumn(0).setMinWidth(120); // Username
		tcm.getColumn(1).setMinWidth(120); // Password
		tcm.getColumn(2).setMinWidth(120); // First Name
		tcm.getColumn(3).setMinWidth(120); // Last Name
		tcm.getColumn(4).setMinWidth(120); // Date of birth
		tcm.getColumn(5).setMinWidth(120); // Phone Number
		
		populateTable(users.getArray()); // Fill the table with data
	}
	
	private void prepareSearchPanel()
	{
		/* Prepares the panel that allows the user to search through the users */
		
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.LINE_AXIS)); // Set a horizontal box layout
		
		// Add the label and text field with 5pt between them.
		
		searchPanel.add(firstNameSearchLabel);
		searchPanel.add(Box.createRigidArea(new Dimension(5,0)));
		searchPanel.add(firstNameSearchTextField);
		searchPanel.add(Box.createRigidArea(new Dimension(5,0)));
		
		// Prepare the button
		searchButton.addActionListener(this);
		searchButton.setBackground(new Color(169,196,235)); // Blue
		searchButton.setEnabled(false); // Disable it - it'll be enabled when the user database is decrypted
		
		firstNameSearchTextField.addActionListener(this); // Add an action listener so it can be detected when enter is pressed in the search field
		
		searchPanel.add(searchButton); // Add the search button to the search panel
	}
	
	private void populateTable(User[] data)
	{
		/* Populates the table with the data passed in as the parameter. */
		
		System.out.println("[INFO] <USER_PANEL> Running populateTable");
		
		// Add each user to the table row by row
		userTableModel.setRowCount(0); // Start at the first row
		
		for (int i = 0; i < data.length; i++) // For each user in the array
		{
		
			// Check that there is a user in position i of the array
			if (data[i] != null)
			{
				User u = data[i];
				// Covert the user data to a string array so that it can be displayed in the table.
				String[] userDataArray = new String[] {u.getID(), u.getUsername(), u.getPassword(), u.getFirstName(), u.getLastName(),
											   u.getDateOfBirth(), u.getPhoneNumber(), u.getSessionsAttendedString().replace(".", " ")};
				
				userTableModel.addRow(userDataArray); // Add the user to the table
			}
		}
		
		resizeRows(); // Call the row resize method so that all of the rows are the correct size
	}
	
	private void createNewUser()
	{
		/* A series of dialog prompts that allows the admin to add a new user */
		
		String id = users.getFreeID(); // Get an id for the user
		
		String username = getUsername(); // Get a username from the admin
		
		// Blocks of code like this one are after every prompt. If the data returned is null,
		// the user pressed cancel and so the return statement cancels the process of adding a user.
		if (username == null)
		{
			return;
		}
		
		// Ask for a password
		String password = JOptionPane.showInputDialog(this,"Please enter a password");
		
		if (password == null)
		{
			return;
		}
		
		// Ask for a first name
		String firstName = JOptionPane.showInputDialog(this,"Please enter your first name");
		
		if (firstName == null)
		{
			return;
		}
		
		// Ask for a last name
		String lastName = JOptionPane.showInputDialog(this,"Please enter your last name");
		
		if (lastName == null)
		{
			return;
		}
		
		// Ask for a date of birth
		String dateOfBirth = JOptionPane.showInputDialog(this,"Please enter your date of birth as dd-mm-yyyy");
		
		if (dateOfBirth == null)
		{
			return;
		}
		
		// Ask for a phone number
		String phoneNumber = JOptionPane.showInputDialog(this,"Please enter your phone number");
			
		if (phoneNumber == null)
		{
			return;
		}
		
		// Create a new user object with attributes the same as the user entered and add it to the user list.
		users.addUser(new User(id,username,password,firstName,lastName,dateOfBirth,phoneNumber, false, new String[0], new QuestionStatList()));
		
		
		// Tell the user that a user was successfully added
		JOptionPane.showMessageDialog(this, "User added!");
		
		// Refresh the table
		populateTable(users.getArray());
	}
	
	public void conditionalSaveChanges()
	{
		/* Saves changes if there has been an edit without saving */
		
		if (edited)
		{
			int result = JOptionPane.showConfirmDialog(this, "Do you want to save the changes you have made to the users?", "Unsaved changes", JOptionPane.YES_NO_OPTION);
			
			if (result == JOptionPane.YES_OPTION)
			{
				saveChanges();
			}
		}
	}
	
	private void saveChanges()
	{
		/* Saves the changes that the user has made to the table */
		
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
				JOptionPane.showMessageDialog(this, u.getFirstName() + " username already in use.", ERROR_SAVING_CHANGES, JOptionPane.ERROR_MESSAGE);
				return; // Exit the method
			}
			
			if (newPassword.contains(","))
			{
				JOptionPane.showMessageDialog(this, u.getFirstName() + " please remove commas from password.", ERROR_SAVING_CHANGES, JOptionPane.ERROR_MESSAGE);
				return; // Exit the method
			}
			
			if (!validateDate(newDateOfBirth))
			{
				JOptionPane.showMessageDialog(this, u.getFirstName() + ": Invaid date of birth", ERROR_SAVING_CHANGES, JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (!validatePhoneNumber(newPhoneNumber))
			{
				JOptionPane.showMessageDialog(this, u.getFirstName() + ": Invaid phone number", ERROR_SAVING_CHANGES, JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (!newSessionsAttended.isEmpty())
			{
				for (String session : newSessionsAttended.split(" "))
				{
					if (!validateDate(session)) // If one of the dates is incorrect
					{
						JOptionPane.showMessageDialog(this, u.getFirstName() + " " + session + ": Invaid date (Sessions attended)", ERROR_SAVING_CHANGES, JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
			}
			
			// commas and || are not allowed as they could break the database therefore we should remove them.
			String[] newUserData = {newUsername, newPassword, newFirstName, newLastName, newDateOfBirth, newPhoneNumber, newSessionsAttended};
			for (int i = 0; i < newUserData.length; i++)
			{
				newUserData[i] = newUserData[i].replace(",","").replace("\\|", "");
			}
			
			String[] sessionsAttendedArray = new String[0];
			if (newUserData[6].length() > 5) // If there is a date
			{
				sessionsAttendedArray = newUserData[6].split(" ");
			}
			
			// Update all of the user's attributes
			u.setUsername(newUserData[0]);
			u.setPassword(newUserData[1]);
			u.setFirstName(newUserData[2]);
			u.setLastName(newUserData[3]);
			u.setDateOfBirth(newUserData[4]);
			u.setPhoneNumber(newUserData[5]);
			u.setSessionsAttended(sessionsAttendedArray);
			
			
			edited = false;
			
		}
		
		// We've not hit a return therefore everything must have been correct therefore
		// write the changes to file.
		
		// Ask if they want to change the encryption key
		int changeKey = JOptionPane.showConfirmDialog(this, "Would you like to change the encryption key?", "Change key?", JOptionPane.YES_NO_OPTION);
		
		// If they selected yes
		if (changeKey == 0)
		{
			// Ask for a new key
			String newKey = JOptionPane.showInputDialog(this, "Please enter a new key", "Key?");
			
			// If they entered a key
			if (newKey != null && !newKey.isEmpty())
			{	
				// Set the new key as the encryption key
				users.setKey(newKey);
			}
		}
		
		users.writeDatabase();
		
		// Tell the user that the database was saved
		JOptionPane.showMessageDialog(this, "Database saved!");
		
	}
	
	private boolean validatePhoneNumber(String phoneNumber)
	{
		/* Validates a phone number using regex */
		return Pattern.matches("^(((\\+44\\s?\\d{4}|\\(?0\\d{4}\\)?)\\s?\\d{3}\\s?\\d{3})|((\\+44\\s?\\d{3}|\\(?0\\d{3}\\)?)\\s?\\d{3}\\s?\\d{4})|((\\+44\\s?\\d{2}|\\(?0\\d{2}\\)?)\\s?\\d{4}\\s?\\d{4}))(\\s?\\#(\\d{4}|\\d{3}))?$", phoneNumber);
		
	}
	
	private boolean validateDate(String date)
	{
		/* Checks if a date is in yyyy-mm-dd */
		
		boolean pass = true;
		
		// Length check
		if (date.length() != 10)
		{
			pass = false;
		}
		
		// Split into day, month, and year
		String[] splitDate = date.split("-");
		
		// In a try catch in case the user didn't enter numbers
		try
		{
			// Convert the day month and year to ints
			int day = Integer.parseInt(splitDate[0]);
			int month = Integer.parseInt(splitDate[1]);
			int year = Integer.parseInt(splitDate[2]);
			
			
			// Range check
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
	
	public String getUsername()
	{
		/* Gets a unique username from the user */
		
		String username = "";
		
		boolean success = false; 
			
		while (!success) // While a unique username hasn't been chosen yet
		{
			// Ask for a username
			username = JOptionPane.showInputDialog(this, "Please enter a username");
			
			if (users.getUserByUsername(username) == null) // If a unique username has been chosen
			{
				success = true;
			}
			else
			{
				// Tell the user that they need to pick a new one
				JOptionPane.showMessageDialog(this, "Username already taken!", "Username taken", JOptionPane.ERROR_MESSAGE);
			}
		}	
		
		return username;
	}
	
	private void editTable()
	{
		/* Enables editing of the table */
		
		userTable.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField())); // Enable editing
		edited = true;
	}
	
	private void deleteUser()
	{
		/* Deletes the user at the selected row index */
		
		int row = userTable.getSelectedRow();
		if (row != -1) // If they actually selected a row
		{
			String selectedID = (String) userTable.getModel().getValueAt(row, 0); // Get the userID
			users.removeUser(selectedID); // Delete the user that the ID corresponds to.
			populateTable(users.getArray()); // Refresh the table
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
				// Filter users by the name that the user has entered and display the data in the table
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
			
			// If they have selected at least 1 user
			if (userTable.getSelectedRows().length > 0)
			{
				System.out.println(userTable.getSelectedRows().length);
				runPrint();
			}
		}
		else if (evt.getSource() == firstNameSearchTextField)
		{
			// Clicks the search button when enter is pressed in the first name search text field
			searchButton.doClick();
		}
		else if (evt.getSource() == saveButton && users.isDecrypted())
		{
			saveChanges();
		}
	}
	
	private void produceReports()
	{
		/* Produces reports for multiple users at once */
		
		int[] selectedRows = userTable.getSelectedRows();
		reportPanels = new JPanel[selectedRows.length]; // Stores the reports that have been generated for the users
		
		// Iterate over the rows that have been selected.
		for (int i = 0; i < selectedRows.length; i++)
		{
			int row = selectedRows[i]; // The row number
			
			String selectedID = (String) userTable.getModel().getValueAt(row, 0); // Get the userID
			User u = users.getUserByID(selectedID); // Get the user corresponding to that id
			QuestionStatList questionStats = u.getQuestionStats(); // Get their question stat list
			String[][] reportData = questionStats.produceReport(questions); // Get the report data
			
			reportPanels[i] = new Report(reportData, u.getUsername()); // Produce a report from the data and add it to the array
		}
	}
	
	public int print(Graphics g, PageFormat pf, int page) throws PrinterException 
	{
		/* Prints the reports */
		
		if (page < reportPanels.length) // If we are printing a report that exists
		{
			JPanel report = reportPanels[page]; // Get the report
			
			JFrame reportFrame = new JFrame(); // Holds the report but is invisible
			
			// Setup the report window
			reportFrame.setSize(600,800);
			reportFrame.add(report);
			reportFrame.repaint();
			reportFrame.pack();
			report.setVisible(true);
			
			// Scale the report so that it remains the correct proportions but fully fills the width
			// of the paper size selected to be printed.
			
			Graphics2D g2d = (Graphics2D)g;
			g2d.translate(pf.getImageableX(), pf.getImageableY());
			
			double scaleFactor = pf.getImageableWidth()/report.getWidth();
			g2d.scale(scaleFactor, scaleFactor);

			// Print the reportTablePanel
			report.printAll(g);

			return PAGE_EXISTS;
		}
		else // No more pages to print
		{
			System.out.println("[INFO] <USER_PANEL> Printed reports");
			return NO_SUCH_PAGE;
		}

	}
	
	private void runPrint()
	{
		/* Setup for the printing process to start */
		System.out.println("[INFO] <USER_PANEL> Running runPrint");
		
		produceReports();
		
		PrinterJob job = PrinterJob.getPrinterJob();
		
		PageFormat pf = job.defaultPage();
		pf.setOrientation(PageFormat.PORTRAIT); // Make the print job portrait
		
		job.setPrintable(this, pf);
		
		boolean doPrint = job.printDialog(); // Show the user a print dialog
		
		// If the user wants to print try and print the reports
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
		/* Adjusts the heights of the rows so that all of the text in them can be read */
		
		// Iterate over the rows
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
		/* When the user resizes a column, resize the rows */
		resizeRows();
	}
	public void columnAdded(TableColumnModelEvent e) {/* required for column listener */}
	
	public void columnRemoved(TableColumnModelEvent e) {/* required for column listener */}
	
	public void columnMoved(TableColumnModelEvent e) {/* required for column listener */}
	
	public void columnSelectionChanged(ListSelectionEvent e) {/* required for column listener */}
}
