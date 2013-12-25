package cards;

import encryption.CryptoUtils;
import encryption.KeyIvTuple;
import game.Game;

import java.io.IOException;
import java.util.*;

/**
 * @author Petter Hannevold
 */

public class Card {

    private String value;

    private List<KeyIvTuple> encryptionKeys;

    private byte[] encryptedValue;

    private int myEncryptedKeyIndex = -1;
    private int myDeckEncryptionIndex = -1;

    private Properties properties;

    private Map<UUID, Integer> ids;

    private Card() {

    }

    /**
     * Constructor for generating "fresh" cards, will only be used in cases that the player is the dealer.
     *
     * @param value the card's value in plaintext.
     */
    public Card(String value) {
        this.value =  value;
        encryptionKeys = new ArrayList<>();
        ids = new HashMap<>();
        encrypt();
    }

    /**
     * Copyconstructor dedicated to cards handed by other players. The constructor will generate a key, add it to the
     * key-list and add another layer of encryption using that key.
     *
     * @param original The card recieved
     */
    public Card(Card original) {

        validateOriginal(original);

        this.ids = original.ids;
        this.encryptionKeys = original.getEncryptionKeys();
        this.encryptedValue = original.getEncryptedValue();

        // Adds my own layer of encryption
        encryptionKeys.add(new KeyIvTuple());
        this.myEncryptedKeyIndex = encryptionKeys.size() - 1;

        this.encryptedValue = CryptoUtils.encryptString(encryptedValue, encryptionKeys.get(myEncryptedKeyIndex));
    }

    /**
     * Adds an encryption key on the specified index.
     * No encryption is done here, this method are designed to add keys from players who already encrypted
     * this card with their key.
     */
    public void addEncryptionKey(int index, KeyIvTuple key) {

        while (index > encryptionKeys.size() - 1) {
            encryptionKeys.add(null);
        }
        encryptionKeys.set(index, key);
    }

    /**
     * Returns a copy of the card's encryption keys, generates first key if none is set
     *
     * @return the card's encryption key
     */
    public List<KeyIvTuple> getEncryptionKeys() {

        List<KeyIvTuple> encryptionKeysCopy = new ArrayList<>();
        for (KeyIvTuple key : encryptionKeys) {
            encryptionKeysCopy.add(key);
        }
        return encryptionKeysCopy;
    }

    /**
     * Decrypts the card if it's not already set.
     * Does not attempt to decrypt if any of the keys are null
     *
     * @return the value if it's set, attempts to decrypt the encrypted value if it's not set.
     */
    public String getValue() {

        if (value == null && !encryptionKeys.contains(null)) {

            value = CryptoUtils.decryptString(encryptedValue, encryptionKeys);

            Properties cardValues = fetchProperties();
            value = cardValues.getProperty(value) != null ? value : null;
        }
        return value;
    }


    /**
     * Generates a version of the card that safely can be distributed by removing
     * the card's value and the encryption key we used to encrypt this card.
     *
     * @return a version of this card that's safe to distribute
     */
    public Card generateDistributableCard() {
        Card card =  new Card();
        card.setEncryptedValue(this.encryptedValue);
        card.ids = this.ids;
        card.encryptionKeys = makeSafeEncryptionKeyList();

        return card;
    }

    private List<KeyIvTuple> makeSafeEncryptionKeyList() {
        List<KeyIvTuple> safeEncryptionKeys = new ArrayList<>();
        for (int i = 0; i < encryptionKeys.size(); i++) {
            if (i == myEncryptedKeyIndex || i == myDeckEncryptionIndex) {
                safeEncryptionKeys.add(null);
            } else {
                safeEncryptionKeys.add(encryptionKeys.get(i));
            }
        }
        return safeEncryptionKeys;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setEncryptedValue(byte[] encryptedValue) {
        this.encryptedValue = encryptedValue;
    }

    public byte[] getEncryptedValue() {
        return encryptedValue;
    }

    public Map<UUID, Integer> getIds() {
        return ids;
    }

    public int getMyDeckEncryptionIndex() {
        return myDeckEncryptionIndex;
    }

    public void setId(Integer id) {
        if (ids.containsKey(Game.me.getId())) {
            throw new IllegalStateException("You've tried to add an ID to a card when you've already ID'ed it before");
        }
        ids.put(Game.me.getId(), id);
    }

    /**
     * Method meant for encrypting a several cards with the same key. Adds a layer of encryption with the given key,
     * adds they key to the encryptionKeys-list and updates myDeckEncryptionIndex
     *
     */
    public void encrypt(KeyIvTuple key) {
        if (myDeckEncryptionIndex != -1) {
            throw new IllegalStateException("Deck already encrypted by us");
        }
        encryptionKeys.add(key);
        myDeckEncryptionIndex = encryptionKeys.size() - 1;
        encrypt(myDeckEncryptionIndex);
    }

    private void encrypt(int keyIndex) {
        if (encryptedValue == null) {
            encryptedValue = CryptoUtils.encryptString(value.getBytes(), encryptionKeys.get(keyIndex));
        } else {
            encryptedValue = CryptoUtils.encryptString(encryptedValue, encryptionKeys.get(keyIndex));
        }
    }

    private void encrypt() {
        if (myEncryptedKeyIndex != -1) {
            throw new IllegalStateException("Card already encrypted by us");
        }
        encryptionKeys.add(new KeyIvTuple());
        myEncryptedKeyIndex = encryptionKeys.size() - 1;
        encrypt(myEncryptedKeyIndex);
    }

    private void validateOriginal(Card card) {
        if (card.encryptedValue == null) {
            throw new IllegalArgumentException("Input card is not valid:");
        }
    }

    private Properties fetchProperties() {

        Properties cardValues;

        if (this.properties == null) {
             cardValues = new Properties();
            try {
                cardValues.load(this.getClass().getResourceAsStream("/cards.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            cardValues = this.properties;
        }
        return cardValues;
    }
}
