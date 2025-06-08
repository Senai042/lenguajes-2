import javax.swing.*;

public class Juego extends JFrame {
    private final Board board;
    private final Jugador jugador;
    private final int TAMACELD = 40;
    private final TableroPanel panel;

    public Juego(Board board) {
        this.board = board;
        this.jugador = new Jugador(1, 1);
        this.board.setCell(1, 1, CeldaType.PLAYER);

        setTitle("Tanquesitos moloncitos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        panel = new TableroPanel(board, TAMACELD);
        add(panel);
        pack(); // ajusta el tamaño de la ventana al tamaño preferido del panel
        setLocationRelativeTo(null);
        setVisible(true);

        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                int key = e.getKeyCode();
                switch (key) {
                    case java.awt.event.KeyEvent.VK_W -> jugador.mover(-1, 0, board);
                    case java.awt.event.KeyEvent.VK_S -> jugador.mover(1, 0, board);
                    case java.awt.event.KeyEvent.VK_A -> jugador.mover(0, -1, board);
                    case java.awt.event.KeyEvent.VK_D -> jugador.mover(0, 1, board);
                }
                panel.repaint(); // redibuja solo el panel, no la ventana completa
            }
        });
    }

    public static void main(String[] args) {
        Board board = new Board(15, 25);
        new Juego(board);
    }
}
