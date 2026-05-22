package domain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Fuente de vida estática. Al ser recogida por un jugador le otorga un escudo:
 * el primer golpe de enemigo no lo mata, solo reinicia posición sin reiniciar monedas.
 * La fuente desaparece una vez usada su carga.
 * Se dibuja como un corazón pequeño rojo.
 */
public class LifeSource implements ICollidable, IRenderable {

    private final int x;
    private final int y;
    private final int size = 18;
    private boolean collected;

    public LifeSource(int x, int y) {
        this.x         = x;
        this.y         = y;
        this.collected = false;
    }

    /**
     * Recoge la fuente y activa el escudo del jugador.
     * Solo tiene efecto si no fue recogida aún.
     */
    public void collect(Player player) {
        if (!collected) {
            collected = true;
            player.activateLifeShield();
        }
    }

    public void reset()              { collected = false; }
    public boolean isCollected()     { return collected; }

    @Override
    public Rectangle getBounds()     { return new Rectangle(x, y, size, size); }

    @Override
    public boolean collidesWith(ICollidable other) {
        return !collected && getBounds().intersects(other.getBounds());
    }

    @Override
    public void render(Graphics g) {
        if (collected) return;
        // Corazón pequeño: dos círculos + triángulo
        int cx = x + size / 2;
        int cy = y + size / 2;
        g.setColor(new Color(220, 30, 60));
        // Mitad izquierda del corazón
        g.fillOval(x,             y + 1, size / 2 + 1, size / 2);
        // Mitad derecha del corazón
        g.fillOval(x + size / 2 - 1, y + 1, size / 2 + 1, size / 2);
        // Parte inferior (triángulo aproximado con polígono)
        int[] px = { x, cx, x + size };
        int[] py = { cy, y + size - 1, cy };
        g.fillPolygon(px, py, 3);
        // Borde
        g.setColor(new Color(140, 0, 30));
        g.drawOval(x,                y + 1, size / 2 + 1, size / 2);
        g.drawOval(x + size / 2 - 1, y + 1, size / 2 + 1, size / 2);
        g.drawPolygon(px, py, 3);
    }
}
