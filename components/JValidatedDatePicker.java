package components;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.net.URL;
import java.io.*;

public class JValidatedDatePicker extends JPanel implements JValidatedComponent
{
	// Hardcoded data
	private String[] days = {"Day", "1","2","3","4","5","6","7","8","9","10",
										  "11","12","13","14","15","16","17","18",
										  "19","20","21","22","23","24","25","26",
										  "27","28","29","30","31"};
	
	private String[] months = {"Month", "January","February","March","April","May",
											   "June","July","August","September","October",
											   "November","December"};
	// ComboBoxes
	private JComboBox<String> daysComboBox = new JComboBox<String>(days);
	
	private JComboBox<String> monthsComboBox = new JComboBox<String>(months);
	
	private JComboBox<String> yearsComboBox;
	
	private final String ERROR_STRING = "Datepicker: Please enter a valid date";
	
	public JValidatedDatePicker()
	{
		setupDatePicker();
		
	}
	
	private void setupDatePicker()
	{
		this.setLayout(new GridLayout(1,3)); // 1 row 3 columns
		
		String[] yearArray = new String[100]; // Store the most recent 100 years
		
		for (int i = yearArray.length - 2; i >= 0; i--)
		{
			yearArray[i+1] = 2018 - i + ""; // Fill the array with the most recent date at the start and the oldest at the end
		}
		
		yearArray[0] = "Year"; // Store year at the start of the array
		
		yearsComboBox = new JComboBox<String>(yearArray); // Create the combobox
		
		this.add(daysComboBox);
		this.add(monthsComboBox);
		this.add(yearsComboBox);
	}
	
	public boolean validateAnswer()
	{
		boolean pass = true;
		
		if (daysComboBox.getSelectedIndex() == 0 || monthsComboBox.getSelectedIndex() == 0 || yearsComboBox.getSelectedIndex() == 0) // If any of the fields haven't been filled in
		{
			pass = false;
		}
		
		return pass;
	}
	
	public boolean presenceCheck()
	{
		boolean pass = false;
		
		if (daysComboBox.getSelectedIndex() != 0 || monthsComboBox.getSelectedIndex() != 0 || yearsComboBox.getSelectedIndex() != 0) // If any of the fields haven't been filled in
		{
			pass = true;
		}
		
		return pass;
	}
	
	public String getErrorString()
	{
		return ERROR_STRING;
	}
}