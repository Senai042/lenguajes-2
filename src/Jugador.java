import Game.Board;
import Game.CeldaType;

public class Jugador {
    private int fila, columna;
    private int xPix, yPix;
    private final int tamCelda;
    private boolean moviendo = false;

    public Jugador(int fila, int columna, int tamCelda) {
        this.fila = fila;
        this.columna = columna;
        this.tamCelda = tamCelda;
        this.xPix = columna * tamCelda;
        this.yPix = fila * tamCelda;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public int getXPix() {
        return xPix;
    }

    public int getYPix() {
        return yPix;
    }

    public boolean estaMoviendo() {
        return moviendo;
    }

    // Este método solo inicia el movimiento, la animación se hace en otro lado
    public boolean mover(int dfila, int dcol, Board board) {
        if (moviendo) return false;

        int nuevaFila = fila + dfila;
        int nuevaCol = columna + dcol;

        if (nuevaFila < 0 || nuevaFila >= board.getLineas() || nuevaCol < 0 || nuevaCol >= board.getColumnas())
            return false;

        if (board.getCell(nuevaFila, nuevaCol) == CeldaType.MURO)
            return false;

        board.setCell(fila, columna, CeldaType.EMPTY);
        fila = nuevaFila;
        columna = nuevaCol;
        board.setCell(fila, columna, CeldaType.PLAYER);

        moviendo = true;
        return true;
    }

    public void animarPaso() {
        int destinoX = columna * tamCelda;
        int destinoY = fila * tamCelda;

        if (xPix < destinoX) xPix += 2;
        if (xPix > destinoX) xPix -= 2;
        if (yPix < destinoY) yPix += 2;
        if (yPix > destinoY) yPix -= 2;

        if (xPix == destinoX && yPix == destinoY) {
            moviendo = false;
        }
    }
}
