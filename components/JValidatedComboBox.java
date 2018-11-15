package components;

import javax.swing.*;
import java.awt.*;

public class JValidatedComboBox extends JComboBox<String> implements JValidatedComponent
{
	public JValidatedComboBox(String[] options)
	{
		super(options);
	}
	public boolean validateAnswer()	// Validates the answer
	{
		return (getSelectedIndex() != 0);
	}
}