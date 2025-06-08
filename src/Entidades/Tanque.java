package Entidades;

import java.awt.*;

public class Tanque {
    private int linea, columna;             // Posición en el tablero
    private int vidas;               // Vidas actuales
    private String habilidad;          // "nada", "rafaga", "balas rapidas", etc.
    private Color color;             // Representación visual
    private boolean jugador;       // ¿Es el jugador?
    private Direccion direccion;    // Dirección actual del tanque

    public Tanque(int row, int col, int lives, String ability, Color color, boolean isPlayer) {
        this.linea = row;
        this.columna = col;
        this.vidas = lives;
        this.habilidad = ability;
        this.color = color;
        this.jugador = isPlayer;
        this.direccion = Direccion.UP;
    }

    public void move(Direccion dir) {
        this.direccion = dir;
        switch (dir) {
            case UP -> linea--;
            case DOWN -> linea++;
            case LEFT -> columna--;
            case RIGHT -> columna++;
        }
    }

    public void receiveDamage() {
        vidas--;
    }

    public boolean isAlive() {
        return vidas > 0;
    }

    public Bala shoot() {
        return new Bala(linea, columna, direccion);
    }

    // Getters y Setters
    public int getLinea() {
        return linea;
    }

    public int getColumna() {
        return columna;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public Color getColor() {
        return color;
    }

    public boolean isJugador() {
        return jugador;
    }

    public String getHabilidad() {
        return habilidad;
    }

    public int getVidas() {
        return vidas;
    }

    public void setPosition(int row, int col) {
        this.linea = row;
        this.columna = col;
    }
}
