import java.util.Base64;
import java.io.IOException;

public class Encrypter {

    public String encode(String s, String key)
	{
        return base64Encode(xorWithKey(s.getBytes(), key.getBytes()));
    }

    public String decode(String s, String key)
	{
        return new String(xorWithKey(base64Decode(s), key.getBytes()));
    }

    private byte[] xorWithKey(byte[] dataToEncode, byte[] key)
	{
		
        byte[] encoded = new byte[dataToEncode.length];
		
        for (int i = 0; i < dataToEncode.length; i++)
		{
            encoded[i] = (byte) (dataToEncode[i] ^ key[i%key.length]);
        }
		
        return encoded;
    }

    private byte[] base64Decode(String s)
	{
		return Base64.getDecoder().decode(s);
    }

    private String base64Encode(byte[] bytes)
	{
        return Base64.getEncoder().encodeToString(bytes).replaceAll("\\s", "");
    }
}