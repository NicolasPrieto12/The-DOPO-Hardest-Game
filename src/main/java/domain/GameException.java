package domain;

/**
 * Excepción personalizada del juego The DOPO Hardest Game.
 * Se lanza cuando ocurre un error relacionado con la lógica del juego,
 * como intentar acceder a un nivel inexistente, cargar una partida corrupta,
 * o inicializar el juego con parámetros inválidos.
 *
 * <p>Todos los errores del juego deben lanzar esta excepción en lugar de
 * excepciones genéricas, para facilitar el manejo centralizado de errores
 * y el registro en el log.</p>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>
 *   if (levels == null || levels.isEmpty()) {
 *       throw new GameException("No se puede iniciar el juego sin niveles.");
 *   }
 * </pre>
 */
public class GameException extends RuntimeException {

    /**
     * Crea una excepción con el mensaje descriptivo del error.
     *
     * @param message Descripción del error ocurrido.
     */
    public GameException(String message) {
        super(message);
    }

    /**
     * Crea una excepción con mensaje descriptivo y la causa original del error.
     * Útil para envolver excepciones de bajo nivel (IOException, NullPointerException, etc.)
     * con contexto del juego.
     *
     * @param message Descripción del error ocurrido.
     * @param cause   La excepción original que causó este error.
     */
    public GameException(String message, Throwable cause) {
        super(message, cause);
    }
}
