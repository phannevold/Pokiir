package cards;

import encryption.CryptoUtils;
import encryption.KeyIvTuple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Card {

    private int id = -1;

    private String value;

    private List<KeyIvTuple> encryptionKeys;

    private byte[] encryptedValue;

    private int myEncryptedKeyIndex = -1;

    private Properties properties;

    /**
     * Constructor for generating "fresh" cards, will only be used in cases that the player is the dealer.
     *
     * @param value the card's value in plaintext.
     */
    public Card(String value) {
        this.value =  value;
        encryptionKeys = new ArrayList<>();
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

        this.id = original.id;
        this.encryptionKeys = original.getEncryptionKeys();
        this.encryptedValue = original.getEncryptedValue();
    }


    /**
     * Returns the card's encryption keys, generates first key if none is set
     *
     * @return the card's encryption key
     */
    public List<KeyIvTuple> getEncryptionKeys() {
        if (myEncryptedKeyIndex == -1) {
            addGeneratedKey();
        }
        return encryptionKeys;
    }

    /**
     *
     * @returnthe value if it's set, attempts to decrypt the encrypted value if it's not set
     */
    public String getValue() {

        if (value == null) {

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
        Card card =  new Card(this);
        card.getEncryptionKeys().set(myEncryptedKeyIndex, null);
        return card;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private void encrypt() {
        if (myEncryptedKeyIndex == -1) {
            addGeneratedKey();
        }
        if (encryptedValue == null) {
            encryptedValue = CryptoUtils.encryptString(value.getBytes(), getEncryptionKeys().get(myEncryptedKeyIndex));
        } else {
            encryptedValue = CryptoUtils.encryptString(encryptedValue, getEncryptionKeys().get(myEncryptedKeyIndex));
        }
    }

    private void addGeneratedKey() {
        if (myEncryptedKeyIndex == -1) {
            encryptionKeys.add(new KeyIvTuple());
            myEncryptedKeyIndex = encryptionKeys.size() - 1;
        }
    }

    private void validateOriginal(Card card) {
        if (card.encryptedValue == null
                || id == -1
                || id > fetchProperties().size()) {
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
