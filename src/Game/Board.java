package Game;

public class Board {
    private final int lineas;
    private final int columnas;
    private CeldaType[][] grid;

    public Board(int rows, int cols) {
        this.lineas = rows;
        this.columnas = cols;
        this.grid = new CeldaType[rows][cols];
        initializeBoard();
    }

    private void initializeBoard() {
        // Inicializar todas las celdas como vacías
        for (int i = 0; i < lineas; i++) {
            for (int j = 0; j < columnas; j++) {
                grid[i][j] = CeldaType.EMPTY;
            }
        }

        // Colocar algunos muros y un jugador como ejemplo
        grid[1][1] = CeldaType.PLAYER;
        grid[2][3] = CeldaType.MURO;

    }

    public CeldaType getCell(int row, int col) {
        return grid[row][col];
    }

    public void setCell(int row, int col, CeldaType type) {
        grid[row][col] = type;
    }

    public int getLineas() {
        return lineas;
    }

    public int getColumnas() {
        return columnas;
    }
}
