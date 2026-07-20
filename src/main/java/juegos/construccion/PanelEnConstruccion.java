package juegos.construccion;

import java.awt.Color;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * ============================================================
 * CLASE PanelEnConstruccion
 * ============================================================
 *
 * Representa una pantalla temporal para los minijuegos que
 * aún no han sido desarrollados.
 *
 * @author Gilberto Hernández
 * @version 1.0
 * ============================================================
 */
public class PanelEnConstruccion extends JPanel {

    /**
     * Constructor.
     *
     * @param nombreJuego Nombre del minijuego.
     */
    public PanelEnConstruccion(String nombreJuego) {

        inicializarComponentes(nombreJuego);

    }

    /**
     * Inicializa los componentes gráficos.
     *
     * @param nombreJuego Nombre del juego.
     */
    private void inicializarComponentes(String nombreJuego) {

        setBackground(new Color(245, 245, 245));

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Box.createVerticalGlue());

        JLabel lblTitulo = new JLabel(nombreJuego);
        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 34));

        JLabel lblMensaje = new JLabel("🚧 En construcción...");
        lblMensaje.setAlignmentX(CENTER_ALIGNMENT);
        lblMensaje.setFont(new Font("SansSerif", Font.PLAIN, 18));
        lblMensaje.setForeground(Color.GRAY);

        add(lblTitulo);
        add(Box.createVerticalStrut(20));
        add(lblMensaje);

        add(Box.createVerticalGlue());

    }

}