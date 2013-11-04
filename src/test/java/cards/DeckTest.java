package cards;

import game.Game;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * @author: Petter
 */
public class DeckTest {

    @Test
    public void testDeckGeneration() {
        assertFalse("two generated decks should not have the same permutation", hasEqualPermutation(new Deck(), new Deck()));
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
}
