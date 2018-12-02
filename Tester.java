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
		t.testSelectFormsPanel();
	}
	
	public void testSelectFormsPanel()
	{
		test.add(new SelectFormsPanel(new FormList(),new QuestionList()));
		test.setVisible(true);
	}
	
	public void testSelectQuestionsPanel()
	{
		test.add(new SelectQuestionsPanel(new QuestionList()));
		test.setVisible(true);
	}
	
	public void testOptionsPanel()
	{
		int result = JOptionPane.showConfirmDialog(null, new EnterOptionsPanel(), "Enter options", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
	}
	
	public void testFormInProgress()
	{
		FormInProgress myForm = new FormInProgress("F00000001", 14, new QuestionPanel[10]);
		
		FormsInProgressList forms = new FormsInProgressList("testUser2");
		
		System.out.println("F12345767 is present: " + forms.isFormPresent("F12345767"));
		
		//System.out.println("Adding forms to testUser2's formInProgress list");
		//forms.addFormInProgress(new FormInProgress("F00000002", 14, new Component[10]));
		//forms.addFormInProgress(new FormInProgress("F00000003", 14, new Component[10]));
		//forms.writeDatabase();
		/*
		FormsInProgressList forms0 = new FormsInProgressList("testUser");
		forms0.addFormInProgress(new FormInProgress("F00000001", 14, new Component[10]));
		forms0.writeDatabase();
		*/
		
		//System.out.println("testUser " + Arrays.toString(new FormsInProgressList("testUser").getArray()));
		//System.out.println("testUser2 " + Arrays.toString(forms.getArray()));
		//System.out.println(Arrays.toString(forms.getArray()));
		//System.out.println("Adding a FormInProgress object to the array");
		//System.out.println(Arrays.toString(forms.getArray()));
		
		
	}
	
	public void testQuestionStat()
	{
			//UserList users = new UserList();
			
			//QuestionStatList qSL = users.getUsers()[0].getQuestionStats();
			
			/*
			qSL.addFailedValidation("Q12345678");
			qSL.addFailedValidation("Q12645678");
			qSL.addFailedValidation("Q18945678");
			qSL.addFailedValidation("Q12340000");
			qSL.addFailedValidation("Q12340000");
			qSL.addFailedValidation("Q13820584");
			qSL.addFailedValidation("Q13820584");
			qSL.addFailedValidation("Q13820584");
			qSL.addFailedValidation("Q13820584");
			qSL.addFailedValidation("Q13820584");
			qSL.addFailedValidation("Q05065830");
			qSL.addFailedValidation("Q15906048");
			qSL.addFailedValidation("Q17545616");
			qSL.addFailedValidation("Q17545616");
			*/
			
			//System.out.println("Ran add FailedValidation");
			
			//System.out.println("Question Stat list belonging to " + users.getUsers()[0].getFirstName() + ": " + qSL.toString());
			
			//users.writeDatabase();
			
			QuestionStat myQuestionStat = new QuestionStat("Q12345678");

			System.out.println(myQuestionStat.toString().split(",")[2]);
			for (int i = 1; i <= 8; i++)
			{
				System.out.println("Adding " + i);
				myQuestionStat.addNumberOfAttemptsNeededToCorrect(i);
				System.out.println(myQuestionStat.toString().split(",")[2]);
			}
	}
	
	public void testUser()
	{
		//User myUser = new User("Bob", "Smith", "07/04/96", "12345678");
		//System.out.println("User created");
		
		UserList myUserList = new UserList();
		
		System.out.println("Unique id: " + myUserList.getFreeID());
		//System.out.println(Arrays.toString(myUserList.userArray));
		//myUserList.addUser(myUser);
		
		//myUserList.writeDatabase();
	}
	
	public void setup()
	{
		test = new JFrame();
		test.setLayout(new GridLayout(0,1));
		test.setSize(300,300);
	}
	public void runTest()
	{
		//QuestionPanelList list = new QuestionPanelList();
		
		//QuestionPanel panel = new QuestionPanel("Q000001");
		
		//list.addQuestionPanel(panel); // Add the question panel to the list
		
		/*
		list.loadDatabase();
		
		System.out.println(list.panels[0].getQuestionID());
		*/
		
		//QuestionPanel qP = new QuestionPanel.QuestionPanelBuilder("TEST01").add(new JButton("Test")).build();
		//System.out.println(qP.getQuestionID());
		
		//list.addQuestionPanel(qP);
		//list.writeDatabase();
		//list.loadDatabase();
		//System.out.println(Arrays.toString(list.panels));
		//QuestionPanel newQP = new QuestionPanel.QuestionPanelBuilder("TEST01").add(new JLabel("Password:")).add(new JPasswordField()).build();
		//list.addQuestionPanel(newQP);
		//list.writeDatabase();
		
		QuestionList qL = new QuestionList();
		
		JFrame test = new JFrame();
		
		test.setLayout(new GridLayout(0,1));
		test.setSize(300,300);
		
		for (Question q : qL.getArray())
		{
			if (q != null)
			{
				System.out.println(q.toString());
				test.add(qL.getPanel(q)); // Get and add the panel
			}
		}
		
		test.setVisible(true);
		/*
		System.out.println("Printing the array");
		
		System.out.println(list.panels[0].getQuestionID());
		*/
	}
	
	public void testFormCreation()
	{
		//Form f =  new Form.FormBuilder("TESTF01").add("TEST01").add("TEST02").build();
		//Form f2 = new Form.FormBuilder("TESTF02").add("TEST01").build();
		//System.out.println(f.toString());
		
		FormList fL = new FormList();
		//System.out.println(Arrays.toString(fL.getArray()));
		
		System.out.println(fL.getFreeID());
	}
	
	public void testQuestionCreationWindow()
	{
		QuestionList qL = new QuestionList();
		
		System.out.println(qL.getFreeID());
		
		//new QuestionCreationWindow(qL);
	}
	
	public void testCheckBoxPanel()
	{
		CheckBoxPanel c = new CheckBoxPanel.CheckBoxPanelBuilder().add("1").add("2").add("3").build();
		
		
		test.add(c);
		
		test.setVisible(true);
		
	}

	public void testValidation()
	{
		//box = new JValidatedComboBox(new String[] {"Please select an option", "test1", "test2"});
		
		//rBP = new RadioButtonPanel.RadioButtonPanelBuilder().add("Test1").add("Test2").add("Test3").build();
	
		tF = new JValidatedTextField("phone");
		
		b = new JButton("test");
		b.addActionListener(this);
		
		//test.add(box);
		//test.add(rBP);
		test.add(tF);
		test.add(b);
		
		test.setVisible(true);
		
		
		
	}
	/*
	public void testDatePicker()
	{
		DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("dd/MM/YYYY");
        dateSettings.setFormatForDatesBeforeCommonEra("dd/MM/uuuu");
        DatePicker datePicker = new DatePicker(dateSettings);
        datePicker.setDateToToday();
		
		URL dateImageURL = Tester.class.getResource("datepickerbutton1.png");
        Image dateExampleImage = Toolkit.getDefaultToolkit().getImage(dateImageURL);
        ImageIcon dateExampleIcon = new ImageIcon(dateExampleImage);
		
		JButton datePickerButton = datePicker.getComponentToggleCalendarButton();
        datePickerButton.setText("");
        datePickerButton.setIcon(dateExampleIcon);
		
		test.add(datePicker);
		test.setVisible(true);
	}
	*/
	

	public void actionPerformed (ActionEvent e)
	{
		if (e.getSource() == b)
		{
			//box.validateAnswer();
			if (tF.validateAnswer())
			{
				System.out.println("Vaildation check Passed");
			}
			else
			{
				System.out.println("Validation check Failed");
			}
		}
	}
}