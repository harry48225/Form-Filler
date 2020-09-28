package com.harry.formfiller.gui.question;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.harry.formfiller.gui.question.component.CheckBoxPanel;
import com.harry.formfiller.gui.question.component.JSaveableComponent;
import com.harry.formfiller.gui.question.component.JSaveableLabel;
import com.harry.formfiller.gui.question.component.JValidatedComboBox;
import com.harry.formfiller.gui.question.component.JValidatedComponent;
import com.harry.formfiller.gui.question.component.JValidatedDatePicker;
import com.harry.formfiller.gui.question.component.JValidatedFileChooser;
import com.harry.formfiller.gui.question.component.JValidatedLocationEntry;
import com.harry.formfiller.gui.question.component.JValidatedPasswordField;
import com.harry.formfiller.gui.question.component.JValidatedTextField;
import com.harry.formfiller.gui.question.component.RadioButtonPanel;

public class QuestionPanel extends JPanel implements JSaveableComponent
{
	/* Holds the graphical components of the question */
	
	private final String questionID; // Stores the id of the question that it belongs to.
	private JComponent[] components; // Stores the components in the array
	
	public QuestionPanel(QuestionPanelBuilder builder)
	{
		questionID = builder.questionID; // Store the id
		components = builder.components;
		setup(components); // Setup the window with the list of components
	}
	
	public QuestionPanel(String saveString)
	{
		/* Load's a questionPanel from its save string */
		
		// The save string is in this format
		// questionID$Component1||Component2
		// Extract all of the information
		
		String[] splitString = saveString.split("\\$");

		questionID = splitString[0];
		
		String[] componentList = splitString[1].split("\\|\\|");
		
		components = new JComponent[componentList.length];
		
		// Load each component in the string
		for (int i = 0; i < componentList.length; i++)
		{
			components[i] = importComponent(componentList[i]);
		}
		
		setup(components);
	}
	
	private JComponent importComponent(String componentString)
	{
		/* Imports a component from its savestring */
		// Save string is formatted as so <componentname>:<componentdata>
		
		// Get the component name
		String componentName = componentString.split(":")[0];
		
		JComponent component = null;
		
		
		// Create the correct type of component based on the component name
		switch (componentName)
		{
			case "label":
				component = new JSaveableLabel(componentString);
				break;
			case "textfield":
				component = new JValidatedTextField(componentString);
				break;
			case "password":
				component = new JValidatedPasswordField(componentString);
				break;
			case "combobox":
				component = new JValidatedComboBox(componentString);
				break;
			case "checkboxes":
				component = new CheckBoxPanel(componentString);
				break;
			case "filechooser":
				component = new JValidatedFileChooser(componentString);
				break;
			case "datepicker":
				component = new JValidatedDatePicker(componentString);
				break;
			case "radiobuttons":
				component = new RadioButtonPanel(componentString);
				break;
			case "locationentry":
				component = new JValidatedLocationEntry(componentString);
				break;
			default:
				break;
		}
		
		return component;
	}
	
	private void setup(JComponent[] components)
	{
		/* Adds all of the components to the question string */
		
		System.out.println("[INFO] <QUESTION_PANEL> Running setup");
		
		this.setLayout(new GridLayout(0,2));
		
		for (JComponent component : components) // For each component in the array
		{
			this.add(component); // Add it to the panel
		}

		// Make the panel the correct size
		this.setMaximumSize(new Dimension(700,300));
	}
	
	public QuestionPanel deepCopy()
	{
		/* Returns a deep copy of the question panel */
		
		return new QuestionPanel(toString()); // Return a copy of the question panel
	}
	
	public String getQuestionID()
	{
		/* Returns the id of the question that the panel belongs to */
		return questionID;
	}
	
	public int getQuestionIDNumber()
	{
		/* Get's just the id number of the question that the panel belongs to */
		return Integer.parseInt(questionID.replace("Q","")); // Remove the Q from the id and convert to int
	}
	
	@Override
	public JComponent[] getComponents()
	{
		/* Returns the array of components in the question panel */
		return components;
	}
	
	public boolean validateAnswers()
	{
		/* Validates what the user has entered into the question panel */
		boolean passed = true;
		
		
		// Iterate over the components in the question and validate the ones with validation available
		for (JComponent c : components) // For each component
		{
			if (c instanceof JValidatedComponent) // If it has validation available
			{
				JValidatedComponent validatedComponent = (JValidatedComponent) c; // Cast to JValidatedComponent
				if (validatedComponent.validateAnswer() == false) // If they got an answer wrong
				{
					passed = false;
				}				
			}
		}
		
		// Put a red border around the question panel if the validation check failed
		if (!passed)
		{
			this.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.red));
		}
		else
		{
			this.setBorder(null);
		}
		
		// Force a redraw
		this.revalidate();
		
		
		return passed;
	}
	
	public String getErrorString()
	{
		/* Returns the error strings of the components in the question panel
			concatenated together. */
			
		String errorString = "";
		
		// Append the error string of each validated component
		for (JComponent c : components)
		{
			if (c instanceof JValidatedComponent)
			{
				JValidatedComponent validatedComponent = (JValidatedComponent) c; // Cast to JValidatedComponent
				errorString = validatedComponent.getErrorString();
			}
		}
		
		return errorString;
	}
	
	public boolean presenceChecks()
	{
		/* Performs the presence check of each validated component in the panel */
		
		boolean passed = true;
		
		// Do a presence check on each validated component in the question panel
		for (JComponent c : components) // For each component
		{
			if (c instanceof JValidatedComponent) // If the component has validation available
			{
				JValidatedComponent validatedComponent = (JValidatedComponent) c; // Cast to JValidatedComponent
				if (validatedComponent.presenceCheck() == false) // If the question wasn't filled in
				{
					passed = false;
					this.setBorder(null);
				}				
			}
		}
		
		return passed;
	}
	
	public String toString()
	{
		/* Outputs a string which fully describes, and can be used to recreate, the question panel */
		
		String outputString = questionID + "$";
		
		// Append the save string of all of the saveable components in the question panel
		for (JComponent c : components)
		{
			if (c instanceof JSaveableComponent) // If the QuestionPanel can be saved
			{
				JSaveableComponent cSave = (JSaveableComponent) c;
				
				outputString += (cSave.toString() + "||"); // Append the save string of the component with a || between them
			}
		}
		
		outputString = outputString.substring(0, outputString.length() - 2); // Trim off the trailing ||
		
		return outputString;
	}
	
	public static class QuestionPanelBuilder
	{
		/* Simplifies the creation of question panels and allows them to be produced asynchronously */
	
	private final String questionID; // Must be set in constructor
		private JComponent[] components = new JComponent[10]; // Store 10 components
		private int nextComponentLocation = 0;
		
		public QuestionPanelBuilder(String tempQuestionID)
		{
			questionID = tempQuestionID;
			
		}
		
		public QuestionPanelBuilder add(JComponent component)
		{
			/* Adds a component to the panel */
			System.out.println("[INFO] <QUESTION_PANEL_BUILDER> Running add");
			
			// Set the size of the component
			component.setMaximumSize(new Dimension(700,300));
			components[nextComponentLocation] = component;
			nextComponentLocation++;
			
			
			return this; // Needed as the class is static
		}
		
		public QuestionPanel build()
		{
			/* Builds a QuestionPanel */
			System.out.println("[INFO] <QUESTION_PANEL_BUILDER> Running build");
			
			trimArray();
			
			return new QuestionPanel(this);
		}
		
		private void trimArray()
		{
			/* Trims the array to the correct length so that there are no null elements */
			
			System.out.println("[INFO] <QUESTION_PANEL_BUILDER> Running trimArray");
		
			JComponent[] newArray = new JComponent[nextComponentLocation]; // Create a new array of the correct size
			
			for (int i = 0; i < nextComponentLocation; i++) // For each object in the array
			{
				newArray[i] = components[i]; // Copy the object
			}
			
			components = newArray; // Store the new trimmed array in components
		}

	}
}