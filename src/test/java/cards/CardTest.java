package cards;

import encryption.CryptoUtils;
import encryption.KeyIvTuple;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author petter.b.hannevold
 */
public class CardTest {

    public static final String CARD_VALUE = "H2";
    Properties properties;

	@Before
	public void setUp() {

    }

    @Test
	public void testGenerateKey() {
        Card card = new Card(CARD_VALUE);
        assertThat(card.getEncryptedValue(), is(notNullValue()));
        Assert.assertThat(new String(card.getEncryptedValue()), is(not(card.getValue())));
	}

    @Test
    public void testEncryptDecryptWithSingleGeneratedKey() {
        Card card = new Card(CARD_VALUE);
        assertThat(card.getEncryptedValue(), is(notNullValue()));
        assertThat(new String(card.getEncryptedValue()), is(not(CARD_VALUE)));
        card.setValue(null);
        assertThat(card.getValue(), is(CARD_VALUE));
    }

    @Test
    public void testEncryptDecryptWithSeveralKeys() {

        Card card = new Card(CARD_VALUE);
        KeyIvTuple myKey = card.getEncryptionKeys().get(0);

        card = card.generateDistributableCard();
        byte[] encryptedValue = card.getEncryptedValue();

        List<KeyIvTuple> keyList = new ArrayList<>();
        keyList.add(myKey);
        keyList.add(new KeyIvTuple());
        keyList.add(new KeyIvTuple());
        keyList.add(new KeyIvTuple());

        for (int i = 1; i < keyList.size(); i++) {
            encryptedValue = CryptoUtils.encryptString(encryptedValue, keyList.get(i));
        }

        try {
            Field field = card.getClass().getDeclaredField("encryptionKeys");
            field.setAccessible(true);
            field.set(card, keyList);
        } catch (NoSuchFieldException
                | IllegalAccessException e) {
            e.printStackTrace();
        }

        card.setEncryptedValue(encryptedValue);

        assertThat(card.getValue(), is(CARD_VALUE));
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

    private void fetchProperties() {
        properties = new Properties();
        try {
            properties.load(this.getClass().getResourceAsStream("/cards.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
