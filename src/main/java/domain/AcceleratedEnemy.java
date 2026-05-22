package domain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Enemigo Acelerado (Tipo A).
 * Se desplaza en línea recta (horizontal o vertical) al doble de velocidad
 * de los demás enemigos. Rebota en paredes. Dificultad: Alta.
 *
 * <p>Velocidad: 6 px/tick (el doble que un Enemy normal).</p>
 * <p>Tamaño: 16x16 px.</p>
 * <p>Color: azul con borde gris oscuro.</p>
 *
 * <p>Aparece en los niveles 3 y 4 de todos los modos de juego.</p>
 */
public class AcceleratedEnemy implements IMovable, ICollidable, IRenderable {

    /** Velocidad base de los enemigos normales × 2. */
    private static final int SPEED = 6;

    private int x;
    private int y;
    private final int size = 16;
    private int dx;
    private int dy;
    private final int boardWidth;
    private final int boardHeight;
    private List<Rectangle> walls = new ArrayList<>();
    private final List<Rectangle> forbiddenZones = new ArrayList<>();

    /**
     * @param x           Posición X inicial.
     * @param y           Posición Y inicial.
     * @param dx          Desplazamiento horizontal por tick (0 si es vertical).
     * @param dy          Desplazamiento vertical por tick (0 si es horizontal).
     * @param boardWidth  Ancho del tablero.
     * @param boardHeight Alto del tablero.
     */
    public AcceleratedEnemy(int x, int y, int dx, int dy, int boardWidth, int boardHeight) {
        this.x           = x;
        this.y           = y;
        this.dx          = dx != 0 ? (dx > 0 ? SPEED : -SPEED) : 0;
        this.dy          = dy != 0 ? (dy > 0 ? SPEED : -SPEED) : 0;
        this.boardWidth  = boardWidth;
        this.boardHeight = boardHeight;
    }

    public void setWalls(List<Rectangle> walls)    { this.walls = walls; }
    public void addForbiddenZone(Rectangle zone)   { forbiddenZones.add(zone); }

    @Override
    public void move() {
        int nextX = x + dx;
        int nextY = y + dy;
        Rectangle nextBounds = new Rectangle(nextX, nextY, size, size);
        boolean blocked = walls.stream().anyMatch(w -> w.intersects(nextBounds))
                       || forbiddenZones.stream().anyMatch(z -> z.intersects(nextBounds));

        if (blocked || nextX <= 0 || nextX + size >= boardWidth)  dx = -dx;
        if (blocked || nextY <= 0 || nextY + size >= boardHeight) dy = -dy;
        x += dx;
        y += dy;
    }

    @Override
    public Rectangle getBounds()                   { return new Rectangle(x, y, size, size); }

    @Override
    public boolean collidesWith(ICollidable other) { return getBounds().intersects(other.getBounds()); }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillOval(x, y, size, size);
        g.setColor(Color.DARK_GRAY);
        g.drawOval(x, y, size, size);
    }
}
