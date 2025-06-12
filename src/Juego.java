import Game.Board;
import Game.CeldaType;
import Entidades.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.awt.Color.*;

public class Juego extends JFrame {
    private final Board board;
    private final Tanque jugador;
    private final int TAMACELD = 40;
    private final TableroPanel panel;
    private final List<Tanque> enemigos = new ArrayList<>();
    private final List<Bala> balas = new ArrayList<>();
    public Juego(Board board) {

        this.board = board;

        // Crear el jugador como un Tanque
        this.jugador = new Tanque(1, 1, 3, "nada", java.awt.Color.BLUE, true, 2); // línea, columna, vidas, habilidad, color, ¿es jugador?
        this.board.setCell(1, 1, CeldaType.PLAYER);

        // Crear enemigos de prueba
        Tanque enemigo1 = new Tanque(5, 5, 1, "nada", RED, false, 2);
        Tanque enemigo2 = new Tanque(8, 8, 1, "nada", GRAY, false, 4);

        board.setCell(5, 5, CeldaType.MALOROJ);
        board.setCell(8, 8, CeldaType.MALOGRI);

        enemigos.add(enemigo1);
        enemigos.add(enemigo2);

        setTitle("Tanquesitos moloncitos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Crear el panel pasándole el jugador
        this.panel = new TableroPanel(board, TAMACELD, jugador, enemigos, balas);
        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Control de teclas para mover al jugador
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (jugador.estaMoviendo()) return;

                int key = e.getKeyCode();
                switch (key) {
                    case KeyEvent.VK_W -> jugador.moverAnimado(Direccion.UP, CeldaType.PLAYER, board);
                    case KeyEvent.VK_S -> jugador.moverAnimado(Direccion.DOWN, CeldaType.PLAYER, board);
                    case KeyEvent.VK_A -> jugador.moverAnimado(Direccion.LEFT, CeldaType.PLAYER, board);
                    case KeyEvent.VK_D -> jugador.moverAnimado(Direccion.RIGHT, CeldaType.PLAYER, board);
                    case KeyEvent.VK_SPACE -> {
                        Bala nueva = jugador.shoot();
                        if (nueva != null) balas.add(nueva);
                    }

                }
            }
        });

        // Timer para animar suavemente al jugador
        new javax.swing.Timer(15, e -> {
            boolean redibujar = false;

            // Animación jugador
            if (jugador.estaMoviendo()) {
                jugador.animarPaso();
                redibujar = true;
            }

            // Animación enemigos
            for (Tanque enemigo : enemigos) {
                if (enemigo.estaMoviendo()) {
                    enemigo.animarPaso();
                    redibujar = true;
                } else {
                    // Movimiento simple
                    int col = enemigo.getColumna();
                    if (col >= board.getColumnas() - 2) {
                        enemigo.moverAnimado(Direccion.LEFT, CeldaType.MALOROJ, board);
                    } else if (col <= 1) {
                        enemigo.moverAnimado(Direccion.RIGHT, CeldaType.MALOROJ, board);
                    } else {
                        if (Math.random() < 0.5) {
                            enemigo.moverAnimado(Direccion.RIGHT, CeldaType.MALOROJ, board);
                        } else {
                            enemigo.moverAnimado(Direccion.LEFT, CeldaType.MALOROJ, board);
                        }
                    }
                }
            }

            // Mover balas
            for (Bala bala : balas) {
                if (bala.isActive()) {
                    bala.mover();
                }
            }

            // Colisiones con enemigos (y eliminar si mueren)
            Iterator<Tanque> it = enemigos.iterator();
            while (it.hasNext()) {
                Tanque enemigo = it.next();
                for (Bala bala : balas) {
                    if (bala.isActive() && colision(bala, enemigo)) {
                        if (enemigo.recibirDanno()) {
                            it.remove(); // eliminar enemigo muerto
                        }
                        bala.deactivate();
                        break;
                    }
                }
            }

            // Colisiones con jugador
            for (Bala bala : balas) {
                if (bala.isActive() && colision(bala, jugador)) {
                    jugador.recibirDanno();
                    bala.deactivate();
                }
            }

            // Eliminar balas fuera del mapa
            balas.removeIf(b -> b.getX() < 0 || b.getX() > getWidth() || b.getY() < 0 || b.getY() > getHeight());

            // Redibujar si algo cambió
            if (redibujar) panel.repaint();
        }).start();

    }

    private boolean colision(Bala bala, Tanque tanque) {
        Rectangle r1 = new Rectangle(bala.getX(), bala.getY(), 8, 8);
        Rectangle r2 = new Rectangle(tanque.getXPix(), tanque.getYPix(), 40, 40); // o tamCelda
        return r1.intersects(r2);
    }


    public static void main(String[] args) {
        Board board = new Board(15, 25);
        new Juego(board);
    }
}
