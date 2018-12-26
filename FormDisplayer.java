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
	
	private JPanel pageErrorsPanel = new JPanel();
	private JLabel pageErrorsLabel = new JLabel("", SwingConstants.CENTER); // This is displayed at the top of the form and contains the error messages from each question
	
	private QuestionPanel[] questionPanels;
	
	private QuestionList questions;
	
	private GUI gui;
	
	private User currentUser;
	private UserList users;
	
	private JPanel formPanel = new JPanel();
	
	private String questionLostFocus = ""; // The question that has most recently lost focus
	private long questionStartTime = 0; // Store the time is milliseconds that the user started attempting the question
	//private long[] timeToCompleteQuestions; // Stores the length of time that each question has been focused for
	private HashMap<String, Long> timeToCompleteQuestions = new HashMap<String,Long>();

	//private int[] failedValidationChecks; // Stores the amount of time the user has failed the validation check for each question       
	private HashMap<String, Integer> failedValidationChecks = new HashMap<String,Integer>();
	
	private ImageIcon requiredIcon;
	
	public FormDisplayer(Form tempF, JPanel[] tempComponents, User tempU, UserList tempUserList, GUI tempGUI, QuestionList tempQuestions)
	{
		form = tempF;
		questions = tempQuestions;
		components = tempComponents;
		currentUser = tempU;
		users = tempUserList;
		gui = tempGUI;
		
		int numberOfQuestions = form.getQuestionIDs().length;
		
		getQuestionPanels();
		
		prepareHashMaps();
		
		questionLostFocus = questionPanels[0].getQuestionID(); // Make the first question in focus
		questionStartTime = System.nanoTime(); // Start the clock
		
		prepareGUI();
		
	}
	
	private void prepareHashMaps() // Add all of the question ids to both hashmaps
	{
		for (QuestionPanel p : questionPanels)
		{
			timeToCompleteQuestions.put(p.getQuestionID(), new Long(0));
			failedValidationChecks.put(p.getQuestionID(), new Integer(0));
		}
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
		System.out.println("[INFO] <FORM_DISPLAYER> Running getQuestionPanels");
		QuestionPanel[] tempQuestionPanels = new QuestionPanel[components.length];
		int nextQuestionPanelLocation = 0;
		
		System.out.println(components.length);
		for (int i = 0; i < components.length; i++)
		{	
			if (components[i] instanceof QuestionPanel)
			{
				System.out.println(components[i]);
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
		
		requiredIcon = new ImageIcon("star.png");
		requiredIcon = new ImageIcon(requiredIcon.getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT)); // Make the icon smaller
		
		this.addWindowListener(this);
		
		this.setLayout(new GridLayout(1,1)); // Set a grid layout
		this.setSize(600,800); // Set the size
		this.setMinimumSize(new Dimension(600,800)); // Set the size
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS)); // Set a box layout
		
		formPanel.setLayout(new GridLayout(0,1)); // Set a grid layout
		
		preparePages();
		
		System.out.println(Arrays.toString(pages));
		formPanel.add(pages[0]);
		
		JPanel pageIndicatorPanel = new JPanel();
		pageIndicatorPanel.setLayout(new GridLayout(1,1));
		pageIndicatorPanel.add(pageIndicatorLabel);
		pageIndicatorPanel.setMaximumSize(new Dimension(2000, 20));
		updatePageIndicatorLabel();
		mainPanel.add(pageIndicatorPanel);

		pageErrorsPanel.setLayout(new GridLayout(1,1));
		pageErrorsPanel.add(pageErrorsLabel);
		pageErrorsPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.red)); // Give it a red border like the questions
		
		Font currentFont = pageErrorsLabel.getFont();
		pageErrorsLabel.setFont(currentFont.deriveFont(Font.PLAIN, 14)); // Make the font larger
		
		pageErrorsPanel.setVisible(false); // This panel should only be visible when there are errors to correct

		mainPanel.add(pageErrorsPanel);

		mainPanel.add(formPanel);
		
		prepareButtonNavigationPanel();
		
		mainPanel.add(buttonNavigationPanel);
		
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
		
		pageErrorsPanel.setVisible(false);
		
		updatePageIndicatorLabel();
		this.repaint();
		this.revalidate();
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
		
		pageErrorsPanel.setVisible(false);
		
		updatePageIndicatorLabel();
		
		this.repaint();
		this.revalidate();
	}
	
	private void preparePages()
	{
		JPanel[] tempPages = new JPanel[100]; // Start with 100 pages, we'll trim off the ones that we don't need at the end
		
		int currentComponent = 0;
		int pageNumber = 0;
		
		while (currentComponent < components.length) // Add all of the components to pages
		{
			JPanel currentPage = new JPanel();
			
			currentPage.setLayout(new BoxLayout(currentPage, BoxLayout.PAGE_AXIS));
			currentPage.setPreferredSize(new Dimension(1000,1000));
			currentPage.add(Box.createVerticalStrut(30));
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
						QuestionPanel qP = (QuestionPanel) component;
						qP.addMouseListener(this);
						currentPage.add(Box.createVerticalGlue());
						
						JPanel rowPanel = new JPanel();
						rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.LINE_AXIS));
						
						JLabel requiredIconLabel = new JLabel();
						requiredIconLabel.setIcon(requiredIcon);
						boolean isQuestionRequired = form.isQuestionRequired(qP.getQuestionID());
						requiredIconLabel.setVisible(isQuestionRequired); // Make it invisble by default
						
						rowPanel.add(requiredIconLabel);
						rowPanel.add(qP); // Add the question panel to the page
						
						currentPage.add(rowPanel);
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
			currentPage.add(Box.createVerticalStrut(30));
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
	
	private boolean validatePage(int pageNumber)
	{
		
		System.out.println("Users question stats before validation: " + currentUser.getQuestionStats());
		
		
		boolean allCorrect = true;
		
		QuestionPanel[] questionsInPageUnTrimmed = new QuestionPanel[QUESTIONS_PER_PAGE];
		int nextQuestionPanelLocation = 0;
		
		for (Component row : pages[pageNumber].getComponents()) // Get the components in the page and iterate over them
		{
			if (row instanceof JPanel)
			{
				JPanel rowPanel = (JPanel) row;
				for (Component c : rowPanel.getComponents())
				{
					if (c instanceof QuestionPanel)
					{
						questionsInPageUnTrimmed[nextQuestionPanelLocation] = (QuestionPanel) c;
						nextQuestionPanelLocation++;
					}
				}
			}
		}
		
		QuestionPanel[] questionsInPage = new QuestionPanel[nextQuestionPanelLocation];
		// Trim the array
		for (int i = 0; i < nextQuestionPanelLocation; i++)
		{
			questionsInPage[i] = questionsInPageUnTrimmed[i];
		}
		
		for (int i = 0; i < questionsInPage.length; i++) // For each question in the form
		{
			QuestionPanel questionPanel = questionsInPage[i];
			String questionID = questionPanel.getQuestionID();
			
			
			
			boolean filledIn = questionPanel.presenceChecks();
			boolean required = form.isQuestionRequired(questionID);
			
			if ((filledIn || required)) // If it's filled in or required
			{
				boolean passed = questionPanel.validateAnswers(); // Validates the answer
				
				if (!passed)
				{
					allCorrect = false;
					currentUser.getQuestionStats().getQuestionStatByID(questionPanel.getQuestionID()).addFailedValidation();
					
					Integer currentValidation = failedValidationChecks.get(questionID);
					failedValidationChecks.put(questionID, currentValidation + 1); // Add to the failed counter.
				}
			}
		}
		
		if (!allCorrect) // If some were filled in incorrectly update the label
		{
			updatePageErrorsLabel(questionsInPage);
		}
		
		return allCorrect;
		
	}
	
	private void updatePageErrorsLabel(QuestionPanel[] questionPanels)
	{
		// Construct the error string based on which questions have failed their checks
		String errorMessages = "";
		
		// All the questions are in the page
		
		for (QuestionPanel qP : questionPanels)
		{
			String questionID = qP.getQuestionID();
			
			boolean filledIn = qP.presenceChecks();
			boolean required = form.isQuestionRequired(questionID);
			
			if (filledIn || required)
			{
				boolean passed = qP.validateAnswers();
				
				if (!passed)
				{
					errorMessages += "<strong>" + qP.getErrorString() + "</strong><br>";
				}
			}
		}
		
		
		String errorString = "<html><center><div style='text-align: center;'> There are problems with the following questions<br>" +
										errorMessages + "Please correct them before continuing <br></div></html>";
										
		pageErrorsLabel.setText(errorString);
		pageErrorsPanel.setVisible(true);
		this.repaint();
		this.revalidate();
	}
	
	private void saveStats() // Called when the user successfully completes a form
	{	
		System.out.println("Users question stats after validation: " + currentUser.getQuestionStats());
		System.out.println(Arrays.asList(timeToCompleteQuestions));
		System.out.println(Arrays.asList(failedValidationChecks));
		
		for (int i = 0; i < questionPanels.length; i++)
		{
			String questionID = questionPanels[i].getQuestionID();
			QuestionPanel questionPanel = questionPanels[i];
			
			boolean filledIn = questionPanel.presenceChecks();
			boolean required = form.isQuestionRequired(questionID);
			
			if (filledIn || required)
			{
				int amountFailed = failedValidationChecks.get(questionID);
				long timeTakenToComplete = timeToCompleteQuestions.get(questionID);
				
				QuestionStat qS = currentUser.getQuestionStats().getQuestionStatByID(questionID);
				
				qS.addNumberOfAttemptsNeededToCorrect(amountFailed); // Get the question stat for the question at add the number of attempts failed
				
				qS.addTimeTakenToComplete(timeTakenToComplete); // Store the time that it took them
				qS.addAttempt();
			}
		}
		
		System.out.println("Users question stats after finishing form: " + currentUser.getQuestionStats());
		
		JOptionPane.showMessageDialog(null, "Form complete!");
		
		users.writeDatabase(); // Save the users stats
			
	}
	
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == submitButton)
		{
			System.out.println("[INFO] <FORM_DISPLAYER> submitButton pressed");
			questionFocusChange();
			if (validatePage(currentPage))
			{
				saveStats();
			}
		}
		else if (evt.getSource() == nextButton)
		{
			if (validatePage(currentPage)) // If they correctly filled in all questions on the page
			{
				goForward();
			}
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
				long currentTimeTaken = timeToCompleteQuestions.get(questionIDs[i]);
				long duration = (System.nanoTime() - questionStartTime) / 1000000000; // The amount of time in seconds that they've just spent on it
				timeToCompleteQuestions.put(questionIDs[i],currentTimeTaken + duration); // Add on the number of seconds that the question was focused for
				
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
			System.out.println(questionLostFocus + " lost focus");
			System.out.println(sourceQuestion.getQuestionID() + " gained focus");
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
			
			// Check if the parent is a question panel
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