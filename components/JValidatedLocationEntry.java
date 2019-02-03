package components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.InetAddress;

import com.google.gson.*;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.*;
import com.google.maps.PlaceAutocompleteRequest.SessionToken;

import java.util.*;

public class JValidatedLocationEntry extends JPanel implements JValidatedComponent, JSaveableComponent, KeyListener
{
	/* This class is a entry component that uses the google maps autocomplete api to autocomplete the address that the user enters  */
	
	private final String ERROR_STRING = "Location: Please enter an address";
	
	// Google maps api setup
	private GeoApiContext context;
	private SessionToken session = new SessionToken();

	private JComboBox<String> addressComboBox = new JComboBox();
	
	// Whether the system is connected to the internet
	private boolean connection = true;
	
	public JValidatedLocationEntry()
	{
		setup();
		prepareGUI();
	}
	
	public JValidatedLocationEntry(String saveString)
	{
		/* Loads a saved location entry from file */
		
		setup();
		prepareGUI();
		
		// Save string is formatted like this
		// locationentry:enteredaddress
		
		String[] splitString = saveString.split(":");
		String enteredAddress = "";
		
		if (splitString.length > 1) // If the location entry was non-empty when it was saved
		{
			enteredAddress = StringEscaper.unescape(splitString[1]);
		}
		
		((JTextField) addressComboBox.getEditor().getEditorComponent()).setText(enteredAddress); // Set the text back to what it was when the component was saved 
	}
	
	private void setup()
	{
		/* Sets up the google maps api context */
		
		context = new GeoApiContext.Builder().apiKey("AIzaSyBgcCPoJcVPvdsClek4TljQ7E7XzcMbU4I").build();
	}
	
	/*
	private AutocompletePrediction[] predictPlaces(String stringToPredictFrom)
	{
		/* Predicts the location that the user is trying to enter using the google maps api and returns an array of places 
		
		AutocompletePrediction[] results = null;
		
		if (!stringToPredictFrom.isEmpty()) // If the user has entered some text
		{
			
			System.out.println("[INFO] <JVALIDATED_LOCATION_ENTRY> Getting results");
			try
			{
				// Try to get the results from google maps
				results = PlacesApi.placeAutocomplete(context, stringToPredictFrom, session).await();
			}
			catch (Exception e)
			{
				// There must have been an internet connection error.
				System.out.println("[ERROR] <JVALIDATED_LOCATION_ENTRY> Error connecting to google maps, most likely no internet connection");
				connection = false; // Prevent further tries
				
			}
			
			System.out.println("[INFO] <JVALIDATED_LOCATION_ENTRY> Recieved results");
		}
		
		return results;
	}
	*/
	private void updatePrediction()
	{
		/* Creates a new thread to get the autocomplete results, making a new thread ensures that the ui
			doesn't lock up while we are waiting for a response from the api */
			
		new AutocompleteWorker(addressComboBox, context).execute();
	}
	private void prepareGUI()
	{	
		/* Prepares the visual elements of the location entry */
		
		this.setLayout(new GridLayout(1,1));
		addressComboBox.setEditable(true); // Allow text to be typed into the combobox
		addressComboBox.getEditor().getEditorComponent().addKeyListener(this); // Add a key listener
		this.add(addressComboBox);
		
		// Make the panel the correct size
		this.setPreferredSize(new Dimension(200,40));
		this.setMaximumSize(new Dimension(200,50));
	}
	
	public String getErrorString()
	{
		/* Returns the error string */
		return ERROR_STRING;
	}
	
	public boolean validateAnswer()
	{
		/* This entry is only validated with a presence check */
		return presenceCheck();
	}
	
	public boolean presenceCheck()
	{
		/* Performs a presence check on the user's entry */
		
		String currentText = (String) addressComboBox.getEditor().getItem(); // Get the text that the user has entered
		
		currentText = currentText.replaceAll("\\s",""); // Get rid of all of the spaces.
		
		return !currentText.isEmpty(); // If the string is empty then the pass fails
	}
	
	public String toString()
	{
		/*  Returns a string that fully describes the location entry component */
		String currentText = (String) addressComboBox.getEditor().getItem();
		
		return "locationentry:" + StringEscaper.escape(currentText);
	}
	
    public void keyTyped(KeyEvent e) 
	{
		/* Runs whenever the user types into the combobox and updates the google maps prediction */
		
		if (connection) // Only run if there is an internet connection
		{
			updatePrediction();
		}
	}

	/* These two methods need to be implemented to be a key listener */
    public void keyPressed(KeyEvent e) {}

    public void keyReleased(KeyEvent e) {}
	
	/* This is a thread that runs in the background and gets the predictions from the google maps api */
	private class AutocompleteWorker extends SwingWorker<AutocompletePrediction[], AutocompletePrediction[]>
	{
		private JComboBox addressComboBox;
		private GeoApiContext context;
		
		public AutocompleteWorker(JComboBox tempAddressComboBox, GeoApiContext tempContext)
		{
			addressComboBox = tempAddressComboBox;
			context = tempContext;
		}
		
		protected AutocompletePrediction[] doInBackground() throws Exception
		{
			/* Gets the text in the combobox and runs the predict places method */
			String currentText = (String) addressComboBox.getEditor().getItem();
			AutocompletePrediction[] predictions = predictPlaces(currentText);
			
			return predictions;
		}
		
		private void addPredictions(AutocompletePrediction[] predictions)
		{
			/* Adds the new predictions and removes the old ones from the combobox */
			
			String currentText = (String) addressComboBox.getEditor().getItem(); // Save the current text for later
			
			if (predictions != null && predictions.length > 0) // If predictions were returned
			{
				// Hide the popup and remove the items from the combobox
				addressComboBox.hidePopup();
				addressComboBox.removeAllItems();
				
				// If less than 10 places were predicted
				if (predictions.length <= 10)
				{
					// Add them to the combobox
					for (int i = 0; i < predictions.length; i++)
					{
						addressComboBox.insertItemAt(predictions[i].description, i);
					}
				}
				
				// Show the drop down
				addressComboBox.showPopup();
			}
			
			
			((JTextField) addressComboBox.getEditor().getEditorComponent()).setText(currentText); // Set the text back to what it was before the query
		}
		
		private AutocompletePrediction[] predictPlaces(String stringToPredictFrom)
		{
			/* Calls the google maps api to predict the places */
			
			AutocompletePrediction[] results = null;
			
			if (!stringToPredictFrom.isEmpty()) // If the user has entered an address
			{
				
				System.out.println("[INFO] <JVALIDATED_LOCATION_ENTRY> Getting results");
				try
				{
					results = PlacesApi.placeAutocomplete(context, stringToPredictFrom, session).await(); // Call the google maps api to predict the places and wait for a response
				}
				catch (Exception e)
				{
					// There must have been a connection issue
					System.out.println("[ERROR] <JVALIDATED_LOCATION_ENTRY> Error connecting to google maps, most likely no internet connection");
					connection = false; // Prevent further tries
					
				}
				
				System.out.println("[INFO] <JVALIDATED_LOCATION_ENTRY> Recieved results");
			}
			return results;
		}

		protected void done()
		{
			// Once the prediction has been returned
			try
			{
				// Add the predictions
				addPredictions(get());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
    }
	}
}