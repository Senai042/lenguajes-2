import Game.*;
import Entidades.*;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
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

    private final PrologConnector pc = new PrologConnector();  // Conector a Prolog
    private final Map<Tanque, Queue<Point>> rutasEnemigos = new HashMap<>();
    private static final int DISTANCIA_UMBRAL = 6;
    private Point ultimaCelJugador = null;


    public Juego(MapData datos) {
        this.board = datos.board;
        this.jugador = datos.jugador;
        this.enemigos.addAll(datos.enemigos);
        this.objetivo = datos.objetivo;
        this.pc.cargarMapa(this.board);

        // Crear el jugador como un Tanque
        //.jugador = new Tanque(1, 1, 3, "nada", java.awt.Color.BLUE, true, 2); // línea, columna, vidas, habilidad, color, ¿es jugador?
        this.panel = new TableroPanel(board, TAMACELD, jugador, enemigos, balas);
        ultimaCelJugador = new Point(jugador.getLinea(), jugador.getColumna());

        setTitle("Tanquesitos moloncitos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Crear el panel pasándole el jugador

        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);


        // Control de teclas para mover al jugador
        final List<Tanque> todos = new ArrayList<>(enemigos);
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
        new Timer(20, e -> {
            // Animación jugador
            if (jugador.estaMoviendo()) {
                jugador.animarPaso();
            }
            // Recalcular ruta si el jugador cambió de celda
            Point celActualJugador = new Point(jugador.getLinea(), jugador.getColumna());
            if (!celActualJugador.equals(ultimaCelJugador)) {
                rutasEnemigos.clear();  // fuerza recálculo para todos los enemigos
                ultimaCelJugador = celActualJugador;
            }
            // Animación enemigos
            for (Tanque enemigo : enemigos) {
                // A) Sacar sus celdas y las del jugador
                int er = enemigo.getLinea(), ec = enemigo.getColumna();
                int jr = celActualJugador.x, jc = celActualJugador.y;
                int manh = Math.abs(er - jr) + Math.abs(ec - jc);  // Calcula distancia Manhattan

                // DEBUG: imprime distancia
                System.out.printf("Enemigo@(%d,%d) — Jugador@(%d,%d) => dist=%d\n",
                        er, ec, jr, jc, manh);

                // Solo si no está ya moviéndose y está dentro del umbral
                if (!enemigo.estaMoviendo() && manh <= DISTANCIA_UMBRAL) {
                    List<Point> ruta = GridPathfinder.bfs(
                            new Point(er, ec),
                            new Point(jr, jc),
                            board.getLineas(),
                            board.getColumnas(),
                            board::isWall   // referencia al método que comprueba si es muro
                    );
                    System.out.println("► Ruta en Java: " + ruta);
                    //rutasEnemigos.put(enemigo, new LinkedList<>(ruta));
                    if (!ruta.isEmpty()) {
                        ruta.remove(0);
                    }
                    System.out.println("► Ruta en Java (sin start): " + ruta);

                    rutasEnemigos.put(enemigo, new LinkedList<>(ruta));
                }

                //    arrancamos el siguiente movimiento de celda:
                Queue<Point> cola = rutasEnemigos.get(enemigo);
                if (cola != null && !cola.isEmpty() && !enemigo.estaMoviendo()) {
                    Point sig = cola.poll();
                    // sig.x = fila, sig.y = columna
                    Direccion dir = obtenerDireccion(er, ec, sig.x, sig.y);
                    enemigo.moverAnimado(dir, CeldaType.MALOROJ, board, todos);
                    System.out.println("► Enemigo inicia moverAnimado " + dir);
                }

                // si está en mitad de animación, avanzamos un fotograma
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


        }).start();}


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

        private Direccion obtenerDireccion ( int cr, int cc, int nr, int nc){
            if (nr > cr) return Direccion.DOWN;
            if (nr < cr) return Direccion.UP;
            if (nc > cc) return Direccion.RIGHT;
            if (nc < cc) return Direccion.LEFT;
            return Direccion.UP; // fallback
        }

        public static void main (String[]args){
            MapData datos = MapLoader.cargarMapa("src/mapas/nivel1.txt");
            new Juego(datos);
        }
    }


