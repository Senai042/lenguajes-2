package Game;

import Entidades.Tanque;
import java.util.List;

public class MapData {
    public Board board;
    public Tanque jugador;
    public List<Tanque> enemigos;
    public int[][] matrizProlog;

    public MapData(Board board, Tanque jugador, List<Tanque> enemigos, int[][] matrizProlog) {
        this.board = board;
        this.jugador = jugador;
        this.enemigos = enemigos;
        this.matrizProlog = matrizProlog;
    }
}
