package juegos.adivina;

import java.util.Random;

/**
 * ============================================================
 * CLASE GeneradorAleatorio
 * ============================================================
 *
 * Implementación de {@link GeneradorNumeros} que utiliza
 * {@code java.util.Random} para generar el número secreto.
 *
 * Es la implementación usada en producción por el juego. En
 * pruebas unitarias puede sustituirse por otra implementación
 * de {@link GeneradorNumeros} que devuelva valores predecibles.
 *
 * @author Abimael
 * @version 2.0
 * ============================================================
 */
public class GeneradorAleatorio implements GeneradorNumeros {

    /**
     * Fuente de aleatoriedad.
     */
    private final Random random = new Random();

    /**
     * Genera un número entero dentro del rango indicado
     * (ambos límites incluidos).
     *
     * @param min Valor mínimo posible.
     * @param max Valor máximo posible.
     *
     * @return Número generado.
     */
    @Override
    public int generar(int min, int max) {

        return random.nextInt(max - min + 1) + min;

    }

}
