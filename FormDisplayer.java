import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import components.*;

import java.util.*;

public class FormDisplayer extends JFrame implements ActionListener, MouseListener, WindowListener
{
	
	private Form form;
	private JPanel[] components;
	
	private JPanel[] pages;
	private int currentPage = 0;
	private final int QUESTIONS_PER_PAGE = 6;
	
	private JPanel buttonNavigationPanel;
	
	private JButton nextButton = new JButton("Next"); // Allows the user to go to the next page
	private JButton backButton = new JButton("Back"); // Allows the user to go to the previous page
	private JButton submitButton = new JButton("Submit"); // Allows the user to complete the process
	
	private JLabel pageIndicatorLabel = new JLabel("", SwingConstants.RIGHT);
	
	private QuestionPanel[] questionPanels;
	
	private QuestionList questions;
	
	private GUI gui;
	
	private User currentUser;
	private UserList users;
	
	private JPanel formPanel = new JPanel();
	
	private String questionLostFocus = ""; // The question that has most recently lost focus
	private long questionStartTime = 0; // Store the time is milliseconds that the user started attempting the question
	private long[] timeToCompleteQuestions; // Stores the length of time that each question has been focused for
	

	private int[] failedValidationChecks; // Stores the amount of time the user has failed the validation check for each question       
	
	public FormDisplayer(Form tempF, JPanel[] tempComponents, User tempU, UserList tempUserList, GUI tempGUI, QuestionList tempQuestions)
	{
		form = tempF;
		questions = tempQuestions;
		components = tempComponents;
		currentUser = tempU;
		users = tempUserList;
		gui = tempGUI;
		
		int numberOfQuestions = form.getQuestionIDs().length;
		
		failedValidationChecks = new int[numberOfQuestions]; // Create a new int array with an element for each question of the form
		timeToCompleteQuestions = new long[numberOfQuestions];
		
		questionLostFocus = form.getQuestionIDs()[0]; // Make the first question in focus
		questionStartTime = System.nanoTime(); // Start the clock
		
		getQuestionPanels();
		
		prepareGUI();
		
	}
	
	private void prepareButtonNavigationPanel()
	{
		buttonNavigationPanel = new JPanel();
		buttonNavigationPanel.setLayout(new BoxLayout(buttonNavigationPanel, BoxLayout.LINE_AXIS)); // Create a new box layout left to right
		
		// Set the correct colours
		nextButton.setBackground(new Color(169,196,235));
		backButton.setBackground(new Color(169,196,235));
		submitButton.setBackground(new Color(130,183,75));
		
		// Add the action listener
		nextButton.addActionListener(this);
		backButton.addActionListener(this);
		submitButton.addActionListener(this);
		
		// Set the correct preferred sizes for the buttons
		nextButton.setMaximumSize(new Dimension(175, 60));
		nextButton.setPreferredSize(new Dimension(175, 60));
		
		backButton.setMaximumSize(new Dimension(175, 60));
		backButton.setPreferredSize(new Dimension(175, 60));
		
		submitButton.setMaximumSize(new Dimension(175, 60));
		submitButton.setPreferredSize(new Dimension(175, 60));
		
		if (pages.length > 1)
		{
			// Hide the back and finish button
			nextButton.setVisible(true);
			backButton.setVisible(false);
			submitButton.setVisible(false);
		}
		else
		{
			nextButton.setVisible(false);
			backButton.setVisible(false);
			submitButton.setVisible(true);
		}
		buttonNavigationPanel.add(backButton);
		buttonNavigationPanel.add(Box.createHorizontalGlue()); // This is invisible and will fill the space between the buttons as the window is resized.
		// It keeps the buttons at the far edges.
		
		buttonNavigationPanel.add(nextButton);
		buttonNavigationPanel.add(submitButton);
		
		buttonNavigationPanel.setMaximumSize(new Dimension(1000, 30));
	}
	
	private void updatePageIndicatorLabel()
	{
		int humanPage = currentPage + 1; // Humans don't start counting at 0 so increment the computer count by one
		
		pageIndicatorLabel.setText("Page " + humanPage + " of " + pages.length);
	}
	
	private void getQuestionPanels()
	{
		QuestionPanel[] tempQuestionPanels = new QuestionPanel[components.length];
		int nextQuestionPanelLocation = 0;
		
		for (int i = 0; i < components.length; i++)
		{
			if (components[i] instanceof QuestionPanel)
			{
				tempQuestionPanels[nextQuestionPanelLocation] = (QuestionPanel) components[i];
				nextQuestionPanelLocation++;
			}
		}
		
		// Trim the array
		
		questionPanels = new QuestionPanel[nextQuestionPanelLocation];
		for (int i = 0; i < nextQuestionPanelLocation; i++)
		{
			questionPanels[i] = tempQuestionPanels[i];
		}
	}
	
	private void prepareGUI()
	{
		System.out.println("[INFO] <FORM_DISPLAYER> Running prepareGUI");
		
		this.addWindowListener(this);
		
		this.setLayout(new GridLayout(1,1)); // Set a grid layout
		this.setSize(400,600); // Set the size
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS)); // Set a box layout
		
		formPanel.setLayout(new GridLayout(0,1)); // Set a grid layout
		
		preparePages();
		
		System.out.println(Arrays.toString(pages));
		formPanel.add(pages[0]);
		
		JPanel pageIndicatorPanel = new JPanel();
		pageIndicatorPanel.setLayout(new GridLayout(1,1));
		pageIndicatorPanel.add(pageIndicatorLabel);
		updatePageIndicatorLabel();
		mainPanel.add(pageIndicatorPanel);

		mainPanel.add(formPanel);
		
		prepareButtonNavigationPanel();
		
		mainPanel.add(buttonNavigationPanel, BorderLayout.SOUTH);
		
		this.add(mainPanel);
		
		this.setVisible(true);
	}
	
	private void goForward()
	{
		formPanel.removeAll();
		currentPage++;
		formPanel.add(pages[currentPage]);
		
		if (currentPage > 0) // Show the back button if we're not on the first page
		{
			backButton.setVisible(true);
		}
		
		if (currentPage == pages.length-1) // If we're at the last page
		{
			nextButton.setVisible(false);
			submitButton.setVisible(true);
		}
		
		updatePageIndicatorLabel();
	}
	
	private void goBackward() // Goes backward a step
	{
		formPanel.removeAll();
		currentPage--;
		formPanel.add(pages[currentPage]);
		
		if (currentPage == 0) // Show the back button if we're not on the first page
		{
			backButton.setVisible(false);
		}
		
		if (currentPage < pages.length-1) // If we're not at the last page
		{
			nextButton.setVisible(true);
			submitButton.setVisible(false);
		}
		
		updatePageIndicatorLabel();
	}
	
	private void preparePages()
	{
		JPanel[] tempPages = new JPanel[100]; // Start with 100 pages, we'll trim off the ones that we don't need at the end
		
		int currentComponent = 0;
		int pageNumber = 0;
		
		while (currentComponent < components.length)
		{
			JPanel currentPage = new JPanel();
			
			currentPage.setLayout(new BoxLayout(currentPage, BoxLayout.PAGE_AXIS));
			currentPage.setPreferredSize(new Dimension(1000,1000));
			currentPage.add(Box.createVerticalGlue());
			int numberOfQuestionsInPage = 0;
			
			// Add the questions to the page
			for (int i = 0; i < QUESTIONS_PER_PAGE; i++)
			{
				
				if (currentComponent < components.length)
				{
					JPanel component = components[currentComponent];
					// Determine whether the component is a question or a header
					boolean isAQuestion = component instanceof QuestionPanel;
					
					if (isAQuestion)
					{
						currentPage.add(Box.createVerticalGlue());
						currentPage.add(component); // Add the question panel to the page
						currentComponent++;
						numberOfQuestionsInPage++;
					}
					else // It's a header
					{
						System.out.println("Header");
						// If we're not at the end of the page add it to the page.
						// Otherwise, (rather than having it at the bottom of the page)
						// add it to the start of the next page
						
						if (i != QUESTIONS_PER_PAGE - 1) // If we're not at the end of the page
						{
							currentPage.add(Box.createVerticalGlue());
							currentPage.add(component); // Add the header to the page
							
							currentComponent++;
							numberOfQuestionsInPage++;
						}
							
					}
				}
				
			}
			
			currentPage.add(Box.createVerticalGlue());
			tempPages[pageNumber] = currentPage;
			pageNumber++;
		}
		
		// Trim the pages array
		
		// Page number is the index of the last page + 1
		
		pages = new JPanel[pageNumber];
		
		for (int i = 0; i < pages.length; i++)
		{
			pages[i] = tempPages[i];
		}
		
		
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
		else if (evt.getSource() == nextButton)
		{
			goForward();
		}
		else if (evt.getSource() == backButton)
		{
			goBackward();
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