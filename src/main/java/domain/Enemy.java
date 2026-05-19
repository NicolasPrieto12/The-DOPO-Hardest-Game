package domain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Representa un enemigo de tipo "punto azul básico".
 * Se desplaza en línea recta y rebota al chocar con las paredes del tablero.
 * No puede entrar en las zonas seguras definidas como zonas prohibidas.
 * Implementa {@link IMovable}, {@link ICollidable} e {@link IRenderable}.
 */
public class Enemy implements IMovable, ICollidable, IRenderable {

    /** Posición horizontal actual del enemigo. */
    private int x;

    /** Posición vertical actual del enemigo. */
    private int y;

    /** Tamaño del diámetro del círculo que representa al enemigo en píxeles. */
    private final int size = 16;

    /** Desplazamiento horizontal por tick. Negativo = izquierda, positivo = derecha. */
    private int dx;

    /** Desplazamiento vertical por tick. Negativo = arriba, positivo = abajo. */
    private int dy;

    /** Ancho del tablero, usado para calcular el rebote en los bordes. */
    private final int boardWidth;

    /** Alto del tablero, usado para calcular el rebote en los bordes. */
    private final int boardHeight;

    /**
     * Lista de zonas prohibidas donde el enemigo no puede entrar.
     * Generalmente son las zonas seguras de inicio y final.
     */
    private final java.util.List<java.awt.Rectangle> forbiddenZones = new java.util.ArrayList<>();

    /** Lista de paredes del tablero para que el enemigo rebote en ellas. */
    private java.util.List<java.awt.Rectangle> walls = new java.util.ArrayList<>();

    /**
     * Crea un enemigo en la posición indicada con la dirección y velocidad dadas.
     *
     * @param x           Posición X inicial.
     * @param y           Posición Y inicial.
     * @param dx          Desplazamiento horizontal por tick.
     * @param dy          Desplazamiento vertical por tick.
     * @param boardWidth  Ancho del tablero para calcular rebotes.
     * @param boardHeight Alto del tablero para calcular rebotes.
     */
    public Enemy(int x, int y, int dx, int dy, int boardWidth, int boardHeight) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
    }

    /**
     * Agrega una zona prohibida que el enemigo no puede atravesar.
     *
     * @param zone Rectángulo que define el área prohibida.
     */
    public void addForbiddenZone(java.awt.Rectangle zone) {
        forbiddenZones.add(zone);
    }

    /**
     * Establece las paredes del tablero para que el enemigo rebote en ellas.
     *
     * @param walls Lista de rectángulos que representan las paredes.
     */
    public void setWalls(java.util.List<java.awt.Rectangle> walls) {
        this.walls = walls;
    }

    /**
     * Mueve al enemigo en su dirección actual.
     * Invierte la dirección si choca con los bordes, paredes o zonas prohibidas.
     */
    @Override
    public void move() {
        int nextX = x + dx;
        int nextY = y + dy;

        java.awt.Rectangle nextBounds = new java.awt.Rectangle(nextX, nextY, size, size);
        boolean forbidden = forbiddenZones.stream().anyMatch(z -> z.intersects(nextBounds));
        boolean wallHit   = walls.stream().anyMatch(w -> w.intersects(nextBounds));

        if (forbidden || wallHit || nextX <= 0 || nextX + size >= boardWidth)  dx = -dx;
        if (forbidden || wallHit || nextY <= 0 || nextY + size >= boardHeight) dy = -dy;

        x += dx;
        y += dy;
    }


    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }


    @Override
    public boolean collidesWith(ICollidable other) {
        return getBounds().intersects(other.getBounds());
    }

    /**
     * Dibuja al enemigo como un círculo azul con borde gris oscuro.
     *
     * @param g El objeto Graphics usado para dibujar.
     */
    @Override
    public void render(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillOval(x, y, size, size);
        g.setColor(Color.DARK_GRAY);
        g.drawOval(x, y, size, size);
    }
}
