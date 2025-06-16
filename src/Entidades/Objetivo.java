package Entidades;

public class Objetivo {
    private int fila, columna;
    private int vidas;

    public Objetivo(int fila, int columna, int vidas) {
        this.fila = fila;
        this.columna = columna;
        this.vidas = vidas;
    }

    public boolean recibirDanno() {
        vidas--;
        return vidas <= 0;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }
}
