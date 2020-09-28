package com.harry.formfiller.gui.question.component;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class JValidatedPasswordField extends JPanel implements JValidatedComponent, JSaveableComponent
{
	/* Saveable and validated password fields */
	
	JPasswordField[] passwordFields = {new JPasswordField(), new JPasswordField()}; // The two password fields
	
	private final String ERROR_STRING = "Passwords: Please enter two matching passwords";
	
	public JValidatedPasswordField()
	{
		preparePanel();
	}
	
	public JValidatedPasswordField(String saveString)
	{
		/* Loads a set of password fields from their savestring */
		
		// The save string is in the format
		// password:password1;password2
		
		String[] passwords = saveString.split(":")[1].split(";");
		
		// Load the passwords and unescape them if they are present
		if (passwords.length > 0) // If there are actually passwords saved
		{
            passwordFields[0].setText(StringEscaper.unescape(passwords[0]));
            
            if (passwords.length > 1) // If there is a second password
            {
                passwordFields[1].setText(StringEscaper.unescape(passwords[1]));
            }
		}
		
		preparePanel();
	}
	
	private void preparePanel()
	{
		/* Prepares the visual element of the password fields */
		
		this.setLayout(new GridLayout(2,1)); // Two rows 1 column
		
		for (JPasswordField passwordfield : passwordFields) // For each password field
		{
			this.add(passwordfield); // Add the password field
		}
	}
	
	public boolean validateAnswer()
	{
		/* Validates the passwords in the fields  */
		String password1 = new String(passwordFields[0].getPassword());
		String password2 = new String(passwordFields[1].getPassword());
		
		return password1.equals(password2) && !password1.equals(""); // Return whether the two password fields match and that they're not empty
	}
	
	public boolean presenceCheck()
	{
		/* Performs a presence check  */
		String password1 = new String(passwordFields[0].getPassword());
		String password2 = new String(passwordFields[1].getPassword());
		
		return !password1.trim().isEmpty() || !password2.trim().isEmpty(); // If either field is filled in
	}
	
	public String getErrorString()
	{
		/* Returns the error string */
		return ERROR_STRING;
	}
	
	public String toString()
	{
		/* Returns an escaped string that fully describes the password fields and their contents. */
		String asString = "password:" + StringEscaper.escape(new String(passwordFields[0].getPassword())) + ";" + StringEscaper.escape(new String(passwordFields[1].getPassword()));
		
		return asString;
	}
}
