package components;

public class StringEscaper
{
	public static String escape(String toEscape)
	{
		// This replaces all of the characters that are used as delimiters with
		// unicode escape characters so that they are still displayed to the user
		// but don't affect the files.
		toEscape = toEscape.replace(";", "\\u003b");
		toEscape = toEscape.replace("~", "\\u007e");
		toEscape = toEscape.replace(":", "\\u003a");
		toEscape = toEscape.replace("|", "\\u007c");
		toEscape = toEscape.replace(",", "\\u002c");
		toEscape = toEscape.replace("$", "\\u0024");
		toEscape = toEscape.replace(".", "\\u002e");
		
		return toEscape;
		
	}
	
	public static String unescape(String toDescape)
	{
		// This converts unicode back to the characters.
		toDescape = toDescape.replace("\\u003b",";");
		toDescape = toDescape.replace("\\u007e","~");
		toDescape = toDescape.replace("\\u003a",":");
		toDescape = toDescape.replace("\\u007c","|");
		toDescape = toDescape.replace("\\u002c",",");
		toDescape = toDescape.replace("\\u0024","$");
		toDescape = toDescape.replace("\\u002e",".");
		
		return toDescape;
	}
}