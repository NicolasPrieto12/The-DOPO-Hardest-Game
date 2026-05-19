package domain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Representa una moneda amarilla que el jugador debe recoger para completar el nivel.
 * Una vez recogida deja de dibujarse y de participar en colisiones.
 * Se puede restablecer a su estado original mediante {@link #reset()}.
 * Implementa {@link ICollidable} e {@link IRenderable}.
 */
public class Coin implements ICollidable, IRenderable {

    /** Posición horizontal fija de la moneda. */
    private final int x;

    /** Posición vertical fija de la moneda. */
    private final int y;

    /** Tamaño del diámetro del círculo que representa la moneda en píxeles. */
    private final int size = 12;

    /** Indica si la moneda ya fue recogida por el jugador. */
    private boolean collected;

    /**
     * Crea una moneda en la posición indicada, lista para ser recogida.
     *
     * @param x Posición X de la moneda.
     * @param y Posición Y de la moneda.
     */
    public Coin(int x, int y) {
        this.x = x;
        this.y = y;
        this.collected = false;
    }

    /**
     * Marca la moneda como recogida. Deja de ser visible y colisionable.
     */
    public void collect() {
        collected = true;
    }

    /**
     * Restablece la moneda a su estado original (no recogida).
     * Se llama cuando el jugador muere o reinicia la partida.
     */
    public void reset() {
        collected = false;
    }

    /**
     * @return true si la moneda ya fue recogida, false si aún está disponible.
     */
    public boolean isCollected() { return collected; }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }

    /**
     * Verifica colisión solo si la moneda no ha sido recogida aún.
     *
     * @param other El objeto con el que se verifica la colisión.
     * @return true si hay colisión y la moneda no está recogida, false en caso contrario.
     */
    @Override
    public boolean collidesWith(ICollidable other) {
        return !collected && getBounds().intersects(other.getBounds());
    }

    /**
     * Dibuja la moneda como un círculo amarillo con borde naranja.
     * No dibuja nada si la moneda ya fue recogida.
     *
     * @param g El objeto Graphics usado para dibujar.
     */
    @Override
    public void render(Graphics g) {
        if (!collected) {
            g.setColor(Color.YELLOW);
            g.fillOval(x, y, size, size);
            g.setColor(Color.ORANGE);
            g.drawOval(x, y, size, size);
        }
    }
}
