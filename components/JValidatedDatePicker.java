package components;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.net.URL;
import java.io.*;
import com.github.lgooddatepicker.components.*;

public class JValidatedDatePicker extends JPanel implements JValidatedComponent, Serializable
{
	
	private transient DatePicker dPicker; // The Date picker marked transient so that it's not to be serialized as it's not serializable.
	
	public JValidatedDatePicker()
	{
		this.setLayout(new GridLayout(1,1)); // ! row 1 column so that the date picker fills it entirely.
		
		setupDatePicker();
		
	}
	
	private void setupDatePicker()
	{
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
	}
	
	/*
	 private void writeObject(ObjectOutputStream out) throws IOException // Runs when the object is serialized
	 {
		out.defaultWriteObject();
		
		this.removeAll(); // Remove the date picker from the JPanel so that it's not serialized
		this.revalidate();
		
		System.out.println("Removed all objects from JPanel");
		
		
	 }
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException // Runs when the object is deserialized
	{
		in.defaultReadObject(); // Regular deserialization
		
		setupDatePicker(); // Re-setup the date picker after deserialization.
		
		this.add(dPicker);
	}
	*/
	
	public boolean validateAnswer()
	{
		return dPicker.isTextFieldValid(); // Return whether the text field contains a valid date
	}
}