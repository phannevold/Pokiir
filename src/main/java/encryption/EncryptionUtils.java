package encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author petter.b.hannevold
 */
public class EncryptionUtils {

	public static String encryptString (String plaintext, byte[] key) {
		return operateCipher(plaintext, key, Cipher.ENCRYPT_MODE);
	}

	public static String decryptString (String ciphertext, byte[] key) {
		return operateCipher(ciphertext, key, Cipher.DECRYPT_MODE);
	}

	private static String operateCipher(String text, byte[] key, int mode) {

		String result = null;

		try {
			Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			aesCipher.init(mode, new SecretKeySpec(key, "AES"));
			result = new String(aesCipher.doFinal(text.getBytes()));
		} catch (NoSuchAlgorithmException
				| NoSuchPaddingException
				| InvalidKeyException
				| IllegalBlockSizeException
				| BadPaddingException e) {
			e.printStackTrace();
		}
		return result;
	}

}

