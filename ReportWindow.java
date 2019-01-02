import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import javax.swing.table.*;

import java.time.*;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

public class ReportWindow extends JFrame implements ActionListener, Printable
{
	// For the table
	private String[] tableHeaders = new String[] {"<html><center>Question<br>type</html>", "<html><center>Number of times<br>failed validation</html>", "<html><center>Average number of attempts<br>needed to correct</html>", "<html><center>Average time<br>to complete (seconds)</html>"}; // The headers for the table
	private String[][] reportData; // The data about how the user is performing with the questions
	private JTable reportTable;
	private JScrollPane reportTableScrollPane;
	private JPanel reportTablePanel = new JPanel(); // Stores the table
	
	private JPanel reportPreviewPanel = new JPanel();
	
	private JButton printButton = new JButton("Print");
	
	private String username; // The user's username
	
	private Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED); // Border style
	
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
		
		this.setSize(600,800);
		
		prepareReportTable();
		
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
	
	private void prepareReportTable()
	{
		reportTable = new JTable(reportData, tableHeaders); // Create a new table
		reportTableScrollPane = new JScrollPane(reportTable);
		
		
		// Make the background of the table white
		reportTable.setOpaque(true);
		reportTable.setFillsViewportHeight(true);
		reportTable.setBackground(Color.WHITE);
		
		// Make it non selectable and non editable
		reportTable.setFocusable(false);
		reportTable.setRowSelectionAllowed(false);
		reportTable.setEnabled(false);
		
		// Disable dragging of the columns and resizing
		reportTable.getTableHeader().setReorderingAllowed(false);
		reportTable.getTableHeader().setResizingAllowed(false);
		
		// Center the text in the cells
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
		
		reportTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		reportTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
		reportTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
		
		reportTablePanel.setLayout(new BorderLayout());
		reportTablePanel.setBackground(Color.WHITE);
		reportTablePanel.add(reportTableScrollPane, BorderLayout.CENTER);
		
		
		
		reportTablePanel.add(createHeaderPanel(), BorderLayout.NORTH);
	}
	
	private JPanel createHeaderPanel()
	{
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.LINE_AXIS));
		headerPanel.setBackground(Color.WHITE);
		
		headerPanel.add(Box.createHorizontalStrut(10));
		headerPanel.add(new JLabel(username + "\'s report"));
		headerPanel.add(Box.createHorizontalGlue());
		String rawToday = LocalDate.now().toString(); // Get today's date
		// By default its yyyy-mm-dd
		// We want dd-mm-yyyy
		String[] todayArray = rawToday.split("-");
		String today = todayArray[2] + "-" + todayArray[1] + "-" + todayArray[0];
		headerPanel.add(new JLabel(today));
		headerPanel.add(Box.createHorizontalStrut(10));
		
		return headerPanel;
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