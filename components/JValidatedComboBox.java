package components;

import javax.swing.*;
import java.awt.*;

public class JValidatedComboBox extends JComboBox<String> implements JValidatedComponent
{
	private final String ERROR_STRING = "Drop-down: Please select an option";
	
	public JValidatedComboBox(String[] options)
	{
		super(options);
	}
	public boolean validateAnswer()	// Validates the answer
	{
		return (getSelectedIndex() != 0);
	}
	
	public boolean presenceCheck()
	{
		return validateAnswer();
	}
	
	public String getErrorString()
	{
		return ERROR_STRING;
	}
}