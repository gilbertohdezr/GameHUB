package juegos.piedra;

/**
 * ============================================================
 * ENUM Jugada
 * ============================================================
 *
 * Define las tres jugadas posibles del juego
 * Piedra, Papel o Tijeras.
 *
 * @version 1.0
 * ============================================================
 */
public enum Jugada {

    PIEDRA, PAPEL, TIJERAS;

    /**
     * Nombre legible de la jugada.
     */
    public String getNombre() {
        switch (this) {
            case PIEDRA:  return "Piedra";
            case PAPEL:   return "Papel";
            case TIJERAS: return "Tijeras";
            default:      return "";
        }
    }

    /**
     * Ruta del recurso de imagen correspondiente a esta jugada.
     */
    public String getRutaImagen() {
        switch (this) {
            case PIEDRA:  return "/images/Piedra_Papel_0_Tijeras/Piedra.png";
            case PAPEL:   return "/images/Piedra_Papel_0_Tijeras/Papel.png";
            case TIJERAS: return "/images/Piedra_Papel_0_Tijeras/Tijeras.png";
            default:      return "";
        }
    }
}
