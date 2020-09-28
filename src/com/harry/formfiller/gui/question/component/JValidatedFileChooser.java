package com.harry.formfiller.gui.question.component;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

public class JValidatedFileChooser extends JPanel implements JValidatedComponent, ActionListener, JSaveableComponent
{
	/* This is a saveable filechooser with built in validation */
	
	private final String type; // Allowed types are all, image, document, video
	
	private transient JFileChooser fileChooser; // The actual file chooser, marked transient so that it's not serialized.
	
	private JButton openButton = new JButton("Upload File"); // The button that will be cliked on to open the file chooser
	
	private JLabel fileLabel;
	
	private final String ERROR_STRING = "File Upload: Please upload a file of the correct type";
	
	public JValidatedFileChooser(String tempType)
	{
		/* Loads a file chooser form file or creates a new one
		   Temp type can either be a save string or just the type */
		fileChooser = new JFileChooser();
		
		if (tempType.contains(":")) // If it's a save string
		{
			// saveString is formatted like this
			// filechooser:type+path
			
			String[] saveData = tempType.split(":")[1].split("\\+");
			
			tempType = saveData[0];
			
			if (saveData.length > 1 && !saveData[1].equals("null")) // If there is a saved file name and it isn't the text null
			{
				fileChooser.setSelectedFile(new File(StringEscaper.unescape(saveData[1]))); // Set the selected file as the file loaded from text.
			}
		}
			
		type = tempType;
		setFileExtensionFilter();
		
		this.setLayout(new GridLayout(1,1)); // This layout ensures that the button entirely fills the JPanel
		
		
		JPanel openButtonAndLabelPanel = new JPanel();
		openButtonAndLabelPanel.setLayout(new GridLayout(2,1));
		
		fileLabel = new JLabel("No file uploaded", SwingConstants.CENTER);
		
		openButton.addActionListener(this);
		openButton.setText("Upload " + type + " file");
		
		
		openButtonAndLabelPanel.add(fileLabel);
		openButtonAndLabelPanel.add(openButton);
		
		this.add(openButtonAndLabelPanel);
	}
	
	public void setFileExtensionFilter()
	{
		/* Gives the file chooser the correct file extension filter */
		
		if (!type.equals("all")) // If something other than all has been selected
		{
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Blank", "txt"); // Blank filter for now

			if (type.equals("image"))
			{
				filter = new FileNameExtensionFilter("Images", "jpg", "jpeg", "jpe", "gif", "png");
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
		/* Checks that the user has uploaded a file of the correct type */
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
			else if (type.equals("document") && extension.matches("pdf|doc|docx"))  // Check if it's a document
			{
				pass = true;
			}
			else if (type.equals("video") && extension.matches("mp4|mov|avi")) // Check if it's a video
			{
				pass = true;	
			}
			else
			{
				pass = false;
			}
		}
		return pass;
	}
	
	private void updateLabel()
	{
		File f = fileChooser.getSelectedFile();
		
		if (f != null)
		{
			String name = f.getName();
			fileLabel.setText("<html>" + name + " uploaded! </html>");
			
			this.revalidate();
		}
	}
	
	public boolean presenceCheck()
	{
		/* Performs a presence check */
		
		return fileChooser != null && fileChooser.getSelectedFile() != null;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == openButton)
		{
			fileChooser.showOpenDialog(this);
			
			updateLabel();
		}
	}
	
	public String getErrorString()
	{
		/* Returns the error string */
		
		return ERROR_STRING;
	}
	
	public String toString()
	{
		/* Returns a string that fully describes the file chooser */
		String asString = "filechooser:" + type + "+" + StringEscaper.escape(fileChooser.getSelectedFile() + "");
		
		return asString;
	}
}