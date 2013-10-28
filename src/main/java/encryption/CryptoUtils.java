package encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.ListIterator;

/**
 * @author petter.b.hannevold
 */
public class CryptoUtils {

	public static byte[] encryptString (String plaintext, List<KeyIvTuple> keys) {

        byte[] byteText = plaintext.getBytes();

        for (KeyIvTuple key : keys) {
            byteText = operateCipher(byteText, key, Cipher.ENCRYPT_MODE);
        }
        return byteText;
    }

	public static String decryptString (byte[] ciphertext, List<KeyIvTuple> keys) {

        ListIterator<KeyIvTuple> keyIterator = keys.listIterator(keys.size());

        while (keyIterator.hasPrevious()) {
            ciphertext = operateCipher(ciphertext, keyIterator.previous(), Cipher.DECRYPT_MODE);
        }
        return new String(ciphertext);
    }

	private static byte[] operateCipher(byte[] text, KeyIvTuple key, int mode) {

		byte[] result = null;

		try {
			Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			aesCipher.init(mode, new SecretKeySpec(key.getKey(), "AES"), new IvParameterSpec(key.getIV()));
			result = aesCipher.doFinal(text);

		} catch (NoSuchAlgorithmException
				| NoSuchPaddingException
				| InvalidKeyException
				| IllegalBlockSizeException
                | InvalidAlgorithmParameterException
				| BadPaddingException e) {
			e.printStackTrace();
        }
        return result;
	}
}

