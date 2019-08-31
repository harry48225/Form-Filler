import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class WordWrapCellRenderer extends JTextPane implements TableCellRenderer 
{
	/* A cell renderer that wraps the words so that they can all be seen
		regardless of the width of the column */
		
	public WordWrapCellRenderer()
	{
		this.setFont(new Font("Monaco", Font.PLAIN, 16)); // Make the text larger 11
	}
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		/* This returns the styling and how the text in the cell should be displayed and handled */
		
		// If the value isn't null, i.e. there is something in the cell
		if (value != null)
		{
			// Set the text of the cell to the value of the string
			setText(value.toString());
			
			// Set the size of the cell to the correct size depending on its contents
			setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
			
			
			// Colour the cell the highlight colour if it's selected.
			if(isSelected)
			{
				setBackground(table.getSelectionBackground());
			}
			else
			{
				setBackground(table.getBackground());
			}
			
			// Make the text centered
			StyledDocument doc = this.getStyledDocument();
			SimpleAttributeSet center = new SimpleAttributeSet();
			StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
			doc.setParagraphAttributes(0, doc.getLength(), center, false);
			
		}
		
		return this;
	}
}