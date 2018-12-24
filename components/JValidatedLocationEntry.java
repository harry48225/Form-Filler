package components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JValidatedLocationEntry extends JPanel implements JValidatedComponent, JSaveableComponent
{
	private JPanel mainPanel = new JPanel();
	
	private JLabel addressLabel = new JLabel("Address");
	private JComboBox<String> addressComboBox = new JComboBox();
	
	public JValidatedLocationEntry()
	{
		prepareGUI();
	}
	
	private void prepareGUI()
	{
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));
		mainPanel.add(addressLabel);
		
		mainPanel.add(Box.createHorizontalStrut(10));
		
		addressComboBox.setEditable(true);
		mainPanel.add(addressComboBox);
		
		this.setLayout(new GridLayout(1,1));
		this.add(mainPanel);
	}
	
	public String getErrorString()
	{
		// Todo
		return "";
	}
	
	public boolean validateAnswer()
	{
		// Todo
		return false;
	}
	
	public boolean presenceCheck()
	{
		// Todo
		return false;
	}
	
	public String toString()
	{
		// Todo
		return "";
	}
}