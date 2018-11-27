import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;


public class StatisticsPanel extends JPanel implements ActionListener
{
	private User currentUser;
	private QuestionStatList questionStats;
	
	private QuestionList questions;
	
	private JPanel mainPanel = new JPanel();
	private JButton helpButton = new JButton("Help");
	
	private JPanel statsPanel = new JPanel();
	private SelectQuestionsPanel questionSelector;
	
	private JButton produceReportButton = new JButton("Produce report");
	
	private JComboBox<String> questionDropDown;
	private JButton viewQuestionButton = new JButton("View question");
	
	
	private JLabel numberOfAttemptsLabel = new JLabel("Number of times attempted: ", SwingConstants.CENTER);
	private JLabel timesFailedValidationLabel = new JLabel("Number of times failed validation: ", SwingConstants.CENTER);
	private JLabel averageTimeTakenToCompleteLabel = new JLabel("Average time taken to complete: ", SwingConstants.CENTER);
	
	private Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED); // Border style
	
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
		
		mainPanel.setLayout(new GridLayout(1,2));
		
		JPanel leftSide = new JPanel();
		leftSide.setLayout(new BorderLayout());
		
		// Prepare the question selector
		questionSelector = new SelectQuestionsPanel(questions);
		
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Select a question");
		Font currentFont = border.getTitleFont();
		border.setTitleFont(currentFont.deriveFont(Font.BOLD, 16)); // Make the font larger and bold
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		questionSelector.setBorder(border); // Set the border
		
		
		viewQuestionButton.addActionListener(this);
		viewQuestionButton.setBackground(new Color(169,196,235));
		viewQuestionButton.setMaximumSize(new Dimension(80, 40));
		questionSelector.addNewButton(viewQuestionButton);
		
		leftSide.add(questionSelector, BorderLayout.CENTER);
		
		// Add the report button
		produceReportButton.addActionListener(this);
		produceReportButton.setBackground(new Color(130,183,75));
		leftSide.add(produceReportButton, BorderLayout.SOUTH);
		
		mainPanel.add(leftSide);
		
		prepareStatisticsPanel();
		mainPanel.add(statsPanel);

		
		this.add(mainPanel, BorderLayout.CENTER); // Add the panel in the center
		
		helpButton.addActionListener(this);
		this.add(helpButton, BorderLayout.NORTH);
	}
	
	private void prepareStatisticsPanel()
	{
		statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.PAGE_AXIS));
		
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Statistics");
		Font currentFont = border.getTitleFont();
		border.setTitleFont(currentFont.deriveFont(Font.BOLD, 16)); // Make the font larger and bold
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		statsPanel.setBorder(border); // Set the border
		
		// Add all of the labels
		numberOfAttemptsLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		timesFailedValidationLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		averageTimeTakenToCompleteLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		
		statsPanel.add(Box.createRigidArea(new Dimension(0,10)));
		statsPanel.add(numberOfAttemptsLabel);
		statsPanel.add(Box.createRigidArea(new Dimension(0,10)));
		statsPanel.add(timesFailedValidationLabel);
		statsPanel.add(Box.createRigidArea(new Dimension(0,10)));
		statsPanel.add(averageTimeTakenToCompleteLabel);
		statsPanel.add(Box.createRigidArea(new Dimension(0,5)));
		
		statsPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
	}
	
	private String calcuateAverageTimeTakenToComplete(QuestionStat stats)
	{
		// To calculate the average
		int numberOfTimes = 0; // Store the number of non null entries in the array
		long totalTime = 0; // Store the total amount of time
		
		for (long time : stats.getTimeTakenToComplete())
		{
			if (time != 0) // If the time isn't null data
			{	
				totalTime += time; // Add the time to the total 
				numberOfTimes++; // Increment the number of times
			}
			else // Break as all meaningful data has been added
			{
				break;
			}
		}	
		
		if (numberOfTimes > 0)
		{
			long average = totalTime / numberOfTimes; // Calculate the average
			
			String seconds = average + "";
			
			//String minutes = average/60 + ""; // Divide by 60 to get the number of minutes
			//String seconds = average % 60 + ""; // Mod by 60 to get the number of seconds
					
			if (seconds.length() < 2) // If it's not 2 digits
			{
				seconds = "0" + seconds; // Pad with a leading zero
			}
					
			String averageTimeTakenToComplete = seconds + " seconds";
			
			return averageTimeTakenToComplete;
		}
		else
		{
			return "";
		}
		
	}
	
	private void updateStatView(String selectedQuestion)
	{
		System.out.println("[INFO] <STATISTICS_PANEL> Running updateStatView");
		
		QuestionStat stats = questionStats.getQuestionStatByID(selectedQuestion); // Get the relevant question stat object
		
		// Update all of the labels without affecting the other text
		numberOfAttemptsLabel.setText(numberOfAttemptsLabel.getText().split(":")[0] + ": " + stats.getNumberOfAttempts());
		timesFailedValidationLabel.setText(timesFailedValidationLabel.getText().split(":")[0] + ": " + stats.getTimesFailedValidation());
		averageTimeTakenToCompleteLabel.setText(averageTimeTakenToCompleteLabel.getText().split(":")[0] + ": " + calcuateAverageTimeTakenToComplete(stats));
		
		/*
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
		*/
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
			
			String selectedQuestion = questionSelector.getSelectedQuestionID();
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