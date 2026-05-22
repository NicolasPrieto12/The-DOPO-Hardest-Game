package domain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Enemigo Deslizador Vertical (Tipo V).
 * Se desplaza exclusivamente en línea recta vertical.
 * Rebota al chocar con paredes superior e inferior. Dificultad: Baja.
 *
 * <p>Velocidad: configurable (normalmente 2 px/tick).</p>
 * <p>Tamaño: 16x16 px.</p>
 * <p>Color: azul con borde gris oscuro.</p>
 *
 * <p>Aparece en el nivel 3 de todos los modos de juego (5 instancias).</p>
 */
public class SliderEnemy implements IMovable, ICollidable, IRenderable {

    private int x;
    private int y;
    private final int size = 16;
    private int dy;
    private final int boardHeight;
    private List<Rectangle> walls = new ArrayList<>();
    private final List<Rectangle> forbiddenZones = new ArrayList<>();

    public SliderEnemy(int x, int y, int dy, int boardHeight) {
        this.x           = x;
        this.y           = y;
        this.dy          = dy;
        this.boardHeight = boardHeight;
    }

    public void setWalls(List<Rectangle> walls)       { this.walls = walls; }
    public void addForbiddenZone(Rectangle zone)      { forbiddenZones.add(zone); }

    @Override
    public void move() {
        int nextY = y + dy;
        Rectangle nextBounds = new Rectangle(x, nextY, size, size);
        boolean blocked = walls.stream().anyMatch(w -> w.intersects(nextBounds))
                       || forbiddenZones.stream().anyMatch(z -> z.intersects(nextBounds))
                       || nextY <= 0 || nextY + size >= boardHeight;
        if (blocked) dy = -dy;
        y += dy;
    }

    @Override
    public Rectangle getBounds()                      { return new Rectangle(x, y, size, size); }

    @Override
    public boolean collidesWith(ICollidable other)    { return getBounds().intersects(other.getBounds()); }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillOval(x, y, size, size);
        g.setColor(Color.DARK_GRAY);
        g.drawOval(x, y, size, size);
    }
}
