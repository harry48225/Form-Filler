import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import components.*;

import java.util.*;
import java.util.List;

public class FormDisplayer extends JFrame implements ActionListener, MouseListener, WindowListener
{
	/* This is the main class that allows the user to attempt a form.
		It displays all of the components of the form to the user and
		allows them to fill it in. It also tracks all of the statstics
		about the user's attempt. */
		
		
	private Form form;
	private JPanel[] components;
	private List<Image> icons; // The form filler application icons
	
	// Page variables
	private JPanel[] pages;
	private int currentPage = 0;
	private final int QUESTIONS_PER_PAGE = 6;
	
	// The buttons that allow the user to move around the form
	private JPanel buttonNavigationPanel;
	
	private JButton nextButton = new JButton("Next"); // Allows the user to go to the next page
	private JButton backButton = new JButton("Back"); // Allows the user to go to the previous page
	private JButton submitButton = new JButton("Submit"); // Allows the user to complete the process
	
	private JLabel pageIndicatorLabel = new JLabel("", SwingConstants.RIGHT);
	
	// This panel displays the error messages to the user (for the questions that have failed their validation checks)
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
	
	// Stores how long the user has been attempting a question for each question in the form
	private HashMap<String, Long> timeToCompleteQuestions = new HashMap<String,Long>();

	// Stores the amount of time the user has failed the validation check for each question       
	private HashMap<String, Integer> failedValidationChecks = new HashMap<String,Integer>();
	
	private ImageIcon requiredIcon; // The red asterisk that indicates to the user that a question is required
	
	int percentComplete = 0;
	
	public FormDisplayer(Form tempF, JPanel[] tempComponents, User tempU, UserList tempUserList, GUI tempGUI, QuestionList tempQuestions, List<Image> tempIcons)
	{
		/* Creates a new form displayer for a form */
		form = tempF;
		questions = tempQuestions;
		components = tempComponents;
		currentUser = tempU;
		users = tempUserList;
		gui = tempGUI;
		icons = tempIcons;
		
		int numberOfQuestions = form.getQuestionIDs().length;
		
		getQuestionPanels();
		
		prepareHashMaps();
		
		questionLostFocus = questionPanels[0].getQuestionID(); // Make the first question in focus
		questionStartTime = System.nanoTime(); // Start the clock
		
		prepareGUI();
		
	}
	
	private void prepareHashMaps()
	{
		/* Add all of the question ids to both hashmaps */
		for (QuestionPanel p : questionPanels)
		{
			timeToCompleteQuestions.put(p.getQuestionID(), new Long(0));
			failedValidationChecks.put(p.getQuestionID(), new Integer(0));
		}
	}
	
	private void prepareButtonNavigationPanel()
	{
		/* Prepares the buttons that are used to navigate around the form */
		
		buttonNavigationPanel = new JPanel();
		buttonNavigationPanel.setLayout(new BoxLayout(buttonNavigationPanel, BoxLayout.LINE_AXIS)); // Create a new box layout left to right
		
		// Set the correct colours
		nextButton.setBackground(new Color(169,196,235)); //Blue
		backButton.setBackground(new Color(169,196,235)); // Blue
		submitButton.setBackground(new Color(130,183,75)); // Green
		
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
		
		// If there is more than 1 page
		if (pages.length > 1)
		{
			// Hide the back and finish button
			nextButton.setVisible(true);
			backButton.setVisible(false);
			submitButton.setVisible(false);
		}
		else // There is only 1 page
		{
			// Just show the finish button
			nextButton.setVisible(false);
			backButton.setVisible(false);
			submitButton.setVisible(true);
		}
		
		// Add the buttons to the window
		
		buttonNavigationPanel.add(backButton);
		buttonNavigationPanel.add(Box.createHorizontalGlue()); // This is invisible and will fill the space between the buttons as the window is resized.
		// It keeps the buttons at the far edges.
		
		buttonNavigationPanel.add(nextButton);
		buttonNavigationPanel.add(submitButton);
		
		// Make the panel the correct size and have a large width so that it fills all of the horizontal space
		buttonNavigationPanel.setMaximumSize(new Dimension(1000, 30));
	}
	
	private void updatePageIndicatorLabel()
	{
		/* Updates the page indicator label in the top right of the window with the page number that the user is currently on */
		
		int humanPage = currentPage + 1; // Humans don't start counting at 0 so increment the computer count by one
		
		pageIndicatorLabel.setText("Page " + humanPage + " of " + pages.length);
	}
	
	private void getQuestionPanels()
	{
		/* Gets all of the question panels that correspond to the questions in the form */
		
		System.out.println("[INFO] <FORM_DISPLAYER> Running getQuestionPanels");
		QuestionPanel[] tempQuestionPanels = new QuestionPanel[components.length];
		int nextQuestionPanelLocation = 0;
	
		// Iterate over each component in the form and add it to the array if it's a question panel
		for (int i = 0; i < components.length; i++)
		{	
			if (components[i] instanceof QuestionPanel)
			{
				tempQuestionPanels[nextQuestionPanelLocation] = (QuestionPanel) components[i];
				nextQuestionPanelLocation++;
			}
		}
		
		// Trim the array to remove null elements
		questionPanels = new QuestionPanel[nextQuestionPanelLocation];
		for (int i = 0; i < nextQuestionPanelLocation; i++)
		{
			questionPanels[i] = tempQuestionPanels[i];
		}
	}
	
	private void prepareGUI()
	{
		/* Prepares the main panel for display to the user */
		System.out.println("[INFO] <FORM_DISPLAYER> Running prepareGUI");
		
		// Load and scale the required icon
		requiredIcon = new ImageIcon("star.png");
		requiredIcon = new ImageIcon(requiredIcon.getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT)); // Make the icon smaller
		
		// Add a window listener so that we can intercept the window closing event
		this.addWindowListener(this);
		
		this.setLayout(new GridLayout(1,1)); // Set a grid layout
		this.setSize(600,800); // Set the size
		this.setLocationRelativeTo(null); // Center it
		this.setIconImages(icons); // Set the icon to the form filler icons
		this.setMinimumSize(new Dimension(600,800)); // Set the size
		this.setTitle(form.getTitle()); // Set the title of the window to the title of the form
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS)); // Set a vertical box layout
		
		formPanel.setLayout(new GridLayout(0,1)); // Set a grid layout
		
		preparePages();
		
		formPanel.add(pages[0]); // Add the first page to the panel
		
		
		// Prepare the page indicator label and panel
		JPanel pageIndicatorPanel = new JPanel();
		pageIndicatorPanel.setLayout(new GridLayout(1,1));
		pageIndicatorPanel.add(pageIndicatorLabel);
		pageIndicatorPanel.setMaximumSize(new Dimension(2000, 20)); // Large width so that it fills all horizontal space
		updatePageIndicatorLabel();
		mainPanel.add(pageIndicatorPanel);

		pageErrorsPanel.setLayout(new GridLayout(1,1));
		pageErrorsPanel.add(pageErrorsLabel);
		pageErrorsPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.red)); // Give it a red border like the questions
		
		// Make the font of the label larger
		Font currentFont = pageErrorsLabel.getFont();
		pageErrorsLabel.setFont(currentFont.deriveFont(Font.PLAIN, 14)); // Make the font larger
		
		pageErrorsPanel.setVisible(false); // This panel should only be visible when there are errors to correct

		// Add all of the sub panels to the main panel
		mainPanel.add(pageErrorsPanel);

		mainPanel.add(formPanel);
		
		prepareButtonNavigationPanel();
		
		mainPanel.add(buttonNavigationPanel);
		
		this.add(mainPanel);
		
		this.setVisible(true);
	}
	
	private void goForward()
	{
		/* Goes to the next page of the form */
		
		// Remove the current page and go to the next page
		formPanel.removeAll();
		currentPage++;
		formPanel.add(pages[currentPage]);
		
		// Show the correct buttons
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
		
		// Force the gui to redraw
		this.repaint();
		this.revalidate();
	}
	
	private void goBackward()
	{
		/* Goes to the previous page of a form */
	
		// Remove the current page and display the previous page
		formPanel.removeAll();
		currentPage--;
		formPanel.add(pages[currentPage]);
		
		// Show the correct buttons
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
		
		// Force the gui to redraw
		this.repaint();
		this.revalidate();
	}
	
	private void preparePages()
	{
		/* Sorts all of the componets of the form into pages */
		
		JPanel[] tempPages = new JPanel[100]; // Start with 100 pages, we'll trim off the ones that we don't need at the end
		
		int currentComponent = 0;
		int pageNumber = 0;
		
		while (currentComponent < components.length) // Add all of the components to pages
		{
			JPanel currentPage = new JPanel();
			
			currentPage.setLayout(new BoxLayout(currentPage, BoxLayout.PAGE_AXIS)); // Create a new vertical box layout
			currentPage.setPreferredSize(new Dimension(1000,1000)); // Make the page arbitrarily large
			currentPage.add(Box.createVerticalStrut(30)); // Add 30px vertical padding
			int numberOfQuestionsInPage = 0;
			
			// Add the questions to the page up to the specified number of questions in a page
			for (int i = 0; i < QUESTIONS_PER_PAGE; i++)
			{
				
				// For each component
				if (currentComponent < components.length)
				{
					JPanel component = components[currentComponent];
					
					// Determine whether the component is a question or a header
					boolean isAQuestion = component instanceof QuestionPanel;
					
					// If it's a question
					if (isAQuestion)
					{
						// Add it to the page and add a mouselistener, also add the required icon if the question is required
						QuestionPanel qP = (QuestionPanel) component;
						qP.addMouseListener(this);
						currentPage.add(Box.createVerticalGlue());
						
						// The row panel contains the question and the required icon
						JPanel rowPanel = new JPanel();
						rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.LINE_AXIS));
						
						// Add the required icon and make it visible if nessesary
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
						// If we're not at the end of the page add it to the page.
						// Otherwise, (rather than having it at the bottom of the page)
						// add it to the start of the next page
						
						if (i != QUESTIONS_PER_PAGE - 1) // If we're not at the end of the page
						{
							currentPage.add(Box.createVerticalGlue()); // Fills the vertical space
							currentPage.add(component); // Add the header to the page
							
							currentComponent++;
							numberOfQuestionsInPage++;
						}
							
					}
				}
				
			}
			
			currentPage.add(Box.createVerticalGlue()); // Fills the empty vertical space
			currentPage.add(Box.createVerticalStrut(30)); // Add 30px vertical padding
			tempPages[pageNumber] = currentPage; // Store the page in the pages array
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
		/* Validates a page of questions */
		
		boolean allCorrect = true;
		
		QuestionPanel[] questionsInPageUnTrimmed = new QuestionPanel[QUESTIONS_PER_PAGE];
		int nextQuestionPanelLocation = 0;
		
		// Create an array containing all of the question panels in the page
		for (Component row : pages[pageNumber].getComponents()) // Get the components in the page and iterate over them
		{
			if (row instanceof JPanel) // If it's a JPanel - this will be a question and the required icon
			{
				JPanel rowPanel = (JPanel) row;
				for (Component c : rowPanel.getComponents()) // Iterate over the components in the row to find the question panel
				{
					if (c instanceof QuestionPanel)
					{
						// Add the question panel to the array
						questionsInPageUnTrimmed[nextQuestionPanelLocation] = (QuestionPanel) c;
						nextQuestionPanelLocation++;
					}
				}
			}
		}
		
		// Create a new array of the exact size required
		QuestionPanel[] questionsInPage = new QuestionPanel[nextQuestionPanelLocation];
		// Trim the array
		for (int i = 0; i < nextQuestionPanelLocation; i++)
		{
			questionsInPage[i] = questionsInPageUnTrimmed[i];
		}
		// Now we have an array containing all of the question panels in the page
		
		// Iterate over each QuestionPanel in the page and valiadate it
		for (int i = 0; i < questionsInPage.length; i++) // For each question in the form
		{
			QuestionPanel questionPanel = questionsInPage[i];
			String questionID = questionPanel.getQuestionID();
			
			// Determine if it's required and whether it's filled in
			boolean filledIn = questionPanel.presenceChecks();
			boolean required = form.isQuestionRequired(questionID);
			
			if ((filledIn || required)) // If it's filled in or required
			{
				boolean passed = questionPanel.validateAnswers(); // Validates the answer
				
				if (!passed) // If they didn't pass the validation check
				{
					allCorrect = false; // Store that they didn't get all questions correct
					
					// Update the user's statistics
					
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
		/* Validates each question in a page and updates the errors label with their error strings */
		
		// Construct the error string based on which questions have failed their checks
		String errorMessages = "";
		
		// All the questions are in the page
		// For each question panel, validate it and then get the error string
		for (QuestionPanel qP : questionPanels)
		{
			String questionID = qP.getQuestionID();
			
			// Work out if it's filled in or required
			boolean filledIn = qP.presenceChecks();
			boolean required = form.isQuestionRequired(questionID);
			
			if (filledIn || required)
			{
				boolean passed = qP.validateAnswers();
				
				// If the question failed its validation check
				if (!passed)
				{
					// Add the error message in bold to the string
					errorMessages += "<strong>" + qP.getErrorString() + "</strong><br>";
				}
			}
		}
		
		
		// Format and finalise the string
		String errorString = "<html><center><div style='text-align: center;'> There are problems with the following questions<br>" +
										errorMessages + "Please correct them before continuing <br></div></html>";

		// Display the error
		pageErrorsLabel.setText(errorString);
		pageErrorsPanel.setVisible(true);
		
		// Force the window to redraw
		this.repaint();
		this.revalidate();
	}
	
	private void saveStats()
	{	
		/* Called when the user successfully completes a form, and updates all of the 
			statistics for each question in the form that the user has filled in */
		
		// For each question panel / question in the form.
		// If they are filled in or required update the statistics
		for (int i = 0; i < questionPanels.length; i++)
		{
			String questionID = questionPanels[i].getQuestionID();
			QuestionPanel questionPanel = questionPanels[i];
			
			// Work out if the question is filled in or required
			boolean filledIn = questionPanel.presenceChecks();
			boolean required = form.isQuestionRequired(questionID);
			
			if (filledIn || required)
			{
				// Get the number of times that they failed the validation check while completing the form
				int amountFailed = failedValidationChecks.get(questionID);
				// Get the time that they took to complete the question
				long timeTakenToComplete = timeToCompleteQuestions.get(questionID);
				
				// Get the question stat object to do with the question
				QuestionStat qS = currentUser.getQuestionStats().getQuestionStatByID(questionID);
				
				// Update the stats
				qS.addNumberOfAttemptsNeededToCorrect(amountFailed); // Get the question stat for the question at add the number of attempts failed
				qS.addTimeTakenToComplete(timeTakenToComplete); // Store the time that it took them
				qS.addAttempt(); // Increment the number of times that the user has completed a question
			}
		}
		
		// Tell the user that they have successfully completed the form
		JOptionPane.showMessageDialog(this, "Form complete!");
		percentComplete = 100;
		users.writeDatabase(); // Save the users stats
			
	}
	
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == submitButton)
		{
			// Validate the last page if the user has pressed the submit button
			System.out.println("[INFO] <FORM_DISPLAYER> submitButton pressed");
			questionFocusChange(); // Stop timing for the current question
			if (validatePage(currentPage)) // If they successfully completed the page
			{
				// Since they have successfully completed the last page the form is completed so save the stats
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
		/* Stops timing the amount of time that the user has spent on the current 
			question and calculates how long they have spent on it */
			
		String[] questionIDs = form.getQuestionIDs();
		
		// Iterate over the questions in the form to find the one that has just lost focus
		for (int i = 0; i < questionIDs.length; i++)
		{
			if (questionIDs[i].equals(questionLostFocus)) // If we've found the question that lost focus
			{
				// Get the time that the user has spent on it before this focus change
				long currentTimeTaken = timeToCompleteQuestions.get(questionIDs[i]);
				
				long duration = (System.nanoTime() - questionStartTime) / 1000000000; // The amount of time in seconds that they've just spent on it
				
				timeToCompleteQuestions.put(questionIDs[i],currentTimeTaken + duration); // Add on the number of seconds that the question was focused for
			}
		}
		
		// Start timing again
		questionStartTime = System.nanoTime();
	}
	

    public void mouseEntered(MouseEvent evt) 
	{
		/* Called when the mouse moves over a panel and calls the focus changed method if nessesary*/
		
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
			// Trigger a focus change
			questionFocusChange();
		}
	}

    public void mouseExited(MouseEvent evt) 
	{
		/* Called when the mouse leaves a panel and works out the question that lost focus */
		
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
		
		questionLostFocus = sourceQuestion.getQuestionID(); // Set the question that lost focus to the id of the question panel
    }

	public void windowClosing(WindowEvent evt)
	{
		/* Intercepts the window closing to save the form on close and update the percentage complete */
		
		users.writeDatabase(); // Save the users stats
		
		System.out.println("[INFO] <FORM_DISPLAYER> Close button pressed");
		
		
		// Work out the number of questions that the user answered correctly
		// in order to work out the percentage of the form that they have
		// completed.
		
		int numberCorrect = 0;
		
		for (QuestionPanel qP : questionPanels) // For each question in the form
		{
			if (qP.validateAnswers()) // If they passed the validation check
			{
				numberCorrect++;
			}
		}
		
		if (percentComplete != 100) // If the form isn't completed
		{
			percentComplete = (numberCorrect * 100)/ questionPanels.length; // Calculate what percentage of the questions are answered correctly
		}
		
		System.out.println("[INFO] <FORM_DISPLAYER> form is " +  percentComplete + "% complete");
		
		
		// Remove the mouselistener from all components and questions in the form
		for (QuestionPanel question : questionPanels) // For each question in the form
		{
			question.removeMouseListener(this); // Remove the mouse listener
			
			for (JComponent component : question.getComponents())
			{
				component.removeMouseListener(this); // Remove the mouse listener
			}

		}
	
		// Save the form
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