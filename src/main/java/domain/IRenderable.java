package domain;

import java.awt.Graphics;

/**
 * Interfaz que define el contrato para todo objeto que pueda dibujarse en pantalla.
 * Permite que el motor de renderizado dibuje cualquier objeto sin conocer su tipo concreto.
 */
public interface IRenderable {

    /**
     * Dibuja el objeto en pantalla usando el contexto gráfico proporcionado.
     *
     * @param g El objeto Graphics usado como pincel para dibujar en el panel.
     */
    void render(Graphics g);
}
