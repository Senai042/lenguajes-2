package Game;

import java.awt.Point;
import java.util.*;
import java.util.function.BiPredicate;

public class GridPathfinder {
    /**
     * Realiza un BFS en una grilla de tamaño rows×cols,
     * evitando las celdas donde isWall.test(fila, col) == true.
     * Devuelve la ruta más corta (lista de Points) desde start hasta goal,
     * incluyendo ambos extremos; si no hay ruta, lista vacía.
     */
    public static List<Point> bfs(
            Point start,
            Point goal,
            int rows,
            int cols,
            BiPredicate<Integer,Integer> isWall
    ) {
        boolean[][] visited = new boolean[rows][cols];
        Map<Point,Point> parent = new HashMap<>();
        Queue<Point> queue = new ArrayDeque<>();

        visited[start.x][start.y] = true;
        queue.add(start);

        // Desplazamientos: arriba, abajo, izquierda, derecha
        int[][] DIRS = {{-1,0},{1,0},{0,-1},{0,1}};

        while (!queue.isEmpty()) {
            Point cur = queue.poll();
            if (cur.equals(goal)) break;

            for (int[] d : DIRS) {
                int nr = cur.x + d[0], nc = cur.y + d[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                        && !visited[nr][nc]
                        && !isWall.test(nr, nc)) {
                    visited[nr][nc] = true;
                    Point next = new Point(nr, nc);
                    parent.put(next, cur);
                    queue.add(next);
                }
            }
        }

        // Si no llegamos al goal, no hay ruta
        if (!visited[goal.x][goal.y]) {
            return Collections.emptyList();
        }

        // Reconstruir ruta desde goal hasta start
        LinkedList<Point> path = new LinkedList<>();
        Point cur = goal;
        while (cur != null) {
            path.addFirst(cur);
            cur = parent.get(cur);
        }
        return path;
    }
}
