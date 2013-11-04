package cards;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Properties;

public class Deck {

	private Card[] cards;

	Properties cardProperties;

	public Deck() {
		cardProperties = new Properties();
		cards = new Card[cardProperties.size()];
		try {
			cardProperties.load(Deck.class.getResourceAsStream("/cards.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
        cards = new Card[cardProperties.size()];

        int i = 0;
        for (Object o : cardProperties.keySet()) {
            Card card = new Card(o.toString());
            cards[i] = card;
            i++;
        }

        shuffleDeck();
        for (i = 0; i < cards.length; i++) {
            cards[i].setId(i);
        }
	}
    

	public Card[] getCards() {
		return cards;
	}

	public void shuffleDeck() {
		SecureRandom randomGenerator = new SecureRandom();

		Card tempCard;

		for (int i = cards.length - 1; i >= 0; i--) {
			int index = randomGenerator.nextInt(i + 1);
			tempCard = cards[i];
			cards[i] = cards[index];
			cards[index] = tempCard;
		}
	}
}
