package cards;

import encryption.EncryptionUtils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Card {

	private int id;

	private String value;

	private List<byte[]> encryptionKeys;

	private String encryptedValue;

	public Card() {
		encryptionKeys = new ArrayList<>();
	}

	/**
	 * Returns the cards' encryption key, generates one if none is set
	 * @return the cards' encryption key
	 */
	public List<byte[]> getEncryptionKeys() {
		if (encryptionKeys.get(0) == null) {
			SecureRandom randomGenerator = new SecureRandom();
			byte[] key = new byte[16];
			randomGenerator.nextBytes(key);
			encryptionKeys.add(0, key);
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
	 * @return
	 */
	public String getValue() {
		if (value == null) {
			try {
				value =  EncryptionUtils.decryptString(encryptedValue, encryptionKeys);
			} catch (NullPointerException e) {
				throw new IllegalStateException("the value or the encrypted value and the encryption key needs to be set");
			}
		}
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setEncryptedValue(String encryptedValue) {
		this.encryptedValue = encryptedValue;
	}

	/**
	 * Returns the encrypted value if it's set, otherwise, encrypts the value
	 *
	 * @return the encrypted cardvalue
	 */
	public String getEncryptedValue() {
		if (encryptedValue == null) {
			encryptedValue = EncryptionUtils.encryptString(value, getEncryptionKeys());
		}
		return encryptedValue;
	}
}
