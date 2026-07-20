package vista;

import interfaces.MenuListener;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

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
 * CLASE PanelMenu
 * ============================================================
 *
 * Muestra el menú principal del Game HUB.
 *
 * Contiene los botones para acceder a cada uno de los
 * minijuegos disponibles.
 *
 * @author Gilberto Hernández
 * @version 1.0
 * ============================================================
 */
public class PanelMenu extends JPanel {

    /**
     * Botones del menú.
     */
    private JButton btnInicio;
    private JButton btnAdivina;
    private JButton btnPiedra;
    private JButton btnTres;
    private JButton btnDados;
    private JButton btnMemorama;
    private JButton btnSalir;

    /**
     * Objeto que recibirá los eventos del menú.
     */
    private MenuListener listener;

    /**
     * Constructor.
     */
    public PanelMenu() {

        inicializarComponentes();

    }

    /**
     * Inicializa todos los componentes gráficos.
     */
    private void inicializarComponentes() {

        setBackground(new Color(45, 45, 45));

        setPreferredSize(new Dimension(300, 650));

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        crearLogo();

        add(Box.createVerticalStrut(25));

        crearBotones();

    }

    /**
     * Agrega el logotipo del proyecto.
     */
    private void crearLogo() {

        ImageIcon icono = new ImageIcon(
                getClass().getResource("/images/logo.png"));

        Image imagen = icono.getImage().getScaledInstance(
                180,
                -1,
                Image.SCALE_SMOOTH);

        JLabel lblLogo = new JLabel(new ImageIcon(imagen));

        lblLogo.setAlignmentX(CENTER_ALIGNMENT);

        add(lblLogo);

    }

    /**
     * Crea todos los botones del menú.
     */
    private void crearBotones() {

        btnInicio = crearBoton("🏠  Inicio");
        btnInicio.addActionListener(e -> {
            if (listener != null) {
                listener.opcionSeleccionada("INICIO");
            }
        });

        btnAdivina = crearBoton("🎯  Adivina Número");
        btnAdivina.addActionListener(e -> {
            if (listener != null) {
                listener.opcionSeleccionada("ADIVINA");
            }
        });

        btnPiedra = crearBoton("✋  Piedra Papel Tijera");
        btnPiedra.addActionListener(e -> {
            if (listener != null) {
                listener.opcionSeleccionada("PIEDRA");
            }
        });

        btnTres = crearBoton("⭕  Tres en Raya");
        btnTres.addActionListener(e -> {
            if (listener != null) {
                listener.opcionSeleccionada("TRES");
            }
        });

        btnDados = crearBoton("🎲  Dados");
        btnDados.addActionListener(e -> {
            if (listener != null) {
                listener.opcionSeleccionada("DADOS");
            }
        });

        btnMemorama = crearBoton("🧠  Memorama");
        btnMemorama.addActionListener(e -> {
            if (listener != null) {
                listener.opcionSeleccionada("MEMORAMA");
            }
        });

        btnSalir = crearBoton("❌  Salir");
        btnSalir.addActionListener(e -> System.exit(0));

        add(btnInicio);
        add(Box.createVerticalStrut(15));

        add(btnAdivina);
        add(Box.createVerticalStrut(15));

        add(btnPiedra);
        add(Box.createVerticalStrut(15));

        add(btnTres);
        add(Box.createVerticalStrut(15));

        add(btnDados);
        add(Box.createVerticalStrut(15));

        add(btnMemorama);

        add(Box.createVerticalGlue());

        add(btnSalir);

    }

    /**
     * Crea un botón con el estilo del menú.
     *
     * @param texto Texto del botón.
     *
     * @return JButton configurado.
     */
    private JButton crearBoton(String texto) {

        JButton boton = new JButton(texto);

        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        boton.setAlignmentX(CENTER_ALIGNMENT);

        boton.setHorizontalAlignment(SwingConstants.LEFT);

        boton.setFocusPainted(false);

        boton.setBorderPainted(false);

        boton.setBackground(new Color(70, 70, 70));

        boton.setForeground(Color.WHITE);

        boton.setFont(new Font("SansSerif", Font.PLAIN, 16));

        return boton;

    }

    /**
     * Establece el objeto que recibirá los eventos del menú.
     *
     * @param listener Implementación de MenuListener.
     */
    public void setMenuListener(MenuListener listener) {

        this.listener = listener;

    }

}