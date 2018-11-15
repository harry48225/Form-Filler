import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.time.*;

public class Register extends JFrame implements ActionListener
{
	private UserList users;
	
	private JCheckBox[] attendanceBoxes;
	
	private JButton saveButton = new JButton("Save register");
	
	public Register(UserList tempUsers)
	{
		users = tempUsers;
		
		prepareGUI();
	}
	
	private void prepareGUI()
	{
		System.out.println("[INFO] <REGISTER> Running prepareGUI");
		
		this.setLayout(new GridLayout(0,1));
		this.setSize(300,300);
		
		this.setTitle("Register");
		
		prepareCheckboxes();
		
		saveButton.addActionListener(this);
		
		this.add(saveButton);
		
		this.setVisible(true);
	}
	
	private void prepareCheckboxes()
	{	
		User[] allUsers = users.getUsers();
		
		attendanceBoxes = new JCheckBox[allUsers.length];
		
		for (int i = 0; i < allUsers.length; i++) // For each user
		{
			attendanceBoxes[i] = new JCheckBox(allUsers[i].getFirstName() + " " + allUsers[i].getLastName()); // Make a new check box with their full name
			
			this.add(attendanceBoxes[i]); // Add the check box to the window
		}
	}
	
	private void addAttendance() // If the user's checkbox is ticked they have todays date added to their sessionsPresentAt array
	{		
		for (int i = 0; i < attendanceBoxes.length; i++) // For each box
		{
			if (attendanceBoxes[i].isSelected()) // If it's checked
			{
				users.getUsers()[i].addPresentToday(); // Add the date to the users sessionsPresentAtArray
			}
		}
		
		users.writeDatabase();
		
	}
	
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == saveButton)
		{
			System.out.println("[INFO] <REGISTER> saveButton pressed");
			
			addAttendance();
		}
	}
}