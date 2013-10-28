package cards;

import encryption.CryptoUtils;
import encryption.KeyIvTuple;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Card {

    private int id;

    private String value;

    private List<KeyIvTuple> encryptionKeys;

    private byte[] encryptedValue;

    public Card() {
        encryptionKeys = new ArrayList<>();
        encryptionKeys.add(null);
    }

    /**
     * Returns the cards' encryption keys, generates first key if none is set
     *
     * @return the cards' encryption key
     */
    public List<KeyIvTuple> getEncryptionKeys() {
        if (encryptionKeys.get(0) == null) {
            encryptionKeys.set(0, new KeyIvTuple());
        }
        return encryptionKeys;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the value if it's set, attempts to decrypt the encrypted value if it's not set
     *
     * @return
     */
    public String getValue() {

        if (value == null) {

            value = CryptoUtils.decryptString(encryptedValue, encryptionKeys);

            Properties cardValues = new Properties();
            try {
                cardValues.load(this.getClass().getResourceAsStream("/cards.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            value = cardValues.getProperty(value) != null ? value : null;
        }
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setEncryptedValue(byte[] encryptedValue) {
        this.encryptedValue = encryptedValue;
    }

    /**
     * Returns the encrypted value if it's set, otherwise, encrypts the value
     *
     * @return the encrypted cardvalue
     */
    public byte[] getEncryptedValue() {
        if (encryptedValue == null) {
            encryptedValue = CryptoUtils.encryptString(value, getEncryptionKeys());
        }
        return encryptedValue;
    }
}
