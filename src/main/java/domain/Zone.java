package domain;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Clase abstracta que representa una zona especial del tablero.
 * Define los atributos y comportamientos comunes de las zonas seguras.
 * Las subclases {@link StartZone} y {@link EndZone} implementan
 * el comportamiento específico de cada tipo de zona.
 * Implementa {@link IRenderable}.
 */
public abstract class Zone implements IRenderable {

    /** Posición horizontal de la zona. */
    protected int x;

    /** Posición vertical de la zona. */
    protected int y;

    /** Ancho de la zona en píxeles. */
    protected int width;

    /** Alto de la zona en píxeles. */
    protected int height;

    /**
     * Crea una zona en la posición y con las dimensiones indicadas.
     *
     * @param x      Posición X de la zona.
     * @param y      Posición Y de la zona.
     * @param width  Ancho de la zona.
     * @param height Alto de la zona.
     */
    public Zone(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Verifica si el jugador está dentro de esta zona.
     *
     * @param player El jugador a verificar.
     * @return true si el jugador está dentro de la zona, false en caso contrario.
     */
    public boolean contains(Player player) {
        return new Rectangle(x, y, width, height).intersects(player.getBounds());
    }

    /** @return La posición X de la zona. */
    public int getX()      { return x; }

    /** @return La posición Y de la zona. */
    public int getY()      { return y; }

    /** @return El ancho de la zona. */
    public int getWidth()  { return width; }

    /** @return El alto de la zona. */
    public int getHeight() { return height; }

    /**
     * {@inheritDoc}
     * Cada subclase define cómo se dibuja visualmente.
     */
    @Override
    public abstract void render(Graphics g);
}
