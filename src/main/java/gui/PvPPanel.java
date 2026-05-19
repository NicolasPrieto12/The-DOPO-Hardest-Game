package gui;

import domain.*;
import domain.LevelPvP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Panel del modo Player vs Player.
 * Jugador 1 usa flechas, Jugador 2 usa WASD.
 * Muestra HUD con muertes, tiempo y nivel de ambos jugadores.
 */
public class PvPPanel extends JPanel implements ActionListener {

    /** Instancia del juego PvP. */
    private final GamePvP game;

    /** Timer del loop del juego. */
    private final Timer timer;

    /** Referencia a la ventana principal. */
    private final GameGUI window;

    public PvPPanel(GamePvP game, GameGUI window) {
        this.game   = game;
        this.window = window;
        setLayout(null);
        setPreferredSize(new Dimension(800, 500));
        setBackground(Color.LIGHT_GRAY);
        setFocusable(true);

        JButton btnRestart = new JButton("Reiniciar");
        btnRestart.setFont(new Font("Arial", Font.BOLD, 13));
        btnRestart.setBackground(new Color(200, 40, 40));
        btnRestart.setForeground(Color.WHITE);
        btnRestart.setFocusPainted(false);
        btnRestart.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRestart.setBounds(680, 460, 110, 30);
        btnRestart.addActionListener(e -> { game.restart(); requestFocusInWindow(); });
        add(btnRestart);

        timer = new Timer(16, this);
        timer.start();

        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e)  { handleKey(e.getKeyCode(), true); }
            @Override public void keyReleased(KeyEvent e) { handleKey(e.getKeyCode(), false); }
        });
    }

    /** Detiene el timer del loop. Se llama al salir del panel. */
    public void stopTimer() { timer.stop(); }

    /**
     * Procesa teclas para ambos jugadores.
     * Jugador 1: flechas. Jugador 2: WASD. P: pausa.
     */
    private void handleKey(int key, boolean pressed) {
        Player p1 = game.getPlayer1();
        Player p2 = game.getPlayer2();
        switch (key) {
            case KeyEvent.VK_UP    -> p1.setMovingUp(pressed);
            case KeyEvent.VK_DOWN  -> p1.setMovingDown(pressed);
            case KeyEvent.VK_LEFT  -> p1.setMovingLeft(pressed);
            case KeyEvent.VK_RIGHT -> p1.setMovingRight(pressed);
            case KeyEvent.VK_W     -> p2.setMovingUp(pressed);
            case KeyEvent.VK_S     -> p2.setMovingDown(pressed);
            case KeyEvent.VK_A     -> p2.setMovingLeft(pressed);
            case KeyEvent.VK_D     -> p2.setMovingRight(pressed);
            case KeyEvent.VK_P     -> { if (pressed) game.pause(); }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        game.update();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        LevelPvP level = game.getCurrentLevel();
        level.render(g);
        game.getPlayer1().render(g);
        game.getPlayer2().render(g);

        drawHUD(g);

        String state = game.getState();
        if (state.equals(GameState.WIN)) {
            drawCenteredMessage(g, "¡Ganó " + game.getWinner() + "!", Color.GREEN);
        } else if (state.equals(GameState.TIMEOUT)) {
            drawCenteredMessage(g, "¡Tiempo agotado! Ganó " + game.getWinner(), Color.RED);
        } else if (state.equals(GameState.PAUSED)) {
            drawCenteredMessage(g, "PAUSA", Color.ORANGE);
        } else if (!level.isCompleted()) {
            g.setColor(new Color(0, 80, 200));
            g.setFont(new Font("Arial", Font.BOLD, 13));
            g.drawString("Recoge todas las monedas para desbloquear la salida", 10, 480);
        }

        if (game.isPlayer1Finished()) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 13));
            g.drawString("J1: ¡Terminó!", 10, 460);
        }
        if (game.isPlayer2Finished()) {
            g.setColor(new Color(30, 100, 220));
            g.setFont(new Font("Arial", Font.BOLD, 13));
            g.drawString("J2: ¡Terminó!", 150, 460);
        }
    }

    private void drawHUD(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 13));

        g.setColor(Color.RED);
        g.drawString("J1 Muertes: " + game.getDeaths1(), 10, 20);

        g.setColor(new Color(30, 100, 220));
        g.drawString("J2 Muertes: " + game.getDeaths2(), 160, 20);

        int secs = game.getSecondsLeft();
        String tiempo = String.format("%d:%02d", secs / 60, secs % 60);
        g.setColor(secs <= 30 ? Color.RED : Color.BLACK);
        g.drawString("Tiempo: " + tiempo, 360, 20);

        g.setColor(Color.BLACK);
        g.drawString("Nivel: " + game.getCurrentLevel().getLevelNumber(), 700, 20);
    }

    private void drawCenteredMessage(Graphics g, String msg, Color color) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(color);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
    }
}
