import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.*;

import components.*;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

import java.util.*;

public class QuestionPanel extends JPanel implements ActionListener, Serializable // Holds the components of the question
{
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
		// The save string is in this format
		// questionID$Component1||Component2
		
		String[] splitString = saveString.split("\\$");

		questionID = splitString[0];
		
		String[] componentList = splitString[1].split("\\|\\|");
		
		components = new JComponent[componentList.length];
		
		for (int i = 0; i < componentList.length; i++)
		{
			components[i] = importComponent(componentList[i]);
		}
		
		setup(components);
	}
	
	private JComponent importComponent(String componentString)
	{
		String componentName = componentString.split(":")[0];
		
		JComponent component = null;
		
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
		}
		
		return component;
	}
	
	private void setup(JComponent[] components) // Adds all the components to itself
	{
		System.out.println("[INFO] <QUESTION_PANEL> Running setup");
		
		this.setLayout(new GridLayout(0,2));
		
		for (JComponent component : components) // For each component in the array
		{
			this.add(component); // Add it to the panel
		}

	}
	
	public String getQuestionID()
	{
		return questionID;
	}

	public void actionPerformed(ActionEvent evt)
	{
		// To do
	}
	
	public JComponent[] getComponents()
	{
		return components;
	}
	
	public boolean validateAnswers() // Validates what the user has entered
	{
		boolean passed = true;
		
		for (JComponent c : components) // For each component
		{
			if (c instanceof JValidatedComponent)
			{
				JValidatedComponent validatedComponent = (JValidatedComponent) c; // Cast to JValidatedComponent
				if (validatedComponent.validateAnswer() == false) // If they got an answer wrong
				{
					passed = false;
				}				
			}
		}
		
		if (!passed)
		{
			this.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.red));
		}
		else
		{
			this.setBorder(null);
		}
		
		this.revalidate();
		return passed;
	}
	
	public String getErrorString()
	{
		String errorString = "";
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
		boolean passed = true;
		
		for (JComponent c : components) // For each component
		{
			if (c instanceof JValidatedComponent)
			{
				JValidatedComponent validatedComponent = (JValidatedComponent) c; // Cast to JValidatedComponent
				if (validatedComponent.presenceCheck() == false) // If they got an answer wrong
				{
					passed = false;
					this.setBorder(null);
				}				
			}
		}
		
		
		return passed;
	}
	
	public String toString() // Outputs a string which can be used to recreate the question panel
	{
		String outputString = questionID + "$";
		
		for (JComponent c : components)
		{
			if (c instanceof JSaveableComponent)
			{
				JSaveableComponent cSave = (JSaveableComponent) c;
				
				outputString += (cSave.toString() + "||");
			}
		}
		
		outputString = outputString.substring(0, outputString.length() - 2); // Trim off the trailing ||
		
		return outputString;
	}
	
	public static class QuestionPanelBuilder // Simplifies the creation of question panels
	{
		private final String questionID; // Must be set in constructor
		private JComponent[] components = new JComponent[10]; // Store 10 components
		private int nextComponentLocation = 0;
		
		public QuestionPanelBuilder(String tempQuestionID)
		{
			questionID = tempQuestionID;
			
		}
		
		public QuestionPanelBuilder add(JComponent component) // Adds a component to the panel
		{
			System.out.println("[INFO] <QUESTION_PANEL_BUILDER> Running add");
			
			components[nextComponentLocation] = component;
			nextComponentLocation++;
			
			
			return this; // Needed as the class is static
		}
		
		public QuestionPanel build() // Builds a QuestionPanel
		{
			System.out.println("[INFO] <QUESTION_PANEL_BUILDER> Running build");
			
			trimArray();
			
			return new QuestionPanel(this);
		}
		
		private void trimArray() // Trims the array to the correct length so that there are no null elements
		{
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