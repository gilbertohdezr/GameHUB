package juegos.adivina;

/**
 * ============================================================
 * CLASE AdivinaModelo
 * ============================================================
 *
 * Contiene toda la lógica del juego "Adivina el Número".
 *
 * Esta clase no conoce nada sobre Swing ni sobre la interfaz
 * gráfica: únicamente administra el número secreto, el conteo
 * de intentos y las reglas del juego.
 *
 * El generador de números se recibe por constructor
 * ({@link GeneradorNumeros}) en lugar de crearse internamente,
 * aplicando el Principio de Inversión de Dependencias (la "D"
 * de SOLID). Esto permite, por ejemplo, inyectar un generador
 * predecible durante pruebas unitarias.
 *
 * @author Abimael
 * @version 2.0
 * ============================================================
 */
public class AdivinaModelo {

    /**
     * Generador utilizado para producir el número secreto.
     */
    private final GeneradorNumeros generador;

    /**
     * Nivel de dificultad de la partida actual.
     */
    private Dificultad dificultad;

    /**
     * Número secreto que el jugador debe adivinar.
     */
    private int numeroSecreto;

    /**
     * Intentos restantes en la partida actual.
     */
    private int intentosRestantes;

    /**
     * Indica si la partida actual ya terminó (por acierto o
     * por agotar los intentos).
     */
    private boolean juegoTerminado;

    /**
     * Constructor por defecto. Utiliza {@link GeneradorAleatorio}
     * como generador y {@link Dificultad#NORMAL} como nivel
     * inicial.
     */
    public AdivinaModelo() {

        this(new GeneradorAleatorio());

    }

    /**
     * Constructor con inyección de dependencias.
     *
     * @param generador Generador de números a utilizar.
     */
    public AdivinaModelo(GeneradorNumeros generador) {

        this.generador = generador;

        reiniciar(Dificultad.NORMAL);

    }

    /**
     * Reinicia la partida manteniendo el nivel de dificultad
     * actual.
     */
    public void reiniciar() {

        reiniciar(dificultad);

    }

    /**
     * Reinicia la partida estableciendo un nuevo nivel de
     * dificultad: genera un nuevo número secreto y restablece
     * el contador de intentos según el nivel indicado.
     *
     * @param nuevaDificultad Nivel de dificultad a utilizar.
     */
    public void reiniciar(Dificultad nuevaDificultad) {

        this.dificultad = nuevaDificultad;

        numeroSecreto = generador.generar(dificultad.getMin(), dificultad.getMax());

        intentosRestantes = dificultad.getMaxIntentos();

        juegoTerminado = false;

    }

    /**
     * Procesa el intento del jugador y determina el resultado.
     *
     * @param numero Número propuesto por el jugador.
     *
     * @return Resultado del intento.
     */
    public Resultado procesarIntento(int numero) {

        if (juegoTerminado) {

            return Resultado.JUEGO_TERMINADO;

        }

        intentosRestantes--;

        if (numero == numeroSecreto) {

            juegoTerminado = true;

            return Resultado.ACIERTO;

        }

        if (intentosRestantes <= 0) {

            juegoTerminado = true;

            return Resultado.SIN_INTENTOS;

        }

        return numero < numeroSecreto ? Resultado.MAYOR : Resultado.MENOR;

    }

    /**
     * Calcula qué tan lejos está un número propuesto respecto
     * al número secreto. Útil para mostrar pistas visuales
     * como la barra de temperatura.
     *
     * @param numero Número propuesto por el jugador.
     *
     * @return Distancia absoluta entre el número propuesto y
     *         el número secreto.
     */
    public int getDistanciaAbsoluta(int numero) {

        return Math.abs(numero - numeroSecreto);

    }

    /**
     * @return Nivel de dificultad de la partida actual.
     */
    public Dificultad getDificultad() {

        return dificultad;

    }

    /**
     * @return Número de intentos restantes.
     */
    public int getIntentosRestantes() {

        return intentosRestantes;

    }

    /**
     * @return Número de intentos ya utilizados en la partida
     *         actual.
     */
    public int getIntentosUsados() {

        return dificultad.getMaxIntentos() - intentosRestantes;

    }

    /**
     * @return Número secreto de la partida actual.
     */
    public int getNumeroSecreto() {

        return numeroSecreto;

    }

    /**
     * @return true si la partida actual ya terminó.
     */
    public boolean isJuegoTerminado() {

        return juegoTerminado;

    }

}
