package components;

import javax.swing.*;
import java.awt.*;


public class CheckBoxPanel extends JPanel implements JSaveableComponent // A better class for managing check boxes
{
	private JCheckBox[] boxes; 
	
	public CheckBoxPanel (CheckBoxPanelBuilder builder)
	{
		boxes = builder.checkboxes;
		
		preparePanel();
	}
	
	
	public CheckBoxPanel (String saveString)
	{
		String[] options = saveString.split(":")[1].split("\\."); // The options are delimted by a .
		
		System.out.println(saveString.split(":")[1]);
		
		boxes = new JCheckBox[options.length];
		
		for (int i = 0; i < boxes.length; i++)
		{
			System.out.println(options[i]);
			
			String[] optionData = options[i].split(";");
			
			boxes[i] = new JCheckBox(optionData[0]);
			boxes[i].setSelected(Boolean.parseBoolean(optionData[1]));
		}
		
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
		
		int numberOfRows = (boxes.length + 1)/2;
		
		this.setPreferredSize(new Dimension(700,40*numberOfRows));
		this.setMaximumSize(new Dimension(700,50*numberOfRows));
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
	
	private String getOptionsString()
	{
		String options = "";
		
		for (JCheckBox checkBox : boxes)
		{
			options += (checkBox.getText() + ";" + checkBox.isSelected() + ".");
		}
		
		options = options.substring(0, options.length() - 1); // Trim off the trailing ,
		
		return options;
	}
	
	public String toString()
	{
		String asString = "checkboxes:";
		
		asString += getOptionsString();
		
		return asString;
	}
}