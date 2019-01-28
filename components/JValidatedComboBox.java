package components;

import javax.swing.*;
import java.awt.*;

public class JValidatedComboBox extends JComboBox<String> implements JValidatedComponent, JSaveableComponent
{
	private String[] options;
	
	private final String ERROR_STRING = "Drop-down: Please select an option";
	
	public JValidatedComboBox(String[] tempOptions)
	{
		options = tempOptions;

		addOptions();
	}
	
	public JValidatedComboBox(String saveString)
	{
		// Loads a saved combobox
		
		// Date should look like
		// combobox:option.option.option;selectedIndex
		String[] componentData = saveString.split(":")[1].split(";");

		options = componentData[0].split("\\.");
		
		addOptions();
		
		int selectedIndex = Integer.parseInt(componentData[1]);
		
		setSelectedIndex(selectedIndex); // Select the selected index
	}
	
	private void addOptions()
	{
		for (String option : options)
		{
			addItem(StringEscaper.unescape(option)); // Each option is escaped so it needs to be unescaped before being added
		}
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
	
	private String getOptionsString()
	{
		String optionString = "";
		
		for (String option : options)
		{
			optionString += StringEscaper.escape(option) + ".";
		}
		
		optionString = optionString.substring(0, optionString.length() - 1); // Trim off the trailing ,
		
		return optionString;
	}
	
	public String toString()
	{
		// Returns a string that fully describes the combobox
		String asString = "combobox:";
		
		asString += getOptionsString();
		
		asString += (";" + getSelectedIndex());
		
		return asString;
	}

}