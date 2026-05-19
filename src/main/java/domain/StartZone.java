package domain;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Representa la zona de inicio del nivel, donde el jugador aparece al comenzar
 * y reaparece tras cada muerte.
 * Se dibuja como un rectángulo verde claro.
 * Extiende {@link Zone}.
 */
public class StartZone extends Zone {

    /**
     * Crea la zona de inicio con la posición y dimensiones indicadas.
     *
     * @param x      Posición X de la zona.
     * @param y      Posición Y de la zona.
     * @param width  Ancho de la zona.
     * @param height Alto de la zona.
     */
    public StartZone(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    /**
     * Reposiciona al jugador en el centro de la zona de inicio.
     * Se llama cuando el jugador muere o cuando se inicia o reinicia el nivel.
     *
     * @param player El jugador que será reposicionado.
     */
    public void resetPlayer(Player player) {
        player.setPosition(
            x + width  / 2 - player.getSize() / 2,
            y + height / 2 - player.getSize() / 2
        );
    }

    /**
     * Dibuja la zona de inicio como un rectángulo verde claro con borde gris oscuro.
     *
     * @param g El objeto Graphics usado para dibujar.
     */
    @Override
    public void render(Graphics g) {
        g.setColor(new Color(100, 200, 100));
        g.fillRect(x, y, width, height);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(x, y, width, height);
    }
}
