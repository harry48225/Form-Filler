package components;

import javax.swing.*;
import java.awt.*;

public class RadioButtonPanel extends JPanel implements JValidatedComponent, JSaveableComponent // A better class for managing check boxes
{
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
		String[] options = saveString.split(":")[1].split("\\."); // The options are delimted by a .	
		
		buttons = new JRadioButton[options.length];
		for (int i = 0; i < buttons.length; i++)
		{	
			String[] optionData = options[i].split(";");
			
			buttons[i] = new JRadioButton(optionData[0]);
			buttons[i].setSelected(Boolean.parseBoolean(optionData[1]));
		}
		
		preparePanel();
	}
	
	private void preparePanel()
	{
		System.out.println("[INFO] <RADIOBUTTON_PANEL> Running preparePanel"); // Debug
		
		this.setLayout(new GridLayout(0,2)); // Infinite rows 2 columns
		
		group = new ButtonGroup();
		
		for (JRadioButton button : buttons) // Add each button to the array
		{
			this.add(button);
			group.add(button); // Add the buttons to the button group
		}
		
		this.setPreferredSize(new Dimension(700,100*buttons.length));
		this.setMaximumSize(new Dimension(700,120*buttons.length));
	}
	
	public boolean validateAnswer()
	{
		return (group.getSelection() != null);
	}
	
	public boolean presenceCheck()
	{
		return validateAnswer();
	}
	
	public static class RadioButtonPanelBuilder // Simplifies the creation of check box panels
	{
		public JRadioButton[] radioButtons = new JRadioButton[100]; // Store 100 options
		private int nextRadioButtonLocation = 0;
		
		public RadioButtonPanelBuilder add(String option)
		{
			System.out.println("[INFO] <RADIOBUTTON_PANEL_BUILDER> Running add"); // Debug
			
			radioButtons[nextRadioButtonLocation] = new JRadioButton(option);
			nextRadioButtonLocation++;
			
			return this;
		}
		
		public RadioButtonPanel build()
		{
			System.out.println("[INFO] <RADIOBUTTON_PANEL_BUILDER> Running build"); // Debug
			
			trimArray();
			
			return new RadioButtonPanel(this);
		}
		
		private void trimArray() // Trims the array to the correct length so that there are no null elements
		{
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
		String options = "";
		
		for (JRadioButton button : buttons)
		{
			options += (button.getText() + ";" + button.isSelected() + ".");
		}
		
		options = options.substring(0, options.length() - 1); // Trim off the trailing ,
		
		return options;
	}
	
	public String toString()
	{
		String asString = "radiobuttons:";
		
		asString += getOptionsString();
		
		return asString;
	}
	
	public String getErrorString()
	{
		return ERROR_STRING;
	}
}