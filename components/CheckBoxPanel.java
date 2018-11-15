package components;

import javax.swing.*;
import java.awt.*;
public class CheckBoxPanel extends JPanel // A better class for managing check boxes
{
	private JCheckBox[] boxes; 
	
	public CheckBoxPanel (CheckBoxPanelBuilder builder)
	{
		boxes = builder.checkboxes;
		
		preparePanel();
	}
	
	private void preparePanel()
	{
		System.out.println("[INFO] <CHECKBOX_PANEL_BUILDER> Running preparePanel"); // Debug
		
		this.setLayout(new GridLayout(0,2)); // Infinite rows 2 columns
		
		for (JCheckBox box : boxes) // Add each check box to the array
		{
			this.add(box);
		}
	}

	public static class CheckBoxPanelBuilder // Simplifies the creation of check box panels
	{
		public JCheckBox[] checkboxes = new JCheckBox[100]; // Store 100 options
		private int nextCheckBoxLocation = 0;
		
		public CheckBoxPanelBuilder add(String option)
		{
			System.out.println("[INFO] <CHECKBOX_PANEL_BUILDER> Running add"); // Debug
			
			checkboxes[nextCheckBoxLocation] = new JCheckBox(option);
			nextCheckBoxLocation++;
			
			return this;
		}
		
		public CheckBoxPanel build()
		{
			System.out.println("[INFO] <CHECKBOX_PANEL_BUILDER> Running build"); // Debug
			
			trimArray();
			
			return new CheckBoxPanel(this);
		}
		
		private void trimArray() // Trims the array to the correct length so that there are no null elements
		{
			System.out.println("[INFO] <CHECKBOX_PANEL_BUILDER> Running trimArray");
			
			JCheckBox[] newArray = new JCheckBox[nextCheckBoxLocation]; // Create a new array of the correct size
			
			for (int i = 0; i < nextCheckBoxLocation; i++) // For each object in the array
			{
				newArray[i] = checkboxes[i]; // Copy the object
			}
			
			checkboxes = newArray; // Store the new trimmed array in components
		}
	}
}