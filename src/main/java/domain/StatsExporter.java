package domain;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Exporta las estadisticas de una partida a un archivo de texto legible.
 * Soporta los tres modos de juego: Single Player, PvP y PvM.
 *
 * <p>Formato del archivo exportado:</p>
 * <pre>
 *   ========================================
 *   THE DOPO HARDEST GAME - Estadisticas
 *   Fecha: 2026-05-10 14:32:05
 *   ========================================
 *   Modo:            Single Player
 *   Estado:          PLAYING
 *   Nivel actual:    1
 *   Muertes:         3
 *   Tiempo restante: 142 segundos
 *   ========================================
 * </pre>
 *
 * <p>Todos los errores de I/O se envuelven en {@link GameException}.</p>
 */
public class StatsExporter {

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String SEPARATOR =
        "========================================";

    /**
     * Exporta las estadisticas de una partida Single Player a un archivo de texto.
     *
     * @param game     La partida de un jugador.
     * @param filePath Ruta del archivo de destino.
     * @throws GameException Si ocurre un error al escribir el archivo.
     */
    public static void exportSingle(Game game, String filePath) throws GameException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            writeHeader(pw, "Single Player");
            pw.println("Estado:          " + game.getState());
            pw.println("Nivel actual:    " + (game.getCurrentLevelIndex() + 1));
            pw.println("Muertes:         " + game.getDeaths());
            pw.println("Tiempo restante: " + game.getSecondsLeft() + " segundos");
            pw.println(SEPARATOR);
            GameLogger.info("Estadisticas single exportadas a: " + filePath);
        } catch (IOException e) {
            GameLogger.error("Error al exportar estadisticas single a: " + filePath, e);
            throw new GameException("No se pudo exportar las estadisticas a: " + filePath, e);
        }
    }

    /**
     * Exporta las estadisticas de una partida PvP a un archivo de texto.
     *
     * @param game     La partida PvP.
     * @param filePath Ruta del archivo de destino.
     * @throws GameException Si ocurre un error al escribir el archivo.
     */
    public static void exportPvP(GamePvP game, String filePath) throws GameException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            writeHeader(pw, "Jugador vs Jugador (PvP)");
            pw.println("Estado:               " + game.getState());
            pw.println("Nivel actual:         " + (game.getCurrentLevelIndex() + 1));
            pw.println("Tiempo restante:      " + game.getSecondsLeft() + " segundos");
            pw.println("Muertes Jugador 1:    " + game.getDeaths1());
            pw.println("Muertes Jugador 2:    " + game.getDeaths2());
            pw.println("Ganador:              " + game.getWinner());
            pw.println(SEPARATOR);
            GameLogger.info("Estadisticas PvP exportadas a: " + filePath);
        } catch (IOException e) {
            GameLogger.error("Error al exportar estadisticas PvP a: " + filePath, e);
            throw new GameException("No se pudo exportar las estadisticas PvP a: " + filePath, e);
        }
    }

    /**
     * Exporta las estadisticas de una partida PvM a un archivo de texto.
     *
     * @param game     La partida PvM.
     * @param filePath Ruta del archivo de destino.
     * @throws GameException Si ocurre un error al escribir el archivo.
     */
    public static void exportPvM(GamePvM game, String filePath) throws GameException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            writeHeader(pw, "Jugador vs Maquina (PvM)");
            pw.println("Estado:               " + game.getState());
            pw.println("Nivel actual:         " + (game.getCurrentLevelIndex() + 1));
            pw.println("Tiempo restante:      " + game.getSecondsLeft() + " segundos");
            pw.println("Muertes Jugador:      " + game.getDeathsPlayer());
            pw.println("Muertes Maquina:      " + game.getDeathsMachine());
            pw.println("Perfil Maquina:       " + game.getMachine().getProfile().name());
            pw.println("Ganador:              " + game.getWinner());
            pw.println(SEPARATOR);
            GameLogger.info("Estadisticas PvM exportadas a: " + filePath);
        } catch (IOException e) {
            GameLogger.error("Error al exportar estadisticas PvM a: " + filePath, e);
            throw new GameException("No se pudo exportar las estadisticas PvM a: " + filePath, e);
        }
    }

    private static void writeHeader(PrintWriter pw, String modo) {
        String fecha = LocalDateTime.now().format(FORMATTER);
        pw.println(SEPARATOR);
        pw.println("THE DOPO HARDEST GAME - Estadisticas");
        pw.println("Fecha: " + fecha);
        pw.println(SEPARATOR);
        pw.println("Modo:            " + modo);
    }
}
