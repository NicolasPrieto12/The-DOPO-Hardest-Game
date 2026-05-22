package domain;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona el guardado y carga de partidas en archivos de texto.
 * Soporta tanto el modo un jugador como el modo PvP.
 * El formato del archivo es clave=valor, una por línea.
 *
 * <p>Formato del archivo de guardado (modo single):</p>
 * <pre>
 *   mode=single
 *   level=0
 *   deaths=3
 *   secondsLeft=142
 *   state=PLAYING
 *   p1x=50
 *   p1y=240
 *   p1type=RED
 *   p1checkpointX=-1
 *   p1checkpointY=-1
 *   coins=1,0,1,
 * </pre>
 *
 * <p>Todos los errores de I/O se envuelven en {@link GameException} y se registran
 * en el log mediante {@link GameLogger}.</p>
 */
public class SaveManager {

    /**
     * Guarda el estado de una partida de un jugador en el archivo indicado.
     *
     * @param game     La partida a guardar.
     * @param filePath Ruta del archivo donde se guardará.
     * @throws IOException Si ocurre un error al escribir el archivo.
     */
    public static void saveGame(Game game, String filePath) throws GameException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println("mode=single");
            pw.println("level=" + game.getCurrentLevelIndex());
            pw.println("deaths=" + game.getDeaths());
            pw.println("secondsLeft=" + game.getSecondsLeft());
            pw.println("state=" + game.getState());
            Player p = game.getPlayer();
            pw.println("p1x=" + p.getX());
            pw.println("p1y=" + p.getY());
            pw.println("p1type=" + p.getType().name());
            pw.println("p1checkpointX=" + p.getCheckpointX());
            pw.println("p1checkpointY=" + p.getCheckpointY());
            Level level = game.getCurrentLevel();
            StringBuilder coinStates = new StringBuilder();
            for (Coin c : level.getCoins()) {
                coinStates.append(c.isCollected() ? "1" : "0").append(",");
            }
            pw.println("coins=" + coinStates);
            GameLogger.info("Partida single guardada en: " + filePath);
        } catch (IOException e) {
            GameLogger.error("Error al guardar partida single en: " + filePath, e);
            throw new GameException("No se pudo guardar la partida en: " + filePath, e);
        }
    }

    /**
     * Guarda el estado de una partida PvP en el archivo indicado.
     *
     * @param game     La partida PvP a guardar.
     * @param filePath Ruta del archivo donde se guardará.
     * @throws IOException Si ocurre un error al escribir el archivo.
     */
    public static void saveGamePvP(GamePvP game, String filePath) throws GameException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println("mode=pvp");
            pw.println("level=" + game.getCurrentLevelIndex());
            pw.println("secondsLeft=" + game.getSecondsLeft());
            pw.println("state=" + game.getState());
            pw.println("deaths1=" + game.getDeaths1());
            pw.println("deaths2=" + game.getDeaths2());
            Player p1 = game.getPlayer1();
            pw.println("p1x=" + p1.getX());
            pw.println("p1y=" + p1.getY());
            pw.println("p1type=" + p1.getType().name());
            pw.println("p1checkpointX=" + p1.getCheckpointX());
            pw.println("p1checkpointY=" + p1.getCheckpointY());
            Player p2 = game.getPlayer2();
            pw.println("p2x=" + p2.getX());
            pw.println("p2y=" + p2.getY());
            pw.println("p2type=" + p2.getType().name());
            pw.println("p2checkpointX=" + p2.getCheckpointX());
            pw.println("p2checkpointY=" + p2.getCheckpointY());
            LevelPvP level = game.getCurrentLevel();
            StringBuilder coinStates = new StringBuilder();
            for (Coin c : level.getCoins()) {
                coinStates.append(c.isCollected() ? "1" : "0").append(",");
            }
            pw.println("coins=" + coinStates);
            GameLogger.info("Partida PvP guardada en: " + filePath);
        } catch (IOException e) {
            GameLogger.error("Error al guardar partida PvP en: " + filePath, e);
            throw new GameException("No se pudo guardar la partida PvP en: " + filePath, e);
        }
    }

    /**
     * Lee el modo de juego guardado en el archivo sin cargarlo completamente.
     *
     * @param filePath Ruta del archivo a leer.
     * @return "single", "pvp" o "unknown" si no se puede determinar.
     */
    public static String readMode(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("mode=")) return line.split("=")[1].trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "unknown";
    }

    /**
     * Carga el estado de una partida de un jugador desde el archivo.
     * Aplica el estado guardado al juego y al jugador proporcionados.
     *
     * @param game     La instancia de Game donde se cargará el estado.
     * @param filePath Ruta del archivo a leer.
     * @throws IOException Si ocurre un error al leer el archivo.
     */
    public static void loadGame(Game game, String filePath) throws GameException {
        try {
            List<String[]> entries = readEntries(filePath);
            for (String[] entry : entries) {
                String key = entry[0], val = entry[1];
                switch (key) {
                    case "level"        -> game.setCurrentLevelIndex(Integer.parseInt(val));
                    case "deaths"       -> game.setDeaths(Integer.parseInt(val));
                    case "secondsLeft"  -> game.setSecondsLeft(Integer.parseInt(val));
                    case "state"        -> game.setState(val);
                    case "p1x"         -> game.getPlayer().setPosition(Integer.parseInt(val), game.getPlayer().getY());
                    case "p1y"         -> game.getPlayer().setPosition(game.getPlayer().getX(), Integer.parseInt(val));
                    case "p1type"      -> game.getPlayer().applyType(PlayerType.valueOf(val));
                    case "p1checkpointX" -> {
                        if (Integer.parseInt(val) >= 0)
                            game.getPlayer().saveCheckpoint(Integer.parseInt(val), game.getPlayer().getCheckpointY());
                    }
                    case "coins" -> applyCoins(game.getCurrentLevel().getCoins(), val);
                }
            }
            GameLogger.info("Partida single cargada desde: " + filePath);
        } catch (IOException e) {
            GameLogger.error("Error al cargar partida single desde: " + filePath, e);
            throw new GameException("No se pudo cargar la partida desde: " + filePath, e);
        } catch (IllegalArgumentException e) {
            GameLogger.error("Archivo de partida corrupto: " + filePath, e);
            throw new GameException("El archivo de partida está corrupto: " + filePath, e);
        }
    }

    /**
     * Carga el estado de una partida PvP desde el archivo.
     *
     * @param game     La instancia de GamePvP donde se cargará el estado.
     * @param filePath Ruta del archivo a leer.
     * @throws IOException Si ocurre un error al leer el archivo.
     */
    public static void loadGamePvP(GamePvP game, String filePath) throws GameException {
        try {
            List<String[]> entries = readEntries(filePath);
            int p1x = -1, p1y = -1, p2x = -1, p2y = -1;
            int cp1x = -1, cp1y = -1, cp2x = -1, cp2y = -1;
            for (String[] entry : entries) {
                String key = entry[0], val = entry[1];
                switch (key) {
                    case "p1x"           -> p1x  = Integer.parseInt(val);
                    case "p1y"           -> p1y  = Integer.parseInt(val);
                    case "p1type"        -> game.getPlayer1().applyType(PlayerType.valueOf(val));
                    case "p1checkpointX" -> cp1x = Integer.parseInt(val);
                    case "p1checkpointY" -> cp1y = Integer.parseInt(val);
                    case "p2x"           -> p2x  = Integer.parseInt(val);
                    case "p2y"           -> p2y  = Integer.parseInt(val);
                    case "p2type"        -> game.getPlayer2().applyType(PlayerType.valueOf(val));
                    case "p2checkpointX" -> cp2x = Integer.parseInt(val);
                    case "p2checkpointY" -> cp2y = Integer.parseInt(val);
                    case "coins"         -> applyCoins(game.getCurrentLevel().getCoins(), val);
                }
            }
            if (p1x >= 0 && p1y >= 0) game.getPlayer1().setPosition(p1x, p1y);
            if (p2x >= 0 && p2y >= 0) game.getPlayer2().setPosition(p2x, p2y);
            if (cp1x >= 0) game.getPlayer1().saveCheckpoint(cp1x, cp1y);
            if (cp2x >= 0) game.getPlayer2().saveCheckpoint(cp2x, cp2y);
            GameLogger.info("Partida PvP cargada desde: " + filePath);
        } catch (IOException e) {
            GameLogger.error("Error al cargar partida PvP desde: " + filePath, e);
            throw new GameException("No se pudo cargar la partida PvP desde: " + filePath, e);
        } catch (IllegalArgumentException e) {
            GameLogger.error("Archivo de partida PvP corrupto: " + filePath, e);
            throw new GameException("El archivo de partida PvP está corrupto: " + filePath, e);
        }
    }

    private static List<String[]> readEntries(String filePath) throws IOException {
        List<String[]> entries = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) entries.add(parts);
            }
        }
        return entries;
    }

    private static void applyCoins(List<Coin> coins, String val) {
        String[] states = val.split(",");
        for (int i = 0; i < Math.min(states.length, coins.size()); i++) {
            if ("1".equals(states[i].trim())) coins.get(i).collect();
        }
    }
}
