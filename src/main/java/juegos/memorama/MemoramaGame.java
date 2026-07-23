package juegos.memorama;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoramaGame {
    private final int filas = 4;
    private final int columnas = 4;
    private int[][] tablero;
    private boolean[][] reveladas;
    private int parejasEncontradas;

    public MemoramaGame() {
        tablero = new int[filas][columnas];
        reveladas = new boolean[filas][columnas];
        reiniciar();
    }

    public void reiniciar() {
        parejasEncontradas = 0;
        List<Integer> numeros = new ArrayList<>();
        int totalPares = (filas * columnas) / 2;

        for (int i = 1; i <= totalPares; i++) {
            numeros.add(i);
            numeros.add(i);
        }
        Collections.shuffle(numeros);

        int idx = 0;
        for (int r = 0; r < filas; r++) {
            for (int c = 0; c < columnas; c++) {
                tablero[r][c] = numeros.get(idx++);
                reveladas[r][c] = false;
            }
        }
    }

    public int getValor(int fila, int col) {
        return tablero[fila][col];
    }

    public boolean esPareja(int f1, int c1, int f2, int c2) {
        if (tablero[f1][c1] == tablero[f2][c2]) {
            reveladas[f1][c1] = true;
            reveladas[f2][c2] = true;
            parejasEncontradas++;
            return true;
        }
        return false;
    }

    public boolean esJuegoTerminado() {
        return parejasEncontradas == (filas * columnas) / 2;
    }

    public int getFilas() { return filas; }
    public int getColumnas() { return columnas; }
}