package components;

import javax.swing.*;
import java.awt.*;

public class RadioButtonPanel extends JPanel implements JValidatedComponent // A better class for managing check boxes
{
	private JRadioButton[] buttons; 
	private ButtonGroup group;
	
	public RadioButtonPanel (RadioButtonPanelBuilder builder)
	{
		buttons = builder.radioButtons;
		group = new ButtonGroup();
		preparePanel();
	}
	
	private void preparePanel()
	{
		System.out.println("[INFO] <RADIOBUTTON_PANEL_BUILDER> Running preparePanel"); // Debug
		
		this.setLayout(new GridLayout(0,2)); // Infinite rows 2 columns
		
		for (JRadioButton button : buttons) // Add each button to the array
		{
			this.add(button);
			group.add(button); // Add the buttons to the button group
		}
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
}