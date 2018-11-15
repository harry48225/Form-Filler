package components;

import javax.swing.*;
import java.awt.*;

public class JValidatedPasswordField extends JPanel implements JValidatedComponent
{
	JPasswordField[] passwordFields = {new JPasswordField(), new JPasswordField()}; // The two password fields
	
	public JValidatedPasswordField()
	{
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
}