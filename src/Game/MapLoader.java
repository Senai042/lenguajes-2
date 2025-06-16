package Game;

import Entidades.Objetivo;
import Entidades.Tanque;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class MapLoader {
    public static MapData cargarMapa(String rutaArchivo) {
        List<Tanque> enemigos = new ArrayList<>();
        Board board = null;
        Tanque jugador = null;
        int[][] matrizProlog = null;
        Objetivo objetivo = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            List<String> lineas = new ArrayList<>();
            String linea;
            while ((linea = reader.readLine()) != null) {
                lineas.add(linea);
            }

            int filas = lineas.size();
            int columnas = lineas.get(0).length();
            board = new Board(filas, columnas);
            matrizProlog = new int[filas][columnas];

            for (int row = 0; row < filas; row++) {
                for (int col = 0; col < columnas; col++) {
                    char c = lineas.get(row).charAt(col);
                    int valorProlog = 0;

                    switch (c) {
                        case '#' -> {
                            board.setCell(row, col, CeldaType.MURO);
                            valorProlog = 1;
                        }
                        case 'P' -> {
                            board.setCell(row, col, CeldaType.PLAYER);
                            jugador = new Tanque(row, col, 3, "nada", Color.BLUE, true, 2);
                            valorProlog = 2;
                        }
                        case 'R' -> {
                            board.setCell(row, col, CeldaType.MALOROJ);
                            enemigos.add(new Tanque(row, col, 5, "Vidas", Color.RED, false, 1));
                            valorProlog = 3;
                        }
                        case 'G' -> {
                            board.setCell(row, col, CeldaType.MALOGRI);
                            enemigos.add(new Tanque(row, col, 3, "normal", Color.GRAY, false, 2));
                            valorProlog = 4;
                        }
                        case 'A' -> {
                            board.setCell(row, col, CeldaType.MALOVER);
                            enemigos.add(new Tanque(row, col, 1, "rapido", Color.GREEN, false, 4));
                            valorProlog = 5;
                        }
                        case 'O' -> {
                            board.setCell(row, col, CeldaType.OBJETIVO);
                            objetivo = new Objetivo(row, col, 6); // 3 impactos para destruirlo
                            valorProlog = 9;
                        }
                        default -> {
                            board.setCell(row, col, CeldaType.EMPTY);
                            valorProlog = 0;
                        }
                    }
                    matrizProlog[row][col] = valorProlog;
                }
            }

        } catch (IOException e) {
            System.err.println("Error leyendo el mapa: " + e.getMessage());
        }

        return new MapData(board, jugador, enemigos, matrizProlog, objetivo);
    }
}
