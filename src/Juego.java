import Game.Board;
import Game.CeldaType;
import Entidades.*;
import Game.MapData;
import Game.MapLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Entidades.Objetivo;

public class Juego extends JFrame {
    private final Board board;
    private final Tanque jugador;
    private final int TAMACELD = 40;
    private final TableroPanel panel;
    private final List<Tanque> enemigos = new ArrayList<>();
    private final List<Bala> balas = new ArrayList<>();
    private final Objetivo objetivo;

    public Juego(MapData datos) {

        this.board = datos.board;
        this.jugador = datos.jugador;
        this.enemigos.addAll(datos.enemigos);
        this.objetivo = datos.objetivo;
        this.panel = new TableroPanel(board, TAMACELD, jugador, enemigos, balas);

        setTitle("Tanquesitos moloncitos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Crear el panel pasándole el jugador

        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);


        // Control de teclas para mover al jugador
        List<Tanque> todos = new ArrayList<>(enemigos);
        todos.add(jugador);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (jugador.estaMoviendo()) return;

                int key = e.getKeyCode();
                switch (key) {
                    case KeyEvent.VK_W -> jugador.moverAnimado(Direccion.UP, CeldaType.PLAYER, board, todos);
                    case KeyEvent.VK_S -> jugador.moverAnimado(Direccion.DOWN, CeldaType.PLAYER, board, todos);
                    case KeyEvent.VK_A -> jugador.moverAnimado(Direccion.LEFT, CeldaType.PLAYER, board, todos);
                    case KeyEvent.VK_D -> jugador.moverAnimado(Direccion.RIGHT, CeldaType.PLAYER, board, todos);
                    case KeyEvent.VK_SPACE -> {
                        Bala nueva = jugador.shoot();
                        if (nueva != null) balas.add(nueva);
                    }
                }
                if (!enemigos.isEmpty()) {
                    Tanque enemigo = enemigos.get(0);
                    if (key == KeyEvent.VK_I) enemigo.moverAnimado(Direccion.UP, CeldaType.MALOROJ, board);
                    if (key == KeyEvent.VK_K) enemigo.moverAnimado(Direccion.DOWN, CeldaType.MALOROJ, board);
                    if (key == KeyEvent.VK_J) enemigo.moverAnimado(Direccion.LEFT, CeldaType.MALOROJ, board);
                    if (key == KeyEvent.VK_L) enemigo.moverAnimado(Direccion.RIGHT, CeldaType.MALOROJ, board);
                    if (key == KeyEvent.VK_E) { // E de "Enemy shoots"
                        Bala nueva = enemigo.shoot();
                        if (nueva != null) balas.add(nueva);
                    }
                }
            }
        });

        // Timer para animar suavemente al jugador
        new javax.swing.Timer(15, e -> {


            // Animación jugador
            if (jugador.estaMoviendo()) {
                jugador.animarPaso();
            }

            // Animación enemigos
            for (Tanque enemigo : enemigos) {
                if (enemigo.estaMoviendo()) {
                    enemigo.animarPaso();
                }
            }

            // Mover balas
            for (Bala bala : balas) {
                if (bala.isActive()) {
                    bala.mover();

                    int col = bala.getX() / TAMACELD;
                    int row = bala.getY() / TAMACELD;

                    if (bala.getX() < 0 ||
                            bala.getX() >= board.getColumnas() * TAMACELD ||
                            bala.getY() < 0 ||
                            bala.getY() >= board.getLineas() * TAMACELD) {
                        bala.deactivate();

                    } else if (board.isWall(row, col)) {
                        bala.deactivate();
                    }else if (objetivo != null &&
                            row == objetivo.getFila() &&
                            col == objetivo.getColumna()) {

                        boolean destruido = objetivo.recibirDanno();
                        bala.deactivate();

                        if (destruido) {
                            System.out.println("¡Objetivo destruido!");
                            pasarAlSiguienteNivel();
                            return;
                        }
                    }

                }
            }


            // Colisiones con enemigos (y eliminar si mueren)
            Iterator<Tanque> it = enemigos.iterator();
            while (it.hasNext()) {
                Tanque enemigo = it.next();
                for (Bala bala : balas) {
                    if (bala.isActive() && colision(bala, enemigo) && bala.getPropietario() != enemigo) {
                        if (enemigo.recibirDanno()) {
                            it.remove();
                        }
                        bala.deactivate();
                    }
                }
            }

            // Colisiones con jugador
            for (Bala bala : balas) {
                if (bala.isActive() && colision(bala, jugador) && bala.getPropietario() != jugador) {
                    boolean muerto = jugador.recibirDanno();
                    bala.deactivate();

                    if (muerto) {
                        // Aquí puedes implementar lo que sucede cuando muere el jugador.
                        System.out.println("Jugador muerto!");
                        // Por ejemplo: finalizar el juego o mostrar pantalla de game over.
                    }
                }
            }

            // Limiar memoria balas.
            balas.removeIf(b -> !b.isActive());

            // Redibujar
            panel.repaint();
        }).start();

    }

    private boolean colision(Bala bala, Tanque tanque) {
        Rectangle r1 = new Rectangle(bala.getX(), bala.getY(), 8, 8);
        Rectangle r2 = new Rectangle(tanque.getXPix(), tanque.getYPix(), 40, 40); // o tamCelda
        return r1.intersects(r2);
    } 


    private static int nivelActual = 1;

    private void pasarAlSiguienteNivel() {
        nivelActual++; // Avanzamos al siguiente número

        String ruta = "src/mapas/nivel" + nivelActual + ".txt";
        MapData datos = MapLoader.cargarMapa(ruta);

        // Si no se encuentra el mapa, significa que el juego terminó
        if (datos == null || datos.board == null) {
            JOptionPane.showMessageDialog(this, "¡Felicidades! Has completado todos los niveles.");
            System.exit(0);
            return;
        }

        // Si el mapa sí existe, cargamos el nuevo nivel
        dispose(); // cerramos la ventana actual
        new Juego(datos); // creamos el siguiente nivel
    }



    public static void main(String[] args) {
        MapData datos = MapLoader.cargarMapa("src/mapas/nivel1.txt");
        new Juego(datos);

    }
}
