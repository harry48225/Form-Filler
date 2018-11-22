import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class WordWrapHeaderRenderer extends JTextPane implements TableCellRenderer 
	{
		public WordWrapHeaderRenderer()
		{
			LookAndFeel.installBorder(this, "TableHeader.cellBorder"); // Make it look like the normal header
			Font currentFont = this.getFont();
			this.setFont(currentFont.deriveFont(Font.BOLD, 13)); // Make the headers bold
		}
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
		{
			if (value != null) 
			{
				//System.out.println(value);
				setText(value.toString());
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