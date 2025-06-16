package Game;

import org .jpl7.*;

import java.awt.Point;
import java.io.File;
import java.lang.Integer;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PrologConnector {
    public PrologConnector() {
        // 1) Localiza el resource
        URL res = getClass().getClassLoader().getResource("prolog/pathfinder.pl");
        if (res == null) {
            throw new RuntimeException("No encontré prolog/pathfinder.pl en resources");
        }

        // 2) Convierte el URL en un File para obtener el path correcto en Windows
        String plPath;
        try {
            File f = new File(res.toURI());
            if (!f.exists()) {
                throw new RuntimeException("El fichero no existe: " + f.getAbsolutePath());
            }
            // Prolog acepta forward-slashes incluso en Windows
            plPath = f.getAbsolutePath().replace("\\", "/");
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error convirtiendo URL a URI", e);
        }

        // 3) Carga el .pl
        Query q = new Query("consult('" + plPath + "')");
        if (!q.hasSolution()) {
            throw new RuntimeException("No pude cargar " + plPath);
        }
        System.out.println("✓ pathfinder.pl cargado en Prolog desde: " + plPath);
    }

    /**
     * Ejecuta cualquier consulta Prolog que no devuelva datos
     */
    public void consultar(String goal) {
        if (!new Query(goal).hasSolution()) {
            throw new RuntimeException("Goal falló: " + goal);
        }
    }

    /**
     * Carga en Prolog todos los hechos muro/2 y camino/2 según el Board Java
     */
    public void cargarMapa(Board board) {
        System.out.println("[PrologConnector] Cargando mapa en Prolog…");
        // 1) Limpia lo anterior (muros, caminos, inicios, objetivos, visitados)
        consultar("reset");
        int count = 0;
        for (int fila = 0; fila < board.getLineas(); fila++) {
            for (int col = 0; col < board.getColumnas(); col++) {
                String fact = board.isWall(fila, col)
                        ? String.format("assert(muro(%d,%d))", fila, col)
                        : String.format("assert(camino(%d,%d))", fila, col);
                consultar(fact);
                count++;
            }
        }
        System.out.println("[PrologConnector] Asertados " + count + " hechos (camino/muro)");
        // 2) Recorre todas las celdas y aserta muro/2 o camino/2
        for (int fila = 0; fila < board.getLineas(); fila++) {
            for (int col = 0; col < board.getColumnas(); col++) {
                if (board.isWall(fila, col)) {
                    consultar(String.format("assert(muro(%d,%d))", fila, col));
                } else {
                    consultar(String.format("assert(camino(%d,%d))", fila, col));
                }
            }
        }
    }

    public List<Point> buscaRuta(int xi, int yi, int xf, int yf) {
        String varR = "R";
        String goal = String.format(
                "clear_search, " +
                        "assert(inicio(%d,%d)), " +
                        "assert(objetivo(%d,%d)), " +
                        "buscar_ruta(%s)",
                xi, yi, xf, yf, varR
        );

        // DEBUG: imprime el goal que vas a enviar
        System.out.println("[PrologConnector] Goal: " + goal);

        Query q = new Query(goal);
        List<Point> ruta = new ArrayList<>();
        if (!q.hasSolution()) {
            System.out.println("[PrologConnector] ¡Prolog no encontró ruta!");
            return ruta;
        }

        Term rTerm = q.oneSolution().get(varR);
        System.out.println("[PrologConnector] Raw R term: " + rTerm);

        Term[] elems = Util.listToTermArray(rTerm);
        for (Term elem : elems) {
            System.out.println("[PrologConnector] Elem: " + elem);
            if (elem.isCompound() && ((Compound)elem).name().equals(",")) {
                int x = Integer.parseInt(((Compound) elem).arg(1).toString());
                int y = Integer.parseInt(((Compound) elem).arg(2).toString());
                ruta.add(new Point(x, y));
            }
        }
        System.out.println("[PrologConnector] Ruta Java: " + ruta);
        return ruta;
    }


}



   /* public List<Point> buscaRuta(int xi, int yi, int xf, int yf) {
        String varR = "R";
        // 1) construye una única goal que borra e inserta hechos y luego llama a buscar_ruta/1
        String goal = String.format(
                "retractall(inicio(_,_)), retractall(objetivo(_,_)), " +
                        "assert(inicio(%d,%d)), assert(objetivo(%d,%d)), " +
                        "buscar_ruta(%s)",
                xi, yi, xf, yf, varR
        );

        Query q = new Query(goal);
        List<Point> ruta = new ArrayList<>();
        if (!q.hasSolution()) {
            // no hay ruta
            return ruta;
        }

        // 2) recupera la variable R
        Term rTerm = q.oneSolution().get(varR);
        Term[] elems = Util.listToTermArray(rTerm);

        // 3) convierte cada par (X,Y) en java.awt.Point
        for (Term elem : elems) {
            if (elem.isCompound() && ((Compound)elem).name().equals(",")) {
                Compound pair = (Compound) elem;
                int x = Integer.parseInt(pair.arg(1).toString());
                int y = Integer.parseInt(pair.arg(2).toString());
                ruta.add(new Point(x, y));
            }
        }
        return ruta;
    }

}*/

