package domain;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Representa la zona final del nivel, la meta que el jugador debe alcanzar
 * después de recoger todas las monedas para completar el nivel.
 * Se dibuja como un rectángulo verde oscuro.
 * Extiende {@link Zone}.
 */
public class EndZone extends Zone {

    /**
     * Crea la zona final con la posición y dimensiones indicadas.
     *
     * @param x      Posición X de la zona.
     * @param y      Posición Y de la zona.
     * @param width  Ancho de la zona.
     * @param height Alto de la zona.
     */
    public EndZone(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    /**
     * Verifica si el jugador ha llegado a la zona final para completar el nivel.
     *
     * @param player El jugador a verificar.
     * @return true si el jugador está dentro de la zona final, false en caso contrario.
     */
    public boolean checkLevelComplete(Player player) {
        return contains(player);
    }

    /**
     * Dibuja la zona final como un rectángulo verde oscuro con borde gris oscuro.
     *
     * @param g El objeto Graphics usado para dibujar.
     */
    @Override
    public void render(Graphics g) {
        g.setColor(new Color(50, 180, 50));
        g.fillRect(x, y, width, height);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(x, y, width, height);
    }
}
