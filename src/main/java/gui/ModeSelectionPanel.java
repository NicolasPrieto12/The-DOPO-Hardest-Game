package gui;

import domain.MachineProfile;

import javax.swing.*;
import java.awt.*;

/**
 * Panel de selección de perfil para el modo Player vs Machine.
 * Muestra dos botones: Aleatorio y Experto.
 * Al seleccionar uno inicia la partida con el perfil correspondiente.
 */
public class ModeSelectionPanel extends JPanel {

    /**
     * Crea el panel con los dos botones de selección de perfil.
     *
     * @param window La ventana principal para iniciar el juego al seleccionar.
     */
    public ModeSelectionPanel(GameGUI window) {
        setLayout(new GridBagLayout());
        setBackground(new Color(10, 40, 80));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 30, 20, 30);
        gbc.gridx = 0;
        gbc.fill  = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Jugador vs Máquina", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 36));
        title.setForeground(new Color(50, 220, 100));

        JLabel subtitle = new JLabel("Selecciona el perfil de la máquina:", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitle.setForeground(Color.WHITE);

        JButton btnRandom = createButton("🎲  Aleatorio");
        JButton btnExpert = createButton("🧠  Experto");
        JButton btnBack   = createButton("← Volver");
        btnBack.setBackground(new Color(80, 80, 80));

        btnRandom.addActionListener(e -> window.startPvM(MachineProfile.RANDOM));
        btnExpert.addActionListener(e -> window.startPvM(MachineProfile.EXPERT));
        btnBack.addActionListener(e   -> window.showMainMenu());

        gbc.gridy = 0; add(title,    gbc);
        gbc.gridy = 1; add(subtitle, gbc);
        gbc.gridy = 2; add(btnRandom, gbc);
        gbc.gridy = 3; add(btnExpert, gbc);
        gbc.gridy = 4; add(btnBack,   gbc);
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Serif", Font.BOLD, 18));
        btn.setBackground(new Color(20, 130, 60));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(280, 50));
        return btn;
    }
}
