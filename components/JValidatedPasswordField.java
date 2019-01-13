package components;

import javax.swing.*;
import java.awt.*;

public class JValidatedPasswordField extends JPanel implements JValidatedComponent, JSaveableComponent
{
	JPasswordField[] passwordFields = {new JPasswordField(), new JPasswordField()}; // The two password fields
	
	private final String ERROR_STRING = "Passwords: Please enter two matching passwords";
	
	public JValidatedPasswordField()
	{
		preparePanel();
	}
	
	public JValidatedPasswordField(String saveString)
	{
		// The save string is in the format
		// password:password1;password2
		
		String[] passwords = saveString.split(":")[1].split(";");
		
		if (passwords.length > 0) // If there are actually passwords saved
		{
            passwordFields[0].setText(passwords[0]);
            passwordFields[1].setText(passwords[1]);
		}
		
		preparePanel();
	}
	
	private void preparePanel()
	{
			this.setLayout(new GridLayout(2,1)); // Two rows 1 column
			
			for (JPasswordField passwordfield : passwordFields) // For each password field
			{
				this.add(passwordfield); // Add the password field
			}
	}
	
	public boolean validateAnswer()
	{
		String password1 = new String(passwordFields[0].getPassword());
		String password2 = new String(passwordFields[1].getPassword());
		
		return password1.equals(password2) && !password1.equals(""); // Return whether the two password fields match and that they're not empty
	}
	
	public boolean presenceCheck()
	{
		String password1 = new String(passwordFields[0].getPassword());
		String password2 = new String(passwordFields[1].getPassword());
		
		return !password1.trim().isEmpty() || !password2.trim().isEmpty(); // If either field is filled in
	}
	
	public String getErrorString()
	{
		return ERROR_STRING;
	}
	
	public String toString()
	{
		String asString = "password:" + new String(passwordFields[0].getPassword()) + ";" + new String(passwordFields[1].getPassword());
		
		return asString;
	}
}
