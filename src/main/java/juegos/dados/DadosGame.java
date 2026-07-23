package juegos.dados;

import interfaces.MiniJuego;

import javax.swing.JPanel;
import java.util.Arrays;
import java.util.Random;

/**
 * Lógica y estado de una partida de Juego de Dados.
 */
public class DadosGame implements MiniJuego {
    public enum Reto {
        PARIDAD("Par o impar", 2),
        RANGO("Mayor o menor", 2),
        EXACTO("Número exacto", 8),
        PAR("Un par", 2),
        DOBLE_PAR("Doble par", 3),
        TERCIA("Tercia", 4),
        CASA_LLENA("Casa llena", 6),
        POKER("Póker", 10),
        ESCALERA("Escalera", 12),
        CINCO_IGUALES("Cinco iguales", 25),
        SUPER_SEIS("Súper seis", 50);

        private final String etiqueta;
        private final int multiplicador;

        Reto(String etiqueta, int multiplicador) {
            this.etiqueta = etiqueta;
            this.multiplicador = multiplicador;
        }

        public String getEtiqueta() {
            return etiqueta;
        }

        public int getMultiplicador() {
            return multiplicador;
        }

        @Override
        public String toString() {
            return etiqueta;
        }
    }

    public record ResultadoTirada(
            int[] dados,
            int suma,
            ResultadoDados.Combinacion combinacion,
            boolean acierto,
            int premio,
            int cambio
    ) {
        public ResultadoTirada {
            dados = dados.clone();
        }

        @Override
        public int[] dados() {
            return dados.clone();
        }
    }

    private final Random random = new Random();
    private PanelDados panel;
    private int puntos;
    private int aciertos;
    private int racha;
    private int record;

    public DadosGame() {
        reiniciar();
    }

    public int[] lanzarDados() {
        int[] dados = new int[5];
        for (int i = 0; i < dados.length; i++) {
            dados[i] = random.nextInt(6) + 1;
        }
        return dados;
    }

    public ResultadoTirada evaluar(int[] dados, int costo, Reto reto, String opcion) {
        if (reto == null) throw new IllegalArgumentException("Selecciona un reto.");
        if (costo <= 0 || costo > puntos) throw new IllegalArgumentException("Puntos insuficientes.");

        int suma = Arrays.stream(dados).sum();
        ResultadoDados.Combinacion combinacion = ResultadoDados.analizar(dados);
        boolean gano = switch (reto) {
            case PARIDAD -> ("PAR".equals(opcion) && suma % 2 == 0)
                    || ("IMPAR".equals(opcion) && suma % 2 != 0);
            case RANGO -> ("MAYOR".equals(opcion) && suma > 18)
                    || ("MENOR".equals(opcion) && suma < 18);
            case EXACTO -> suma == Integer.parseInt(opcion);
            case PAR -> combinacion == ResultadoDados.Combinacion.PAR;
            case DOBLE_PAR -> combinacion == ResultadoDados.Combinacion.DOBLE_PAR;
            case TERCIA -> combinacion == ResultadoDados.Combinacion.TERCIA;
            case CASA_LLENA -> combinacion == ResultadoDados.Combinacion.CASA_LLENA;
            case POKER -> combinacion == ResultadoDados.Combinacion.POKER;
            case ESCALERA -> combinacion == ResultadoDados.Combinacion.ESCALERA;
            case CINCO_IGUALES -> combinacion == ResultadoDados.Combinacion.CINCO_IGUALES;
            case SUPER_SEIS -> combinacion == ResultadoDados.Combinacion.SUPER_SEIS;
        };

        int premio = gano ? costo * reto.getMultiplicador() : 0;
        int cambio = gano ? premio - costo : -costo;
        puntos += cambio;
        if (gano) {
            aciertos++;
            racha++;
            record = Math.max(record, premio);
        } else {
            racha = 0;
        }
        return new ResultadoTirada(dados, suma, combinacion, gano, premio, cambio);
    }

    public int getPuntos() { return puntos; }
    public int getAciertos() { return aciertos; }
    public int getRacha() { return racha; }
    public int getRecord() { return record; }
    public int getNivel() { return aciertos >= 25 ? 4 : aciertos >= 10 ? 3 : aciertos >= 3 ? 2 : 1; }
    public String getNombreNivel() { return switch (getNivel()) { case 4 -> "Maestro"; case 3 -> "Experto"; case 2 -> "Jugador"; default -> "Novato"; }; }

    @Override
    public String getNombre() {
        return "Juego de Dados";
    }

    @Override
    public JPanel getPanel() {
        if (panel == null) panel = new PanelDados(this, null);
        return panel;
    }

    @Override
    public void reiniciar() {
        puntos = 1000;
        aciertos = 0;
        racha = 0;
        record = 0;
        if (panel != null) panel.actualizarDesdeJuego();
    }
}
