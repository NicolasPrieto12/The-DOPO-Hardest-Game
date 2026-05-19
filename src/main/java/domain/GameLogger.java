package domain;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase utilitaria para el registro de errores y eventos del juego.
 * Escribe los mensajes en un archivo de log llamado {@code game_errors.log}
 * ubicado en el directorio de ejecución del juego.
 *
 * <p>Todos los métodos son estáticos, no se necesita instanciar esta clase.</p>
 *
 * <p>Formato de cada entrada en el log:</p>
 * <pre>
 *   [2026-05-10 14:32:05] [ERROR] No se pudo cargar la partida: archivo no encontrado
 *   [2026-05-10 14:32:10] [INFO]  Partida guardada correctamente en: partida.dopo
 * </pre>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>
 *   GameLogger.error("No se pudo cargar la partida", e);
 *   GameLogger.info("Partida guardada correctamente en: " + filePath);
 * </pre>
 */
public class GameLogger {

    /** Nombre del archivo donde se escriben los logs. */
    private static final String LOG_FILE = "game_errors.log";

    /** Formato de fecha y hora para cada entrada del log. */
    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Registra un mensaje de error en el log junto con el stack trace de la excepción.
     * Se usa cuando ocurre una excepción que debe quedar registrada.
     *
     * @param message Descripción del error ocurrido.
     * @param e       La excepción que causó el error.
     */
    public static void error(String message, Throwable e) {
        write("[ERROR] " + message);
        if (e != null) {
            write("        Causa: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    /**
     * Registra un mensaje de error simple en el log sin excepción asociada.
     *
     * @param message Descripción del error ocurrido.
     */
    public static void error(String message) {
        write("[ERROR] " + message);
    }

    /**
     * Registra un mensaje informativo en el log.
     * Se usa para eventos importantes como guardar o cargar partidas.
     *
     * @param message El mensaje informativo a registrar.
     */
    public static void info(String message) {
        write("[INFO]  " + message);
    }

    /**
     * Registra una advertencia en el log.
     * Se usa para situaciones inesperadas que no son errores críticos.
     *
     * @param message El mensaje de advertencia a registrar.
     */
    public static void warn(String message) {
        write("[WARN]  " + message);
    }

    /**
     * Escribe una línea en el archivo de log con la fecha y hora actual.
     * Si el archivo no existe lo crea. Si existe, agrega al final (append).
     *
     * @param message El mensaje a escribir en el log.
     */
    private static void write(String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String line = "[" + timestamp + "] " + message;
        System.out.println(line);
        try (PrintWriter pw = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            pw.println(line);
        } catch (IOException ex) {
            System.err.println("No se pudo escribir en el log: " + ex.getMessage());
        }
    }
}
