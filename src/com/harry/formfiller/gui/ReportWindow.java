package com.harry.formfiller.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


public class ReportWindow extends JFrame implements ActionListener, Printable
{
	/* Displays the report to the user and allows them to print it */
	
	// For the actual report
	private String[][] reportData; // The data about how the user is performing with the questions
	private JPanel reportTablePanel = new JPanel(); // Stores the report
	
	private JPanel reportPreviewPanel = new JPanel();
	
	private JButton printButton = new JButton("Print");
	
	private String username; // The user's username
	
	private transient Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED); // Border style
	
	public ReportWindow(String[][] tempReportData, String tempUsername, List<Image> icons)
	{
		reportData = tempReportData;
		username = tempUsername;
		
		this.setIconImages(icons);
		prepareGUI();
	}
	
	private void prepareGUI()
	{
		/* Prepares the panel visually */
		
		System.out.println("[INFO] <REPORT_WINDOW> Running prepareGUI");
		
		// Give the window an appropriate title
		this.setTitle(username + "\'s report");
		
		this.setLayout(new BorderLayout());
		
		
		this.setResizable(false);
		
		// Set the size and center the window
		this.setSize(600,800);
		this.setLocationRelativeTo(null); // Center it
		
		// Produce a report for the user
		reportTablePanel = new Report(reportData, username);
		
		// Add it to the panel
		reportPreviewPanel.setLayout(new GridLayout(1,1));
		reportPreviewPanel.add(reportTablePanel);
		
		
		// Create a preview border
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Report preview");
		Font currentFont = border.getTitleFont();
		border.setTitleFont(currentFont.deriveFont(Font.BOLD, 16)); // Make the font larger and bold
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		reportPreviewPanel.setBorder(border); // Set the border
		
		// Add the report to this panel
		this.add(reportPreviewPanel, BorderLayout.CENTER);
		
		// Prepare and add the print button
		printButton.addActionListener(this);
		printButton.setBackground(new Color(130,183,75)); // green
		this.add(printButton, BorderLayout.SOUTH); // Add the button to the bottom of the window

		this.setVisible(true);
		
	}
	
	public int print(Graphics g, PageFormat pf, int page) throws PrinterException 
	{
		/* Called when the report gets printed */
		
		// As the report only has one page return NO_SUCH_PAGE if a page greater than 1 is called to printed
		if (page > 0) {
			return NO_SUCH_PAGE;
		}

		// Stretch the report panel without altering the 
		// aspect ratio to fill an a4 piece of paper.
		Graphics2D g2d = (Graphics2D)g;
		g2d.translate(pf.getImageableX(), pf.getImageableY());
		double scaleFactor = pf.getImageableWidth()/reportTablePanel.getWidth();
		g2d.scale(scaleFactor, scaleFactor);

		// Print the reportTablePanel
		reportTablePanel.printAll(g);

		return PAGE_EXISTS;
	}
	
	private void runPrint()
	{
		/* Allows the user to print the report */
		System.out.println("[INFO] <REPORT_WINDOW> Running runPrint");
		
		// Get a new printer job
		PrinterJob job = PrinterJob.getPrinterJob();
		
		PageFormat pf = job.defaultPage();
		pf.setOrientation(PageFormat.PORTRAIT); // Make the print job portrait
		
		// Make the job printable
		job.setPrintable(this, pf);
		
		// Show the print dialog
		boolean doPrint = job.printDialog();
		
		if (doPrint) // If the user didn't press cancel
		{
			// Try to print the job
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