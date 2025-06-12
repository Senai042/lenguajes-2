package Entidades;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Bala {
    private double xPix, yPix;
    private final Direccion direccion;
    private boolean active = true;
    private final int velocidad = 4; // velocidad base en píxeles
    private BufferedImage imagen;

    public Bala(int linea, int columna, Direccion direccion) {
        this.xPix = columna * 40 + 20 - 4; // centrada
        this.yPix = linea * 40 + 20 - 4;
        this.direccion = direccion;

        try {
            imagen = ImageIO.read(getClass().getResource("/Textures/bulletBeigeSilver.png"));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("No se pudo cargar la imagen de la bala: " + e.getMessage());
        }
    }

    public void mover() {
        double diagSpeed = velocidad / Math.sqrt(2);

        switch (direccion) {
            case UP -> yPix -= velocidad;
            case DOWN -> yPix += velocidad;
            case LEFT -> xPix -= velocidad;
            case RIGHT -> xPix += velocidad;
            case UP_LEFT -> {
                yPix -= diagSpeed;
                xPix -= diagSpeed;
            }
            case UP_RIGHT -> {
                yPix -= diagSpeed;
                xPix += diagSpeed;
            }
            case DOWN_LEFT -> {
                yPix += diagSpeed;
                xPix -= diagSpeed;
            }
            case DOWN_RIGHT -> {
                yPix += diagSpeed;
                xPix += diagSpeed;
            }
        }
    }

    public void draw(Graphics g) {
        if (imagen != null) {
            g.drawImage(imagen, (int) xPix, (int) yPix, 8, 8, null);
        } else {
            g.setColor(Color.ORANGE);
            g.fillOval((int) xPix, (int) yPix, 8, 8);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    public int getX() {
        return (int) xPix;
    }

    public int getY() {
        return (int) yPix;
    }
}
