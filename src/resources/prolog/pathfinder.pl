% pathfinder.pl
% -------------------------
% BFS Pathfinding mínimo para tanques enemigos.
% Encuentra la ruta más corta (en número de pasos) desde inicio hasta objetivo.
% -------------------------

%% Predicados dinámicos
:- dynamic muro/2.
:- dynamic camino/2.
:- dynamic inicio/2.
:- dynamic objetivo/2.
:- dynamic visitado/2.

%% reset/0
%% Limpia todos los hechos (muros, caminos, inicios, objetivos, visitados)
reset :-
    retractall(muro(_,_)),
    retractall(camino(_,_)),
    retractall(inicio(_,_)),
    retractall(objetivo(_,_)),
    retractall(visitado(_,_)).

%% clear_search/0
%% Sólo borra hechos de búsqueda, NO muros ni caminos
clear_search :-
    retractall(inicio(_,_)),
    retractall(objetivo(_,_)),
    retractall(visitado(_,_)).

%% vecino(+X,+Y,-X1,-Y1)
%% Celdas adyacentes en las cuatro direcciones
vecino(X,Y,X1,Y)   :- X1 is X + 1.
vecino(X,Y,X1,Y)   :- X1 is X - 1.
vecino(X,Y,X,Y1)   :- Y1 is Y + 1.
vecino(X,Y,X,Y1)   :- Y1 is Y - 1.

%% buscar_ruta(-Ruta)
%% Ruta mínima (BFS) desde inicio hasta objetivo
buscar_ruta(Ruta) :-

    shortest_path(Ruta).

%% shortest_path(-Ruta)
shortest_path(Ruta) :-
    inicio(Xi,Yi),
    objetivo(Xf,Yf),
    bfs([[(Xi,Yi)]], (Xf,Yf), RevRuta),
    reverse(RevRuta, Ruta).

%% bfs(+ColaDeRutas, +Meta, -RutaEncontrada)
bfs([[Meta|Camino]|_], Meta, [Meta|Camino]).
bfs([RutaActual|Resto], Meta, RutaEncontrada) :-
    expand(RutaActual, NuevasRutas),
    append(Resto, NuevasRutas, ColaNueva),
    bfs(ColaNueva, Meta, RutaEncontrada).

%% expand(+RutaHastaAhora, -ListaDeNuevasRutas)
expand([(X,Y)|Camino], Nuevas) :-
    findall(
      [(NX,NY),(X,Y)|Camino],
      (
        vecino(X,Y,NX,NY),
        camino(NX,NY),
        \+ muro(NX,NY),          % <-- CORREGIDO: NX,NY en lugar de XN,NY
        \+ member((NX,NY),Camino)
      ),
      Nuevas
    ).
