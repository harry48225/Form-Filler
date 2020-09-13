import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

import java.util.List;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.general.DefaultPieDataset;

public class StatisticsPanel extends JPanel implements ActionListener, Helper
{
	/* This panel allows the user to select a question and view 
	statistics about it, they can also produce a report */
	private final String HELP_STRING = "This is the statistics panel. You can view information about each question that you've filled in. To do this select the question from the drop down and press view question. You can also press produce report to produce a printable report detailing your progress with each question type.";
	
	private User currentUser;
	private QuestionStatList questionStats;
	private List<Image> icons;
	
	private QuestionList questions;
	
	private JPanel mainPanel = new JPanel();
	
	private JPanel statsPanel = new JPanel();
	private SelectQuestionsPanel questionSelector;
	
	private JButton produceReportButton = new JButton("Produce report");
	
	private JComboBox<String> questionDropDown;
	private JButton viewQuestionButton = new JButton("View question");
	
	
	private JLabel numberOfAttemptsLabel = new JLabel("Number of times attempted: ", SwingConstants.CENTER);
	private JLabel timesFailedValidationLabel = new JLabel("Number of times failed validation: ", SwingConstants.CENTER);
	private JLabel averageTimeTakenToCompleteLabel = new JLabel("Average time taken to complete: ", SwingConstants.CENTER);
	
	private NumberOfAttemptsToCorrectChart correctionsChart;
	private TimeTakenToCompleteChart timeChart;
	private ValidationChart validationChart;
	private Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED); // Border style
	
	public StatisticsPanel(User tempUser, QuestionList tempQuestions, List<Image> tempIcons)
	{
		currentUser = tempUser;
		questionStats = currentUser.getQuestionStats();
		questions = tempQuestions;
		icons = tempIcons;
		
		prepareGUI();
	}
	
	public String getHelpString()
	{
		/* Returns the help string */
		return HELP_STRING;
	}
	
	public void refresh()
	{
		/* Refreshes the question table with the latest question information */
		questionSelector.refreshTable();
	}
	
	private void prepareGUI()
	{
		/* Prepares the panel for display */
		
		System.out.println("[INFO] <STATISTICS_PANEL> Running prepareGUI");
		
		this.setLayout(new BorderLayout());
		
		
		// Use a grid layout with 1 row and 2 columns
		mainPanel.setLayout(new GridLayout(1,2));
		
		JPanel leftSide = new JPanel();
		leftSide.setLayout(new BorderLayout());
		
		// Prepare the question selector
		questionSelector = new SelectQuestionsPanel(questions);
		
		// Make double clicking on a row open that question to be attempted.
		questionSelector.getTable().addMouseListener(new MouseAdapter() {
							public void mousePressed(MouseEvent mouseEvent) {
								JTable table =(JTable) mouseEvent.getSource();
								if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
									viewQuestionButton.doClick();
								}
							}
						});
		
		// Add a "Select a question" border
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Select a question");
		Font currentFont = border.getTitleFont();
		border.setTitleFont(currentFont.deriveFont(Font.BOLD, 16)); // Make the font larger and bold
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		questionSelector.setBorder(border); // Set the border
		
		
		// Add a view question button of the correct colour and size to the question selector
		viewQuestionButton.addActionListener(this);
		viewQuestionButton.setBackground(new Color(169,196,235)); // Blue
		viewQuestionButton.setMaximumSize(new Dimension(80, 40));
		questionSelector.addNewButton(viewQuestionButton);
		
		leftSide.add(questionSelector, BorderLayout.CENTER); // Put the question selector in the center of the left side
		
		// Add the report button
		produceReportButton.addActionListener(this);
		produceReportButton.setBackground(new Color(130,183,75)); // green
		leftSide.add(produceReportButton, BorderLayout.SOUTH); // Add it to the bottom of left side
		
		mainPanel.add(leftSide);
		
		// Prepare and add the statistics panel
		prepareStatisticsPanel();
		mainPanel.add(statsPanel);

		
		this.add(mainPanel, BorderLayout.CENTER); // Add the panel in the center
		
	}
	
	private void prepareStatisticsPanel()
	{
		/* Prepares the statistics panel that displays the graphs and other statistics */
		
		statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.PAGE_AXIS)); // Vertical box layout
		
		// Add a statistics border
		
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Statistics");
		Font currentFont = border.getTitleFont();
		border.setTitleFont(currentFont.deriveFont(Font.BOLD, 16)); // Make the font larger and bold
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		statsPanel.setBorder(border); // Set the border
		
		// Add all of the labels and make the text center aligned
		numberOfAttemptsLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		timesFailedValidationLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		averageTimeTakenToCompleteLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		
		// Add the labels with 10px vertical space between them
		statsPanel.add(Box.createRigidArea(new Dimension(0,10)));
		statsPanel.add(numberOfAttemptsLabel);
		statsPanel.add(Box.createRigidArea(new Dimension(0,10)));
		statsPanel.add(timesFailedValidationLabel);
		statsPanel.add(Box.createRigidArea(new Dimension(0,10)));
		statsPanel.add(averageTimeTakenToCompleteLabel);
		statsPanel.add(Box.createRigidArea(new Dimension(0,5)));
		
		// Add a horizontal line separator
		statsPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		// Create and add blank charts
		correctionsChart = new NumberOfAttemptsToCorrectChart(new int[] {0}, 0);
		statsPanel.add(correctionsChart);
		
		timeChart = new TimeTakenToCompleteChart(new long[] {0}, 0);
		statsPanel.add(timeChart);
		
		validationChart = new ValidationChart(0,0);
		statsPanel.add(validationChart);
	}
	
	private void updateStatView(String selectedQuestion)
	{
		/* Takes the id of a question and updates the graphs and labels
			with the statistics of that question */
			
		System.out.println("[INFO] <STATISTICS_PANEL> Running updateStatView");
		
		QuestionStat stats = questionStats.getQuestionStatByID(selectedQuestion); // Get the relevant question stat object
		
		// Update all of the labels without affecting the other text
		// All of the labels are in the form <description>:<Actual stat>
		// Therefore by splitting at the : and taking the first part we can get the
		// description and then append the new data
		numberOfAttemptsLabel.setText(numberOfAttemptsLabel.getText().split(":")[0] + ": " + stats.getNumberOfAttempts());
		timesFailedValidationLabel.setText(timesFailedValidationLabel.getText().split(":")[0] + ": " + stats.getTimesFailedValidation());
		averageTimeTakenToCompleteLabel.setText(averageTimeTakenToCompleteLabel.getText().split(":")[0] + ": " + stats.getAverageTimeTakenToComplete() + " seconds");
		
		// Update the graphs with the new question's data
		correctionsChart.updateChart(stats.getNumberOfAttemptsNeededToCorrect(), stats.getNumberOfAttempts());
		timeChart.updateChart(stats.getTimeTakenToComplete(), stats.getNumberOfAttempts());
		validationChart.updateChart(stats.getNumberOfAttempts(), stats.getTimesFailedValidation());
	}
	
	private void viewReport()
	{
		/* Opens a window to view a report produced by question stat list */
		System.out.println("[INFO] <STATISTICS_PANEL> running viewReport");
		
		// Get the report data
		String[][] reportData = questionStats.produceReport(questions);
		
		new ReportWindow(reportData, currentUser.getUsername(), icons); // Open a report window with that data
	}
	
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == viewQuestionButton)
		{
			System.out.println("[INFO] <STATISTICS_PANEL> viewQuestionButton pressed");
			
			// Loads the selected question's statistics
			String selectedQuestion = questionSelector.getSelectedQuestionID();
			if (selectedQuestion != null) // If a question was actually selected
			{
				updateStatView(selectedQuestion);
			}
		}
		else if (evt.getSource() == produceReportButton)
		{
			// Opens a report for the user to view
			System.out.println("[INFO] <STATISTICS_PANEL> produceReportButton pressed");
			viewReport();
		}
	}
	
	private class TimeTakenToCompleteChart extends JPanel
	{
		/* A line chart that displays the length of time that it took
			the user to complete a question for their most recent attempts */
			
		private long[] timeTakenToComplete;
		private int totalNumberOfSuccessfulAttempts;
		private int validNumberOfAttempts; // The array contains some rogue values so we need to deal with those.
		
		public TimeTakenToCompleteChart(long[] tempTimeTakenToComplete, int tempSuccessfulAttempts)
		{
			timeTakenToComplete = tempTimeTakenToComplete;
			totalNumberOfSuccessfulAttempts = tempSuccessfulAttempts;
			
			updateChart();
			
		}
		
		public void updateChart(long[] tempTimeTakenToComplete, int tempSuccessfulAttempts)
		{
			/* Updates the chart with new data. This is the public facing
			method that allows new data to be specified */
			
			timeTakenToComplete = tempTimeTakenToComplete;
			totalNumberOfSuccessfulAttempts = tempSuccessfulAttempts;
			
			updateChart();
		}
		
		private void calculateValidNumberOfAttempts()
		{
			/* Works out how many of the data points are actually valid.
				This is needed because there are some rogue values in the array */
				
			// Find how many data points there are
			// and start the chart at the first one
			int numberOfDataPoints = 0;
			for (long attempt : timeTakenToComplete) // For each attempt
			{
				if (attempt != -1) // If it's not a rogue value
				{
					numberOfDataPoints++;
				}
				else
				{
					break; // The rest will be null
				}
			}
			
			validNumberOfAttempts = numberOfDataPoints;
		}
		
		private void updateChart()
		{
			/* Updates the chart with the new data */
			calculateValidNumberOfAttempts();
			
			// Create a new line chart
			JFreeChart chart = ChartFactory.createXYLineChart(
			"Time taken to complete",
			"Attempt number", "Time (seconds)",
			createDataset(),
			PlotOrientation.VERTICAL, // x axis on the bottom and y on top
			false,false,false); // No legends, tooltips, or URLs
			
			// Adjust the axis so that the x axis only starts at the oldest most recent attempt number rather than 0
			XYPlot plot = (XYPlot) chart.getPlot();  

			NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
			NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
			
			// Make the axis start at the oldest attempt number that we have data for
			xAxis.setLowerBound(totalNumberOfSuccessfulAttempts - validNumberOfAttempts + 1);
			xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); // Only show integers on the axis
			yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); // Only show integers on the axis
			
			ChartPanel cP = new ChartPanel(chart);
			cP.setPopupMenu(null); // Disable the extra options popup menu
			
			// Remove the current chart
			this.removeAll();
			// Make the chart the correct size
			this.setPreferredSize(new Dimension(200,200));
			this.setLayout(new GridLayout(1,1));
			this.add(cP);
			
			// Force the window to redraw
			this.revalidate();
			this.repaint();
		}
		
		private XYDataset createDataset()
		{
			/* Creates an XYDataset from the number of attempts and time taken to complete
				that can be displayed in the graph*/
				
			XYSeriesCollection dataset = new XYSeriesCollection();
			XYSeries data = new XYSeries("data");
			
			// Add a data point for each of the recent attempts
			for (int i = validNumberOfAttempts-1; i >= 0; i--) // Go most recent backwards
			{
				if (timeTakenToComplete[i] != -1)
				{
					int xCoordinate = totalNumberOfSuccessfulAttempts - i;
					data.add(xCoordinate, timeTakenToComplete[i]);
				}
			}
			
			dataset.addSeries(data);
			return dataset;
		}
	}
	
	
	private class NumberOfAttemptsToCorrectChart extends JPanel
	{
		/* A line chart that displays the number of times that it took
			the user to correct a question for their most recent attempts */
			
		private int[] numberOfAttemptsToCorrect;
		private int totalNumberOfSuccessfulAttempts;
		private int validNumberOfAttempts; // The array contains some rogue values so we need to deal with those.
		
		public NumberOfAttemptsToCorrectChart(int[] tempNumberOfAttempts, int tempSuccessfulAttempts)
		{
			numberOfAttemptsToCorrect = tempNumberOfAttempts;
			totalNumberOfSuccessfulAttempts = tempSuccessfulAttempts;
			
			updateChart();
			
		}
		
		public void updateChart(int[] tempNumberOfAttempts, int tempSuccessfulAttempts)
		{
			/* Updates the chart with new data. This is the public facing
			method that allows new data to be specified */
			
			numberOfAttemptsToCorrect = tempNumberOfAttempts;
			totalNumberOfSuccessfulAttempts = tempSuccessfulAttempts;
			
			updateChart();
		}
		
		private void calculateValidNumberOfAttempts()
		{
			/* Works out how many of the data points are actually valid.
				This is needed because there are some rogue values in the array */
				
			// Find how many data points there are
			// and start the chart at the first one
			int numberOfDataPoints = 0;
			for (int attempt : numberOfAttemptsToCorrect)
			{
				if (attempt != -1)
				{
					numberOfDataPoints++;
				}
				else
				{
					break; // The rest will be null
				}
			}
			
			validNumberOfAttempts = numberOfDataPoints;
		}
		
		private void updateChart()
		{
			/* Updates the chart with the new data */
			
			calculateValidNumberOfAttempts();
				
			// Create a new line chart
			JFreeChart chart = ChartFactory.createXYLineChart(
			"Times taken to correct an error",
			"Attempt number", "Number of times",
			createDataset(),
			PlotOrientation.VERTICAL, // x axis on the bottom and y on top
			false,false,false); // No legends, tooltips, or URLs
			
			// Adjust the axis so that the x axis only starts at the oldest most recent attempt number rather than 0
			XYPlot plot = (XYPlot) chart.getPlot();  

			NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
			NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
			
			// Make the axis start at the oldest attempt number that we have data for
			xAxis.setLowerBound(totalNumberOfSuccessfulAttempts - validNumberOfAttempts + 1);
			xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); // Only show integers on the axis
			yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); // Only show integers on the axis
			
			ChartPanel cP = new ChartPanel(chart);
			cP.setPopupMenu(null); // Disable the extra options popup menu
			
			// Remove the current chart
			this.removeAll();
			// Make the chart the correct size
			this.setPreferredSize(new Dimension(200,200));
			this.setLayout(new GridLayout(1,1));
			this.add(cP);
			
			// Force the window to redraw
			this.revalidate();
			this.repaint();
		}
		private XYDataset createDataset()
		{
			/* Creates an XYDataset from the number of attempts and time taken to complete
				that can be displayed in the graph*/
				
			XYSeriesCollection dataset = new XYSeriesCollection();
			XYSeries data = new XYSeries("data");
			
			// Add a data point for each of the recent attempts
			for (int i = validNumberOfAttempts-1; i >= 0; i--) // Go from the most recent backwards
			{
				if (numberOfAttemptsToCorrect[i] != -1)
				{
					int xCoordinate = totalNumberOfSuccessfulAttempts - i;
					data.add(xCoordinate, numberOfAttemptsToCorrect[i]);
				}
			}
			
			dataset.addSeries(data);
			return dataset;
		}
	}
	
	public class ValidationChart extends JPanel
	{
		/* A pie chart that displays the proportion of attempts
			that the user has completed the question successfully
			first time. */
			
		// The two categories (bins) of data
		private static final String KEY1 = "Failed Validation";
		private static final String KEY2 = "Successful completion";

		private int totalNumberOfAttempts;
		private int timesFailedValidation;
		
		public ValidationChart(int tempTotalNumberOfAttempts, int tempTimesFailedValidation) 
		{
		
			totalNumberOfAttempts = tempTotalNumberOfAttempts;
			timesFailedValidation = tempTimesFailedValidation;
			
			updateChart();
		}
		
		private DefaultPieDataset createDataset()
		{
			/* Creates a DefaultPieDataset with the data that has been
				given to the class */
				
			DefaultPieDataset dataset = new DefaultPieDataset();
			dataset.setValue(KEY1, timesFailedValidation);
			dataset.setValue(KEY2, totalNumberOfAttempts);
			
			return dataset;
		}
		
		public void updateChart(int tempTotalNumberOfAttempts, int tempTimesFailedValidation)
		{
			/* Updates the chart with new data. This is the public facing
			method that allows new data to be specified */
			
			totalNumberOfAttempts = tempTotalNumberOfAttempts;
			timesFailedValidation = tempTimesFailedValidation;
			
			updateChart();
		}
		
		private void updateChart()
		{
			
			/* Updates the chart with the new data */
			
			// Create a new chart with legends and tool tips but not urls
			JFreeChart chart = ChartFactory.createPieChart(
				"Percentage of times failed validation check", createDataset(), true, true, false);
			
			ChartPanel cP = new ChartPanel(chart);	
			cP.setPopupMenu(null); // Disable the extra options popup menu
			
			// Remove the current chart from the panel
			this.removeAll();
			// Make it the correct size
			this.setPreferredSize(new Dimension(200,200));
			this.setLayout(new GridLayout(1,1));
			this.add(cP);
			
			// Force the panel to redraw
			this.revalidate();
			this.repaint();
		}
	}
	
}