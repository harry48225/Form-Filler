public class Utils
{
	/* Contains useful functions */
	
	public static boolean isBeforeInDictionary(String word1, String word2)
	{
		/* Returns true if word1 comes before word 2 in the dictionary, checks recursively */
		
		// Covert the first letter to an integer
		int word1FirstLetter = (int) word1.toLowerCase().toCharArray()[0];
		int word2FirstLetter = (int) word2.toLowerCase().toCharArray()[0];
		boolean beforeInDictionary = false;
		
		// Compare the letters
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
			// If either word is just 1 letter
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

}