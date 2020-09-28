package com.harry.formfiller.util;

import java.util.Base64;

public class Encrypter {
	/*
		This class takes a string and then xors it with an encryption key.
		It then encodes it with base 64. The class can also decrypt the strings.
	*/
    public String encode(String s, String key)
	{
		/* Encodes a given string with the given key */
        return base64Encode(xorWithKey(s.getBytes(), key.getBytes()));
    }

    public String decode(String s, String key)
	{
		/* Decodes a given string with the given key */
        return new String(xorWithKey(base64Decode(s), key.getBytes()));
    }

    private byte[] xorWithKey(byte[] dataToEncode, byte[] key)
	{
		/* Private method that actually does the xoring */
        byte[] encoded = new byte[dataToEncode.length];
		
        for (int i = 0; i < dataToEncode.length; i++)
		{
            encoded[i] = (byte) (dataToEncode[i] ^ key[i%key.length]);
        }
		
        return encoded;
    }

    private byte[] base64Decode(String s)
	{
		/* Decodes base 64 back to UTF-8 */
		return Base64.getDecoder().decode(s);
    }

    private String base64Encode(byte[] bytes)
	{
		/* Encodes UTF-8 into base 64 */
        return Base64.getEncoder().encodeToString(bytes).replaceAll("\\s", "");
    }
}