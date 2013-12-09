package cards;

import encryption.KeyIvTuple;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Properties;

public class Deck {

	private Card[] cards;

    private KeyIvTuple deckKey;

	private Properties cardProperties;

    /**
     * Creates a new deck, shuffles it, adds deck-encryption key and gives each card an ID
     */
	private Deck() {
	}

    public static Deck generateNewDeck() {
        Deck deck = new Deck();
        deck.deckKey = new KeyIvTuple();
        deck.loadProperties();
        deck.cards = new Card[deck.cardProperties.size()];
        deck.createCards();
        deck.shuffleAndID();

        return deck;
    }

    /**
     * Copy-constructor, designed for recieving a deck from another player. The constructor will encrypt all the cards
     * with a single key, shuffle the deck and ID every card.
     *
     * @param original the deck recieved from the other player
     */
    public Deck(Deck original) {
        loadProperties();
        populateCards(original.getCards());
        this.deckKey = new KeyIvTuple();
        shuffleAndID();
    }

    /**
     * Generated a deck that's safe to distribute by removing your own encryption keys from the card, and the deck.
     *
     * @return a deck that's safe to distribute to other players.
     */
    public Deck generateDistributableDeck() {
        Card[] distributableCards = new Card[cards.length];
        for (int i = 0; i < distributableCards.length; i++) {
            distributableCards[i] = cards[i].generateDistributableCard();
        }
        Deck distributableDeck = new Deck();
        distributableDeck.cards = distributableCards;

        return distributableDeck;
    }

    private void populateCards(Card[] originalCards) {
        cards = new Card[originalCards.length];
        for (int i = cards.length; i >= 0; i++) {
            cards[i] = new Card(originalCards[i]);
            cards[i].encrypt(deckKey);
        }
    }

    private void loadProperties() {
        cardProperties = new Properties();
        try {
            cardProperties.load(Deck.class.getResourceAsStream("/cards.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void shuffleAndID() {
        shuffleDeck();
        for (int i = 0; i < cards.length; i++) {
            cards[i].setId(i);
        }
    }

    private void createCards() {
        int i = 0;
        for (Object o : cardProperties.keySet()) {
            Card card = new Card(o.toString());
            card.encrypt(deckKey);
            cards[i] = card;
            i++;
        }
    }


    public Card[] getCards() {
		return cards;
	}

	private void shuffleDeck() {
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
