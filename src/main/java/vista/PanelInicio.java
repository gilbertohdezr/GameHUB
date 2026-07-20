package vista;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * ============================================================
 * CLASE PanelInicio
 * ============================================================
 *
 * Representa la pantalla de bienvenida del Game HUB.
 *
 * Esta vista se muestra al iniciar la aplicación y permanecerá
 * visible hasta que el usuario seleccione un minijuego.
 *
 * @author Gilberto Hernández
 * @version 1.0
 * ============================================================
 */
public class PanelInicio extends JPanel {

    /**
     * Constructor.
     */
    public PanelInicio() {

        inicializarComponentes();

    }

    /**
     * Inicializa los componentes gráficos.
     */
    private void inicializarComponentes() {

        setBackground(new Color(245, 245, 245));

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Box.createVerticalGlue());

        crearLogo();

        add(Box.createVerticalStrut(20));

        crearTitulo();

        add(Box.createVerticalStrut(10));

        crearDescripcion();

        add(Box.createVerticalGlue());

    }

    /**
     * Crea el logotipo principal.
     */
    private void crearLogo() {

        ImageIcon icono = new ImageIcon(
                getClass().getResource("/images/logo.png"));

        Image imagen = icono.getImage().getScaledInstance(
                280,
                -1,
                Image.SCALE_SMOOTH);

        JLabel lblLogo = new JLabel(new ImageIcon(imagen));

        lblLogo.setAlignmentX(CENTER_ALIGNMENT);

        add(lblLogo);

    }

    /**
     * Crea el título principal.
     */
    private void crearTitulo() {

        JLabel lblTitulo = new JLabel("Bienvenido");

        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);

        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 34));

        add(lblTitulo);

    }

    /**
     * Crea el mensaje de bienvenida.
     */
    private void crearDescripcion() {

        JLabel lblDescripcion = new JLabel(
                "Seleccione un juego del menú para comenzar.");

        lblDescripcion.setAlignmentX(CENTER_ALIGNMENT);

        lblDescripcion.setFont(new Font("SansSerif", Font.PLAIN, 18));

        lblDescripcion.setForeground(Color.GRAY);

        add(lblDescripcion);

    }

}