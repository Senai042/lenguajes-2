public class Jugador {
    private int fila;
    private int columna;

    public Jugador(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public void mover(int dfila, int dcolumna, Board board) {
        int nuevaFila = fila + dfila;
        int nuevaCol = columna + dcolumna;

        // Validar límites
        if (nuevaFila < 0 || nuevaFila >= board.getLineas() || nuevaCol < 0 || nuevaCol >= board.getColumnas()) {
            return;
        }

        // Validar que no sea un muro
        if (board.getCell(nuevaFila, nuevaCol) == CeldaType.MURO) {
            return;
        }

        // Limpiar celda actual
        board.setCell(fila, columna, CeldaType.EMPTY);

        // Mover
        fila = nuevaFila;
        columna = nuevaCol;

        // Actualizar celda nueva
        board.setCell(fila, columna, CeldaType.PLAYER);
    }
}
