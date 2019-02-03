package components;

import javax.swing.*;
import java.awt.*;

public class JSaveableLabel extends JLabel implements JSaveableComponent
{
	/* A JLabel that is a JSaveableComponent */
	
	private String text;
	
	public JSaveableLabel(String saveString)
	{
		// The input to this method could
		// either be a saveString or just the text
		
		if (saveString.contains(":"))
		{
			// The text in the save string is esacped therefore it needs to be unescaped before it is displayed
			text =  StringEscaper.unescape(saveString.split(":")[1]);
		}
		else
		{
			text = saveString;
		}
		
		setText(text);
	}
	
	public String toString()
	{
		// Returns a string that fully describes the label
		return "label:" + StringEscaper.escape(text);
	}
}