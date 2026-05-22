package domain;

import java.awt.Color;

/**
 * Define los tipos de jugador disponibles en el juego.
 * Cada tipo tiene su propio color, velocidad base y tamaño base.
 *
 * <p>Tipos disponibles:</p>
 * <ul>
 *   <li>{@link #RED}: estado base, velocidad 3, tamaño 20</li>
 *   <li>{@link #BLUE}: obtenido con SkinCoin, velocidad 4, tamaño 30</li>
 *   <li>{@link #GREEN}: obtenido con GreenCoin, velocidad 3, tamaño 20, resistente</li>
 * </ul>
 */
public enum PlayerType {

    /** Cuadrado rojo clásico. Velocidad y tamaño normales. */
    RED(Color.RED, 3, 20),

    /** Cuadrado azul (Inky). Velocidad 1.5x y tamaño 1.5x respecto al rojo. */
    BLUE(new Color(30, 100, 220), (int)(3 * 1.5), (int)(20 * 1.5)),

    /** Cuadrado verde (Clyde). Resistente: al primer golpe pierde velocidad en vez de morir. */
    GREEN(new Color(0, 180, 60), 3, 20);

    /** Color de renderizado del jugador. */
    public final Color color;

    /** Velocidad de desplazamiento en píxeles por tick. */
    public final int speed;

    /** Tamaño del lado del cuadrado en píxeles. */
    public final int size;

    PlayerType(Color color, int speed, int size) {
        this.color = color;
        this.speed = speed;
        this.size  = size;
    }
}
