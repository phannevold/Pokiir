package cards;

import encryption.KeyIvTuple;
import game.Game;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author: Petter
 */
public class DeckTest {

    private final int SIZE_OF_DECK = 52;
    Deck deck;

    Properties cardValues;

    @Before
    public void setup() {
        deck = Deck.generateNewDeck();
        cardValues = fetchProperties();
    }

    @Test
    public void testDeckGeneration() {
        assertThat(deck.getCards().length, is(cardValues.size()));
        assertFalse("two generated decks should not have the same permutation", hasEqualPermutation(deck, Deck.generateNewDeck()));
    }

    @Test
    public void shouldBeAbleToDecryptCardsInGeneratedDeck() {
        Card card = deck.getCards()[0];
        assertThat(cardValues.keySet().contains(card.getValue()), is(true));
    }

    @Test
    public void deckShouldContainAllCards() {

        Set cardKeys = cardValues.keySet();
        assertThat(cardKeys.size(), is(SIZE_OF_DECK));
        for (Card card : deck.getCards()) {
            assertThat(cardKeys.remove(card.getValue()), is(true));
        }
        assertThat(cardKeys.size(), is(0));
    }

    @Test
    public void distributableDeckShouldNotContainSensitiveData() {

        for (Card card : deck.getCards()) {
            assertThat(card.getEncryptionKeys().size(), is(2));
        }
        for (Card card : deck.getCards()) {
            card.addEncryptionKey(2, new KeyIvTuple());
            card.addEncryptionKey(3, new KeyIvTuple());
        }

        deck = deck.generateDistributableDeck();
        for (Card card : deck.getCards()) {
            assertThat(card.getValue(), is(nullValue()));
            assertThat(card.getEncryptionKeys().get(0), is(nullValue()));
            assertThat(card.getEncryptionKeys().get(1), is(nullValue()));
            assertThat(card.getEncryptionKeys().get(2), is(notNullValue()));
            assertThat(card.getEncryptionKeys().get(3), is(notNullValue()));
        }

        try {
            Field deckKeyField = Deck.class.getDeclaredField("deckKey");
            deckKeyField.setAccessible(true);
            assertThat(deckKeyField.get(deck), is(nullValue()));
        } catch (NoSuchFieldException
                | IllegalAccessException e) {
            e.printStackTrace();
            fail();
        }
    }


    private boolean hasEqualPermutation(Deck one, Deck two) {
        boolean equals = true;

        for (int i = 0; i < one.getCards().length; i++)
            if (two.getCards().length < i) {
                equals = false;
                break;
            } else if (!one.getCards()[i].getIds().get(Game.me.getId()).equals(two.getCards()[i])) {
                equals = false;
                break;
            }
        return equals;
    }

    private Properties fetchProperties() {
        Properties cardValues = new Properties();
        try {
            cardValues.load(Deck.class.getResourceAsStream("/cards.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cardValues;
    }
}
