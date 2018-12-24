import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class WordWrapCellRenderer extends JTextPane implements TableCellRenderer 
	{
		public WordWrapCellRenderer()
		{
			//Font currentFont = this.getFont();
			this.setFont(new Font("Monaco", Font.PLAIN, 13)); // Make the text larger 11
		}
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
		{
			if (value != null) 
			{
				//System.out.println(value);
				setText(value.toString());
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