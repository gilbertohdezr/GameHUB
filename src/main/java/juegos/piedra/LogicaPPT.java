package juegos.piedra;

import java.util.Random;

/**
 * ============================================================
 * CLASE LogicaPPT
 * ============================================================
 *
 * Contiene la lógica pura del juego Piedra, Papel o Tijeras.
 * No tiene ningún componente gráfico; solo cálculos.
 *
 * @version 1.0
 * ============================================================
 */
public class LogicaPPT {

    private static final Random random = new Random();

    /**
     * Calcula el resultado de un tiro desde la perspectiva del jugador.
     *
     * @param jugador Jugada elegida por el jugador.
     * @param maquina Jugada de la máquina.
     * @return Resultado desde la perspectiva del jugador.
     */
    public static ResultadoTiro calcularResultado(Jugada jugador, Jugada maquina) {
        if (jugador == maquina) {
            return ResultadoTiro.EMPATE;
        }
        if ((jugador == Jugada.PIEDRA  && maquina == Jugada.TIJERAS) ||
            (jugador == Jugada.PAPEL   && maquina == Jugada.PIEDRA)  ||
            (jugador == Jugada.TIJERAS && maquina == Jugada.PAPEL)) {
            return ResultadoTiro.GANA;
        }
        return ResultadoTiro.PIERDE;
    }

    /**
     * Genera una jugada aleatoria para la máquina.
     *
     * @return Jugada aleatoria.
     */
    public static Jugada generarJugadaMaquina() {
        Jugada[] jugadas = Jugada.values();
        return jugadas[random.nextInt(jugadas.length)];
    }
}
