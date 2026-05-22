package domain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Moneda verde que transforma al jugador en Clyde ({@link PlayerType#GREEN}).
 * Clyde es resistente: al primer contacto con un enemigo no muere, sino que pierde velocidad.
 */
public class GreenCoin implements ICollidable, IRenderable {

    private final int x;
    private final int y;
    private final int size = 14;
    private boolean collected;

    public GreenCoin(int x, int y) {
        this.x         = x;
        this.y         = y;
        this.collected = false;
    }

    public void collect(Player player) {
        collected = true;
        player.applyType(PlayerType.GREEN);
    }

    public void reset() { collected = false; }

    public boolean isCollected() { return collected; }

    @Override
    public Rectangle getBounds() { return new Rectangle(x, y, size, size); }

    @Override
    public boolean collidesWith(ICollidable other) {
        return !collected && getBounds().intersects(other.getBounds());
    }

    @Override
    public void render(Graphics g) {
        if (!collected) {
            g.setColor(new Color(0, 200, 80));
            g.fillOval(x, y, size, size);
            g.setColor(new Color(0, 120, 40));
            g.drawOval(x, y, size, size);
        }
    }
}
