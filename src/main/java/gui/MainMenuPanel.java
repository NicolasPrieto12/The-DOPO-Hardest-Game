package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Panel del menú principal del juego.
 * Muestra el título del juego y los tres botones de modalidad:
 * un jugador, jugador vs jugador y jugador vs máquina.
 * Las dos últimas modalidades están en construcción.
 */
public class MainMenuPanel extends JPanel {

    /**
     * Crea el panel del menú principal con el título y los botones de modalidad.
     *
     * @param window La ventana principal, usada para navegar al juego al seleccionar una modalidad.
     */
    public MainMenuPanel(GameGUI window) {
        setLayout(new GridBagLayout());
        setBackground(new Color(10, 40, 80));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("The DOPO Hardest Game", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 42));
        title.setForeground(new Color(50, 220, 100));

        JButton btnSingle = createButton("1 Jugador");
        JButton btnPvP    = createButton("Jugador vs Jugador");
        JButton btnPvM    = createButton("Jugador vs Máquina");
        JButton btnInstr  = createButton("Instrucciones");

        btnSingle.addActionListener(e -> window.startGame());
        btnPvP.addActionListener(e -> window.startPvP());
        btnPvM.addActionListener(e -> window.showModeSelection());
        btnInstr.addActionListener(e -> window.showInstructions());

        gbc.gridy = 0; add(title, gbc);
        gbc.gridy = 1; add(btnSingle, gbc);
        gbc.gridy = 2; add(btnPvP, gbc);
        gbc.gridy = 3; add(btnPvM, gbc);
        gbc.gridy = 4; add(btnInstr, gbc);
    }

    /**
     * Crea un botón estilizado con el texto indicado para el menú principal.
     *
     * @param text El texto que mostrará el botón.
     * @return El botón configurado con el estilo del menú.
     */
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Serif", Font.BOLD, 18));
        btn.setBackground(new Color(20, 130, 60));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(280, 45));
        return btn;
    }
}
