package juegos.piedra;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.net.URL;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * ============================================================
 * CLASE PanelSeleccionModo
 * ============================================================
 *
 * Pantalla de selección del modo de juego.
 * Muestra dos tarjetas:
 *  - Modo Pepe  (5 vidas)
 *  - Modo Dios  (infinito, rachas de 10 en 10)
 *
 * Al seleccionar un modo invoca el callback onModoSeleccionado.
 *
 * @version 1.0
 * ============================================================
 */
public class PanelSeleccionModo extends JPanel {

    private final Consumer<Integer> onModoSeleccionado;

    // =========================================================
    // Constructor
    // =========================================================

    /**
     * @param onModoSeleccionado Callback que recibe el modo elegido
     *                           (PanelPiedra.MODO_PEPE o MODO_DIOS).
     */
    public PanelSeleccionModo(Consumer<Integer> onModoSeleccionado) {
        this.onModoSeleccionado = onModoSeleccionado;
        inicializarComponentes();
    }

    // =========================================================
    // Construcción
    // =========================================================

    private void inicializarComponentes() {
        setBackground(new Color(28, 28, 35));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        add(Box.createVerticalGlue());

        crearBanner();

        add(Box.createVerticalStrut(10));

        JLabel lblSub = etiqueta("Elige tu modo de juego para comenzar",
                new Color(170, 170, 190), new Font("SansSerif", Font.ITALIC, 15));
        add(lblSub);

        add(Box.createVerticalStrut(28));

        // Tarjetas de modo
        JPanel panelTarjetas = new JPanel(new GridLayout(1, 2, 22, 0));
        panelTarjetas.setOpaque(false);
        panelTarjetas.setMaximumSize(new Dimension(580, 290));
        panelTarjetas.setAlignmentX(CENTER_ALIGNMENT);

        panelTarjetas.add(crearTarjeta(
                "🐸  Modo Pepe",
                "/images/Piedra_Papel_0_Tijeras/Pepe.png",
                "5 vidas para sobrevivir",
                "Si pierdes las 5... 💀 Game Over",
                new Color(45, 85, 45),
                new Color(65, 115, 65),
                PanelPiedra.MODO_PEPE
        ));

        panelTarjetas.add(crearTarjeta(
                "⚡  Modo Dios",
                "/images/Piedra_Papel_0_Tijeras/Te_gane.png",
                "Sin límite — ¡juega sin parar!",
                "Cada 10 victorias activas tu Racha 🔥",
                new Color(85, 70, 20),
                new Color(130, 105, 30),
                PanelPiedra.MODO_DIOS
        ));

        add(panelTarjetas);
        add(Box.createVerticalGlue());
    }

    private void crearBanner() {
        // Imagen principal PPT
        try {
            URL url = getClass().getResource(
                    "/images/Piedra_Papel_0_Tijeras/Piedra_Papel_o_Tijeras.png");
            if (url != null) {
                Image img = new ImageIcon(url).getImage()
                        .getScaledInstance(190, -1, Image.SCALE_SMOOTH);
                JLabel lblBanner = new JLabel(new ImageIcon(img));
                lblBanner.setAlignmentX(CENTER_ALIGNMENT);
                add(lblBanner);
                add(Box.createVerticalStrut(10));
            }
        } catch (Exception ignored) {}

        JLabel lblTitulo = etiqueta("Piedra, Papel o Tijeras",
                Color.WHITE, new Font("SansSerif", Font.BOLD, 26));
        add(lblTitulo);
    }

    /**
     * Crea una tarjeta de selección de modo con imagen, descripción y botón.
     */
    private JPanel crearTarjeta(String titulo, String rutaImg,
                                 String desc1, String desc2,
                                 Color colorBase, Color colorHover,
                                 int modo) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(colorBase);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorHover, 2),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Imagen meme de la tarjeta
        try {
            URL url = getClass().getResource(rutaImg);
            if (url != null) {
                Image img = new ImageIcon(url).getImage()
                        .getScaledInstance(105, 105, Image.SCALE_SMOOTH);
                JLabel lblImg = new JLabel(new ImageIcon(img));
                lblImg.setAlignmentX(CENTER_ALIGNMENT);
                card.add(lblImg);
                card.add(Box.createVerticalStrut(10));
            }
        } catch (Exception ignored) {}

        // Título del modo
        card.add(etiqueta(titulo, Color.WHITE, new Font("SansSerif", Font.BOLD, 17)));
        card.add(Box.createVerticalStrut(8));

        // Descripción
        card.add(etiqueta(desc1, new Color(220, 220, 220), new Font("SansSerif", Font.PLAIN, 13)));
        card.add(Box.createVerticalStrut(3));
        card.add(etiqueta(desc2, new Color(200, 200, 200), new Font("SansSerif", Font.ITALIC, 12)));
        card.add(Box.createVerticalStrut(14));

        // Botón Jugar
        JButton btnJugar = new JButton("¡Jugar!");
        btnJugar.setAlignmentX(CENTER_ALIGNMENT);
        btnJugar.setMaximumSize(new Dimension(110, 36));
        btnJugar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnJugar.setForeground(Color.WHITE);
        btnJugar.setBackground(colorHover);
        btnJugar.setFocusPainted(false);
        btnJugar.setBorderPainted(false);
        btnJugar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnJugar.addActionListener(e -> seleccionar(modo));
        card.add(btnJugar);

        // Hover en la tarjeta completa
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(colorHover);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(colorBase);
            }
            public void mouseClicked(java.awt.event.MouseEvent e) {
                seleccionar(modo);
            }
        });

        return card;
    }

    private void seleccionar(int modo) {
        if (onModoSeleccionado != null) {
            onModoSeleccionado.accept(modo);
        }
    }

    // =========================================================
    // Helper
    // =========================================================

    private JLabel etiqueta(String texto, Color color, Font fuente) {
        JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
        lbl.setAlignmentX(CENTER_ALIGNMENT);
        lbl.setForeground(color);
        lbl.setFont(fuente);
        return lbl;
    }
}
