import javax.swing.*;
import java.awt.*;

public class TableroPanel extends JPanel {
    private final Board board;
    private final int TAMACELD;

    public TableroPanel(Board board, int tamaceld) {
        this.board = board;
        this.TAMACELD = tamaceld;
        setPreferredSize(new Dimension(board.getColumnas() * TAMACELD, board.getLineas() * TAMACELD));
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int row = 0; row < board.getLineas(); row++) {
            for (int col = 0; col < board.getColumnas(); col++) {
                drawCell(g, row, col);
            }
        }
    }

    private void drawCell(Graphics g, int row, int col) {
        int x = col * TAMACELD;
        int y = row * TAMACELD;

        CeldaType type = board.getCell(row, col);

        switch (type) {
            case EMPTY -> g.setColor(Color.WHITE);
            case MURO -> g.setColor(Color.DARK_GRAY);
            case PLAYER -> g.setColor(Color.BLUE);
            case MALOVER -> g.setColor(Color.GREEN);
            case MALOROJ -> g.setColor(Color.RED);
            case MALOAMA -> g.setColor(Color.YELLOW);
            case OBJETIVO -> g.setColor(Color.MAGENTA);
        }

        g.fillRect(x, y, TAMACELD, TAMACELD);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, TAMACELD, TAMACELD);
    }
}
