package hub;

import vista.VentanaPrincipal;

/**
 * ============================================================
 * CLASE GameHub
 * ============================================================
 *
 * Coordina el inicio de la aplicación.
 *
 * Es responsable de crear la ventana principal y mostrarla
 * al usuario.
 *
 * Conforme el proyecto evolucione, esta clase también será
 * la encargada de registrar e inicializar los minijuegos.
 *
 * @author Gilberto Hernández
 * @version 1.0
 * ============================================================
 */
public class GameHub {

    /**
     * Referencia a la ventana principal.
     */
    private VentanaPrincipal ventanaPrincipal;

    /**
     * Constructor.
     */
    public GameHub() {

    }

    /**
     * Inicia la aplicación.
     */
    public void iniciar() {

        ventanaPrincipal = new VentanaPrincipal();

        ventanaPrincipal.setVisible(true);

    }

}