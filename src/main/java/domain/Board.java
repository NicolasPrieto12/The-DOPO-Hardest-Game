package domain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

/**
 * Representa el tablero físico del juego.
 * Define el espacio donde ocurre la acción: el fondo, las paredes,
 * la zona de inicio y la zona final.
 * Implementa {@link IRenderable} para dibujarse en el panel del juego.
 *
 * <p>Dimensiones por defecto: 800x500 px.</p>
 * <p>El fondo se dibuja en verde claro, las paredes en gris.</p>
 * <p>Las zonas (StartZone y EndZone) se dibujan encima de las paredes.</p>
 */
public class Board implements IRenderable {

    /** Ancho por defecto del tablero en píxeles. */
    private static final int DEFAULT_WIDTH  = 800;

    /** Alto por defecto del tablero en píxeles. */
    private static final int DEFAULT_HEIGHT = 500;

    /** Ancho actual del tablero. */
    private final int width;

    /** Alto actual del tablero. */
    private final int height;

    /** Zona verde de inicio donde aparece el jugador. */
    private final StartZone startZone;

    /** Zona verde final que el jugador debe alcanzar para ganar. */
    private final EndZone endZone;

    /** Lista de rectángulos que representan las paredes del tablero. */
    private final List<Rectangle> walls;

    /**
     * Crea un tablero con las zonas seguras y las paredes definidas.
     *
     * @param startZone Zona de inicio del jugador.
     * @param endZone   Zona final que el jugador debe alcanzar.
     * @param walls     Lista de rectángulos que representan las paredes.
     */
    public Board(StartZone startZone, EndZone endZone, List<Rectangle> walls) {
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        this.startZone = startZone;
        this.endZone = endZone;
        this.walls = walls;
    }

    /**
     * Retorna las dimensiones por defecto del tablero.
     *
     * @return Arreglo con [ancho, alto] del tablero.
     */
    public static int[] getDefaultSize() {
        return new int[]{DEFAULT_WIDTH, DEFAULT_HEIGHT};
    }

    /**
     * Dibuja el tablero completo: fondo, zonas seguras y paredes.
     *
     * @param g El objeto Graphics usado para dibujar.
     */
    @Override
    public void render(Graphics g) {
        // Fondo
        g.setColor(new Color(180, 210, 180));
        g.fillRect(0, 0, width, height);

        // Paredes primero para que las zonas queden encima
        g.setColor(new Color(130, 130, 130));
        for (Rectangle wall : walls) {
            g.fillRect(wall.x, wall.y, wall.width, wall.height);
        }

        // Zonas encima de las paredes
        startZone.render(g);
        endZone.render(g);
    }

    /** @return La zona de inicio del tablero. */
    public StartZone getStartZone()   { return startZone; }

    /** @return La zona final del tablero. */
    public EndZone getEndZone()       { return endZone; }

    /** @return La lista de paredes del tablero. */
    public List<Rectangle> getWalls() { return walls; }

    /** @return El ancho del tablero en píxeles. */
    public int getWidth()             { return width; }

    /** @return El alto del tablero en píxeles. */
    public int getHeight()            { return height; }
}
