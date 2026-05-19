package gui;

import domain.*;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Fábrica de niveles del juego.
 * Nivel 1: tablero medio con obstáculos, 2 enemigos básicos, 4 monedas.
 * Nivel 2: tablero con más obstáculos, 8 monedas, 1 básico + 3 patrulleros, 1 checkpoint.
 * Ambos niveles son idénticos en diseño para modo single y PvP.
 */
public class LevelFactory {

    private static final int BW = 800;
    private static final int BH = 500;
    private static final int WT = 20;

    // =============================================
    // PAREDES COMPARTIDAS
    // =============================================

    /**
     * Paredes del nivel 1: borde exterior + obstáculos en forma de L
     * que crean pasillos y zonas de riesgo.
     */
    private static List<Rectangle> buildLevel1Walls() {
        return new ArrayList<>(List.of(
            // Borde exterior
            new Rectangle(0, 0, BW, WT),
            new Rectangle(0, BH - WT, BW, WT),
            new Rectangle(0, 0, WT, BH),
            new Rectangle(BW - WT, 0, WT, BH),
            // Obstáculo superior izquierdo
            new Rectangle(180, 90, 120, WT),
            new Rectangle(180, 90, WT, 100),
            // Obstáculo superior derecho
            new Rectangle(500, 90, 120, WT),
            new Rectangle(600, 90, WT, 100),
            // Obstáculo inferior izquierdo
            new Rectangle(180, 390, 120, WT),
            new Rectangle(180, 290, WT, 100),
            // Obstáculo inferior derecho
            new Rectangle(500, 390, 120, WT),
            new Rectangle(600, 290, WT, 100),
            // Bloque central superior
            new Rectangle(350, 130, 100, WT),
            // Bloque central inferior
            new Rectangle(350, 350, 100, WT)
        ));
    }

    /**
     * Paredes del nivel 2: borde exterior + laberinto más complejo.
     */
    private static List<Rectangle> buildLevel2Walls() {
        return new ArrayList<>(List.of(
            // Borde exterior
            new Rectangle(0, 0, BW, WT),
            new Rectangle(0, BH - WT, BW, WT),
            new Rectangle(0, 0, WT, BH),
            new Rectangle(BW - WT, 0, WT, BH),
            // Pasillo superior izquierdo
            new Rectangle(160, 70, WT, 130),
            new Rectangle(160, 70, 120, WT),
            // Pasillo superior derecho
            new Rectangle(620, 70, WT, 130),
            new Rectangle(520, 70, 120, WT),
            // Pasillo inferior izquierdo
            new Rectangle(160, 300, WT, 130),
            new Rectangle(160, 410, 120, WT),
            // Pasillo inferior derecho
            new Rectangle(620, 300, WT, 130),
            new Rectangle(520, 410, 120, WT)
        ));
    }

    // =============================================
    // MODO UN JUGADOR
    // =============================================

    /**
     * Nivel 1 single: tablero medio, 4 monedas, 2 enemigos básicos con obstáculos.
     */
    public static Level buildSingleLevel1(Player player) {
        int zoneW = 80, zoneH = 100;
        int zoneY = BH / 2 - zoneH / 2;

        StartZone start = new StartZone(WT, zoneY, zoneW, zoneH);
        EndZone   end   = new EndZone(BW - WT - zoneW, zoneY, zoneW, zoneH);

        List<Rectangle> walls = buildLevel1Walls();
        Board board = new Board(start, end, walls);

        player.setWalls(walls);
        player.setStartPosition(
            start.getX() + start.getWidth()  / 2 - player.getSize() / 2,
            start.getY() + start.getHeight() / 2 - player.getSize() / 2
        );
        player.respawn();

        List<Coin> coins = List.of(
            new Coin(260, 160),
            new Coin(520, 160),
            new Coin(260, 320),
            new Coin(520, 320)
        );

        // Enemigo 1: se mueve horizontalmente por la franja central
        Enemy enemy1 = new Enemy(300, BH / 2 - 8, 3, 0, BW, BH);
        enemy1.setWalls(walls);
        enemy1.addForbiddenZone(rect(start));
        enemy1.addForbiddenZone(rect(end));

        // Enemigo 2: se mueve verticalmente por el centro
        Enemy enemy2 = new Enemy(BW / 2 - 8, 150, 0, 2, BW, BH);
        enemy2.setWalls(walls);
        enemy2.addForbiddenZone(rect(start));
        enemy2.addForbiddenZone(rect(end));

        return new Level(1, board, List.of(enemy1, enemy2), coins);
    }

    /**
     * Nivel 2 single: laberinto complejo, 8 monedas, 1 básico + 3 patrulleros, 1 checkpoint.
     * Los patrulleros rodean zonas clave del mapa.
     */
    public static Level buildSingleLevel2(Player player) {
        int zoneW = 70, zoneH = 100;
        int zoneY = BH / 2 - zoneH / 2;

        StartZone      start = new StartZone(WT, zoneY, zoneW, zoneH);
        EndZone        end   = new EndZone(BW - WT - zoneW, zoneY, zoneW, zoneH);
        CheckpointZone cp    = new CheckpointZone(BW / 2 - 35, BH / 2 - 30, 70, 60);

        List<Rectangle> walls = buildLevel2Walls();
        Board board = new Board(start, end, walls);

        player.setWalls(walls);
        player.setStartPosition(
            start.getX() + start.getWidth()  / 2 - player.getSize() / 2,
            start.getY() + start.getHeight() / 2 - player.getSize() / 2
        );
        player.respawn();

        List<Coin> coins = List.of(
            new Coin(200, 110), new Coin(560, 110),
            new Coin(200, 370), new Coin(560, 370),
            new Coin(350, 180), new Coin(430, 180),
            new Coin(350, 290), new Coin(430, 290)
        );

        Rectangle cpRect = rect(cp);

        Enemy basicEnemy = new Enemy(240, BH / 2 - 8, 3, 0, BW, BH);
        basicEnemy.setWalls(walls);
        basicEnemy.addForbiddenZone(rect(start));
        basicEnemy.addForbiddenZone(rect(end));
        basicEnemy.addForbiddenZone(cpRect);

        // Patrullero 1: corredor izquierdo entre paredes laterales
        PatrolEnemy patrol1 = new PatrolEnemy(100, 140, 2, new int[][]{
            {100, 140}, {155, 140}, {155, 360}, {100, 360}
        });
        patrol1.addForbiddenZone(rect(start));
        patrol1.addForbiddenZone(rect(end));
        patrol1.addForbiddenZone(cpRect);

        // Patrullero 2: corredor derecho entre paredes laterales
        PatrolEnemy patrol2 = new PatrolEnemy(645, 140, 2, new int[][]{
            {645, 140}, {700, 140}, {700, 360}, {645, 360}
        });
        patrol2.addForbiddenZone(rect(start));
        patrol2.addForbiddenZone(rect(end));
        patrol2.addForbiddenZone(cpRect);

        // Patrullero 3: zona central abierta alrededor del checkpoint
        PatrolEnemy patrol3 = new PatrolEnemy(290, 160, 2, new int[][]{
            {290, 160}, {490, 160}, {490, 320}, {290, 320}
        });
        patrol3.addForbiddenZone(rect(start));
        patrol3.addForbiddenZone(rect(end));
        patrol3.addForbiddenZone(cpRect);

        return new Level(2, board,
            List.of(basicEnemy),
            List.of(patrol1, patrol2, patrol3),
            coins, new ArrayList<>(), List.of(cp)
        );
    }

    // =============================================
    // MODO PVP
    // =============================================

    /**
     * Nivel 1 PvP: mismo diseño que nivel 1 single.
     * P1 inicia izquierda → gana llegando a la derecha.
     * P2 inicia derecha  → gana llegando a la izquierda.
     */
    public static LevelPvP buildPvPLevel1(Player p1, Player p2) {
        int zoneW = 80, zoneH = 100;
        int zoneY = BH / 2 - zoneH / 2;

        StartZone start1 = new StartZone(WT, zoneY, zoneW, zoneH);
        EndZone   end1   = new EndZone(BW - WT - zoneW, zoneY, zoneW, zoneH);
        StartZone start2 = new StartZone(BW - WT - zoneW, zoneY, zoneW, zoneH);
        EndZone   end2   = new EndZone(WT, zoneY, zoneW, zoneH);

        List<Rectangle> walls = buildLevel1Walls();
        BoardPvP board = new BoardPvP(start1, start2, end1, end2, walls);

        setupPlayerPvP(p1, walls, start1);
        setupPlayerPvP(p2, walls, start2);

        List<Coin> coins = List.of(
            new Coin(260, 160),
            new Coin(520, 160),
            new Coin(260, 320),
            new Coin(520, 320)
        );

        Enemy enemy1 = new Enemy(300, BH / 2 - 8, 3, 0, BW, BH);
        enemy1.setWalls(walls);
        enemy1.addForbiddenZone(rect(start1));
        enemy1.addForbiddenZone(rect(start2));

        Enemy enemy2 = new Enemy(BW / 2 - 8, 150, 0, 2, BW, BH);
        enemy2.setWalls(walls);
        enemy2.addForbiddenZone(rect(start1));
        enemy2.addForbiddenZone(rect(start2));

        return new LevelPvP(1, board, List.of(enemy1, enemy2), coins);
    }

    /**
     * Nivel 2 PvP: mismo diseño de mapa que nivel 2 single.
     * Rutas de patrulleros adaptadas para el modo PvP:
     * patrol1 patrulla el corredor izquierdo,
     * patrol2 patrulla el corredor derecho,
     * patrol3 patrulla el centro en sentido contrario al single.
     * 6 monedas normales + 2 SkinCoins, 1 básico + 3 patrulleros, 1 checkpoint.
     */
    public static LevelPvP buildPvPLevel2(Player p1, Player p2) {
        int zoneW = 70, zoneH = 100;
        int zoneY = BH / 2 - zoneH / 2;

        StartZone      start1 = new StartZone(WT, zoneY, zoneW, zoneH);
        EndZone        end1   = new EndZone(BW - WT - zoneW, zoneY, zoneW, zoneH);
        StartZone      start2 = new StartZone(BW - WT - zoneW, zoneY, zoneW, zoneH);
        EndZone        end2   = new EndZone(WT, zoneY, zoneW, zoneH);
        CheckpointZone cp     = new CheckpointZone(BW / 2 - 35, BH / 2 - 30, 70, 60);

        List<Rectangle> walls = buildLevel2Walls();
        BoardPvP board = new BoardPvP(start1, start2, end1, end2, walls);

        setupPlayerPvP(p1, walls, start1);
        setupPlayerPvP(p2, walls, start2);

        List<Coin> coins = List.of(
            new Coin(200, 110), new Coin(560, 110),
            new Coin(200, 370), new Coin(560, 370),
            new Coin(350, 180), new Coin(430, 180)
        );
        List<SkinCoin> skinCoins = List.of(
            new SkinCoin(350, 290),
            new SkinCoin(430, 290)
        );

        Rectangle cpRect = rect(cp);

        Enemy basicEnemy = new Enemy(240, BH / 2 - 8, 3, 0, BW, BH);
        basicEnemy.setWalls(walls);
        basicEnemy.addForbiddenZone(rect(start1));
        basicEnemy.addForbiddenZone(rect(start2));
        basicEnemy.addForbiddenZone(cpRect);

        // Patrullero 1 PvP: corredor izquierdo de abajo hacia arriba
        PatrolEnemy patrol1 = new PatrolEnemy(100, 360, 2, new int[][]{
            {100, 360}, {155, 360}, {155, 140}, {100, 140}
        });
        patrol1.addForbiddenZone(rect(start1));
        patrol1.addForbiddenZone(rect(start2));
        patrol1.addForbiddenZone(cpRect);

        // Patrullero 2 PvP: corredor derecho de arriba hacia abajo
        PatrolEnemy patrol2 = new PatrolEnemy(700, 140, 2, new int[][]{
            {700, 140}, {645, 140}, {645, 360}, {700, 360}
        });
        patrol2.addForbiddenZone(rect(start1));
        patrol2.addForbiddenZone(rect(start2));
        patrol2.addForbiddenZone(cpRect);

        // Patrullero 3 PvP: zona central en sentido contrario al single
        PatrolEnemy patrol3 = new PatrolEnemy(490, 160, 2, new int[][]{
            {490, 160}, {290, 160}, {290, 320}, {490, 320}
        });
        patrol3.addForbiddenZone(rect(start1));
        patrol3.addForbiddenZone(rect(start2));
        patrol3.addForbiddenZone(cpRect);

        return new LevelPvP(2, board,
            List.of(basicEnemy),
            List.of(patrol1, patrol2, patrol3),
            coins, skinCoins,
            List.of(cp)
        );
    }

    // =============================================
    // MODO PVM
    // =============================================

    /**
     * Nivel 1 PvM (fácil): mismo tablero que nivel 1, 2 enemigos básicos.
     * 4 monedas amarillas + 2 SkinCoins azules.
     */
    public static LevelPvP buildPvMLevel1(Player player, MachinePlayer machine) {
        int zoneW = 80, zoneH = 100;
        int zoneY = BH / 2 - zoneH / 2;

        StartZone start1 = new StartZone(WT, zoneY, zoneW, zoneH);
        EndZone   end1   = new EndZone(BW - WT - zoneW, zoneY, zoneW, zoneH);
        StartZone start2 = new StartZone(BW - WT - zoneW, zoneY, zoneW, zoneH);
        EndZone   end2   = new EndZone(WT, zoneY, zoneW, zoneH);

        List<Rectangle> walls = buildLevel1Walls();
        BoardPvP board = new BoardPvP(start1, start2, end1, end2, walls);

        setupPlayerPvP(player,  walls, start1);
        setupPlayerPvP(machine, walls, start2);

        List<Coin> coins = List.of(
            new Coin(260, 160), new Coin(520, 160),
            new Coin(260, 320), new Coin(520, 320)
        );
        List<SkinCoin> skinCoins = List.of(
            new SkinCoin(400, 160),
            new SkinCoin(400, 320)
        );

        Enemy enemy1 = new Enemy(300, BH / 2 - 8, 3, 0, BW, BH);
        enemy1.setWalls(walls);
        enemy1.addForbiddenZone(rect(start1));
        enemy1.addForbiddenZone(rect(start2));

        Enemy enemy2 = new Enemy(BW / 2 - 8, 150, 0, 2, BW, BH);
        enemy2.setWalls(walls);
        enemy2.addForbiddenZone(rect(start1));
        enemy2.addForbiddenZone(rect(start2));

        return new LevelPvP(1, board,
            List.of(enemy1, enemy2), new ArrayList<>(),
            coins, skinCoins
        );
    }

    /**
     * Nivel 2 PvM (intermedio): tablero nivel 2, 3 patrulleros.
     * 4 monedas amarillas + 2 SkinCoins azules.
     */
    public static LevelPvP buildPvMLevel2(Player player, MachinePlayer machine) {
        int zoneW = 70, zoneH = 100;
        int zoneY = BH / 2 - zoneH / 2;

        StartZone      start1 = new StartZone(WT, zoneY, zoneW, zoneH);
        EndZone        end1   = new EndZone(BW - WT - zoneW, zoneY, zoneW, zoneH);
        StartZone      start2 = new StartZone(BW - WT - zoneW, zoneY, zoneW, zoneH);
        EndZone        end2   = new EndZone(WT, zoneY, zoneW, zoneH);
        CheckpointZone cp     = new CheckpointZone(BW / 2 - 35, BH / 2 - 30, 70, 60);

        List<Rectangle> walls = buildLevel2Walls();
        BoardPvP board = new BoardPvP(start1, start2, end1, end2, walls);

        setupPlayerPvP(player,  walls, start1);
        setupPlayerPvP(machine, walls, start2);

        List<Coin> coins = List.of(
            new Coin(200, 110), new Coin(560, 110),
            new Coin(200, 370), new Coin(560, 370)
        );
        List<SkinCoin> skinCoins = List.of(
            new SkinCoin(390, 180),
            new SkinCoin(390, 290)
        );

        Rectangle cpRect = rect(cp);

        PatrolEnemy patrol1 = new PatrolEnemy(100, 140, 2, new int[][]{
            {100, 140}, {155, 140}, {155, 360}, {100, 360}
        });
        patrol1.addForbiddenZone(rect(start1));
        patrol1.addForbiddenZone(rect(start2));
        patrol1.addForbiddenZone(cpRect);

        PatrolEnemy patrol2 = new PatrolEnemy(645, 360, 2, new int[][]{
            {645, 360}, {700, 360}, {700, 140}, {645, 140}
        });
        patrol2.addForbiddenZone(rect(start1));
        patrol2.addForbiddenZone(rect(start2));
        patrol2.addForbiddenZone(cpRect);

        PatrolEnemy patrol3 = new PatrolEnemy(290, 160, 2, new int[][]{
            {290, 160}, {490, 160}, {490, 320}, {290, 320}
        });
        patrol3.addForbiddenZone(rect(start1));
        patrol3.addForbiddenZone(rect(start2));
        patrol3.addForbiddenZone(cpRect);

        return new LevelPvP(2, board,
            new ArrayList<>(),
            List.of(patrol1, patrol2, patrol3),
            coins, skinCoins,
            List.of(cp)
        );
    }

    // =============================================
    // HELPERS
    // =============================================

    private static void setupPlayerPvP(Player p, List<Rectangle> walls, StartZone start) {
        p.setWalls(walls);
        p.setStartPosition(
            start.getX() + start.getWidth()  / 2 - p.getSize() / 2,
            start.getY() + start.getHeight() / 2 - p.getSize() / 2
        );
        p.respawn();
    }

    private static Rectangle rect(Zone z) {
        return new Rectangle(z.getX(), z.getY(), z.getWidth(), z.getHeight());
    }
}
