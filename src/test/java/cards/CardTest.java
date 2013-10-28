package cards;

import org.junit.Before;
import org.junit.Test;

/**
 * @author petter.b.hannevold
 */
public class CardTest {

	Card[] cards;

	@Before
	public void setUp() {
		cards = new Card[]{
				new Card(),
				new Card(),
				new Card()};

		for (int i = 0; i < cards.length; i++) {
			cards[i].setId(i);
		}
	}

	@Test
	public void testGenerateKey() {
	}
}
