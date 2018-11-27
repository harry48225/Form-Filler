import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import components.*;

import java.util.*;

public class FormDisplayer extends JFrame implements ActionListener, MouseListener, WindowListener
{
	
	private Form form;
	private QuestionPanel[] questionPanels;
	
	private GUI gui;
	
	private User currentUser;
	private UserList users;
	
	private JPanel formPanel = new JPanel();
	private JButton submitButton = new JButton("Submit");
	
	private String questionLostFocus = ""; // The question that has most recently lost focus
	private long questionStartTime = 0; // Store the time is milliseconds that the user started attempting the question
	private long[] timeToCompleteQuestions; // Stores the length of time that each question has been focused for
	
	private int[] failedValidationChecks; // Stores the amount of time the user has failed the validation check for each question
	
	public FormDisplayer(Form tempF, QuestionPanel[] tempQuestionPanels, User tempU, UserList tempUserList, GUI tempGUI)
	{
		form = tempF;
		questionPanels = tempQuestionPanels;
		currentUser = tempU;
		users = tempUserList;
		gui = tempGUI;
		
		int numberOfQuestions = form.getQuestionIDs().length;
		
		failedValidationChecks = new int[numberOfQuestions]; // Create a new int array with an element for each question of the form
		timeToCompleteQuestions = new long[numberOfQuestions];
		
		questionLostFocus = form.getQuestionIDs()[0]; // Make the first question in focus
		questionStartTime = System.nanoTime(); // Start the clock
		
		prepareGUI();
		
	}
	
	private void prepareGUI()
	{
		System.out.println("[INFO] <FORM_DISPLAYER> Running prepareGUI");
		
		this.addWindowListener(this);
		
		this.setLayout(new BorderLayout()); // Set a grid layout
		this.setSize(300,300); // Set the size
		
		submitButton.addActionListener(this);
		
		formPanel.setLayout(new GridLayout(0,1)); // Set a grid layout
			
		for (QuestionPanel question : questionPanels) // For each question in the form
		{
			question.addMouseListener(this); // Add the formDisplayer as a mouse listener
			
			for (JComponent component : question.getComponents())
			{
				component.addMouseListener(this); // Add a mouse listener to each component in the QuestionPanel
			}
			
			formPanel.add(question); // Get and add the panel
		}
		
		this.add(formPanel, BorderLayout.CENTER);
		this.add(submitButton, BorderLayout.SOUTH);
		
		this.setVisible(true);
	}
	
	private void validateQuestions()
	{
		
		System.out.println("Users question stats before validation: " + currentUser.getQuestionStats());
		
		
		boolean allCorrect = true;
		
		for (int i = 0; i < questionPanels.length; i++) // For each question in the form
		{
			QuestionPanel questionPanel = questionPanels[i];
			
			currentUser.getQuestionStats().getQuestionStatByID(questionPanel.getQuestionID()).addAttempt();
			
			boolean passed = questionPanel.validateAnswers(); // Validates the answer
			
			if (!passed)
			{
				allCorrect = false;
				currentUser.getQuestionStats().getQuestionStatByID(questionPanel.getQuestionID()).addFailedValidation();
				
				failedValidationChecks[i] += 1; // Add to the failed counter.
			}
		}
		
		System.out.println("Users question stats after validation: " + currentUser.getQuestionStats());
		
		if (allCorrect)
		{
			// Store the number of times that it's taken the user to correct an error
			
			for (int i = 0; i < questionPanels.length; i++)
			{
				int amountFailed = failedValidationChecks[i];
				long timeTakenToComplete = timeToCompleteQuestions[i];
				
				String questionID = questionPanels[i].getQuestionID();
				
				if (amountFailed > 0) // If they failed at least once
				{
					currentUser.getQuestionStats().getQuestionStatByID(questionID).addNumberOfAttemptsNeededToCorrect(amountFailed); // Get the question stat for the question at add the number of attempts failed
				}
				
				currentUser.getQuestionStats().getQuestionStatByID(questionID).addTimeTakenToComplete(timeTakenToComplete); // Store the time that it took them
			}
			
			System.out.println("Users question stats after finishing form: " + currentUser.getQuestionStats());
			
			JOptionPane.showMessageDialog(null, "Form complete!");
		}
	}
	
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == submitButton)
		{
			System.out.println("[INFO] <FORM_DISPLAYER> submitButton pressed");
			questionFocusChange();
			validateQuestions();
		}
	}
	
	private void questionFocusChange()
	{
		String[] questionIDs = form.getQuestionIDs();
		
		for (int i = 0; i < questionIDs.length; i++)
		{
			if (questionIDs[i].equals(questionLostFocus)) // If we've found the question that lost focus
			{
				timeToCompleteQuestions[i] += (System.nanoTime() - questionStartTime) / 1000000000; // Add on the number of seconds that the question was focused for
				
				//System.out.println(questionLostFocus + " was focused for " + timeToCompleteQuestions[i] + "s");
			}
		}
		
		questionStartTime = System.nanoTime();
	}
	

    public void mouseEntered(MouseEvent evt) 
	{
		QuestionPanel sourceQuestion;
		
		if (evt.getSource() instanceof QuestionPanel) // Check that it was a QuestionPanel that triggered the event
		{
			sourceQuestion = (QuestionPanel) evt.getSource(); // Cast the object to a question panel
		}
		else // It's not a question panel
		{
			JComponent component = (JComponent) evt.getSource(); // It'll be a component that's in a question panel that fired the event
			sourceQuestion = (QuestionPanel) component.getParent();	//getting its parent will get the question panel
		}
		
		if (!questionLostFocus.equals(sourceQuestion.getQuestionID())) // If a new question has gotten focus
		{
			//System.out.println(questionLostFocus + " lost focus");
			//System.out.println(sourceQuestion.getQuestionID() + " gained focus");
			questionFocusChange();
			
		}
	}

    public void mouseExited(MouseEvent evt) 
	{
		QuestionPanel sourceQuestion;
		
		if (evt.getSource() instanceof QuestionPanel) // Check that it was a QuestionPanel that triggered the event
		{
			sourceQuestion = (QuestionPanel) evt.getSource(); // Cast the object to a question panel
		}
		else // It's not a question panel
		{
			JComponent component = (JComponent) evt.getSource(); // It'll be a component that's in a question panel that fired the event
			sourceQuestion = (QuestionPanel) component.getParent();	//getting its parent will get the question panel
		}
		
		questionLostFocus = sourceQuestion.getQuestionID();
    }

	public void windowClosing(WindowEvent evt)
	{
		users.writeDatabase(); // Save the users stats
		
		System.out.println("[INFO] <FORM_DISPLAYER> Close button pressed");
		
		int numberCorrect = 0;
		
		for (QuestionPanel qP : questionPanels) // For each question in the form
		{
			if (qP.validateAnswers()) // If they passed the validation check
			{
				numberCorrect++;
			}
		}
		
		int percentComplete = (numberCorrect * 100)/ questionPanels.length; // Calculate what percentage of the questions are answered correctly
		
		
		System.out.println("[INFO] <FORM_DISPLAYER> form is " +  percentComplete + "% complete");
		
		for (QuestionPanel question : questionPanels) // For each question in the form
		{
			question.removeMouseListener(this); // Remove the mouse listener
			
			for (JComponent component : question.getComponents())
			{
				component.removeMouseListener(this); // Remove the mouse listener
			}

		}

		gui.saveForm(form, percentComplete);
	}

	public void mousePressed(MouseEvent evt) 
	{
		// Exists to satisfy mouse listener
    }

    public void mouseReleased(MouseEvent evt) 
	{
       // Exists to satisfy mouse listener
    }
	
    public void mouseClicked(MouseEvent evt) 
	{
		// Exists to satisfy mouse listener
    }
	
	public void windowOpened(WindowEvent evt)
	{
		// Exists to satisfy window listener
	}

	public void windowClosed(WindowEvent evt)
	{
		// Exists to satisfy window listener
	}
	
	public void windowIconified(WindowEvent evt)
	{
		// Exists to satisfy window listener
	}
	
	public void windowDeiconified(WindowEvent evt)
	{
		// Exists to satisfy window listener
	}
	
	public void windowActivated(WindowEvent evt)
	{
		// Exists to satisfy window listener
	}
	
	public void windowDeactivated(WindowEvent evt)
	{
		// Exists to satisfy window listener
	}
}