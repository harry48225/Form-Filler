package com.harry.formfiller.question;

import com.harry.formfiller.gui.question.QuestionPanel;

public class ExportedQuestion
{
	/* This class is used to export and import questions. It's what a question is stored as when it's exported.
		It holds both the question and question panel */

	private Question question; // The question and question panel
	private QuestionPanel questionPanel;
	
	public ExportedQuestion(Question tempQuestion, QuestionPanel tempQuestionPanel)
	{
		question = tempQuestion;
		questionPanel = tempQuestionPanel;
	}
	
	public ExportedQuestion(String saveString)
	{
		/* Loads an exported question from its saveString */

		// Save string is formatted like so
		// <question>###<questionpanel>
	
		// Split the string and extract the question and question panel strings
		String[] splitSaveString = saveString.split("###");
		String questionString = splitSaveString[0];
		String questionPanelString = splitSaveString[1];
		
		// Load the question and question panel
		question = new Question(questionString);
		questionPanel = new QuestionPanel(questionPanelString);
	}
	
	public Question getQuestion()
	{
		/* Returns the question that was exported */
		return question;
	}
	
	public QuestionPanel getQuestionPanel()
	{
		/* Returns the question panel that was exported */
		return questionPanel;
	}
	
	public String toString()
	{
		/* Returns a string that fully describes the ExportedQuestion object and can be used to recreate it */
		return question.toString() + "###" + questionPanel.toString();
	}
	
}