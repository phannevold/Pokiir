package game;

import java.util.UUID;

/**
 * @author: Petter Hannevold
 */
public class Player {

    private final UUID id = UUID.randomUUID();

    public UUID getId() {
        return id;
    }
}
