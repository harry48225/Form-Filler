import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import javax.swing.table.*;

import java.time.*;

public class Report extends JPanel
{
	private String[] tableHeaders = new String[] {"<html><center>Question<br>type<</html>", "<html><center>Number of times<br>failed validation</html>", "<html><center>Average number<br>of attempts<br>needed to correct</html>", "<html><center>Average time<br>to complete (seconds)</html>"}; // The headers for the table
	private String[][] reportData; // The data about how the user is performing with the questions
	private JTable reportTable;
	private JScrollPane reportTableScrollPane;
	private JPanel reportTablePanel = new JPanel(); // Stores the table
	
	private String username; // The user's username
	
	public Report(String[][] tempReportData, String tempUsername)
	{
		reportData = tempReportData;
		username = tempUsername;
		
		prepareGUI();
	}
	
	private void prepareGUI()
	{
			
		System.out.println("[INFO] <REPORT> Running prepareGUI");
		
		this.setLayout(new GridLayout(1,1));
		
		prepareReportTable();
		
		this.add(reportTablePanel);
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
		
		// Make the headers taller by overriding the getPreferredSize method
		reportTableScrollPane.setColumnHeader(new JViewport() {
				@Override public Dimension getPreferredSize() {
					Dimension d = super.getPreferredSize();
					d.height = 50;
						return d;
				}
		});
		
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
	
}