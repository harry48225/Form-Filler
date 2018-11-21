import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EnterOptionsPanel extends JPanel implements ActionListener
{
	private JLabel optionListHeader = new JLabel("Options", SwingConstants.CENTER); 
	private DefaultListModel<String> optionListModel = new DefaultListModel<String>();
	private JList<String> optionList = new JList<String>(optionListModel);
	private JScrollPane optionScoller = new JScrollPane();
	
	private JLabel headerLabel = new JLabel("What options are required?", SwingConstants.CENTER);
	private JLabel optionLabel = new JLabel("Option:", SwingConstants.CENTER);
	private JTextField optionField = new JTextField();
	private JButton addOptionButton = new JButton("Add option");
	private JButton deleteOptionButton = new JButton("Deleted selected");
	public EnterOptionsPanel()
	{
		prepareGUI();
	}
	
	private void prepareGUI()
	{
		// We don't need to handle the ok and cancel buttons because those are
		// provided by the JOptionPane that the panel will be displayed in
		// just need to add the option label, text field, etc. and the JList to display
		// the options
		
		System.out.println("[INFO] <ENTER_OPTIONS_PANEL> Running prepareGUI"); // Debug
		
		this.setLayout(new GridBagLayout());
		this.setPreferredSize(new Dimension(500,350));
		// Defines how the component is placed in the grid
		GridBagConstraints constraints = new GridBagConstraints();
		
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5,5,5,5); // 5 px padding all around
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.gridx = 0;
		constraints.gridy = 0;
		
		// Add the header label
		constraints.gridwidth = 4;
		this.add(headerLabel, constraints);
		
		// Add the enter option components
		constraints.weightx = 0.1;
		constraints.weighty = 1;
		constraints.gridwidth = 1;
		constraints.gridy = 2;
		this.add(optionLabel, constraints);
		constraints.gridx = 1;
		constraints.weightx = 1;
		optionField.addActionListener(this);
		this.add(optionField, constraints);
		
		// Add the buttons
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 2;
		constraints.insets = new Insets(20,5,5,20);
		addOptionButton.addActionListener(this);
		addOptionButton.setBackground(new Color(169,196,235));
		addOptionButton.setMaximumSize(new Dimension(100, 10));
		this.add(addOptionButton, constraints);
		
		constraints.gridy = 4;
		deleteOptionButton.addActionListener(this);
		deleteOptionButton.setBackground(new Color(169,196,235));
		this.add(deleteOptionButton, constraints);
		
		// Add the JList header
		constraints.insets = new Insets(5,5,5,5); // 5 px padding all around
		constraints.gridx = 2;
		constraints.gridy = 1;
		constraints.weighty = 0;
		constraints.weightx = 1;
		constraints.gridwidth = 2;
		this.add(optionListHeader, constraints);
		
		// Add the JList
		optionScoller.setViewportView(optionList);
		optionList.setLayoutOrientation(JList.VERTICAL);
		constraints.weighty = 1;
		constraints.gridy = 2;
		constraints.gridheight = 3;
		this.add(optionScoller, constraints);

	}
	
	public String[] getOptions()
	{
		return (String[]) optionListModel.toArray();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == addOptionButton)
		{
			System.out.println("[INFO] <ENTER_OPTIONS_PANEL> addOptionButton pressed"); // Debug
			
			// Only add the option if the user has actually entered an option
			if (!optionField.getText().isEmpty())
			{
				optionListModel.addElement(optionField.getText());
				optionField.setText(""); // Clear the text field
				System.out.println("[INFO] <ENTER_OPTIONS_PANEL> Option added"); // Debug
			}
		}
		else if (e.getSource() == optionField) // If enter was pressed in the text field simulate a click of the addOption button
		{
			addOptionButton.doClick();
		}
		else if (e.getSource() == deleteOptionButton)
		{
			System.out.println("[INFO] <ENTER_OPTIONS_PANEL> deleteOptionButton pressed"); // Debug
			// Only add the option if the user has actually entered an option
			if (optionList.getSelectedValue() != null)
			{
				optionListModel.removeElement(optionList.getSelectedValue()); // Remove the selected value
				System.out.println("[INFO] <ENTER_OPTIONS_PANEL> Option deleted"); // Debug
			}
		}
	}
}