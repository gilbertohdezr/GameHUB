package interfaces;

/**
 * ============================================================
 * INTERFAZ MenuListener
 * ============================================================
 *
 * Permite notificar la selección de una opción del menú sin
 * acoplar el PanelMenu con la VentanaPrincipal.
 *
 * @author Gilberto Hernández
 * @version 1.0
 * ============================================================
 */
public interface MenuListener {

    /**
     * Se ejecuta cuando el usuario selecciona una opción del menú.
     *
     * @param opcion Identificador de la vista seleccionada.
     */
    void opcionSeleccionada(String opcion);

}