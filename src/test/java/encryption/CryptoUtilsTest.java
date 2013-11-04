package encryption;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author: Petter
 */
public class CryptoUtilsTest {

    private String PLAINTEXT = "test";

    @Test
    public void testEncryptDecrypt() {

        List<KeyIvTuple> keyList = new ArrayList<>();
        keyList.add(new KeyIvTuple());

        byte[] encryptedString = CryptoUtils.encryptString(PLAINTEXT.getBytes(), keyList.get(0));
        String decryptedString = CryptoUtils.decryptString(encryptedString, keyList);

        assertThat(decryptedString, is(PLAINTEXT));
    }

    @Test
    public void testEncrypDecryptMultipleKeys() {

        List<KeyIvTuple> keyList = new ArrayList<>();
        keyList.add(new KeyIvTuple());
        keyList.add(new KeyIvTuple());
        keyList.add(new KeyIvTuple());

        byte[] text = PLAINTEXT.getBytes();

        for (KeyIvTuple key : keyList) {
            text = CryptoUtils.encryptString(text, key);
        }

        String decryptedText = CryptoUtils.decryptString(text, keyList);
        assertThat(decryptedText, is(PLAINTEXT));
    }

    private String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];

        int v;
        for (int i = 0; i < bytes.length; i++) {
            v = bytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[(i * 2) + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
