import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class WordWrapHeaderRenderer extends JTextPane implements TableCellRenderer 
{
	/* A header renderer that wraps the words so that they can all be seen
		regardless of the width of the column */
		
	public WordWrapHeaderRenderer()
	{
		LookAndFeel.installBorder(this, "TableHeader.cellBorder"); // Make it look like the normal header
		Font currentFont = this.getFont();
		this.setFont(currentFont.deriveFont(Font.BOLD, 17)); // Make the headers bold
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
	{
		/* This returns the styling and how the text in the header should be displayed and handled */
		
		// If the value isn't null, i.e. there is something in the cell
		if (value != null) 
		{
			// Set the text of the cell to the value of the string
			setText(value.toString());
			
			// Set the size of the cell to the correct size depending on its contents
			setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
			
			// Make the text centered
			StyledDocument doc = this.getStyledDocument();
			SimpleAttributeSet center = new SimpleAttributeSet();
			StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
			doc.setParagraphAttributes(0, doc.getLength(), center, false);
			setOpaque(false);
			
		}
		return this;
	}
	
	
}