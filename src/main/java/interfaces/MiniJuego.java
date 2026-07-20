package interfaces;

import javax.swing.JPanel;

/**
 * ============================================================
 * INTERFAZ MiniJuego
 * ============================================================
 *
 * Define el comportamiento básico que debe implementar
 * cualquier minijuego del Game HUB.
 *
 * Gracias a esta interfaz, todos los juegos pueden ser
 * administrados de manera uniforme por la aplicación.
 *
 * @author Gilberto Hernández
 * @version 1.0
 * ============================================================
 */
public interface MiniJuego {

    /**
     * Devuelve el nombre del minijuego.
     *
     * @return Nombre del juego.
     */
    String getNombre();

    /**
     * Devuelve el panel principal del minijuego.
     *
     * @return Panel que contiene la interfaz del juego.
     */
    JPanel getPanel();

    /**
     * Reinicia el estado del minijuego.
     *
     * Este método será invocado cuando el usuario desee
     * comenzar una nueva partida.
     */
    void reiniciar();

}