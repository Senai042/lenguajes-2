import Entidades.Bala;
import Entidades.Tanque;
import Game.Board;
import Game.CeldaType;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class TableroPanel extends JPanel {
    private final Board board;
    private final int TAMACELD;
    private final Tanque jugador;
    private final List<Tanque> enemigos;
    private final List<Bala> balas;
    private Image fondo;
    private Image muros;
    private BufferedImage imgObjetivo;

    public TableroPanel(Board board, int tamaceld, Tanque jugador, List<Tanque> enemigos,List<Bala> balas) {
        this.board = board;
        this.TAMACELD = tamaceld;
        this.jugador = jugador;
        this.enemigos = enemigos;
        this.balas = balas;
        setPreferredSize(new Dimension(board.getColumnas() * TAMACELD, board.getLineas() * TAMACELD));
        setBackground(Color.BLACK);

        try {
            fondo = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Textures/grass.png"));
            muros = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Textures/sandbagBrown.png"));
        } catch (Exception e) {
            System.err.println("Error cargando texturas: " + e.getMessage());
        }
        try {
            imgObjetivo = ImageIO.read(getClass().getResource("/Textures/objetivo.png"));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("No se pudo cargar la imagen del objetivo: " + e.getMessage());
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fondo != null) {
            g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        for (int row = 0; row < board.getLineas(); row++) {
            for (int col = 0; col < board.getColumnas(); col++) {
                drawCell(g, row, col);
            }
        }

        for (Tanque enemigo : enemigos) {
            enemigo.draw(g);
        }

        jugador.draw(g);
        for (Bala bala : balas) {
            if (bala.isActive()) {
                bala.draw(g);
            }
        }
    }

    private void drawCell(Graphics g, int row, int col) {
        int x = col * TAMACELD;
        int y = row * TAMACELD;

        CeldaType type = board.getCell(row, col);

        switch (type) {
            case MURO -> {
                if (muros != null) {
                    g.drawImage(muros, x, y, TAMACELD, TAMACELD, this);
                    return; // ya dibujamos la textura, no seguimos
                } else {
                    g.setColor(Color.DARK_GRAY);
                }
            }
            case EMPTY -> g.setColor(new Color(0, 0, 0, 0)); // transparente sobre fondo
            case PLAYER -> g.setColor(new Color(0, 0, 0, 0)); // ya se dibuja por separado
            case MALOVER -> g.setColor(new Color(0, 0, 0, 0));
            case MALOROJ -> g.setColor(new Color(0, 0, 0, 0));
            case MALOGRI -> g.setColor(new Color(0, 0, 0, 0));
            case OBJETIVO -> {
                if (imgObjetivo != null) {
                    g.drawImage(imgObjetivo, x, y, TAMACELD, TAMACELD, null);
                } else {
                    g.setColor(Color.MAGENTA);
                    g.fillRect(x, y, TAMACELD, TAMACELD);
                }
            }

        }

        g.fillRect(x, y, TAMACELD, TAMACELD);
        g.drawRect(x, y, TAMACELD, TAMACELD);
    }

}
