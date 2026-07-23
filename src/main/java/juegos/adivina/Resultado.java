package juegos.adivina;

/**
 * ============================================================
 * ENUM Resultado
 * ============================================================
 *
 * Representa los posibles resultados al procesar un intento
 * del jugador en el juego "Adivina el Número".
 *
 * Se mantiene en su propio archivo (en lugar de ser un enum
 * interno de {@link AdivinaModelo}) para que pueda crecer o
 * ser reutilizado sin acoplarse a la clase del modelo.
 *
 * @author Abimael
 * @version 2.0
 * ============================================================
 */
public enum Resultado {

    /**
     * El jugador adivinó el número secreto.
     */
    ACIERTO,

    /**
     * El número propuesto es mayor que el número secreto.
     */
    MAYOR,

    /**
     * El número propuesto es menor que el número secreto.
     */
    MENOR,

    /**
     * El jugador agotó sus intentos sin acertar.
     */
    SIN_INTENTOS,

    /**
     * La partida ya había terminado antes de este intento.
     */
    JUEGO_TERMINADO

}
