import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import javax.swing.table.*;

public class ReportWindow extends JFrame implements ActionListener, Printable
{
	// For the table
	private String[] tableHeaders = new String[] {"<html><center>Question<br>type</html>", "<html><center>Number of times<br>failed validation</html>", "<html><center>Average number of attempts<br>needed to correct</html>", "<html><center>Average time<br>to complete</html>"}; // The headers for the table
	private String[][] reportData; // The data about how the user is performing with the questions
	private JTable reportTable;
	private JScrollPane reportTableScrollPane;
	private JPanel reportTablePanel = new JPanel(); // Stores the table
	
	private JButton printButton = new JButton("Print");
	
	private String username; // The user's username
	
	public ReportWindow(String[][] tempReportData, String tempUsername)
	{
		reportData = tempReportData;
		username = tempUsername;
		
		prepareGUI();
	}
	
	private void prepareGUI()
	{
			
		System.out.println("[INFO] <REPORT_WINDOW> Running prepareGUI");
		
		this.setTitle(username + "\'s report");
		
		this.setLayout(new BorderLayout());
		
		this.setResizable(false);
		
		this.setSize(700,500);
		
		
		reportTable = new JTable(reportData, tableHeaders); // Create a new table
		reportTableScrollPane = new JScrollPane(reportTable);
		
		reportTablePanel.setLayout(new BorderLayout());
		reportTablePanel.add(reportTableScrollPane, BorderLayout.CENTER);
		reportTablePanel.add(new JLabel(username + "\'s report"), BorderLayout.NORTH);
		
		this.add(reportTablePanel, BorderLayout.CENTER);
			
		printButton.addActionListener(this);
		this.add(printButton, BorderLayout.SOUTH);
		
		this.setVisible(true);
		
	}
	
	public int print(Graphics g, PageFormat pf, int page) throws PrinterException 
	{
		if (page > 0) {
			return NO_SUCH_PAGE;
		}

		Graphics2D g2d = (Graphics2D)g;
		g2d.translate(pf.getImageableX(), pf.getImageableY());

		// Print the entire visible contents of a
		// java.awt.Frame.
		reportTablePanel.printAll(g);

		return PAGE_EXISTS;
	}
	
	private void runPrint()
	{
		System.out.println("[INFO] <REPORT_WINDOW> Running runPrint");
		
		PrinterJob job = PrinterJob.getPrinterJob();
		
		PageFormat pf = job.defaultPage();
		pf.setOrientation(PageFormat.LANDSCAPE); // Make the print job landscape
		
		job.setPrintable(this, pf);
		
		boolean doPrint = job.printDialog();
		
		if (doPrint)
		{
			try
			{
				job.print();
			}
			catch (PrinterException e)
			{
				System.out.println("[ERROR] <REPORT_WINDOW> Error printing " + e);
			}
		}
	}
	
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == printButton)
		{
			System.out.println("[INFO] <REPORT_WINDOW> printButton pressed");
			runPrint();
		}
	}
}