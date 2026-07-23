package vista;

import interfaces.MenuListener;
import juegos.construccion.PanelEnConstruccion;
import juegos.memorama.PanelMemorama; // <-- 1. IMPORTAMOS TU JUEGO

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * ============================================================
 * CLASE VentanaPrincipal
 * ============================================================
 *
 * Representa la ventana principal de la aplicación.
 *
 * Esta clase contiene toda la estructura visual del Game HUB.
 * En ella se mostrarán el menú lateral y el área donde se
 * visualizarán los diferentes minijuegos.
 *
 * @author Gilberto Hernández
 * @version 1.0
 * ============================================================
 */
public class VentanaPrincipal extends JFrame implements MenuListener {

    /**
     * Panel donde se mostrará el menú lateral.
     */
    private PanelMenu panelMenu;

    /**
     * Administra el cambio entre las diferentes vistas.
     */
    private CardLayout cardLayout;

    /**
     * Contenedor principal donde se mostrarán los paneles.
     */
    private JPanel panelContenido;

    /**
     * Constructor.
     */
    public VentanaPrincipal() {

        inicializarComponentes();

    }

    /**
     * Configura completamente la ventana.
     */
    private void inicializarComponentes() {

        setTitle("GameHUB");

        Image icono = Toolkit.getDefaultToolkit()
                .getImage(getClass().getResource("/icons/app.png"));

        setIconImage(icono);

        try {

            java.awt.Taskbar.getTaskbar().setIconImage(icono);

        } catch (Exception ignored) {

        }

        setSize(1000, 650);

        setMinimumSize(new Dimension(1000, 650));

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        crearPanelMenu();

        crearPanelContenido();

        mostrarVista("INICIO");

    }

    /**
     * Crea el panel lateral.
     */
    private void crearPanelMenu() {

        panelMenu = new PanelMenu();

        // La ventana escuchará los eventos del menú.
        panelMenu.setMenuListener(this);

        add(panelMenu, BorderLayout.WEST);

    }

    /**
     * Crea el panel principal.
     */
    private void crearPanelContenido() {

        cardLayout = new CardLayout();

        panelContenido = new JPanel(cardLayout);

        panelContenido.add(new PanelInicio(), "INICIO");

        panelContenido.add(
                new PanelEnConstruccion("Adivina Número"),
                "ADIVINA");

        panelContenido.add(
                new PanelEnConstruccion("Piedra, Papel o Tijera"),
                "PIEDRA");

        panelContenido.add(
                new PanelEnConstruccion("Tres en Raya"),
                "TRES");

        panelContenido.add(
                new PanelEnConstruccion("Dados"),
                "DADOS");

        // <-- 2. REEMPLAZAMOS PanelEnConstruccion POR TU JUEGO REAL
        panelContenido.add(
                new PanelMemorama(),
                "MEMORAMA");

        add(panelContenido, BorderLayout.CENTER);

    }

    /**
     * Agrega una nueva vista al contenedor principal.
     *
     * @param nombre Nombre con el que se registrará la vista.
     * @param panel Panel que representa la vista.
     */
    public void agregarVista(String nombre, JPanel panel) {

        panelContenido.add(panel, nombre);

    }

    /**
     * Muestra una vista previamente registrada.
     *
     * @param nombre Nombre de la vista.
     */
    public void mostrarVista(String nombre) {

        cardLayout.show(panelContenido, nombre);

    }

    /**
     * Atiende las opciones seleccionadas desde el menú.
     *
     * @param opcion Opción elegida por el usuario.
     */
    @Override
    public void opcionSeleccionada(String opcion) {

        mostrarVista(opcion);

    }

}