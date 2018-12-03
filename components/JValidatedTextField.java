package components;

import javax.swing.*;
import java.awt.*;

import java.util.regex.*;

public class JValidatedTextField extends JTextField implements JValidatedComponent
{
	private String type; // Available types: email, phone number, ... none
	
	private String ERROR_STRING_EMAIL = "Email: Please enter a valid email address";
	private String ERROR_STRING_PHONE = "Phone number: Please enter a valid phone number";
	private String ERROR_STRING_GENERAL = "Text Field: Please fill in the field";
	
	public JValidatedTextField(String tempType)
	{
		type = tempType;
	}
	
	public boolean validateAnswer()
	{
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
			passed = !this.getText().equals("");
		}
		
		return passed;
	}
	
	public boolean presenceCheck()
	{
		return !this.getText().trim().isEmpty();
	}
	
	private boolean validateEmail()
	{
		return Pattern.matches("(?:[a-z0-9!#$%&\'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&\'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])", this.getText());
	}
	
	private boolean validatePhoneNumber()
	{
		return Pattern.matches("^(((\\+44\\s?\\d{4}|\\(?0\\d{4}\\)?)\\s?\\d{3}\\s?\\d{3})|((\\+44\\s?\\d{3}|\\(?0\\d{3}\\)?)\\s?\\d{3}\\s?\\d{4})|((\\+44\\s?\\d{2}|\\(?0\\d{2}\\)?)\\s?\\d{4}\\s?\\d{4}))(\\s?\\#(\\d{4}|\\d{3}))?$", this.getText());
		
	}
	
	public String getErrorString()
	{
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
}