package domain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Moneda especial de tipo skin que transforma al jugador al ser recogida.
 * Se dibuja en azul para distinguirse de las monedas normales amarillas.
 * Al recogerla, aplica el tipo {@link PlayerType#BLUE} al jugador,
 * aumentando su velocidad y tamaño en 1.5x.
 * Implementa {@link ICollidable} e {@link IRenderable}.
 */
public class SkinCoin implements ICollidable, IRenderable {

    /** Posición horizontal fija de la moneda. */
    private final int x;

    /** Posición vertical fija de la moneda. */
    private final int y;

    /** Tamaño del diámetro del círculo en píxeles. */
    private final int size = 14;

    /** Indica si la moneda ya fue recogida. */
    private boolean collected;

    /**
     * Crea una moneda de skin en la posición indicada.
     *
     * @param x Posición X de la moneda.
     * @param y Posición Y de la moneda.
     */
    public SkinCoin(int x, int y) {
        this.x         = x;
        this.y         = y;
        this.collected = false;
    }

    /**
     * Recoge la moneda y aplica la transformación al jugador.
     * Cambia el tipo del jugador a {@link PlayerType#BLUE}.
     *
     * @param player El jugador que recoge la moneda.
     */
    public void collect(Player player) {
        collected = true;
        player.applyType(PlayerType.BLUE);
    }

    /** Restablece la moneda a no recogida. */
    public void reset() { collected = false; }

    /** @return true si la moneda ya fue recogida. */
    public boolean isCollected() { return collected; }

    /** {@inheritDoc} */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }

    /** {@inheritDoc} */
    @Override
    public boolean collidesWith(ICollidable other) {
        return !collected && getBounds().intersects(other.getBounds());
    }

    /**
     * Dibuja la moneda como un círculo azul con borde azul oscuro.
     * No dibuja nada si ya fue recogida.
     *
     * @param g El objeto Graphics usado para dibujar.
     */
    @Override
    public void render(Graphics g) {
        if (!collected) {
            g.setColor(new Color(50, 150, 255));
            g.fillOval(x, y, size, size);
            g.setColor(new Color(0, 80, 180));
            g.drawOval(x, y, size, size);
        }
    }
}
