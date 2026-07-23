package juegos.adivina;

/**
 * ============================================================
 * ENUM Dificultad
 * ============================================================
 *
 * Define los niveles de dificultad disponibles para el juego
 * "Adivina el Número".
 *
 * Cada nivel encapsula su propio rango de números y su propio
 * límite de intentos, evitando "números mágicos" dispersos en
 * el código y facilitando agregar nuevos niveles a futuro.
 *
 * @author Abimael
 * @version 2.0
 * ============================================================
 */
public enum Dificultad {

    /**
     * Nivel fácil: rango reducido y más intentos disponibles.
     */
    FACIL("Fácil", 1, 50, 15),

    /**
     * Nivel normal: configuración balanceada por defecto.
     */
    NORMAL("Normal", 1, 100, 10),

    /**
     * Nivel difícil: rango amplio y menos intentos disponibles.
     */
    DIFICIL("Difícil", 1, 500, 8);

    /**
     * Nombre legible del nivel, usado en la interfaz gráfica.
     */
    private final String etiqueta;

    /**
     * Valor mínimo del rango de números posibles.
     */
    private final int min;

    /**
     * Valor máximo del rango de números posibles.
     */
    private final int max;

    /**
     * Número máximo de intentos permitidos.
     */
    private final int maxIntentos;

    /**
     * Constructor del enum.
     *
     * @param etiqueta    Nombre legible del nivel.
     * @param min         Valor mínimo del rango.
     * @param max         Valor máximo del rango.
     * @param maxIntentos Intentos máximos permitidos.
     */
    Dificultad(String etiqueta, int min, int max, int maxIntentos) {

        this.etiqueta = etiqueta;

        this.min = min;

        this.max = max;

        this.maxIntentos = maxIntentos;

    }

    /**
     * @return Valor mínimo del rango.
     */
    public int getMin() {

        return min;

    }

    /**
     * @return Valor máximo del rango.
     */
    public int getMax() {

        return max;

    }

    /**
     * @return Intentos máximos permitidos.
     */
    public int getMaxIntentos() {

        return maxIntentos;

    }

    /**
     * Se sobreescribe para que el {@code JComboBox} muestre
     * directamente el nombre legible del nivel.
     *
     * @return Nombre legible del nivel de dificultad.
     */
    @Override
    public String toString() {

        return etiqueta;

    }

}
