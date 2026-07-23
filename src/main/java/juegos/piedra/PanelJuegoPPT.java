package juegos.piedra;

import java.awt.CardLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;

import javax.swing.JPanel;

/**
 * ============================================================
 * CLASE PanelJuegoPPT
 * ============================================================
 *
 * Contenedor raíz del juego Piedra, Papel o Tijeras.
 * Se registra en VentanaPrincipal con la clave "PIEDRA".
 *
 * Usa su propio CardLayout interno para navegar entre:
 *  - "SELECCION"  : PanelSeleccionModo (pantalla de elección)
 *  - "COUNTDOWN"  : PanelCountdown     (cuenta regresiva 3-2-1)
 *  - "JUEGO"      : PanelPiedra        (partida activa)
 *
 * Flujo:
 *   Selección → Countdown (música inicia aquí) → Juego
 *   Juego → "Cambiar modo" → Selección (música se detiene)
 *   Cambio de foco / vista → Reinicio completo del juego y música.
 *
 * @version 1.2
 * ============================================================
 */
public class PanelJuegoPPT extends JPanel {

    private final CardLayout          cardLayout;
    private final PanelSeleccionModo  panelSeleccion;
    private PanelCountdown            panelCountdown;
    private PanelPiedra               panelPiedra;

    /** Modo guardado mientras dura el countdown. */
    private int modoElegido;

    // =========================================================
    // Constructor
    // =========================================================

    public PanelJuegoPPT() {
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        panelSeleccion = new PanelSeleccionModo(this::prepararJuego);
        add(panelSeleccion, "SELECCION");

        cardLayout.show(this, "SELECCION");

        // Escuchadores de cambio de visibilidad (cuando se cambia de juego en el menú lateral)
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                resetearJuegoCompleto();
            }
        });

        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && !isShowing()) {
                resetearJuegoCompleto();
            }
        });
    }

    // =========================================================
    // Navegación interna
    // =========================================================

    /**
     * Paso 1: el usuario eligió un modo.
     * Muestra el overlay del countdown e inicia la música
     * de forma INMEDIATA (no espera los 3 segundos).
     *
     * @param modo PanelPiedra.MODO_PEPE o PanelPiedra.MODO_DIOS.
     */
    private void prepararJuego(int modo) {
        modoElegido = modo;

        // Limpiar countdown anterior si lo había
        if (panelCountdown != null) {
            panelCountdown.detener();
            remove(panelCountdown);
        }

        // La música arranca ANTES del countdown (no espera los 3 seg)
        ReproductorMusica.iniciar();

        // Crear el panel de countdown con su callback al terminar
        panelCountdown = new PanelCountdown(this::iniciarJuego);
        add(panelCountdown, "COUNTDOWN");
        revalidate();
        repaint();

        cardLayout.show(this, "COUNTDOWN");
        panelCountdown.iniciar();
    }

    /**
     * Paso 2: el countdown terminó.
     * Crea y muestra el panel de juego.
     */
    private void iniciarJuego() {
        if (panelPiedra != null) {
            remove(panelPiedra);
        }
        panelPiedra = new PanelPiedra(modoElegido, this::volverASeleccion);
        add(panelPiedra, "JUEGO");
        revalidate();
        repaint();
        cardLayout.show(this, "JUEGO");
    }

    /**
     * Regresa a la pantalla de selección y detiene la música.
     */
    private void volverASeleccion() {
        ReproductorMusica.detener();
        cardLayout.show(this, "SELECCION");
    }

    /**
     * Reinicia completamente el estado del minijuego cuando el panel
     * pierde el foco / visibilidad (por ejemplo al hacer click en otro minijuego).
     */
    public void resetearJuegoCompleto() {
        ReproductorMusica.detener();

        if (panelCountdown != null) {
            panelCountdown.detener();
            remove(panelCountdown);
            panelCountdown = null;
        }

        if (panelPiedra != null) {
            remove(panelPiedra);
            panelPiedra = null;
        }

        cardLayout.show(this, "SELECCION");
        revalidate();
        repaint();
    }
}
