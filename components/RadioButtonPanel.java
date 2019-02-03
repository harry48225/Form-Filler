package components;

import javax.swing.*;
import java.awt.*;

public class RadioButtonPanel extends JPanel implements JValidatedComponent, JSaveableComponent // A better class for managing check boxes
{
	/* A set of radiobuttons that is validated and can be saved */
	
	private JRadioButton[] buttons; 
	private ButtonGroup group;
	
	private final String ERROR_STRING = "Radiobuttons: Please select an option";
	
	public RadioButtonPanel (RadioButtonPanelBuilder builder)
	{
		buttons = builder.radioButtons;
		preparePanel();
	}
	
	public RadioButtonPanel(String saveString)
	{
		/* Recreates the radiobuttons from their save string */
		
		// String looks like radiobuttons:option.option.option. ... etc.
		String[] options = saveString.split(":")[1].split("\\."); // The options are delimted by a . The second item of the split string is the set of options
		
		buttons = new JRadioButton[options.length]; // Create a radio button array of the correct size
		
		// Recreate each saved button
		for (int i = 0; i < buttons.length; i++)
		{	
			String[] optionData = options[i].split(";");
			
			buttons[i] = new JRadioButton(StringEscaper.unescape(optionData[0])); // Unescape the text after loading
			buttons[i].setSelected(Boolean.parseBoolean(optionData[1]));
		}
		
		preparePanel();
	}
	
	private void preparePanel()
	{
		/* Prepares the panel to display the radiobuttons and adds them to it */
		
		System.out.println("[INFO] <RADIOBUTTON_PANEL> Running preparePanel"); // Debug
		
		this.setLayout(new GridLayout(0,2)); // Infinite rows 2 columns
		
		// Create a button group for the radio buttons
		group = new ButtonGroup();
		
		for (JRadioButton button : buttons) // Add each button to the array
		{
			this.add(button);
			group.add(button); // Add the buttons to the button group
		}
		
		// Calculate the number of rows
		int numberOfRows = (buttons.length + 1)/2;
		
		// Make the panel the correct size, 40px per row
		this.setPreferredSize(new Dimension(700,40*numberOfRows));
		this.setMaximumSize(new Dimension(700,50*numberOfRows));
	}
	
	public boolean validateAnswer()
	{
		/* Checks that the user has selected a button */
		return (group.getSelection() != null);
	}
	
	public boolean presenceCheck()
	{
		/* Performs a presence check, this is the same as the normal validation */
		return validateAnswer();
	}
	
	public static class RadioButtonPanelBuilder // Simplifies the creation of check box panels
	{
		public JRadioButton[] radioButtons = new JRadioButton[100]; // Store 100 options
		private int nextRadioButtonLocation = 0;
		
		public RadioButtonPanelBuilder add(String option)
		{
			/* Creates a new radio button with the option entered as a parameter */
			
			System.out.println("[INFO] <RADIOBUTTON_PANEL_BUILDER> Running add"); // Debug
			
			radioButtons[nextRadioButtonLocation] = new JRadioButton(option);
			nextRadioButtonLocation++;
			
			return this;
		}
		
		public RadioButtonPanel build()
		{
			/* Creates a new radio button panel with the added options */
			
			System.out.println("[INFO] <RADIOBUTTON_PANEL_BUILDER> Running build"); // Debug
			
			trimArray();
			
			return new RadioButtonPanel(this);
		}
		
		private void trimArray()
		{
			/* Trims the array to the correct length so that there are no null elements */
			
			System.out.println("[INFO] <RADIOBUTTON_PANEL_BUILDER> Running trimArray");
			
			JRadioButton[] newArray = new JRadioButton[nextRadioButtonLocation]; // Create a new array of the correct size
			
			for (int i = 0; i < nextRadioButtonLocation; i++) // For each object in the array
			{
				newArray[i] = radioButtons[i]; // Copy the object
			}
			
			radioButtons = newArray; // Store the new trimmed array in components
		}
	}
	
	private String getOptionsString()
	{
		/* Returns the options that the radio buttons have, and whether they are selected, as an escaped string */
		String options = "";
		
		for (JRadioButton button : buttons)
		{
			options += (StringEscaper.escape(button.getText()) + ";" + button.isSelected() + ".");
		}
		
		options = options.substring(0, options.length() - 1); // Trim off the trailing ,
		
		return options;
	}
	
	public String toString()
	{
		/* Returns an escaped string that fully describes the radiobuttons */
		String asString = "radiobuttons:";
		
		asString += getOptionsString();
		
		return asString;
	}
	
	public String getErrorString()
	{
		/* Returns the error string */
		
		return ERROR_STRING;
	}
}