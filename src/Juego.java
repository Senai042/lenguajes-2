import Game.Board;
import Game.CeldaType;
import Entidades.*;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import static java.awt.Color.*;

public class Juego extends JFrame {
    private final Board board;
    private final Tanque jugador;
    private final int TAMACELD = 40;
    private final TableroPanel panel;
    private final List<Tanque> enemigos = new ArrayList<>();
    public Juego(Board board) {

        this.board = board;

        // Crear el jugador como un Tanque
        this.jugador = new Tanque(1, 1, 3, "nada", java.awt.Color.BLUE, true, 2); // línea, columna, vidas, habilidad, color, ¿es jugador?
        this.board.setCell(1, 1, CeldaType.PLAYER);

        // Crear enemigos de prueba
        Tanque enemigo1 = new Tanque(5, 5, 1, "nada", RED, false, 2);
        Tanque enemigo2 = new Tanque(8, 8, 1, "nada", GRAY, false, 6);

        board.setCell(5, 5, CeldaType.MALOROJ);
        board.setCell(8, 8, CeldaType.MALOGRI);

        enemigos.add(enemigo1);
        enemigos.add(enemigo2);

        setTitle("Tanquesitos moloncitos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Crear el panel pasándole el jugador
        this.panel = new TableroPanel(board, TAMACELD, jugador, enemigos);
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
                }
            }
        });

        // Timer para animar suavemente al jugador
        new javax.swing.Timer(15, e -> {
            boolean redibujar = false;

            //jugador
            if (jugador.estaMoviendo()) {
                jugador.animarPaso();
                redibujar = true;
            }
            //enemigos
            for (Tanque enemigo : enemigos) {
                if (enemigo.estaMoviendo()) {
                    enemigo.animarPaso();
                    redibujar = true;
                } else {
                    // Movimiento simple horizontal de ida y vuelta
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
            if (redibujar) panel.repaint();
        }).start();
    }

    public static void main(String[] args) {
        Board board = new Board(15, 25);
        new Juego(board);
    }
}
