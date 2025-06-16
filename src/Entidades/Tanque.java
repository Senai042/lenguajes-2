package Entidades;

import Game.CeldaType;
import Game.Board;
import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class Tanque {
    private int linea, columna;
    private int vidas;
    private final String habilidad;
    private final Color color;
    private final boolean jugador;
    private Direccion direccion;

    //texturas
    private BufferedImage imgTanque;
    private BufferedImage imgCanon;

    // Movimiento en píxeles
    private int xPix, yPix;
    private final int tamCelda = 40; // ajustable según tu config
    private boolean moviendo = false;
    private final int velocidad;

    //balas
    private long tiempoUltimoDisparo = 0;
    private final long COOLDOWN_DISPARO = 500; // milisegundos


    public Tanque(int row, int col, int lives, String ability, Color color, boolean isPlayer, int velocidad) {
        this.linea = row;
        this.columna = col;
        this.vidas = lives;
        this.habilidad = ability;
        this.color = color;
        this.jugador = isPlayer;
        this.direccion = Direccion.UP;
        this.velocidad = velocidad;
        this.xPix = col * tamCelda;
        this.yPix = row * tamCelda;

        cargarTexturasPorColor(color);
    }

    private void cargarTexturasPorColor(Color color) {
        String baseNombre = "Red";
        if (color.equals(Color.BLUE)) {
            baseNombre = "Blue";
        } else if (color.equals(Color.RED)) {
            baseNombre = "Red";
        } else if (color.equals(Color.GREEN)) {
            baseNombre = "Green";
        } else if (color.equals(Color.GRAY)) {
            baseNombre = "Beige";
        } else {
            System.err.println("Color de tanque no reconocido, usando imágenes por defecto.");
        }

        try {
            imgTanque = ImageIO.read(getClass().getResource("/Textures/tank" + baseNombre + ".png"));
            imgCanon = ImageIO.read(getClass().getResource("/Textures/barrel" + baseNombre + ".png"));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("No se pudieron cargar las texturas del tanque " + baseNombre + ": " + e.getMessage());
        }
    }


    //Movimiento animado por casilla
    public boolean moverAnimado(Direccion dir, CeldaType tipo, Board board) {
        if (moviendo) return false;

        int nuevaLinea = linea, nuevaCol = columna;

        switch (dir) {
            case UP -> nuevaLinea--;
            case DOWN -> nuevaLinea++;
            case LEFT -> nuevaCol--;
            case RIGHT -> nuevaCol++;
        }

        if (nuevaLinea < 0 || nuevaLinea >= board.getLineas() || nuevaCol < 0 || nuevaCol >= board.getColumnas())
            return false;

        if (board.getCell(nuevaLinea, nuevaCol) == CeldaType.MURO)
            return false;


        board.setCell(linea, columna, CeldaType.EMPTY);
        linea = nuevaLinea;
        columna = nuevaCol;
        board.setCell(linea, columna, tipo);

        this.direccion = dir;
        moviendo = true;
        return true;
    }

    public boolean moverAnimado(Direccion dir, CeldaType tipo, Board board, List<Tanque> otros) {
        if (moviendo) return false;

        int nuevaLinea = linea;
        int nuevaCol = columna;

        // Dirección
        switch (dir) {
            case UP -> nuevaLinea--;
            case DOWN -> nuevaLinea++;
            case LEFT -> nuevaCol--;
            case RIGHT -> nuevaCol++;
        }

        // Verificar límites y muros
        if (nuevaLinea < 0 || nuevaLinea >= board.getLineas() ||
                nuevaCol < 0 || nuevaCol >= board.getColumnas()) {
            return false;
        }

        if (board.getCell(nuevaLinea, nuevaCol) == CeldaType.MURO || board.getCell(nuevaLinea, nuevaCol) == CeldaType.OBJETIVO ) {
            return false;
        } 

        // Verificar colisión con otros tanques
        for (Tanque t : otros) {
            if (t != this && t.getLinea() == nuevaLinea && t.getColumna() == nuevaCol) {
                return false;
            }
        }

        // Limpiar celda anterior solo si no era OBJETIVO
        if (board.getCell(linea, columna) != CeldaType.OBJETIVO) {
            board.setCell(linea, columna, CeldaType.EMPTY);
        }

        // Actualizar posición
        linea = nuevaLinea;
        columna = nuevaCol;

        // Colocar tipo solo si la nueva celda no es OBJETIVO
        if (board.getCell(linea, columna) != CeldaType.OBJETIVO) {
            board.setCell(linea, columna, tipo);
        }

        this.direccion = dir;
        moviendo = true;
        return true;
    }


    public void animarPaso() {
        int destinoX = columna * tamCelda;
        int destinoY = linea * tamCelda;

        if (xPix < destinoX) xPix += Math.min(velocidad, destinoX - xPix);
        if (xPix > destinoX) xPix -= Math.min(velocidad, xPix - destinoX);
        if (yPix < destinoY) yPix += Math.min(velocidad, destinoY - yPix);
        if (yPix > destinoY) yPix -= Math.min(velocidad, yPix - destinoY);

        if (xPix == destinoX && yPix == destinoY) {
            moviendo = false;
        }
    }

    public boolean estaMoviendo() {
        return moviendo;
    }

    public boolean recibirDanno() {
        vidas--;
        return vidas <=0;
    }

    public Bala shoot() {
        long ahora = System.currentTimeMillis();

        if (ahora - tiempoUltimoDisparo >= COOLDOWN_DISPARO) {
            tiempoUltimoDisparo = ahora;
            return new Bala(linea, columna, direccion, this);
        } else {
            return null; // aún en cooldown
        }
    }


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

    public int getXPix() {
        return xPix;
    }

    public int getYPix() {
        return yPix;
    }

    public void setPosition(int row, int col) {
        this.linea = row;
        this.columna = col;
        this.xPix = col * tamCelda;
        this.yPix = row * tamCelda;
    }

    //renderizado suave
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        int centerX = xPix + tamCelda / 2;
        int centerY = yPix + tamCelda / 2;

        double angle = switch (direccion) {
            case UP -> 0;
            case RIGHT -> Math.PI / 2;
            case DOWN -> Math.PI;
            case LEFT -> -Math.PI / 2;
        };

        AffineTransform old = g2d.getTransform(); // guardamos el estado original

        // Rotamos alrededor del tanque
        g2d.rotate(angle, centerX, centerY);

        // cuerpo del tanque
        if (imgTanque != null) {
            g2d.drawImage(imgTanque, xPix, yPix, tamCelda, tamCelda, null);
        } else {
            g2d.setColor(color);
            g2d.fillRect(xPix, yPix, tamCelda, tamCelda);
        }

        //cañón
        if (imgCanon != null) {
            int ancho = 10 , largo = 30;
            int canonX = centerX - ancho / 2;
            int canonY = centerY - largo;
            g2d.drawImage(imgCanon, canonX, canonY, ancho, largo,null);
        }

        g2d.setTransform(old); // restauramos el estado original
    }
}
