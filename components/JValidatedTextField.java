package components;

import javax.swing.*;
import java.awt.*;

import java.util.regex.*;

public class JValidatedTextField extends JTextField implements JValidatedComponent, JSaveableComponent
{
	/* A saveable and validated text field */
	
	private String type; // Available types: email, phone number, ... none
	
	private String ERROR_STRING_EMAIL = "Email: Please enter a valid email address";
	private String ERROR_STRING_PHONE = "Phone number: Please enter a valid phone number";
	private String ERROR_STRING_GENERAL = "Text Field: Please fill in the field";
	
	public JValidatedTextField(String tempType)
	{
		/* Creates a new text field, or loads the text field from its save string */
		
		// Temp type can either be a save string or just the type
		
		if (tempType.contains(":")) // If it's a save string
		{
			// saveString is formatted like this
			// textfield:enteredText;type
			
			String[] textFieldData = tempType.split(":")[1].split(";"); // Split the data
			
			String enteredText = StringEscaper.unescape(textFieldData[0]); // Get the entered text and unescape it
			
			tempType = textFieldData[1]; // Get the type 
			
			setText(enteredText);
		}
		
		
		type = tempType;
	}
	
	public boolean validateAnswer()
	{
		/* Validates the users entry based on what type the text field is */
		
		boolean passed = false;
		
		if (type.equals("email"))
		{
			System.out.println("Validating email");
			passed = validateEmail();
		}
		else if (type.equals("phone"))
		{
			passed = validatePhoneNumber();
		}
		else
		{
			// If the text field has no particular validation type, do a presence check
			passed = !this.getText().equals("");
		}
		
		return passed;
	}
	
	public boolean presenceCheck()
	{
		/* Performs a presence check */
		
		return !this.getText().trim().isEmpty();
	}
	
	private boolean validateEmail()
	{
		/* Uses regex to validate the email */
		
		return Pattern.matches("(?:[a-z0-9!#$%&\'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&\'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])", this.getText());
	}
	
	private boolean validatePhoneNumber()
	{
		/* Uses regex to validate a phone number */
		
		return Pattern.matches("^(((\\+44\\s?\\d{4}|\\(?0\\d{4}\\)?)\\s?\\d{3}\\s?\\d{3})|((\\+44\\s?\\d{3}|\\(?0\\d{3}\\)?)\\s?\\d{3}\\s?\\d{4})|((\\+44\\s?\\d{2}|\\(?0\\d{2}\\)?)\\s?\\d{4}\\s?\\d{4}))(\\s?\\#(\\d{4}|\\d{3}))?$", this.getText());
		
	}
	
	public String getErrorString()
	{
		/* Returns the correct error string depending on the type of validation the text field has */
		String returnString;
		
		if (type.equals("email"))
		{
			returnString = ERROR_STRING_EMAIL;
		}
		else if (type.equals("phone"))
		{
			returnString = ERROR_STRING_PHONE;
		}
		else
		{
			returnString = ERROR_STRING_GENERAL;
		}
		
		return returnString;
	}
	
	public String toString()
	{
		/*  Returns a string that fully describes the text field. */
		
		String asString = "textfield:" + StringEscaper.escape(getText()) + ";" + type;
		
		return asString;
	}
}