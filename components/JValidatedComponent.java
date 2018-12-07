package components;

public interface JValidatedComponent
{
	boolean validateAnswer();
	
	boolean presenceCheck();
	
	String getErrorString();
}