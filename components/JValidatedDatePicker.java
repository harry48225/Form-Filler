package components;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.net.URL;
import java.io.*;
import java.time.*;

import java.util.*;

import com.github.lgooddatepicker.components.*;

public class JValidatedDatePicker extends JPanel implements JValidatedComponent, JSaveableComponent
{	
	private DatePicker dPicker;
	
	private final String ERROR_STRING = "Datepicker: Please enter a valid date";
	
	public JValidatedDatePicker()
	{
		setupDatePicker();
		
	}
	
	public JValidatedDatePicker(String saveString)
	{
		//Load the datepicker from file
		
		// saveString is formatted like this uuuu-MM-dd (ISO-8601)
		
		String selectedDateString = StringEscaper.unescape(saveString.split(":")[1]); // Get the date and unescape it
		
		setupDatePicker();
		
		if (!selectedDateString.equals("-1"))// If a date was selected
		{
			LocalDate selectedDate = LocalDate.parse(selectedDateString);
			dPicker.setDate(selectedDate);
		}
		
	}
	
	private void setupDatePicker()
	{
		this.setLayout(new GridLayout(1,1)); // 1 row 1 column
		
		dPicker = new DatePicker();
		
		DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("dd/MM/YYYY");
        dateSettings.setFormatForDatesBeforeCommonEra("dd/MM/uuuu");
		
		dPicker.setSettings(dateSettings);
		
		URL dateImageURL = JValidatedDatePicker.class.getResource("datepickerbutton1.png");
        Image dateExampleImage = Toolkit.getDefaultToolkit().getImage(dateImageURL);
        ImageIcon dateExampleIcon = new ImageIcon(dateExampleImage);
		
		JButton datePickerButton = dPicker.getComponentToggleCalendarButton();
        datePickerButton.setText("");
        datePickerButton.setIcon(dateExampleIcon);
		
		this.add(dPicker);
		/*
		String[] yearArray = new String[100]; // Store the most recent 100 years
		
		for (int i = yearArray.length - 2; i >= 0; i--)
		{
			int year = Calendar.getInstance().get(Calendar.YEAR);

			yearArray[i+1] = year - i + ""; // Fill the array with the most recent date at the start and the oldest at the end
		}
		
		yearArray[0] = "Year"; // Store year at the start of the array
		
		yearsComboBox = new JComboBox<String>(yearArray); // Create the combobox
		
		
		this.add(daysComboBox);
		this.add(monthsComboBox);
		this.add(yearsComboBox);
		*/
		
		this.setPreferredSize(new Dimension(100, 50));
		this.setMaximumSize(new Dimension(700, 60));
	}
	
	public boolean validateAnswer()
	{
		return presenceCheck() && dPicker.isTextFieldValid(); // Return whether the text field contains a valid date
	}
	
	public boolean presenceCheck()
	{
		return !dPicker.getText().isEmpty();
	}
	
	public String getErrorString()
	{
		return ERROR_STRING;
	}
	
	public String toString()
	{
		// This returns a string that fully describes the date picker
		String asString = "datepicker:";
		
		String dateString = dPicker.getDateStringOrEmptyString();
		
		if (dateString.isEmpty())
		{
			dateString = "-1";
		}
		
		asString += StringEscaper.escape(dateString); // Escape the date string
		
		return asString;
	}
}