package encryption;

import java.security.SecureRandom;

/**
 * @author: Petter Hannevold
 */
public class KeyIvTuple {

    private final byte[] key;
    private final byte[] IV;

    public KeyIvTuple(byte[] key, byte[] IV) {
        this.key = key;
        this.IV = IV;
    }

    public KeyIvTuple() {
        SecureRandom randomGenerator = new SecureRandom();
        byte[] key = new byte[16];
        randomGenerator.nextBytes(key);
        byte[] IV = new byte[16];
        randomGenerator.nextBytes(IV);

        this.key = key;
        this.IV = IV;
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getIV() {
        return IV;
    }
}
