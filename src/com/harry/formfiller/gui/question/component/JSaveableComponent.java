package com.harry.formfiller.gui.question.component;

public interface JSaveableComponent
{
	/* Saveable component should implement this.
		It tells the system that the component can be saved.
		They also need a constructor which takes the
		string output from the toString method */

	String toString();
}
