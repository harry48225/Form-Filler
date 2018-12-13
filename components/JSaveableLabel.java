package components;

import javax.swing.*;
import java.awt.*;

public class JSaveableLabel extends JLabel implements JSaveableComponent
{
	private String text;
	
	public JSaveableLabel(String saveString)
	{
		// The input to this method could
		// either be a saveString or just the text
		
		if (saveString.contains(":"))
		{
			text = saveString.split(":")[1];
		}
		else
		{
			text = saveString;
		}
		
		setText(text);
	}
	
	public String toString()
	{
		return "label:" + text;
	}
}