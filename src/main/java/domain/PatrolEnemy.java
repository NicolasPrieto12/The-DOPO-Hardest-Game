package domain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Enemigo patrullero que sigue una ruta circular o geométrica predefinida.
 * Se mueve entre una serie de puntos de control (waypoints) en orden,
 * avanzando hacia el siguiente punto cuando llega al actual.
 * Implementa {@link IMovable}, {@link ICollidable} e {@link IRenderable}.
 */
public class PatrolEnemy implements IMovable, ICollidable, IRenderable {

    /** Posición horizontal actual del enemigo. */
    private int x;

    /** Posición vertical actual del enemigo. */
    private int y;

    /** Tamaño del diámetro del círculo que representa al enemigo en píxeles. */
    private final int size = 16;

    /** Velocidad de desplazamiento en píxeles por tick. */
    private final int speed;

    /** Lista de puntos de control que definen la ruta de patrulla. */
    private final int[][] waypoints;

    /** Índice del punto de control actual hacia el que se dirige el enemigo. */
    private int currentWaypoint;

    /** Lista de zonas prohibidas que el patrullero no puede atravesar. */
    private final java.util.List<java.awt.Rectangle> forbiddenZones = new java.util.ArrayList<>();

    /**
     * Crea un enemigo patrullero con la ruta y velocidad indicadas.
     *
     * @param x         Posición X inicial.
     * @param y         Posición Y inicial.
     * @param speed     Velocidad de desplazamiento en píxeles por tick.
     * @param waypoints Arreglo de puntos [x, y] que definen la ruta de patrulla.
     */
    public PatrolEnemy(int x, int y, int speed, int[][] waypoints) {
        this.x              = x;
        this.y              = y;
        this.speed          = speed;
        this.waypoints      = waypoints;
        this.currentWaypoint = 0;
    }

    /**
     * Agrega una zona prohibida que el patrullero no puede atravesar.
     *
     * @param zone Rectángulo que define el área prohibida.
     */
    public void addForbiddenZone(java.awt.Rectangle zone) {
        forbiddenZones.add(zone);
    }

    /**
     * Mueve al enemigo hacia el siguiente punto de control.
     * Si el siguiente paso entra en una zona prohibida, salta al siguiente waypoint.
     */
    @Override
    public void move() {
        int targetX = waypoints[currentWaypoint][0];
        int targetY = waypoints[currentWaypoint][1];

        int diffX = targetX - x;
        int diffY = targetY - y;

        if (Math.abs(diffX) <= speed && Math.abs(diffY) <= speed) {
            x = targetX;
            y = targetY;
            currentWaypoint = (currentWaypoint + 1) % waypoints.length;
        } else {
            double dist = Math.sqrt(diffX * diffX + diffY * diffY);
            int nextX = x + (int)(speed * diffX / dist);
            int nextY = y + (int)(speed * diffY / dist);

            java.awt.Rectangle nextBounds = new java.awt.Rectangle(nextX, nextY, size, size);
            boolean forbidden = forbiddenZones.stream().anyMatch(z -> z.intersects(nextBounds));

            if (!forbidden) {
                x = nextX;
                y = nextY;
            } else {
                // Si la siguiente posicion esta prohibida, salta al siguiente waypoint
                currentWaypoint = (currentWaypoint + 1) % waypoints.length;
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }

    /** {@inheritDoc} */
    @Override
    public boolean collidesWith(ICollidable other) {
        return getBounds().intersects(other.getBounds());
    }

    /**
     * Dibuja al enemigo patrullero como un círculo azul oscuro con borde gris.
     *
     * @param g El objeto Graphics usado para dibujar.
     */
    @Override
    public void render(Graphics g) {
        g.setColor(new Color(0, 50, 180));
        g.fillOval(x, y, size, size);
        g.setColor(Color.DARK_GRAY);
        g.drawOval(x, y, size, size);
    }
}
