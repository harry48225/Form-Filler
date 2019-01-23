import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.time.*;
import java.util.Date;
import java.util.List;

public class Register extends JFrame implements ActionListener
{
	private UserList users;
	
	private JPanel userPanel = new JPanel();
	private JScrollPane userPanelScroller = new JScrollPane(userPanel);
	private JCheckBox[] attendanceBoxes;
	
	private JButton saveButton = new JButton("Save register");
	
	public Register(UserList tempUsers, List<Image> icons)
	{
		users = tempUsers;
		
		this.setIconImages(icons);
		prepareGUI();
	}
	
	private void prepareGUI()
	{
		System.out.println("[INFO] <REGISTER> Running prepareGUI");
		
		this.setLayout(new BorderLayout());
		this.setSize(300,400);
		this.setMinimumSize(new Dimension(300,400));
		this.setLocationRelativeTo(null); // Center it
		
		this.setTitle("Register");
		
		prepareCheckboxes();
		
		saveButton.addActionListener(this);
		saveButton.setBackground(new Color(130,183,75));
		
		this.add(userPanelScroller, BorderLayout.CENTER);
		this.add(saveButton, BorderLayout.SOUTH);
		
		this.setVisible(true);
	}
	
	private void prepareCheckboxes()
	{	
		userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.PAGE_AXIS));
		User[] allUsers = getUserOrder();
		
		attendanceBoxes = new JCheckBox[allUsers.length];
		
		for (int i = 0; i < allUsers.length; i++) // For each user
		{
			attendanceBoxes[i] = new JCheckBox(allUsers[i].getFirstName() + " " + allUsers[i].getLastName()); // Make a new check box with their full name
			attendanceBoxes[i].setName(allUsers[i].getUsername());
			userPanel.add(attendanceBoxes[i]); // Add the check box to the window
			userPanel.add(Box.createRigidArea(new Dimension(0,5))); // Add some padding
		}
	}
	
	private void addAttendance() // If the user's checkbox is ticked they have todays date added to their sessionsPresentAt array
	{		
		for (int i = 0; i < attendanceBoxes.length; i++) // For each box
		{
			if (attendanceBoxes[i].isSelected()) // If it's checked
			{
				// Each checkbox is called the username of the user that's for
				// therefore this line will get the user that the box corressponds with.
				User u =  users.getUserByUsername(attendanceBoxes[i].getName());
				System.out.println("Adding present" + u.getUsername());
				u.addPresentToday(); // Add the date to the users sessionsPresentAtArray
			}
		}
		
		users.writeDatabase();
		
	}
	
	private User[] getUserOrder() // Returns a list of users with the users that have attended the most recent session at the top
	{
		users.sortByFirstName();
		
		long DAY_IN_MS = 1000 * 60 * 60 * 24;
		Date twoWeeksAgo = new Date(System.currentTimeMillis() - (15 * DAY_IN_MS)); // Calculate the date two weeks ago
		
		User[] allUsers = users.getUsers();
		
		User[] topUsersInRegister = new User[allUsers.length];
		User[] restOfUsersInRegister = new User[allUsers.length];
		
		int nextTopUsersLocation = 0;
		int nextRestOfUsersLocation = 0;
		
		
		// Look through all of the users and put the ones that were present in the last two
		// weeks at the top and the ones that weren't at the bottom.
		for (User u : allUsers) // For each user
		{
			boolean presentRecently = false;
			for (String session : u.getSessionsAttended()) // For each session
			{
				String[] splitSession = session.split("-");
				int day = Integer.parseInt(splitSession[0]);
				int month = Integer.parseInt(splitSession[1]);
				int year = Integer.parseInt(splitSession[2]);
				Date sessionDate = new Date(year, month, day);
				
				if (sessionDate.after(twoWeeksAgo)) // If the date is after two weeks ago
				{
					presentRecently = true;
					break; // No need to continue comparisons
				}
			}
			
			if (presentRecently)
			{
				topUsersInRegister[nextTopUsersLocation] = u;
				nextTopUsersLocation++;
			}
			else
			{
				restOfUsersInRegister[nextRestOfUsersLocation] = u;
				nextRestOfUsersLocation++;
			}
		}
		
		// We need to convert both arrays into a single, longer, array
		User[] finalUserArray = new User[allUsers.length];
		
		for (int i = 0; i < finalUserArray.length; i++)
		{
			if (i >= nextTopUsersLocation)
			{
				finalUserArray[i] = restOfUsersInRegister[i - nextTopUsersLocation];
			}
			else
			{
				finalUserArray[i] = topUsersInRegister[i];
			}
		}
		
		return finalUserArray;
	}
	
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == saveButton)
		{
			System.out.println("[INFO] <REGISTER> saveButton pressed");
			
			addAttendance();
			
			this.dispose();
		}
	}
}