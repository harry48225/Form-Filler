import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StatisticsPanel extends JPanel implements ActionListener
{
	private User currentUser;
	private QuestionStatList questionStats;
	
	private QuestionList questions;
	
	private JPanel mainPanel = new JPanel();
	private JButton helpButton = new JButton("Help");
	
	private JButton produceReportButton = new JButton("Produce report");
	
	private JComboBox<String> questionDropDown;
	private JButton viewQuestionButton = new JButton("View question");
	
	
	private JLabel selectedQuestionLabel = new JLabel("Selected question: ");
	private JLabel timesFailedValidationLabel = new JLabel("Times failed validation: ");
	private JLabel mostRecentAttemptsNeededToCorrectLabel = new JLabel("5 most recent number of attempts taken to correct an error: ");
	private JLabel timeTakenToCompleteLabel = new JLabel("5 most recent time taken to complete the question: ");
	private JLabel averageTimeTakenToCompleteLabel = new JLabel("Average time taken to complete: ");
	
	public StatisticsPanel(User tempUser, QuestionList tempQuestions)
	{
		currentUser = tempUser;
		questionStats = currentUser.getQuestionStats();
		questions = tempQuestions;
		
		prepareGUI();
	}
	
	private void prepareGUI()
	{
		System.out.println("[INFO] <STATISTICS_PANEL> Running prepareGUI");
		
		this.setLayout(new BorderLayout());
		
		questionDropDown = new JComboBox<String>(questionStats.getIDArray());
		
		mainPanel.setLayout(new GridLayout(0,1));
		
		// Add the report button
		produceReportButton.addActionListener(this);
		mainPanel.add(produceReportButton);
		
		// Add the question selection
		mainPanel.add(questionDropDown);
		viewQuestionButton.addActionListener(this);
		mainPanel.add(viewQuestionButton);
		
		// Add all of the labels
		mainPanel.add(selectedQuestionLabel);
		mainPanel.add(timesFailedValidationLabel);
		mainPanel.add(mostRecentAttemptsNeededToCorrectLabel);
		mainPanel.add(timeTakenToCompleteLabel);
		mainPanel.add(averageTimeTakenToCompleteLabel);
		
		this.add(mainPanel, BorderLayout.CENTER); // Add the panel in the center
		
		helpButton.addActionListener(this);
		this.add(helpButton, BorderLayout.NORTH);
	}
	
	private void updateStatView(String selectedQuestion)
	{
		System.out.println("[INFO] <STATISTICS_PANEL> Running updateStatView");
		
		selectedQuestionLabel.setText("Selected question: " + selectedQuestion);
		
		QuestionStat stats = questionStats.getQuestionStatByID(selectedQuestion); // Get the relevant question stat object
		
		// Time failed validation
		timesFailedValidationLabel.setText("Times failed validation: " + stats.getTimesFailedValidation());
		
		// Attempts needed to correct
		
		String attemptsNeededToCorrect = "";
		
		for (int attempt : stats.getNumberOfAttemptsNeededToCorrect())
		{
			if (attempt != 0) // If the attempt isn't null data
			{
				attemptsNeededToCorrect += attempt + ",";
			}
			else // Break as all the meaningful data has been added
			{
				break;
			}
		}
		
		mostRecentAttemptsNeededToCorrectLabel.setText("5 most recent number of attempts taken to correct an error: " + attemptsNeededToCorrect);
		
		String timeTakenToComplete = "";
		
		
		// To calculate the average
		int numberOfTimes = 0; // Store the number of non null entries in the array
		long totalTime = 0; // Store the total amount of time
		
		for (long time : stats.getTimeTakenToComplete())
		{
			if (time != 0) // If the time isn't null data
			{
				String minutes = time/60 + ""; // Divide by 60 to get the number of minutes
				String seconds = time % 60 + ""; // Mod by 60 to get the number of seconds
				
				if (seconds.length() < 2) // If it's not 2 digits
				{
					seconds = "0" + seconds; // Pad with a leading zero
				}
				
				timeTakenToComplete += minutes + ":" + seconds + ",";
				
				totalTime += time; // Add the time to the total 
				numberOfTimes++; // Increment the number of times
			}
			else // Break as all meaningful data has been added
			{
				break;
			}
		}	
		
		timeTakenToCompleteLabel.setText("5 most recent time taken to complete the question: " + timeTakenToComplete);
		
		long average = totalTime / numberOfTimes; // Calculate the average
		
		String minutes = average/60 + ""; // Divide by 60 to get the number of minutes
		String seconds = average % 60 + ""; // Mod by 60 to get the number of seconds
				
		if (seconds.length() < 2) // If it's not 2 digits
		{
			seconds = "0" + seconds; // Pad with a leading zero
		}
				
		String averageTimeTakenToComplete = minutes + ":" + seconds;
		averageTimeTakenToCompleteLabel.setText("Average time taken to complete: " + averageTimeTakenToComplete);
	}
	
	private void viewReport() // Opens a window to view a report produced by question stat list
	{
		System.out.println("[INFO] <STATISTICS_PANEL> running viewReport");
		
		String[][] reportData = questionStats.produceReport(questions);
		
		new ReportWindow(reportData, currentUser.getUsername()); // Open a report window
	}
	
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == viewQuestionButton)
		{
			System.out.println("[INFO] <STATISTICS_PANEL> viewQuestionButton pressed");
			
			String selectedQuestion = (String) questionDropDown.getSelectedItem();
			updateStatView(selectedQuestion);
		}
		else if (evt.getSource() == produceReportButton)
		{
			System.out.println("[INFO] <STATISTICS_PANEL> produceReportButton pressed");
			viewReport();
		}
		else if (evt.getSource() == helpButton)
		{
			System.out.println("[INFO] <STATISTICS_PANEL> helpButton pressed");
			
			JOptionPane.showMessageDialog(null,"This is the statistics panel. You can view information about each question that you've filled in. \r\n To do this select the question from the drop down and press view question. \r\n You can also press produce report to produce a printable report detailing your progress with each question type.");
		}
	}
}