import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import javax.swing.table.*;

import java.time.*;

import java.util.List;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

public class ReportWindow extends JFrame implements ActionListener, Printable
{
	// For the table
	private String[][] reportData; // The data about how the user is performing with the questions
	private JPanel reportTablePanel = new JPanel(); // Stores the table
	
	private JPanel reportPreviewPanel = new JPanel();
	
	private JButton printButton = new JButton("Print");
	
	private String username; // The user's username
	
	private Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED); // Border style
	
	public ReportWindow(String[][] tempReportData, String tempUsername, List<Image> icons)
	{
		reportData = tempReportData;
		username = tempUsername;
		
		this.setIconImages(icons);
		prepareGUI();
	}
	
	private void prepareGUI()
	{
			
		System.out.println("[INFO] <REPORT_WINDOW> Running prepareGUI");
		
		this.setTitle(username + "\'s report");
		
		this.setLayout(new BorderLayout());
		
		this.setLocationRelativeTo(null); // Center it
		
		this.setResizable(false);
		
		this.setSize(600,800);
		
		reportTablePanel = new Report(reportData, username);
		
		reportPreviewPanel.setLayout(new GridLayout(1,1));
		reportPreviewPanel.add(reportTablePanel);
		
		TitledBorder border = BorderFactory.createTitledBorder(loweredetched, "Report preview");
		Font currentFont = border.getTitleFont();
		border.setTitleFont(currentFont.deriveFont(Font.BOLD, 16)); // Make the font larger and bold
		
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		
		reportPreviewPanel.setBorder(border); // Set the border
		
		
		this.add(reportPreviewPanel, BorderLayout.CENTER);
			
		printButton.addActionListener(this);
		printButton.setBackground(new Color(130,183,75)); // green
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
		double scaleFactor = pf.getImageableWidth()/reportTablePanel.getWidth();
		g2d.scale(scaleFactor, scaleFactor);

		// Print the reportTablePanel
		reportTablePanel.printAll(g);

		return PAGE_EXISTS;
	}
	
	private void runPrint()
	{
		System.out.println("[INFO] <REPORT_WINDOW> Running runPrint");
		
		PrinterJob job = PrinterJob.getPrinterJob();
		
		PageFormat pf = job.defaultPage();
		pf.setOrientation(PageFormat.PORTRAIT); // Make the print job portrait
		
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