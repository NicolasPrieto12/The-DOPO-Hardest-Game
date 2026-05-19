package domain;

/**
 * Clase que define las constantes de estado del juego.
 * Se usa para controlar el flujo del juego en la clase Game
 * y para que GamePanel sepa qué pantalla mostrar en cada momento.
 */
public class GameState {

    /** El juego está en curso y el jugador puede moverse. */
    public static final String PLAYING = "PLAYING";

    /** El jugador ha muerto. Estado reservado para uso futuro. */
    public static final String DEAD    = "DEAD";

    /** El jugador completó todos los niveles. */
    public static final String WIN     = "WIN";

    /** El juego está pausado, no se actualiza ningún objeto. */
    public static final String PAUSED  = "PAUSED";

    /** El tiempo límite del nivel llegó a cero, el jugador perdió. */
    public static final String TIMEOUT = "TIMEOUT";
}
