package juegos.adivina;

/**
 * ============================================================
 * INTERFAZ GeneradorNumeros
 * ============================================================
 *
 * Abstrae la forma en que se genera el número secreto del
 * juego.
 *
 * Gracias a esta interfaz, {@link AdivinaModelo} no depende
 * directamente de {@code java.util.Random} (Inversión de
 * Dependencias). Esto permite, por ejemplo, inyectar un
 * generador falso y predecible durante pruebas unitarias.
 *
 * @author Abimael
 * @version 2.0
 * ============================================================
 */
public interface GeneradorNumeros {

    /**
     * Genera un número entero dentro del rango indicado
     * (ambos límites incluidos).
     *
     * @param min Valor mínimo posible.
     * @param max Valor máximo posible.
     *
     * @return Número generado.
     */
    int generar(int min, int max);

}
