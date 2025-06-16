package Game;

import Entidades.Tanque;
import java.util.List;
import Entidades.Objetivo;

public class MapData {
    public Board board;
    public Tanque jugador;
    public List<Tanque> enemigos;
    public int[][] matrizProlog;
    public Objetivo objetivo;

    public MapData(Board board, Tanque jugador, List<Tanque> enemigos, int[][] matrizProlog, Objetivo objetivo) {
        this.board = board;
        this.jugador = jugador;
        this.enemigos = enemigos;
        this.matrizProlog = matrizProlog;
        this.objetivo = objetivo;
    }
}
