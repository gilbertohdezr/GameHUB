package juegos.dados;

import java.util.Arrays;

/**
 * Analiza las combinaciones producidas por cinco dados.
 */
public final class ResultadoDados {
    private ResultadoDados() {
    }

    public enum Combinacion {
        NINGUNA("Sin combinación"),
        PAR("Un par"),
        DOBLE_PAR("Doble par"),
        TERCIA("Tercia"),
        CASA_LLENA("Casa llena"),
        POKER("Póker"),
        ESCALERA("Escalera"),
        CINCO_IGUALES("Cinco iguales"),
        SUPER_SEIS("Súper seis");

        private final String etiqueta;

        Combinacion(String etiqueta) {
            this.etiqueta = etiqueta;
        }

        public String getEtiqueta() {
            return etiqueta;
        }
    }

    public static Combinacion analizar(int[] dados) {
        if (dados == null || dados.length != 5) {
            throw new IllegalArgumentException("Se requieren exactamente cinco dados.");
        }

        int[] frecuencias = new int[7];
        int[] ordenados = dados.clone();
        for (int dado : dados) {
            if (dado < 1 || dado > 6) {
                throw new IllegalArgumentException("Los dados deben estar entre 1 y 6.");
            }
            frecuencias[dado]++;
        }
        Arrays.sort(ordenados);

        if (frecuencias[6] == 5) return Combinacion.SUPER_SEIS;

        int maximo = 0;
        int pares = 0;
        boolean tieneDos = false;
        boolean tieneTres = false;
        for (int valor = 1; valor <= 6; valor++) {
            maximo = Math.max(maximo, frecuencias[valor]);
            if (frecuencias[valor] == 2) {
                pares++;
                tieneDos = true;
            }
            if (frecuencias[valor] == 3) tieneTres = true;
        }

        if (maximo == 5) return Combinacion.CINCO_IGUALES;
        if (Arrays.equals(ordenados, new int[]{1, 2, 3, 4, 5})
                || Arrays.equals(ordenados, new int[]{2, 3, 4, 5, 6})) {
            return Combinacion.ESCALERA;
        }
        if (maximo == 4) return Combinacion.POKER;
        if (tieneTres && tieneDos) return Combinacion.CASA_LLENA;
        if (tieneTres) return Combinacion.TERCIA;
        if (pares == 2) return Combinacion.DOBLE_PAR;
        if (pares == 1) return Combinacion.PAR;
        return Combinacion.NINGUNA;
    }
}
