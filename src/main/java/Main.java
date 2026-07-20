import hub.GameHub;

/**
 * ============================================================
 * GAME HUB TEST
 * ============================================================
 *
 * Punto de entrada de la aplicación.
 *
 * Esta clase únicamente crea una instancia del GameHub y
 * delega en él el inicio de toda la aplicación.
 *
 * @author Gilberto Hernández
 * @version 1.0
 * ============================================================
 */
public class Main {

    /**
     * Método principal de la aplicación.
     *
     * @param args Argumentos enviados desde la línea de comandos.
     */
    public static void main(String[] args) {

        GameHub gameHub = new GameHub();
        gameHub.iniciar();

    }

}