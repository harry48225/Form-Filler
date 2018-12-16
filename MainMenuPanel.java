import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenuPanel extends JPanel implements ActionListener
{
	private boolean adminMode;
	private User user;
	
	private JPanel usernamePanel;
	
	private JLabel usernameLabel;
	
	
	public MainMenuPanel(User tempUser)
	{
		user = tempUser;
		
		adminMode = user.isAdmin();
		
		prepareGUI();
	}
	
	private void prepareGUI()
	{
		this.setLayout(new BorderLayout());
		
		prepareUsernamePanel();
		
		this.add(usernamePanel, BorderLayout.NORTH);
	}
	
	private void prepareUsernamePanel()
	{
		usernamePanel = new JPanel();
		usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.PAGE_AXIS));
		
		usernamePanel.add(Box.createVerticalStrut(20));
		
		usernameLabel = new JLabel("Welcome " + user.getUsername(), SwingConstants.CENTER);
		usernameLabel.setPreferredSize(new Dimension(10000,50));
		usernameLabel.setMaximumSize(new Dimension(10000,50));
		Font currentFont = usernameLabel.getFont();
		usernameLabel.setFont(currentFont.deriveFont(Font.BOLD, 32)); // Make the font larger and bold
		
		usernamePanel.add(usernameLabel);
		
		usernamePanel.add(Box.createVerticalStrut(50));
	}
	
	public void actionPerformed(ActionEvent evt)
	{
		
	}
}