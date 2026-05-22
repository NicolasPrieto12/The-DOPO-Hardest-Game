package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Panel de instrucciones del juego.
 * Muestra 3 páginas navegables: modo 1 jugador, PvP y PvM.
 * Cada página incluye los símbolos de los ítems y sus explicaciones.
 */
public class InstructionsPanel extends JPanel {

    private final GameGUI window;
    private int currentPage = 0;

    private final JPanel contentPanel;
    private final JButton btnNext;
    private final JButton btnPrev;
    private final JLabel  pageLabel;

    private static final String[] PAGE_TITLES = {
        "Modo 1 Jugador",
        "Modo Jugador vs Jugador (PvP)",
        "Modo Jugador vs Máquina (PvM)"
    };

    public InstructionsPanel(GameGUI window) {
        this.window = window;
        setLayout(new BorderLayout());
        setBackground(new Color(10, 40, 80));

        // ── Título superior ──
        JLabel title = new JLabel("INSTRUCCIONES", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 32));
        title.setForeground(new Color(50, 220, 100));
        title.setBorder(BorderFactory.createEmptyBorder(18, 0, 8, 0));
        add(title, BorderLayout.NORTH);

        // ── Contenido central (se reemplaza al cambiar página) ──
        contentPanel = new JPanel();
        contentPanel.setBackground(new Color(10, 40, 80));
        add(contentPanel, BorderLayout.CENTER);

        // ── Barra inferior de navegación ──
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        navBar.setBackground(new Color(10, 40, 80));

        btnPrev  = navButton("◀ Anterior");
        pageLabel = new JLabel("", SwingConstants.CENTER);
        pageLabel.setForeground(Color.WHITE);
        pageLabel.setFont(new Font("Arial", Font.BOLD, 13));
        pageLabel.setPreferredSize(new Dimension(160, 30));
        btnNext  = navButton("Siguiente ▶");

        JButton btnMenu = navButton("⬅ Menú Principal");
        btnMenu.setBackground(new Color(180, 40, 40));
        btnMenu.addActionListener(e -> window.showMainMenu());

        btnPrev.addActionListener(e -> changePage(-1));
        btnNext.addActionListener(e -> changePage(+1));

        navBar.add(btnMenu);
        navBar.add(Box.createHorizontalStrut(20));
        navBar.add(btnPrev);
        navBar.add(pageLabel);
        navBar.add(btnNext);
        add(navBar, BorderLayout.SOUTH);

        renderPage();
    }

    private void changePage(int delta) {
        currentPage = Math.max(0, Math.min(2, currentPage + delta));
        renderPage();
    }

    private void renderPage() {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());

        // Subtítulo de página
        JLabel sub = new JLabel(PAGE_TITLES[currentPage], SwingConstants.CENTER);
        sub.setFont(new Font("Serif", Font.BOLD, 22));
        sub.setForeground(new Color(255, 210, 50));
        sub.setBorder(BorderFactory.createEmptyBorder(4, 0, 10, 0));
        contentPanel.add(sub, BorderLayout.NORTH);

        // Tabla de ítems
        JPanel itemsPanel = buildItemsPanel(currentPage);
        JScrollPane scroll = new JScrollPane(itemsPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(15, 55, 100));
        contentPanel.add(scroll, BorderLayout.CENTER);

        // Navegación
        btnPrev.setEnabled(currentPage > 0);
        btnNext.setEnabled(currentPage < 2);
        pageLabel.setText("Página " + (currentPage + 1) + " / 3");

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel buildItemsPanel(int page) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(15, 55, 100));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        switch (page) {
            case 0 -> buildSinglePlayerPage(panel);
            case 1 -> buildPvPPage(panel);
            case 2 -> buildPvMPage(panel);
        }
        return panel;
    }

    // ─────────────────────────────────────────────
    // PÁGINA 1 — MODO 1 JUGADOR
    // ─────────────────────────────────────────────
    private void buildSinglePlayerPage(JPanel p) {
        addSection(p, "🎮  Controles", "Usa las teclas de flecha (↑ ↓ ← →) o W A S D para moverte.");
        addSection(p, "🎯  Objetivo",
            "Recoge TODAS las monedas amarillas del nivel para desbloquear la salida (zona verde derecha).\n" +
            "Llega a la zona verde para completar el nivel. ¡Tienes 3 minutos por nivel!");

        addItemRow(p, buildCoinIcon(Color.YELLOW, Color.ORANGE),
            "Moneda amarilla",
            "Recoge todas para desbloquear la salida y avanzar al siguiente nivel.");

        addItemRow(p, buildCoinIcon(new Color(30, 100, 220), new Color(0, 60, 160)),
            "Moneda azul (Inky)",
            "Al recogerla te transformas en Inky: tu cuadrado crece y tu velocidad aumenta 1.5×.");

        addItemRow(p, buildCoinIcon(new Color(0, 200, 80), new Color(0, 120, 40)),
            "Moneda verde (Clyde)",
            "Al recogerla te transformas en Clyde (cuadrado verde resistente).\n" +
            "El primer golpe de un enemigo NO te mata: absorbes el impacto pero pierdes velocidad.\n" +
            "El segundo golpe sí reinicia tu posición y las monedas.");

        addItemRow(p, buildHeartIcon(),
            "Fuente de vida ❤",
            "Al tocarla obtienes un escudo extra: el próximo golpe de enemigo solo reinicia tu posición\n" +
            "sin cambiar tu velocidad ni tipo. El segundo golpe reinicia todo normalmente.");

        addItemRow(p, buildCheckpointIcon(),
            "Checkpoint (CP)",
            "Zona verde con la etiqueta CP. Al pisarla guarda tu progreso: si mueres después,\n" +
            "reapareces en el checkpoint conservando las monedas que ya recogiste antes de llegar a él.\n" +
            "Si mueres antes de activarlo, vuelves al inicio y se reinician todas las monedas.");

        addItemRow(p, buildBombIcon(),
            "Bomba 💣",
            "¡No la toques! Es estática pero destruye al instante a cualquier jugador que pase por ella.\n" +
            "Reinicia tu posición y las monedas.");

        addItemRow(p, buildEnemyIcon(Color.BLUE),
            "Enemigo azul",
            "Se mueve por el tablero y rebota en las paredes. Si te toca, mueres (o pierdes el escudo).");

        addSection(p, "💀  Contador de muertes",
            "Cada vez que un enemigo o bomba te elimina, el contador sube. No afecta si puedes ganar,\n" +
            "pero refleja tu desempeño en el nivel.");

        addSection(p, "⏱  Tiempo límite",
            "Tienes exactamente 3 minutos (3:00) para completar cada nivel.\n" +
            "Si el tiempo llega a 0:00 antes de terminar, ¡pierdes el nivel!");
    }

    // ─────────────────────────────────────────────
    // PÁGINA 2 — MODO PVP
    // ─────────────────────────────────────────────
    private void buildPvPPage(JPanel p) {
        addSection(p, "🎮  Controles",
            "Jugador 1 (rojo): teclas de flecha  ↑ ↓ ← →\n" +
            "Jugador 2 (rojo): teclas  W  A  S  D");

        addSection(p, "🎯  Objetivo",
            "Jugador 1 inicia en el lado IZQUIERDO y debe llegar al lado DERECHO.\n" +
            "Jugador 2 inicia en el lado DERECHO y debe llegar al lado IZQUIERDO.\n" +
            "El primero en recoger todas las monedas y llegar a su zona gana el nivel.\n" +
            "¡Tienen 3 minutos por nivel!");

        addItemRow(p, buildCoinIcon(Color.YELLOW, Color.ORANGE),
            "Moneda amarilla",
            "Recoge todas para desbloquear la salida. Ambos jugadores comparten las mismas monedas.");

        addItemRow(p, buildCoinIcon(new Color(30, 100, 220), new Color(0, 60, 160)),
            "Moneda azul (Inky)",
            "Transforma al jugador que la recoge: más velocidad y tamaño 1.5×.");

        addItemRow(p, buildCoinIcon(new Color(0, 200, 80), new Color(0, 120, 40)),
            "Moneda verde (Clyde)",
            "Da escudo al jugador que la recoge. Primer golpe absorbido (pierde velocidad),\n" +
            "segundo golpe reinicia posición y monedas.");

        addItemRow(p, buildHeartIcon(),
            "Fuente de vida ❤",
            "Escudo extra: el próximo golpe solo reinicia posición sin cambiar velocidad ni tipo.");

        addItemRow(p, buildBombIcon(),
            "Bomba 💣",
            "Elimina al jugador que la toque. ¡Cuidado, son estáticas!");

        addItemRow(p, buildEnemyIcon(Color.BLUE),
            "Enemigo azul",
            "Se mueve por el tablero. Si toca a un jugador, lo elimina (o consume su escudo).");

        addSection(p, "⏱  Tiempo límite",
            "3 minutos por nivel. Si se acaba el tiempo sin que nadie complete el nivel, se declara empate.");
    }

    // ─────────────────────────────────────────────
    // PÁGINA 3 — MODO PVM
    // ─────────────────────────────────────────────
    private void buildPvMPage(JPanel p) {
        addSection(p, "🎮  Controles",
            "Jugador humano: teclas de flecha  ↑ ↓ ← →\n" +
            "La máquina se mueve sola automáticamente.");

        addSection(p, "🎯  Objetivo",
            "El jugador humano inicia en el lado IZQUIERDO y debe llegar al lado DERECHO.\n" +
            "La máquina inicia en el lado DERECHO y debe llegar al lado IZQUIERDO.\n" +
            "El primero en recoger todas las monedas y llegar a su zona gana. ¡3 minutos por nivel!");

        addSection(p, "🤖  Perfiles de la máquina",
            "• Aleatorio (fácil): la máquina se mueve de forma aleatoria con algo de tendencia hacia\n" +
            "  las monedas. Es más fácil de superar.\n\n" +
            "• Experto (difícil): la máquina va directamente a la moneda más cercana y luego a su meta\n" +
            "  por la ruta más corta. ¡Es muy difícil de vencer!");

        addItemRow(p, buildCoinIcon(Color.YELLOW, Color.ORANGE),
            "Moneda amarilla",
            "Recoge todas para desbloquear la salida. La máquina también las puede recoger.");

        addItemRow(p, buildCoinIcon(new Color(30, 100, 220), new Color(0, 60, 160)),
            "Moneda azul (Inky)",
            "Transforma al jugador (o máquina) que la recoge: más velocidad y tamaño 1.5×.");

        addItemRow(p, buildCoinIcon(new Color(0, 200, 80), new Color(0, 120, 40)),
            "Moneda verde (Clyde)",
            "Da escudo al que la recoge. Primer golpe absorbido (pierde velocidad),\n" +
            "segundo golpe reinicia posición y monedas.");

        addItemRow(p, buildHeartIcon(),
            "Fuente de vida ❤",
            "Escudo extra: el próximo golpe solo reinicia posición sin cambiar velocidad ni tipo.");

        addItemRow(p, buildBombIcon(),
            "Bomba 💣",
            "Elimina a cualquier elemento (jugador o máquina) que la toque.");

        addItemRow(p, buildEnemyIcon(Color.BLUE),
            "Enemigo azul",
            "Se mueve por el tablero. Elimina al jugador o máquina que toque.");

        addSection(p, "⏱  Tiempo límite",
            "3 minutos por nivel. Si se acaba el tiempo sin ganador, se declara empate.");
    }

    // ─────────────────────────────────────────────
    // HELPERS DE CONSTRUCCIÓN DE UI
    // ─────────────────────────────────────────────

    private void addSection(JPanel parent, String title, String body) {
        parent.add(Box.createVerticalStrut(10));
        JLabel lbl = new JLabel("<html><b>" + title + "</b></html>");
        lbl.setForeground(new Color(255, 210, 50));
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        parent.add(lbl);

        JTextArea ta = new JTextArea(body);
        ta.setEditable(false);
        ta.setOpaque(false);
        ta.setForeground(Color.WHITE);
        ta.setFont(new Font("Arial", Font.PLAIN, 13));
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setAlignmentX(LEFT_ALIGNMENT);
        ta.setMaximumSize(new Dimension(700, 200));
        parent.add(ta);
        parent.add(Box.createVerticalStrut(4));
    }

    private void addItemRow(JPanel parent, JComponent icon, String name, String desc) {
        parent.add(Box.createVerticalStrut(10));
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(720, 70));

        icon.setPreferredSize(new Dimension(36, 36));
        row.add(icon, BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(new Color(255, 210, 50));
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));

        JTextArea descArea = new JTextArea(desc);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setForeground(Color.WHITE);
        descArea.setFont(new Font("Arial", Font.PLAIN, 12));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);

        text.add(nameLabel);
        text.add(descArea);
        row.add(text, BorderLayout.CENTER);
        parent.add(row);
    }

    // ─────────────────────────────────────────────
    // ICONOS DIBUJADOS
    // ─────────────────────────────────────────────

    private JComponent buildCoinIcon(Color fill, Color border) {
        return new JComponent() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(fill);
                g.fillOval(4, 4, 28, 28);
                g.setColor(border);
                g.drawOval(4, 4, 28, 28);
            }
        };
    }

    private JComponent buildHeartIcon() {
        return new JComponent() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(220, 30, 60));
                g.fillOval(4,  6, 14, 12);
                g.fillOval(18, 6, 14, 12);
                int[] px = {4, 25, 32};
                int[] py = {14, 34, 14};
                g.fillPolygon(px, py, 3);
                g.setColor(new Color(140, 0, 30));
                g.drawOval(4,  6, 14, 12);
                g.drawOval(18, 6, 14, 12);
                g.drawPolygon(px, py, 3);
            }
        };
    }

    private JComponent buildBombIcon() {
        return new JComponent() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(Color.BLACK);
                g.fillOval(6, 10, 24, 24);
                g.setColor(new Color(180, 100, 0));
                g.drawLine(18, 10, 24, 4);
                g.setColor(Color.YELLOW);
                g.fillOval(23, 2, 6, 6);
                g.setColor(Color.DARK_GRAY);
                g.drawOval(6, 10, 24, 24);
            }
        };
    }

    private JComponent buildEnemyIcon(Color color) {
        return new JComponent() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(color);
                g.fillOval(6, 6, 24, 24);
                g.setColor(Color.DARK_GRAY);
                g.drawOval(6, 6, 24, 24);
            }
        };
    }

    private JComponent buildCheckpointIcon() {
        return new JComponent() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(140, 200, 140));
                g.fillRect(2, 2, 32, 32);
                g.setColor(new Color(60, 120, 60));
                g.drawRect(2, 2, 32, 32);
                g.setColor(new Color(40, 100, 40));
                g.setFont(new Font("Arial", Font.BOLD, 11));
                g.drawString("CP", 8, 22);
            }
        };
    }

    private JButton navButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(new Color(20, 130, 60));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
