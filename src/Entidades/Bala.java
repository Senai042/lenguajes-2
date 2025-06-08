package Entidades;

public class Bala {
    private int linea, columna;
    private Direccion direccion;
    private boolean active = true;

    public Bala(int linea, int columna, Direccion direccion) {
        this.linea = linea;
        this.columna = columna;
        this.direccion = direccion;
    }

    public void move() {
        switch (direccion) {
            case UP -> linea--;
            case DOWN -> linea++;
            case LEFT -> columna--;
            case RIGHT -> columna++;
        }
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    // Getters
    public int getLinea() {
        return linea;
    }

    public int getColumna() {
        return columna;
    }

    public Direccion getDireccion() {
        return direccion;
    }
}
