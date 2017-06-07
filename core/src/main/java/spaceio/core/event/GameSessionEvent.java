package spaceio.core.event;

import com.simsilica.event.EventType;

public class GameSessionEvent {

    /**
     *  Singals that the local player has joined the game.
     */
    public static EventType<GameSessionEvent> sessionStarted = EventType.create("SessionStarted", GameSessionEvent.class);

    /**
     *  Singals that the local player has left the game.
     */
    public static EventType<GameSessionEvent> sessionEnded = EventType.create("SessionEnded", GameSessionEvent.class);

    /**
     *  Creates a game session event.
     */
    public GameSessionEvent() {
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[]";
    }
}