package cards;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Properties;

public class Deck {

	private Card[] cards;

	Properties cardProperties;

	private Deck() {
		cardProperties = new Properties();
		cards = new Card[cardProperties.size()];
		try {
			cardProperties.load(Deck.class.getResourceAsStream("/cards.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Deck createDeck() {

		Deck deck = new Deck();
		deck.cards = new Card[52];

		int i = 0;
		for (Object o : deck.cardProperties.keySet()) {
			Card card = new Card(o.toString());
			deck.cards[i] = card;
			i++;
		}

		deck.shuffleDeck();
		for (int j = 0; j < deck.cards.length; j++) {
			deck.cards[j].setId(j);
		}
		return deck;
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
