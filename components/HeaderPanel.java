package components;

import javax.swing.*;
import java.awt.*;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

public class HeaderPanel extends JPanel implements JSaveableComponent
{
	/* This component is a header that can be used in a form to delimit between sections */

	private String headerText;
	
	Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED); // Border style
	
	public HeaderPanel(String tempHeaderText)
	{
		/* Creates a new header with the specified text / loads a header from its savestring */
		
		// Headertext could be a save string in that case there will
		// be a : in the string
		
		// If it's a saveString
		if (tempHeaderText.contains(":"))
		{
			// Extract the header text
			headerText = tempHeaderText.split(":")[1];
		}
		else
		{
			headerText = tempHeaderText;
		}
		
		preparePanel();
	}
	
	private void preparePanel()
	{
		/* Prepares the header panel for display */

		// Make it the correct size
		this.setPreferredSize(new Dimension(300, 50));

		// Add a border with only the top line with the text centered, this is the actual header
		TitledBorder border = BorderFactory.createTitledBorder(
									BorderFactory.createMatteBorder(2,0,0,0, Color.BLACK), headerText);
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		this.setBorder(border);
	}
	
	public String toString()
	{
		/* Returns a string that fully describes and can be used to recreate the header */
		return "header:" + headerText;
	}
}