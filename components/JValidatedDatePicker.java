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
	/* This is a saveable and valiadated date picker based on LGoodDatePicker */
	
	private DatePicker dPicker;
	
	private final String ERROR_STRING = "Datepicker: Please enter a valid date";
	
	public JValidatedDatePicker()
	{
		setupDatePicker();
		
	}
	
	public JValidatedDatePicker(String saveString)
	{
		/* Loads the datepicker from file */
		
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
		/* Sets up the date picker with an open picker button and the correct date format and validation */
		
		this.setLayout(new GridLayout(1,1)); // 1 row 1 column
		
		dPicker = new DatePicker();
		
		// Give the date picker the correct format
		DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("dd/MM/YYYY");
        dateSettings.setFormatForDatesBeforeCommonEra("dd/MM/uuuu");
		
		dPicker.setSettings(dateSettings);
		
		// Add the open date picker button
		URL dateImageURL = JValidatedDatePicker.class.getResource("datepickerbutton1.png");
        Image dateExampleImage = Toolkit.getDefaultToolkit().getImage(dateImageURL);
        ImageIcon dateExampleIcon = new ImageIcon(dateExampleImage);
		
		JButton datePickerButton = dPicker.getComponentToggleCalendarButton();
        datePickerButton.setText("");
        datePickerButton.setIcon(dateExampleIcon);
		
		this.add(dPicker);
		
		// Make the panel the correct size
		this.setPreferredSize(new Dimension(100, 50));
		this.setMaximumSize(new Dimension(700, 60));
	}
	
	public boolean validateAnswer()
	{
		/* Validates the date that the user entered */
		
		return presenceCheck() && dPicker.isTextFieldValid(); // Return whether the text field contains a valid date
	}
	
	public boolean presenceCheck()
	{
		/* Performs a presence check */
		
		return !dPicker.getText().isEmpty();
	}
	
	public String getErrorString()
	{
		/* Returns the error string */
		
		return ERROR_STRING;
	}
	
	public String toString()
	{
		/* This returns a string that fully describes the date picker */
		String asString = "datepicker:";
		
		String dateString = dPicker.getDateStringOrEmptyString();
		
		if (dateString.isEmpty()) // If no date was entered
		{
			dateString = "-1"; // Set the string to a rogue value
		}
		
		asString += StringEscaper.escape(dateString); // Escape the date string
		
		return asString;
	}
}