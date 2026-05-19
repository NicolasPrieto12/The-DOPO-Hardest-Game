package domain;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Zona segura intermedia del nivel.
 * Cuando el jugador la alcanza, guarda su posición como checkpoint.
 * Si el jugador muere después de activarla, reaparece aquí en lugar
 * de volver al inicio, conservando las monedas ya recogidas.
 * Se dibuja en verde tenue para distinguirse de las zonas de inicio y final.
 * Extiende {@link Zone}.
 */
public class CheckpointZone extends Zone {

    /** Indica si este checkpoint ya fue activado por el jugador. */
    private boolean activated;

    /**
     * Crea una zona de checkpoint en la posición y dimensiones indicadas.
     *
     * @param x      Posición X de la zona.
     * @param y      Posición Y de la zona.
     * @param width  Ancho de la zona.
     * @param height Alto de la zona.
     */
    public CheckpointZone(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.activated = false;
    }

    /**
     * Verifica si el jugador está en la zona y si es así activa el checkpoint,
     * guardando la posición del jugador.
     *
     * @param player El jugador a verificar.
     */
    public void checkAndActivate(Player player) {
        if (contains(player)) {
            activated = true;
            player.saveCheckpoint(
                x + width  / 2 - player.getSize() / 2,
                y + height / 2 - player.getSize() / 2
            );
        }
    }

    /** @return true si el checkpoint ya fue activado. */
    public boolean isActivated() { return activated; }

    /** Resetea el checkpoint a no activado. */
    public void reset() { activated = false; }

    /**
     * Dibuja la zona como un rectángulo verde tenue.
     * Cuando está activado se dibuja más brillante para indicar que está activo.
     *
     * @param g El objeto Graphics usado para dibujar.
     */
    @Override
    public void render(Graphics g) {
        g.setColor(activated ? new Color(80, 180, 80) : new Color(140, 200, 140));
        g.fillRect(x, y, width, height);
        g.setColor(new Color(60, 120, 60));
        g.drawRect(x, y, width, height);

        g.setColor(new Color(40, 100, 40));
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 10));
        g.drawString("CP", x + width / 2 - 8, y + height / 2 + 4);
    }
}
