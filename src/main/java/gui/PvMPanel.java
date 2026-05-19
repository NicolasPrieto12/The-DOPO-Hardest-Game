package gui;

import domain.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Panel del modo Player vs Machine.
 * El jugador humano usa flechas. La máquina se mueve automáticamente.
 * Muestra HUD con muertes, tiempo, nivel y perfil de la máquina.
 */
public class PvMPanel extends JPanel implements ActionListener {

    /** Instancia del juego PvM. */
    private final GamePvM game;

    /** Timer del loop del juego. */
    private final Timer timer;

    /** Referencia a la ventana principal. */
    private final GameGUI window;

    /**
     * Crea el panel PvM.
     *
     * @param game   La instancia del juego PvM.
     * @param window La ventana principal.
     */
    public PvMPanel(GamePvM game, GameGUI window) {
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

    /** Detiene el timer del loop. */
    public void stopTimer() { timer.stop(); }

    private void handleKey(int key, boolean pressed) {
        Player p = game.getPlayer();
        switch (key) {
            case KeyEvent.VK_UP    -> p.setMovingUp(pressed);
            case KeyEvent.VK_DOWN  -> p.setMovingDown(pressed);
            case KeyEvent.VK_LEFT  -> p.setMovingLeft(pressed);
            case KeyEvent.VK_RIGHT -> p.setMovingRight(pressed);
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
        game.getPlayer().render(g);
        game.getMachine().render(g);

        drawHUD(g);

        String state = game.getState();
        if (state.equals(GameState.WIN)) {
            drawCenteredMessage(g, "¡Ganó " + game.getWinner() + "!", Color.GREEN);
        } else if (state.equals(GameState.TIMEOUT)) {
            drawCenteredMessage(g, "¡Tiempo agotado!", Color.RED);
        } else if (state.equals(GameState.PAUSED)) {
            drawCenteredMessage(g, "PAUSA", Color.ORANGE);
        } else if (!level.isCompleted()) {
            g.setColor(new Color(0, 80, 200));
            g.setFont(new Font("Arial", Font.BOLD, 13));
            g.drawString("Recoge todas las monedas para desbloquear la salida", 10, 480);
        }

        if (!game.getWinner().isEmpty() && !state.equals(GameState.WIN)) {
            g.setColor(new Color(0, 80, 200));
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString("Último ganador: " + game.getWinner(), 300, 460);
        }
    }

    private void drawHUD(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 13));

        g.setColor(Color.RED);
        g.drawString("Jugador muertes: " + game.getDeathsPlayer(), 10, 20);

        g.setColor(new Color(30, 100, 220));
        String perfil = game.getMachine().getProfile() == MachineProfile.EXPERT ? "Experto" : "Aleatorio";
        g.drawString("Máquina (" + perfil + ") muertes: " + game.getDeathsMachine(), 200, 20);

        int secs = game.getSecondsLeft();
        String tiempo = String.format("%d:%02d", secs / 60, secs % 60);
        g.setColor(secs <= 30 ? Color.RED : Color.BLACK);
        g.drawString("Tiempo: " + tiempo, 560, 20);

        g.setColor(Color.BLACK);
        g.drawString("Nivel: " + game.getCurrentLevel().getLevelNumber(), 720, 20);
    }

    private void drawCenteredMessage(Graphics g, String msg, Color color) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(color);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
    }
}
