public class Utils
{
	public static boolean isBeforeInDictionary(String word1, String word2) // Returns true if word1 comes before word 2 in the dictionary
	{
		int word1FirstLetter = (int) word1.toLowerCase().toCharArray()[0];
		int word2FirstLetter = (int) word2.toLowerCase().toCharArray()[0];
		boolean beforeInDictionary = false;
		
		if (word1FirstLetter < word2FirstLetter)
		{
			beforeInDictionary = true;
		}
		else if (word1FirstLetter > word2FirstLetter)
		{
			beforeInDictionary = false;
		}
		else // They must be the same letter
		{
			if (word1.length() == 1 || word2.length() == 1)
			{
				beforeInDictionary = word1.length() < word2.length(); // If word 1 is shorter it comes before word 2 in the dictionary
			}
			else
			{
				beforeInDictionary = isBeforeInDictionary(word1.substring(1), word2.substring(1)); // Trim the first character from the word and call again
			}
		}
		
		return beforeInDictionary;
	}
	
	public static String escapeString(String toEscape)
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
	
	public static String unescapeString(String toDescape)
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