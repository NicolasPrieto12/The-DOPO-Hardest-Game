package domain;

/**
 * Interfaz que define el contrato para todo objeto que pueda moverse en el tablero.
 * Cualquier clase que implemente esta interfaz debe definir su propia lógica de movimiento.
 */
public interface IMovable {

    /**
     * Ejecuta el movimiento del objeto según su lógica propia.
     */
    void move();
}
