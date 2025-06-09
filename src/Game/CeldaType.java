package Game;

public enum CeldaType {
    EMPTY,        // espacio libre
    MURO,         // muro
    PLAYER,       // tanque jugador (azul)
    MALOROJ,    // enemigo tipo ráfaga (rojo)
    MALOVER,  // enemigo normal (verde)
    MALOGRI, // enemigo con balas rápidas (amarillo)
    OBJETIVO     // objetivo a destruir
}
