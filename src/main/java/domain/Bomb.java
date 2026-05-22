package domain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Bomba estática. Destruye a cualquier elemento (jugador o enemigo) que colisione con ella.
 * No se desactiva al explotar: sigue activa durante toda la partida.
 *
 * <p>Tamaño: 18x18 px.</p>
 * <p>Apariencia: cuerpo negro con mecha marrón y chispa amarilla.</p>
 * <p>Efecto: muerte instantánea al contacto. No puede ser absorbida por el escudo.</p>
 *
 * <p>Aparece en los niveles 3 y 4 de todos los modos de juego.</p>
 */
public class Bomb implements ICollidable, IRenderable {

    private final int x;
    private final int y;
    private final int size = 18;

    public Bomb(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Rectangle getBounds() { return new Rectangle(x, y, size, size); }

    @Override
    public boolean collidesWith(ICollidable other) {
        return getBounds().intersects(other.getBounds());
    }

    @Override
    public void render(Graphics g) {
        // Cuerpo negro
        g.setColor(Color.BLACK);
        g.fillOval(x + 2, y + 4, size - 4, size - 4);
        // Mecha
        g.setColor(new Color(180, 100, 0));
        g.drawLine(x + size / 2, y + 4, x + size / 2 + 4, y);
        // Chispa
        g.setColor(Color.YELLOW);
        g.fillOval(x + size / 2 + 3, y - 2, 5, 5);
    }
}
