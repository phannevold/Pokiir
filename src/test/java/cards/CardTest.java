package cards;

import com.sun.corba.se.impl.orb.ParserTable;
import encryption.KeyIvTuple;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

/**
 * @author petter.b.hannevold
 */
public class CardTest {

    public static final String CARD_VALUE = "H2";
    Card[] cards;
    Card card;
    Properties properties;

	@Before
	public void setUp() {
		cards = new Card[]{
                    new Card(),
                    new Card(),
                    new Card() };

        card = cards[0];

		for (int i = 0; i < cards.length; i++) {
			cards[i].setId(i);
		}

        fetchProperties();
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
        List<byte[]> keys = new ArrayList<>();
        List<byte[]> IVs = new ArrayList<>();
        for (Card card : cards) {
            keys.add(card.getEncryptionKeys().get(0).getKey());
            IVs.add(card.getEncryptionKeys().get(0).getKey());
        }

        Iterator<byte[]> keyIterator = keys.iterator();
        while(keyIterator.hasNext()) {
            byte[] key = keyIterator.next();
            keyIterator.remove();
            assertTrue("Key list should not contain the key we just removed", !keys.contains(key));
        }

        Iterator<byte[]> IvIterator = keys.iterator();
        while(IvIterator.hasNext()) {
            byte[] Iv = IvIterator.next();
            IvIterator.remove();
            assertTrue("Key list should not contain the key we just removed", !keys.contains(Iv));
        }
	}

    @Test
    public void testEncryptDecryptWithSingleGeneratedKey() {
        card.setValue(CARD_VALUE);
        assertThat(card.getEncryptedValue(), is(notNullValue()));
        card.setValue(null);
        assertThat(card.getValue(), is(CARD_VALUE));
    }
}
