package com.harry.formfiller.gui.question.component;

public interface JValidatedComponent
{
	boolean validateAnswer();
	
	boolean presenceCheck();
	
	String getErrorString();
}