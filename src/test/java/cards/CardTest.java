package cards;

import com.sun.corba.se.impl.orb.ParserTable;
import encryption.KeyIvTuple;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.security.Key;
import java.util.*;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

/**
 * @author petter.b.hannevold
 */
public class CardTest {

    public static final String CARD_VALUE = "H2";
    Properties properties;

	@Before
	public void setUp() {

    }

    private void fetchProperties() {
        properties = new Properties();
        try {
            properties.load(this.getClass().getResourceAsStream("/cards.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @Test
    public void mouseclicker() {

        Random random = new Random();

        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        while(true) {
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
            try {
                Thread.sleep(10 + random.nextInt(10));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
