package com.harry.formfiller.gui.question.component;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class CheckBoxPanel extends JPanel implements JSaveableComponent // A better class for managing check boxes
{
	/* A validated and saveable panel of checkboxes */
	
	private JCheckBox[] boxes; 
	
	public CheckBoxPanel (CheckBoxPanelBuilder builder)
	{
		boxes = builder.checkboxes;
		
		preparePanel();
	}
	
	
	public CheckBoxPanel (String saveString)
	{
		/* Loads checkboxes from their save string */
		
		String[] options = saveString.split(":")[1].split("\\."); // The options are delimted by a .
		
		boxes = new JCheckBox[options.length]; // Create a checkbox array of the correct length
		
		// Create a new checkbox from each checkbox in the string
		for (int i = 0; i < boxes.length; i++)
		{	
			String[] optionData = options[i].split(";");
			
			boxes[i] = new JCheckBox(StringEscaper.unescape(optionData[0])); // The label text is stored escaped we need to un escape it.
			boxes[i].setSelected(Boolean.parseBoolean(optionData[1])); // Make the checkbox selected if it was selected when it was saved
		}
		
		preparePanel();
		
		
	}
	
	private void preparePanel()
	{
		/* Prepares the checkboxes and the panel */
		
		System.out.println("[INFO] <CHECKBOX_PANEL_BUILDER> Running preparePanel"); // Debug
		
		this.setLayout(new GridLayout(0,2)); // Infinite rows 2 columns
		
		for (JCheckBox box : boxes) // Add each check box to the panel
		{
			this.add(box);
		}
		
		// Calculate the number of rows
		int numberOfRows = (boxes.length + 1)/2;
		
		// Make the panel the correct size of the number of checkboxes that it has, 40px for each checkbox
		this.setPreferredSize(new Dimension(700,40*numberOfRows));
		this.setMaximumSize(new Dimension(700,50*numberOfRows));
	}

	public static class CheckBoxPanelBuilder
	{
		 /* Simplifies the creation of check box panels */
		 
		public JCheckBox[] checkboxes = new JCheckBox[100]; // Store 100 options
		private int nextCheckBoxLocation = 0;
		
		public CheckBoxPanelBuilder add(String option)
		{
			/* Adds a checkbox to the panel */
			
			System.out.println("[INFO] <CHECKBOX_PANEL_BUILDER> Running add"); // Debug

			checkboxes[nextCheckBoxLocation] = new JCheckBox(option);
			nextCheckBoxLocation++;
			
			return this;
		}
		
		public CheckBoxPanel build()
		{
			/* Creates a new checkbox panel with the entered options */
			
			System.out.println("[INFO] <CHECKBOX_PANEL_BUILDER> Running build"); // Debug
			
			trimArray();
			
			return new CheckBoxPanel(this);
		}
		
		private void trimArray()
		{
			/* Trims the array to the correct length so that there are no null elements */
			
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
		/* Returns each of the options and whether they are selected or not as an escaped string */
		
		String options = "";
		
		// Add each checkbox to the string
		for (JCheckBox checkBox : boxes)
		{
			options += (StringEscaper.escape(checkBox.getText()) + ";" + checkBox.isSelected() + ".");
		}
		
		options = options.substring(0, options.length() - 1); // Trim off the trailing ,
		
		return options;
	}
	
	public String toString()
	{
		/* Returns an escaped string that fully describes the checkbox panel */
		
		String asString = "checkboxes:";
		
		asString += getOptionsString();
		
		return asString;
	}
}