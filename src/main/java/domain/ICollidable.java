package domain;

import java.awt.Rectangle;

/**
 * Interfaz que define el contrato para todo objeto que pueda participar en colisiones.
 * Permite que el sistema de colisiones trabaje con cualquier objeto sin conocer su tipo concreto.
 */
public interface ICollidable {

    /**
     * Retorna el área rectangular que representa los límites del objeto.
     * Se usa para calcular intersecciones entre objetos.
     *
     * @return Rectangle con la posición y tamaño del objeto.
     */
    Rectangle getBounds();

    /**
     * Verifica si este objeto colisiona con otro objeto colisionable.
     *
     * @param other El otro objeto con el que se verifica la colisión.
     * @return true si hay colisión, false en caso contrario.
     */
    boolean collidesWith(ICollidable other);
}
