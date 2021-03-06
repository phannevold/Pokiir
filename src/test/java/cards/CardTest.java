package cards;

import encryption.CryptoUtils;
import encryption.KeyIvTuple;
import game.Game;
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
 * @author Petter Hannevold
 */
public class CardTest {

    public static final String CARD_VALUE = "H2";
    private final int CARD_ID = 5;
    Properties properties;

    @Before
	public void setUp() {

    }

    @Test
    public void testEncryptionDecryptionExternalKey() {
        Card card = new Card(CARD_VALUE);
        card.encrypt(new KeyIvTuple());

        assertThat(card.getValue(), is(CARD_VALUE));
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

    @Test
    public void assertThatGeneratedCardIsKosher() {
        Card card = new Card(CARD_VALUE);

        card.addEncryptionKey(1, new KeyIvTuple());
        card.addEncryptionKey(2, new KeyIvTuple());
        card.setId(CARD_ID);
        card.encrypt(new KeyIvTuple());
        int myDeckEncryptionIndex = card.getMyDeckEncryptionIndex();

        card = card.generateDistributableCard();

        assertThat(card.getEncryptedValue(), is(notNullValue()));
        assertThat(card.getValue(), is(nullValue()));
        assertThat(card.getEncryptionKeys().get(0), is(nullValue()));
        assertThat(card.getIds().get(Game.me.getId()), is(CARD_ID));
        assertThat(card.getEncryptionKeys().get(myDeckEncryptionIndex), is(nullValue()));

        int myEncryptionKeyIndex = -1;
        int myDeckEncryptionKeyIndex = -1;
        String value = null;

        try {
            Field myEncryptionKeyIndexField = card.getClass().getDeclaredField("myEncryptedKeyIndex");
            Field valueField = card.getClass().getDeclaredField("value");
            Field myDeckEncryptionKeyIndexField = card.getClass().getDeclaredField("myDeckEncryptionIndex");
            myEncryptionKeyIndexField.setAccessible(true);
            valueField.setAccessible(true);
            myDeckEncryptionKeyIndexField.setAccessible(true);

            myEncryptionKeyIndex = (Integer)myEncryptionKeyIndexField.get(card);
            value = (String)valueField.get(card);
            myDeckEncryptionKeyIndex = (Integer)myDeckEncryptionKeyIndexField.get(card);
        } catch (NoSuchFieldException
                | IllegalAccessException e) {
            e.printStackTrace();
        }

        assertThat(myEncryptionKeyIndex, is(-1));
        assertThat(value, is(nullValue()));
        assertThat(myDeckEncryptionKeyIndex, is(-1));
    }

    @Test
    public void testCardGenerationAndDecryption() {
        Card card = new Card(CARD_VALUE);
        KeyIvTuple originalKey = card.getEncryptionKeys().get(0);

        card = card.generateDistributableCard();
        card.addEncryptionKey(0, originalKey);

        card = new Card(card);

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
