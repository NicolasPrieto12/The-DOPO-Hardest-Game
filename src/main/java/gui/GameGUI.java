package gui;

import domain.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Ventana principal de la aplicación (GUI = Graphical User Interface).
 * Es el punto de entrada visual del juego. Extiende {@link JFrame} de Swing.
 *
 * <p>Responsabilidades:</p>
 * <ul>
 *   <li>Mostrar el menú principal al iniciar.</li>
 *   <li>Navegar entre el menú, el modo un jugador y el modo PvP.</li>
 *   <li>Construir los niveles usando {@link LevelFactory}.</li>
 *   <li>Gestionar el menú de opciones (Abrir, Guardar, Salir, etc.).</li>
 *   <li>Delegar el guardado y carga de partidas a {@link SaveManager}.</li>
 *   <li>Confirmar el cierre de la aplicación antes de salir.</li>
 * </ul>
 */
public class GameGUI extends JFrame {

    /** Panel del juego activo en modo un jugador. */
    private GamePanel gamePanel;

    /** Panel del juego activo en modo PvP. */
    private PvPPanel pvpPanel;

    /** Panel del juego activo en modo PvM. */
    private PvMPanel pvmPanel;

    /** Instancia del juego de un jugador activo. */
    private Game currentGame;

    /** Instancia del juego PvP activo. */
    private GamePvP currentPvP;

    /** Instancia del juego PvM activo. */
    private GamePvM currentPvM;

    /**
     * Crea y muestra la ventana principal.
     */
    public GameGUI() {
        setTitle("The DOPO Hardest Game 2026");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) { confirmExit(); }
        });

        showMainMenu();
        setJMenuBar(buildMenuBar());
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /** Muestra el menú principal. */
    public void showMainMenu() {
        if (gamePanel != null) { gamePanel.stopTimer(); gamePanel = null; }
        if (pvpPanel  != null) { pvpPanel.stopTimer();  pvpPanel  = null; }
        if (pvmPanel  != null) { pvmPanel.stopTimer();  pvmPanel  = null; }
        getContentPane().removeAll();
        MainMenuPanel menu = new MainMenuPanel(this);
        menu.setPreferredSize(new Dimension(800, 500));
        add(menu);
        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    /** Inicia el modo un jugador con los dos niveles. */
    public void startGame() {
        Player player = new Player(0, 0, PlayerType.RED);
        Level  level1 = LevelFactory.buildSingleLevel1(player);
        Level  level2 = LevelFactory.buildSingleLevel2(player);

        Game.resetInstance();
        currentGame = Game.getInstance(player, List.of(level1, level2));
        currentGame.start();

        getContentPane().removeAll();
        gamePanel = new GamePanel(currentGame, this);
        add(gamePanel);
        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
        gamePanel.requestFocusInWindow();
    }

    /** Inicia el modo PvP con los dos niveles. */
    public void startPvP() {
        Player p1 = new Player(0, 0, PlayerType.RED);
        Player p2 = new Player(0, 0, PlayerType.RED);
        LevelPvP l1 = LevelFactory.buildPvPLevel1(p1, p2);
        LevelPvP l2 = LevelFactory.buildPvPLevel2(p1, p2);

        currentPvP = new GamePvP(p1, p2, List.of(l1, l2));
        currentPvP.start();

        getContentPane().removeAll();
        pvpPanel = new PvPPanel(currentPvP, this);
        add(pvpPanel);
        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
        pvpPanel.requestFocusInWindow();
    }

    /** Muestra el panel de selección de perfil para el modo PvM. */
    public void showModeSelection() {
        if (gamePanel != null) { gamePanel.stopTimer(); gamePanel = null; }
        if (pvpPanel  != null) { pvpPanel.stopTimer();  pvpPanel  = null; }
        if (pvmPanel  != null) { pvmPanel.stopTimer();  pvmPanel  = null; }
        getContentPane().removeAll();
        ModeSelectionPanel panel = new ModeSelectionPanel(this);
        panel.setPreferredSize(new Dimension(800, 500));
        add(panel);
        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    /**
     * Inicia el modo PvM con el perfil de máquina indicado.
     *
     * @param profile Perfil de la máquina (RANDOM o EXPERT).
     */
    public void startPvM(MachineProfile profile) {
        Player        player  = new Player(0, 0, PlayerType.RED);
        MachinePlayer machine = new MachinePlayer(0, 0, profile);
        LevelPvP l1 = LevelFactory.buildPvMLevel1(player, machine);
        LevelPvP l2 = LevelFactory.buildPvMLevel2(player, machine);

        currentPvM = new GamePvM(player, machine, List.of(l1, l2));
        currentPvM.start();

        getContentPane().removeAll();
        pvmPanel = new PvMPanel(currentPvM, this);
        add(pvmPanel);
        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
        pvmPanel.requestFocusInWindow();
    }

    /** Construye la barra de menú con todas las opciones. */
    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menú");

        JMenuItem open       = new JMenuItem("Abrir");
        JMenuItem save       = new JMenuItem("Guardar");
        JMenuItem saveAs     = new JMenuItem("Guardar como");
        JMenuItem export     = new JMenuItem("Exportar");
        JMenuItem exportAs   = new JMenuItem("Exportar como");
        JMenuItem backToMenu = new JMenuItem("Salir al menú principal");
        JMenuItem exit       = new JMenuItem("Salir");

        open.addActionListener(e -> loadGame());
        save.addActionListener(e -> saveGame(false));
        saveAs.addActionListener(e -> saveGame(true));

        export.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Esta en construccion", "Exportar", JOptionPane.INFORMATION_MESSAGE));
        exportAs.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Esta en construccion", "Exportar como", JOptionPane.INFORMATION_MESSAGE));

        backToMenu.addActionListener(e -> showMainMenu());
        exit.addActionListener(e -> confirmExit());

        menu.add(open);
        menu.add(save);
        menu.add(saveAs);
        menu.addSeparator();
        menu.add(export);
        menu.add(exportAs);
        menu.addSeparator();
        menu.add(backToMenu);
        menu.add(exit);

        menuBar.add(menu);
        return menuBar;
    }

    /** Guarda la partida activa. */
    private void saveGame(boolean forceChooser) {
        if (currentGame == null && currentPvP == null) {
            JOptionPane.showMessageDialog(this, "No hay partida activa para guardar.",
                    "Guardar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar partida");
        chooser.setSelectedFile(new File("partida.dopo"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String path = chooser.getSelectedFile().getAbsolutePath();
                if (currentGame != null) SaveManager.saveGame(currentGame, path);
                else                     SaveManager.saveGamePvP(currentPvP, path);
                JOptionPane.showMessageDialog(this, "Partida guardada correctamente.",
                        "Guardar", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Carga una partida desde archivo. */
    private void loadGame() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Abrir partida");
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        String path = chooser.getSelectedFile().getAbsolutePath();
        String mode = SaveManager.readMode(path);

        try {
            if ("single".equals(mode)) {
                startGame();
                SaveManager.loadGame(currentGame, path);
                JOptionPane.showMessageDialog(this, "Partida cargada correctamente.",
                        "Abrir", JOptionPane.INFORMATION_MESSAGE);
            } else if ("pvp".equals(mode)) {
                startPvP();
                SaveManager.loadGamePvP(currentPvP, path);
                JOptionPane.showMessageDialog(this, "Partida PvP cargada correctamente.",
                        "Abrir", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Archivo de partida no reconocido.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Muestra confirmación antes de salir. */
    private void confirmExit() {
        Object[] options = {"Sí", "No"};
        int choice = JOptionPane.showOptionDialog(this,
                "¿Seguro que te quieres salir?", "Salir",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[1]);
        if (choice == 0) System.exit(0);
    }

    /** Punto de entrada de la aplicación. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameGUI::new);
    }
}
