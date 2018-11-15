package components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.filechooser.*;

public class JValidatedFileChooser extends JPanel implements JValidatedComponent, ActionListener
{
	private final String type; // Allowed types are all, image, document, video
	
	private transient JFileChooser fileChooser; // The actual file chooser, marked transient so that it's not serialized.
	
	private JButton openButton = new JButton("Upload File"); // The button that will be cliked on to open the file chooser
	
	public JValidatedFileChooser(String tempType)
	{
		type = tempType;
			
		fileChooser = new JFileChooser();
		setFileExtensionFilter();
		
		this.setLayout(new GridLayout(1,1)); // This layout ensures that the button entirely fills the JPanel
		
		openButton.addActionListener(this);
		
		this.add(openButton);
	}
	
	public void setFileExtensionFilter()
	{
		if (!type.equals("all")) // If something other than all has been selected
		{
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Blank", "txt"); // Blank filter for now

			if (type.equals("image"))
			{
				filter = new FileNameExtensionFilter("Images", "jpg", "gif", "png");
			}
			else if (type.equals("document")) 
			{
				filter = new FileNameExtensionFilter("Documents", "pdf", "doc", "docx");
			}
			else if (type.equals("video"))
			{
				filter = new FileNameExtensionFilter("Videos", "mp4", "mov", "avi");	
			}
			
			fileChooser.setFileFilter(filter);
		}
	}

	public boolean validateAnswer()
	{
		boolean pass = false;
		
		if (fileChooser != null && fileChooser.getSelectedFile() != null)  // If the file chooser isn't null and a file has been selected.
		{
		
			String extension = fileChooser.getSelectedFile().getName().split("\\.")[1]; // Splitting the file name at the dot and taking the second half will extract the extension.
			
			if (type.equals("all"))
			{
				pass = true; // If all files are allowed it's an automatic pass
			}
			else if (type.equals("image") && extension.matches("jpg|gif|png")) // Use a regex to check if it's an image
			{
				pass = true;
			}
			else if (type.equals("document") && extension.matches("pdf|doc|docx")) 
			{
				pass = true;
			}
			else if (type.equals("video") && extension.matches("mp4|mov|avi"))
			{
				pass = true;	
			}
		}
		return pass;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == openButton)
		{
			fileChooser = new JFileChooser();
			setFileExtensionFilter();
			fileChooser.showOpenDialog(this);
		}
	}
}