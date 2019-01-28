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
	private final String ERROR_STRING = "Location: Please enter an address";
	private GeoApiContext context;
	private SessionToken session = new SessionToken();

	private JComboBox<String> addressComboBox = new JComboBox();
	
	private boolean connection = true;
	
	public JValidatedLocationEntry()
	{
		setup();
		prepareGUI();
	}
	
	public JValidatedLocationEntry(String saveString)
	{
		// Loads a saved location entry from file
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
				//System.out.println(e);
				System.out.println("[ERROR] <JVALIDATED_LOCATION_ENTRY> Error connecting to google maps, most likely no internet connection");
				connection = false;
				
			}
			
			System.out.println("[INFO] <JVALIDATED_LOCATION_ENTRY> Recieved results");
		}
		return results;
	}
	
	private void updatePrediction()
	{
		new AutocompleteWorker(addressComboBox, context).execute();
	}
	private void prepareGUI()
	{	
		this.setLayout(new GridLayout(1,1));
		addressComboBox.setEditable(true);
		addressComboBox.getEditor().getEditorComponent().addKeyListener(this);
		this.add(addressComboBox);
		
		this.setPreferredSize(new Dimension(200,40));
		this.setMaximumSize(new Dimension(200,50));
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
		// Returns a string that fully describes the location entry component
		String currentText = (String) addressComboBox.getEditor().getItem();
		
		return "locationentry:" + StringEscaper.escape(currentText);
	}
	
    public void keyTyped(KeyEvent e) {
		if (connection)
		{
			updatePrediction();
		}
	}

    public void keyPressed(KeyEvent e) {
        //System.out.println("KEY Pressed: ");
    }

    public void keyReleased(KeyEvent e) {
        //System.out.println("KEY released: ");
    }
	
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
			String currentText = (String) addressComboBox.getEditor().getItem();
			AutocompletePrediction[] predictions = predictPlaces(currentText);
			
			return predictions;
		}
		
		private void addPredictions(AutocompletePrediction[] predictions)
		{
			String currentText = (String) addressComboBox.getEditor().getItem();
			if (predictions != null && predictions.length > 0)
			{
				addressComboBox.hidePopup();
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
					//System.out.println(e);
					System.out.println("[ERROR] <JVALIDATED_LOCATION_ENTRY> Error connecting to google maps, most likely no internet connection");
					connection = false;
					
				}
				
				System.out.println("[INFO] <JVALIDATED_LOCATION_ENTRY> Recieved results");
			}
			return results;
		}

		protected void done()
		{
			try
			{
				addPredictions(get());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
    }
	}
}