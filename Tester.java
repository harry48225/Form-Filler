import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

import components.*;
public class Tester implements ActionListener // Just to quickly test things without loading the whole program
{

	JFrame test;
	JButton b;
	RadioButtonPanel rBP;
	JValidatedTextField tF;
	
	JValidatedComboBox box;
	
	CheckBoxPanel cBP;
	
	public static void main(String[] args)
	{
		Tester t = new Tester();
		
		t.setup();
		//t.testQuestionCreationWindow();
		//t.testFormCreation();
		//t.testCheckBoxPanel();
		//t.testValidation();
		//t.testDatePicker();
		//t.testUser();
		//t.testQuestionStat();
		//t.testFormInProgress();
		//t.testOptionsPanel();
		//t.testSelectQuestionsPanel();
		//t.testSelectFormsPanel();
		t.testComponentSaving();
	}
	
	public void setup()
	{
		test = new JFrame();
		test.setLayout(new GridLayout(0,1));
		test.setSize(300,300);
	}
	
	public void testComponentSaving()
	{
		b = new JButton("test");
		b.addActionListener(this);
		
		//cBP = new CheckBoxPanel.CheckBoxPanelBuilder().add("test 1").add("test 2").build();
		cBP = new CheckBoxPanel("checkboxes:test 1;true.test 2;false");
		test.add(cBP);
		test.add(b);
		
		test.setVisible(true);
		
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == b)
		{
			System.out.println(cBP.toString());
		}
	}
	
}