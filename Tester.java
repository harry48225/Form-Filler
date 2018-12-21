import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

import components.*;
public class Tester implements ActionListener // Just to quickly test things without loading the whole program
{

	JFrame test;
	JButton b;
	QuestionPanel c;
	JValidatedTextField tF;
	
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
		//t.testComponentSaving();
		//t.testEncryption();
		t.testQuestionStatRandomAccess();
	}
	
	public void testQuestionStatRandomAccess()
	{
		UserList users = new UserList();
		QuestionStatList qSL = users.getUserByUsername("bob123").getQuestionStats();
		
		System.out.println(qSL.toString());
		System.out.println(Arrays.toString(qSL.toString().split("\\|")));
		System.out.println(Arrays.toString(qSL.getIDArray()));
		System.out.println(Arrays.toString(qSL.getQuestionsStruggleTheMost(new QuestionList())));
	}
	public void setup()
	{
		test = new JFrame();
		test.setLayout(new GridLayout(0,1));
		test.setSize(300,300);
	}
	
	public void testEncryption()
	{
		UserList u = new UserList();
		
		u.loadSensitiveDatabase("harris");
		for (User us : u.getUsers())
		{
			System.out.println(us.toString());
			System.out.println(us.getQuestionStats().toString());
		}
		
		
		//u.writeDatabase();
		//u.writeSensitiveDatabase("harris");
	}
	
	public void testComponentSaving()
	{
		b = new JButton("test");
		b.addActionListener(this);
		
		//cBP = new CheckBoxPanel.CheckBoxPanelBuilder().add("test 1").add("test 2").build();
		//cBP = new CheckBoxPanel("checkboxes:test 1;true.test 2;false");
		
		//c = new RadioButtonPanel.RadioButtonPanelBuilder().add("test 1").add("test 2").build();
		//c = new JValidatedComboBox(new String[] {"Please select an option", "option 1", "option 2"});
		//c = new JValidatedComboBox("combobox:Please select an option.option 1.option 2;2");
		
		//c = new JValidatedDatePicker();
		//c = new JValidatedDatePicker("datepicker:4.6.3");
		
		//c = new JValidatedFileChooser("image");
		//c = new JValidatedFileChooser("filechooser:image");
		
		//c = new JValidatedTextField("phone");
		//c = new JValidatedTextField("textfield:07232334515;phone");
		
		//c = new JValidatedPasswordField();
		//c = new JValidatedPasswordField("password:passwordexample;adifferentpassword");
		
		//c = new JSaveableLabel("test label");
		//c = new JSaveableLabel("label:test label");
		
		//c = new QuestionPanel.QuestionPanelBuilder("Q12345678").add(
		//new JSaveableLabel("Test label")).add(new CheckBoxPanel.CheckBoxPanelBuilder().add(
		//"test 1").add("test 2").build()).build();
		//c = new QuestionPanel("Q12345678$label:Test label||checkboxes:test 1;true.test 2;false");
		
		c = new QuestionPanel("Q97512145$label:test||radiobuttons:1;false.2;false.3;false.4;false.5;false.6;false.7;false.8;false");
		test.add(c);
		test.add(b);
		
		test.setVisible(true);
		
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == b)
		{
			System.out.println(c.toString());
		}
	}
	
}