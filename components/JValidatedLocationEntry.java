package components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import com.google.gson.*;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.*;
import com.google.maps.PlaceAutocompleteRequest.SessionToken;

import java.util.*;

public class JValidatedLocationEntry extends JPanel implements JValidatedComponent, JSaveableComponent, KeyListener
{
	private final String ERROR_STRING = "Location: Please enter an address";
	private GeoApiContext context;
	private SessionToken session = new SessionToken();
	private JPanel mainPanel = new JPanel();
	
	private JLabel addressLabel = new JLabel("Address");
	private JComboBox<String> addressComboBox = new JComboBox();
	
	public JValidatedLocationEntry()
	{
		setup();
		prepareGUI();
	}
	
	public JValidatedLocationEntry(String saveString)
	{
		setup();
		prepareGUI();
		
		// Save string is formatted like this
		// locationentry:enteredaddress
		
		String[] splitString = saveString.split(":");
		
		String enteredAddress = splitString[1];
		
		((JTextField) addressComboBox.getEditor().getEditorComponent()).setText(enteredAddress); // Set the text back to what it was when the component was saved 
	}
	
	private void setup()
	{
		context = new GeoApiContext.Builder().apiKey("AIzaSyBgcCPoJcVPvdsClek4TljQ7E7XzcMbU4I").build();
	}
	
	private AutocompletePrediction[] predictPlaces(String stringToPredictFrom)
	{
		
		AutocompletePrediction[] results = null;
		
		if (!stringToPredictFrom.isEmpty())
		{
			
			System.out.println("[INFO] <JVALIDATED_LOCATION_ENTRY> Getting results");
			try
			{
				results = PlacesApi.placeAutocomplete(context, stringToPredictFrom, session).await();
			}
			catch (Exception e)
			{
				System.out.println(e);
			}
			
			System.out.println("[INFO] <JVALIDATED_LOCATION_ENTRY> Recieved results");
		}
		return results;
	}
	
	private void updatePrediction()
	{
		String currentText = (String) addressComboBox.getEditor().getItem();
		AutocompletePrediction[] predictions = predictPlaces(currentText);
		
		if (predictions != null && predictions.length > 0)
		{
			addressComboBox.removeAllItems();
			
			if (predictions.length <= 10)
			{
				for (int i = 0; i < predictions.length; i++)
				{
					addressComboBox.insertItemAt(predictions[i].description, i);
				}
			}
			
			addressComboBox.showPopup();
		}
		((JTextField) addressComboBox.getEditor().getEditorComponent()).setText(currentText); // Set the text back to what it was before the query
	}
	private void prepareGUI()
	{
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));
		mainPanel.add(addressLabel);
		
		mainPanel.add(Box.createHorizontalStrut(10));
		
		addressComboBox.setEditable(true);
		addressComboBox.getEditor().getEditorComponent().addKeyListener(this);
		mainPanel.add(addressComboBox);
		
		this.setLayout(new GridLayout(1,1));
		this.add(mainPanel);
	}
	
	public String getErrorString()
	{
		return ERROR_STRING;
	}
	
	public boolean validateAnswer()
	{
		return presenceCheck();
	}
	
	public boolean presenceCheck()
	{
		String currentText = (String) addressComboBox.getEditor().getItem();
		
		currentText = currentText.replaceAll("\\s",""); // Get rid of all of the spaces.
		
		return !currentText.isEmpty(); // If the string is empty then the pass fails
	}
	
	public String toString()
	{
		String currentText = (String) addressComboBox.getEditor().getItem();
		
		return "locationentry:" + currentText;
	}
	
    public void keyTyped(KeyEvent e) {
	   updatePrediction();
    }

    public void keyPressed(KeyEvent e) {
        //System.out.println("KEY Pressed: ");
    }

    public void keyReleased(KeyEvent e) {
        //System.out.println("KEY released: ");
    }
}