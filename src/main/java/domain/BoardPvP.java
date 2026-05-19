package domain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

/**
 * Tablero especial para el modo PvP.
 * Tiene dos zonas de inicio (una por jugador en lados opuestos)
 * y dos zonas finales (cada jugador debe llegar al lado contrario).
 * Jugador 1 inicia izquierda y gana llegando a la derecha.
 * Jugador 2 inicia derecha y gana llegando a la izquierda.
 */
public class BoardPvP implements IRenderable {

    private static final int DEFAULT_WIDTH  = 800;
    private static final int DEFAULT_HEIGHT = 500;

    private final int width  = DEFAULT_WIDTH;
    private final int height = DEFAULT_HEIGHT;

    /** Zona de inicio del jugador 1 (lado izquierdo). */
    private final StartZone startZone1;

    /** Zona de inicio del jugador 2 (lado derecho). */
    private final StartZone startZone2;

    /** Zona final del jugador 1 (lado derecho, donde debe llegar). */
    private final EndZone endZone1;

    /** Zona final del jugador 2 (lado izquierdo, donde debe llegar). */
    private final EndZone endZone2;

    /** Paredes del tablero. */
    private final List<Rectangle> walls;

    /**
     * Crea el tablero PvP con zonas opuestas para cada jugador.
     *
     * @param startZone1 Zona de inicio del jugador 1 (izquierda).
     * @param startZone2 Zona de inicio del jugador 2 (derecha).
     * @param endZone1   Zona final del jugador 1 (derecha).
     * @param endZone2   Zona final del jugador 2 (izquierda).
     * @param walls      Lista de paredes del tablero.
     */
    public BoardPvP(StartZone startZone1, StartZone startZone2,
                    EndZone endZone1, EndZone endZone2,
                    List<Rectangle> walls) {
        this.startZone1 = startZone1;
        this.startZone2 = startZone2;
        this.endZone1   = endZone1;
        this.endZone2   = endZone2;
        this.walls      = walls;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(new Color(180, 210, 180));
        g.fillRect(0, 0, width, height);

        // Paredes primero
        g.setColor(new Color(130, 130, 130));
        for (Rectangle wall : walls) {
            g.fillRect(wall.x, wall.y, wall.width, wall.height);
        }

        // Zonas encima
        startZone1.render(g);
        startZone2.render(g);
        endZone1.render(g);
        endZone2.render(g);
    }

    /** @return Zona de inicio del jugador 1. */
    public StartZone getStartZone1() { return startZone1; }

    /** @return Zona de inicio del jugador 2. */
    public StartZone getStartZone2() { return startZone2; }

    /** @return Zona final del jugador 1 (derecha). */
    public EndZone getEndZone1()     { return endZone1; }

    /** @return Zona final del jugador 2 (izquierda). */
    public EndZone getEndZone2()     { return endZone2; }

    /** @return Lista de paredes. */
    public List<Rectangle> getWalls() { return walls; }

    /** @return Ancho del tablero. */
    public int getWidth()  { return width; }

    /** @return Alto del tablero. */
    public int getHeight() { return height; }
}
