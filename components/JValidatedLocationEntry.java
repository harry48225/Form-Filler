package components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JValidatedLocationEntry extends JPanel implements JValidatedComponent, JSaveableComponent, KeyListener
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
		addressComboBox.getEditor().getEditorComponent().addKeyListener(this);
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
	
    public void keyTyped(KeyEvent e) {
       System.out.println("KEY TYPED: ");
    }

    public void keyPressed(KeyEvent e) {
        //System.out.println("KEY Pressed: ");
    }

    public void keyReleased(KeyEvent e) {
        //System.out.println("KEY released: ");
    }
}