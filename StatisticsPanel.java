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
		return HELP_STRING;
	}
	
	public void refresh()
	{
		questionSelector.refreshTable();
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
		produceReportButton.setBackground(new Color(130,183,75)); // green
		leftSide.add(produceReportButton, BorderLayout.SOUTH);
		
		mainPanel.add(leftSide);
		
		prepareStatisticsPanel();
		mainPanel.add(statsPanel);

		
		this.add(mainPanel, BorderLayout.CENTER); // Add the panel in the center
		
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
		
		correctionsChart = new NumberOfAttemptsToCorrectChart(new int[] {0}, 0);
		statsPanel.add(correctionsChart);
		
		timeChart = new TimeTakenToCompleteChart(new long[] {0}, 0);
		statsPanel.add(timeChart);
		
		validationChart = new ValidationChart(0,0);
		statsPanel.add(validationChart);
	}
	
	private void updateStatView(String selectedQuestion)
	{
		System.out.println("[INFO] <STATISTICS_PANEL> Running updateStatView");
		
		QuestionStat stats = questionStats.getQuestionStatByID(selectedQuestion); // Get the relevant question stat object
		
		// Update all of the labels without affecting the other text
		numberOfAttemptsLabel.setText(numberOfAttemptsLabel.getText().split(":")[0] + ": " + stats.getNumberOfAttempts());
		timesFailedValidationLabel.setText(timesFailedValidationLabel.getText().split(":")[0] + ": " + stats.getTimesFailedValidation());
		averageTimeTakenToCompleteLabel.setText(averageTimeTakenToCompleteLabel.getText().split(":")[0] + ": " + stats.getAverageTimeTakenToComplete() + " seconds");
		
		correctionsChart.updateChart(stats.getNumberOfAttemptsNeededToCorrect(), stats.getNumberOfAttempts());
		timeChart.updateChart(stats.getTimeTakenToComplete(), stats.getNumberOfAttempts());
		validationChart.updateChart(stats.getNumberOfAttempts(), stats.getTimesFailedValidation());
	}
	
	private void viewReport() // Opens a window to view a report produced by question stat list
	{
		System.out.println("[INFO] <STATISTICS_PANEL> running viewReport");
		
		String[][] reportData = questionStats.produceReport(questions);
		
		new ReportWindow(reportData, currentUser.getUsername(), icons); // Open a report window
	}
	
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == viewQuestionButton)
		{
			System.out.println("[INFO] <STATISTICS_PANEL> viewQuestionButton pressed");
			
			String selectedQuestion = questionSelector.getSelectedQuestionID();
			if (selectedQuestion != null)
			{
				updateStatView(selectedQuestion);
			}
		}
		else if (evt.getSource() == produceReportButton)
		{
			System.out.println("[INFO] <STATISTICS_PANEL> produceReportButton pressed");
			viewReport();
		}
	}
	
	private class TimeTakenToCompleteChart extends JPanel
	{
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
			timeTakenToComplete = tempTimeTakenToComplete;
			totalNumberOfSuccessfulAttempts = tempSuccessfulAttempts;
			
			updateChart();
		}
		
		private void calculateValidNumberOfAttempts()
		{
			// Find how many data points there are
			// and start the chart at the first one
			int numberOfDataPoints = 0;
			for (long attempt : timeTakenToComplete)
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
			calculateValidNumberOfAttempts();
			
			JFreeChart chart = ChartFactory.createXYLineChart(
			"Time taken to complete",
			"Attempt number", "Time (seconds)",
			createDataset(),
			PlotOrientation.VERTICAL,
			false,false,false);
			
			System.out.println(validNumberOfAttempts + " " + totalNumberOfSuccessfulAttempts);
			XYPlot plot = (XYPlot) chart.getPlot();  

			NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
			NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
			
			// Make the axis start at the oldest attempt number that we have data for
			xAxis.setLowerBound(totalNumberOfSuccessfulAttempts - validNumberOfAttempts + 1);
			xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); // Only show integers on the axis
			yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); // Only show integers on the axis
			
			ChartPanel cP = new ChartPanel(chart);
			cP.setPopupMenu(null);
			
			this.removeAll();
			this.setPreferredSize(new Dimension(200,200));
			this.setLayout(new GridLayout(1,1));
			this.add(cP);
			
			this.revalidate();
			this.repaint();
		}
		
		private XYDataset createDataset()
		{
			XYSeriesCollection dataset = new XYSeriesCollection();
			XYSeries data = new XYSeries("data");
			
			for (int i = validNumberOfAttempts-1; i >= 0; i--)
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
			numberOfAttemptsToCorrect = tempNumberOfAttempts;
			totalNumberOfSuccessfulAttempts = tempSuccessfulAttempts;
			
			updateChart();
		}
		
		private void calculateValidNumberOfAttempts()
		{
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
			calculateValidNumberOfAttempts();
			
			JFreeChart chart = ChartFactory.createXYLineChart(
			"Times taken to correct an error",
			"Attempt number", "Number of times",
			createDataset(),
			PlotOrientation.VERTICAL,
			false,false,false);
			
			XYPlot plot = (XYPlot) chart.getPlot();  

			NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
			NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
			
			// Make the axis start at the oldest attempt number that we have data for
			xAxis.setLowerBound(totalNumberOfSuccessfulAttempts - validNumberOfAttempts + 1);
			xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); // Only show integers on the axis
			yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); // Only show integers on the axis
			
			ChartPanel cP = new ChartPanel(chart);
			cP.setPopupMenu(null);
			
			this.removeAll();
			this.setPreferredSize(new Dimension(200,200));
			this.setLayout(new GridLayout(1,1));
			this.add(cP);
			
			this.revalidate();
			this.repaint();
		}
		private XYDataset createDataset()
		{
			XYSeriesCollection dataset = new XYSeriesCollection();
			XYSeries data = new XYSeries("data");
			
			for (int i = validNumberOfAttempts-1; i >= 0; i--)
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
	
	public class ValidationChart extends JPanel {

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
			DefaultPieDataset dataset = new DefaultPieDataset();
			dataset.setValue(KEY1, timesFailedValidation);
			dataset.setValue(KEY2, totalNumberOfAttempts);
			
			return dataset;
		}
		
		public void updateChart(int tempTotalNumberOfAttempts, int tempTimesFailedValidation)
		{
			totalNumberOfAttempts = tempTotalNumberOfAttempts;
			timesFailedValidation = tempTimesFailedValidation;
			
			updateChart();
		}
		
		private void updateChart()
		{
			JFreeChart chart = ChartFactory.createPieChart(
				"Percentage of times failed validation check", createDataset(), true, true, false);
			
			ChartPanel cP = new ChartPanel(chart);	
			cP.setPopupMenu(null);
			
			this.removeAll();
			this.setPreferredSize(new Dimension(200,200));
			this.setLayout(new GridLayout(1,1));
			this.add(cP);
			
			this.revalidate();
			this.repaint();
		}
	}
	
}