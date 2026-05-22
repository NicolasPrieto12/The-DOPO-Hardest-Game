package gui;

import domain.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Panel principal del juego modo un jugador.
 * Dibuja el tablero, el jugador y el HUD en cada frame.
 * Captura la entrada del teclado para mover al jugador.
 */
public class GamePanel extends JPanel implements ActionListener {

    /** Instancia del juego. */
    private final Game game;

    /** Timer del loop del juego (~60fps). */
    private final Timer timer;

    /** Referencia a la ventana principal. */
    private final GameGUI window;

    public GamePanel(Game game, GameGUI window) {
        this.game   = game;
        this.window = window;
        setLayout(null);
        setPreferredSize(new Dimension(800, 500));
        setBackground(Color.LIGHT_GRAY);
        setFocusable(true);

        JButton btnSkip = new JButton("Pasar nivel");
        btnSkip.setFont(new Font("Arial", Font.BOLD, 13));
        btnSkip.setBackground(new Color(20, 130, 60));
        btnSkip.setForeground(Color.WHITE);
        btnSkip.setFocusPainted(false);
        btnSkip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSkip.setBounds(680, 425, 110, 30);
        btnSkip.addActionListener(e -> { game.nextLevel(); requestFocusInWindow(); });
        add(btnSkip);

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

    private void handleKey(int key, boolean pressed) {
        Player p = game.getPlayer();
        switch (key) {
            case KeyEvent.VK_UP,    KeyEvent.VK_W -> p.setMovingUp(pressed);
            case KeyEvent.VK_DOWN,  KeyEvent.VK_S -> p.setMovingDown(pressed);
            case KeyEvent.VK_LEFT,  KeyEvent.VK_A -> p.setMovingLeft(pressed);
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> p.setMovingRight(pressed);
            case KeyEvent.VK_P -> { if (pressed) game.pause(); }
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

        Level level = game.getCurrentLevel();
        level.render(g);
        game.getPlayer().render(g);

        drawHUD(g);

        String state = game.getState();
        if (state.equals(GameState.WIN)) {
            drawCenteredMessage(g, "¡Juego completado!", Color.GREEN);
        } else if (state.equals(GameState.TIMEOUT)) {
            drawCenteredMessage(g, "¡Has perdido! El reloj llegó a 0", Color.RED);
        } else if (state.equals(GameState.PAUSED)) {
            drawCenteredMessage(g, "PAUSA", Color.ORANGE);
        } else if (!level.isCompleted()) {
            g.setColor(new Color(0, 80, 200));
            g.setFont(new Font("Arial", Font.BOLD, 13));
            g.drawString("Recoge todas las monedas para desbloquear la salida", 10, 480);
        }
    }

    private void drawHUD(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Muertes: " + game.getDeaths(), 10, 20);

        int secs = game.getSecondsLeft();
        String tiempo = String.format("%d:%02d", secs / 60, secs % 60);
        g.setColor(secs <= 30 ? Color.RED : Color.BLACK);
        g.drawString("Tiempo: " + tiempo, 360, 20);

        g.setColor(Color.BLACK);
        g.drawString("Nivel: " + game.getCurrentLevel().getLevelNumber(), 700, 20);

        Player p = game.getPlayer();
        if (p.getType() == PlayerType.BLUE) {
            g.setColor(new Color(30, 100, 220));
            g.drawString("★ Inky", 10, 40);
        } else if (p.getType() == PlayerType.GREEN) {
            g.setColor(new Color(0, 180, 60));
            g.drawString("★ Clyde" + (p.isShielded() ? " [Escudo]" : " [Sin escudo]"), 10, 40);
        }
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
