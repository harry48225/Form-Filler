package components;

import javax.swing.*;
import java.awt.*;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

public class HeaderPanel extends JPanel implements JSaveableComponent
{
	private String headerText;
	
	Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED); // Border style
	
	public HeaderPanel(String tempHeaderText)
	{
		// Headertext could be a save string in that case there will
		// be a : in the string
		
		if (tempHeaderText.contains(":"))
		{
			headerText = tempHeaderText.split(":")[1];
		}
		
		headerText = tempHeaderText;
		
		preparePanel();
	}
	
	private void preparePanel()
	{
		this.setPreferredSize(new Dimension(300, 50));
		TitledBorder border = BorderFactory.createTitledBorder(
									BorderFactory.createMatteBorder(2,0,0,0, Color.BLACK), headerText);
		border.setTitleJustification(TitledBorder.CENTER); // Put the title in the center
		this.setBorder(border);
	}
	
	public String toString()
	{
		return "header:" + headerText;
	}
}