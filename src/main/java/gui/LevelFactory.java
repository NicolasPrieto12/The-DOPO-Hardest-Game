package gui;

import domain.*;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Fábrica de niveles del juego.
 * Nivel 1: tablero medio con obstáculos, 2 enemigos básicos, 4 monedas.
 * Nivel 2: tablero con más obstáculos, 8 monedas, 1 básico + 3 patrulleros, 1 checkpoint.
 * Nivel 3: laberinto zigzag, 8 monedas + GreenCoin, 5 SliderEnemy + 5 AcceleratedEnemy, 6 bombas.
 * Nivel 4: diseño en cruz, 10 monedas + SkinCoin + GreenCoin, 8 AcceleratedEnemy, 4 bombas + 2 LifeSource.
 * Ambos niveles son idénticos en diseño para modo single y PvP.
 *
 * <p>Todos los métodos son estáticos. No se necesita instanciar esta clase.</p>
 * <p>Cada modo (single, PvP, PvM) tiene sus propios métodos de construcción de nivel.</p>
 */
public class LevelFactory {

    private static final int BW = 800;
    private static final int BH = 500;
    private static final int WT = 20;

    /**
     * Paredes del nivel 3 (difícil): laberinto en zigzag con pasillos estrechos
     * y zonas cerradas que obligan a rutas peligrosas.
     */
    private static List<Rectangle> buildLevel3Walls() {
        return new ArrayList<>(List.of(
            // Borde exterior
            new Rectangle(0,   0,   BW, WT),
            new Rectangle(0,   BH - WT, BW, WT),
            new Rectangle(0,   0,   WT, BH),
            new Rectangle(BW - WT, 0, WT, BH),
            // Muro horizontal superior izquierdo (deja pasillo arriba)
            new Rectangle(100, 100, 200, WT),
            // Muro vertical izquierdo central
            new Rectangle(100, 100, WT, 150),
            // Muro horizontal central izquierdo (deja pasillo abajo)
            new Rectangle(100, 250, 160, WT),
            // Muro horizontal superior derecho
            new Rectangle(500, 100, 200, WT),
            // Muro vertical derecho central
            new Rectangle(680, 100, WT, 150),
            // Muro horizontal central derecho
            new Rectangle(540, 250, 160, WT),
            // Muro horizontal inferior izquierdo
            new Rectangle(100, 370, 200, WT),
            // Muro horizontal inferior derecho
            new Rectangle(500, 370, 200, WT),
            // Bloque central superior
            new Rectangle(330, 130, 140, WT),
            // Bloque central inferior
            new Rectangle(330, 340, 140, WT),
            // Muros verticales centrales (crean pasillo angosto)
            new Rectangle(330, 130, WT, 100),
            new Rectangle(450, 130, WT, 100)
        ));
    }

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

    /**
     * Paredes del nivel 4: diseño en forma de cruz con corredores diagonales
     * y zonas cerradas en las esquinas. Completamente distinto a los niveles anteriores.
     */
    private static List<Rectangle> buildLevel4Walls() {
        return new ArrayList<>(List.of(
            // Borde exterior
            new Rectangle(0,       0,       BW, WT),
            new Rectangle(0,       BH - WT, BW, WT),
            new Rectangle(0,       0,       WT, BH),
            new Rectangle(BW - WT, 0,       WT, BH),
            // Cruz central horizontal
            new Rectangle(200, 230, 160, WT),
            new Rectangle(440, 230, 160, WT),
            // Cruz central vertical
            new Rectangle(380, 80,  WT, 140),
            new Rectangle(380, 280, WT, 140),
            // Bloques esquina superior izquierda
            new Rectangle(80,  80,  120, WT),
            new Rectangle(80,  80,  WT,  120),
            // Bloques esquina superior derecha
            new Rectangle(600, 80,  120, WT),
            new Rectangle(700, 80,  WT,  120),
            // Bloques esquina inferior izquierda
            new Rectangle(80,  390, 120, WT),
            new Rectangle(80,  270, WT,  120),
            // Bloques esquina inferior derecha
            new Rectangle(600, 390, 120, WT),
            new Rectangle(700, 270, WT,  120),
            // Muros internos que crean pasillos en zigzag
            new Rectangle(200, 130, WT, 100),
            new Rectangle(580, 130, WT, 100),
            new Rectangle(200, 310, WT, 100),
            new Rectangle(580, 310, WT, 100)
        ));
    }

    // =============================================
    // NIVEL 3 - DIFICIL (los tres modos)
    // =============================================

    /**
     * Helper para crear los 10 enemigos del nivel 3.
     * 5 SliderEnemy (Tipo V) + 5 AcceleratedEnemy (Tipo A).
     */
    private static void addLevel3Enemies(List<SliderEnemy> sliders,
                                         List<AcceleratedEnemy> accelerated,
                                         List<Rectangle> walls,
                                         List<Rectangle> forbidden) {
        // 5 Deslizadores verticales (Tipo V) - velocidad 2
        int[][] sliderPos = {{200, 60}, {400, 60}, {480, 280}, {300, 280}, {150, 200}};
        for (int[] p : sliderPos) {
            SliderEnemy s = new SliderEnemy(p[0], p[1], 2, BH);
            s.setWalls(walls);
            for (Rectangle f : forbidden) s.addForbiddenZone(f);
            sliders.add(s);
        }
        // 5 Acelerados (Tipo A) - velocidad 6, alternando horizontal y vertical
        int[][] accPos  = {{250, 420}, {550, 420}, {400, 300}, {200, 350}, {550, 350}};
        int[]   accDx   = {  6,          -6,          6,          0,           0};
        int[]   accDy   = {  0,           0,          0,          6,          -6};
        for (int i = 0; i < accPos.length; i++) {
            AcceleratedEnemy a = new AcceleratedEnemy(
                accPos[i][0], accPos[i][1], accDx[i], accDy[i], BW, BH);
            a.setWalls(walls);
            for (Rectangle f : forbidden) a.addForbiddenZone(f);
            accelerated.add(a);
        }
    }

    /**
     * Nivel 3 single (difícil): laberinto zigzag, 8 monedas, 1 GreenCoin,
     * 5 SliderEnemy + 5 AcceleratedEnemy, 6 bombas.
     */
    public static Level buildSingleLevel3(Player player) {
        int zoneW = 70, zoneH = 100;
        int zoneY = BH / 2 - zoneH / 2;

        StartZone start = new StartZone(WT, zoneY, zoneW, zoneH);
        EndZone   end   = new EndZone(BW - WT - zoneW, zoneY, zoneW, zoneH);

        List<Rectangle> walls = buildLevel3Walls();
        Board board = new Board(start, end, walls);

        player.setWalls(walls);
        player.setStartPosition(
            start.getX() + start.getWidth()  / 2 - player.getSize() / 2,
            start.getY() + start.getHeight() / 2 - player.getSize() / 2
        );
        player.respawn();

        List<Coin> coins = List.of(
            new Coin(160, 60),  new Coin(400, 60),
            new Coin(160, 310), new Coin(400, 310),
            new Coin(580, 60),  new Coin(580, 310),
            new Coin(280, 430), new Coin(480, 430)
        );
        GreenCoin greenCoin = new GreenCoin(390, 230);

        List<Bomb> bombs = List.of(
            new Bomb(220, 160), new Bomb(540, 160),
            new Bomb(220, 300), new Bomb(540, 300),
            new Bomb(350, 60),  new Bomb(350, 400)
        );

        List<Rectangle> forbidden = List.of(rect(start), rect(end));
        List<SliderEnemy>      sliders     = new ArrayList<>();
        List<AcceleratedEnemy> accelerated = new ArrayList<>();
        addLevel3Enemies(sliders, accelerated, walls, forbidden);

        return new Level(3, board,
            new ArrayList<>(), new ArrayList<>(),
            coins, new ArrayList<>(), new ArrayList<>(),
            sliders, accelerated, bombs, greenCoin
        );
    }

    /**
     * Nivel 3 PvP (difícil): mismo diseño que single nivel 3.
     */
    public static LevelPvP buildPvPLevel3(Player p1, Player p2) {
        int zoneW = 70, zoneH = 100;
        int zoneY = BH / 2 - zoneH / 2;

        StartZone start1 = new StartZone(WT, zoneY, zoneW, zoneH);
        EndZone   end1   = new EndZone(BW - WT - zoneW, zoneY, zoneW, zoneH);
        StartZone start2 = new StartZone(BW - WT - zoneW, zoneY, zoneW, zoneH);
        EndZone   end2   = new EndZone(WT, zoneY, zoneW, zoneH);

        List<Rectangle> walls = buildLevel3Walls();
        BoardPvP board = new BoardPvP(start1, start2, end1, end2, walls);

        setupPlayerPvP(p1, walls, start1);
        setupPlayerPvP(p2, walls, start2);

        List<Coin> coins = List.of(
            new Coin(160, 60),  new Coin(400, 60),
            new Coin(160, 310), new Coin(400, 310),
            new Coin(580, 60),  new Coin(580, 310),
            new Coin(280, 430), new Coin(480, 430)
        );
        GreenCoin greenCoin = new GreenCoin(390, 230);

        List<Bomb> bombs = List.of(
            new Bomb(220, 160), new Bomb(540, 160),
            new Bomb(220, 300), new Bomb(540, 300),
            new Bomb(350, 60),  new Bomb(350, 400)
        );

        List<Rectangle> forbidden = List.of(rect(start1), rect(start2));
        List<SliderEnemy>      sliders     = new ArrayList<>();
        List<AcceleratedEnemy> accelerated = new ArrayList<>();
        addLevel3Enemies(sliders, accelerated, walls, forbidden);

        return new LevelPvP(3, board,
            new ArrayList<>(), new ArrayList<>(),
            coins, new ArrayList<>(), new ArrayList<>(),
            sliders, accelerated, bombs, greenCoin
        );
    }

    /**
     * Nivel 3 PvM (difícil): mismo diseño que PvP nivel 3.
     */
    public static LevelPvP buildPvMLevel3(Player player, MachinePlayer machine) {
        int zoneW = 70, zoneH = 100;
        int zoneY = BH / 2 - zoneH / 2;

        StartZone start1 = new StartZone(WT, zoneY, zoneW, zoneH);
        EndZone   end1   = new EndZone(BW - WT - zoneW, zoneY, zoneW, zoneH);
        StartZone start2 = new StartZone(BW - WT - zoneW, zoneY, zoneW, zoneH);
        EndZone   end2   = new EndZone(WT, zoneY, zoneW, zoneH);

        List<Rectangle> walls = buildLevel3Walls();
        BoardPvP board = new BoardPvP(start1, start2, end1, end2, walls);

        setupPlayerPvP(player,  walls, start1);
        setupPlayerPvP(machine, walls, start2);

        List<Coin> coins = List.of(
            new Coin(160, 60),  new Coin(400, 60),
            new Coin(160, 310), new Coin(400, 310),
            new Coin(580, 60),  new Coin(580, 310),
            new Coin(280, 430), new Coin(480, 430)
        );
        GreenCoin greenCoin = new GreenCoin(390, 230);

        List<Bomb> bombs = List.of(
            new Bomb(220, 160), new Bomb(540, 160),
            new Bomb(220, 300), new Bomb(540, 300),
            new Bomb(350, 60),  new Bomb(350, 400)
        );

        List<Rectangle> forbidden = List.of(rect(start1), rect(start2));
        List<SliderEnemy>      sliders     = new ArrayList<>();
        List<AcceleratedEnemy> accelerated = new ArrayList<>();
        addLevel3Enemies(sliders, accelerated, walls, forbidden);

        return new LevelPvP(3, board,
            new ArrayList<>(), new ArrayList<>(),
            coins, new ArrayList<>(), new ArrayList<>(),
            sliders, accelerated, bombs, greenCoin
        );
    }

    // =============================================
    // NIVEL 4 - MUY DIFICIL (los tres modos)
    // =============================================

    /** Helper que crea los 8 AcceleratedEnemy del nivel 4 en zonas abiertas. */
    private static List<AcceleratedEnemy> buildLevel4Enemies(List<Rectangle> walls,
                                                              List<Rectangle> forbidden) {
        // Posiciones y direcciones elegidas en zonas abiertas del mapa (fuera de paredes)
        int[][] pos = {
            {100, 160}, {100, 300},   // corredor izquierdo
            {620, 160}, {620, 300},   // corredor derecho
            {280, 50},  {450, 50},    // franja superior
            {280, 420}, {450, 420}    // franja inferior
        };
        int[] dx = { 6, 6, -6, -6,  6, -6,  6, -6 };
        int[] dy = { 0, 0,  0,  0,  0,  0,  0,  0 };
        List<AcceleratedEnemy> list = new ArrayList<>();
        for (int i = 0; i < pos.length; i++) {
            AcceleratedEnemy a = new AcceleratedEnemy(pos[i][0], pos[i][1], dx[i], dy[i], BW, BH);
            a.setWalls(walls);
            for (Rectangle f : forbidden) a.addForbiddenZone(f);
            list.add(a);
        }
        return list;
    }

    public static Level buildSingleLevel4(Player player) {
        int zoneW = 70, zoneH = 100;
        int zoneY = BH / 2 - zoneH / 2;

        StartZone start = new StartZone(WT, zoneY, zoneW, zoneH);
        EndZone   end   = new EndZone(BW - WT - zoneW, zoneY, zoneW, zoneH);

        List<Rectangle> walls = buildLevel4Walls();
        Board board = new Board(start, end, walls);
        player.setWalls(walls);
        player.setStartPosition(
            start.getX() + start.getWidth()  / 2 - player.getSize() / 2,
            start.getY() + start.getHeight() / 2 - player.getSize() / 2
        );
        player.respawn();

        List<Coin> coins = List.of(
            new Coin(110, 150), new Coin(110, 310),
            new Coin(650, 150), new Coin(650, 310),
            new Coin(290, 40),  new Coin(460, 40),
            new Coin(290, 430), new Coin(460, 430),
            new Coin(290, 170), new Coin(460, 170)
        );
        SkinCoin skinCoin   = new SkinCoin(370, 240);
        GreenCoin greenCoin = new GreenCoin(410, 240);

        List<Bomb> bombs = List.of(
            new Bomb(240, 170), new Bomb(520, 170),
            new Bomb(240, 290), new Bomb(520, 290)
        );
        List<LifeSource> lifeSources = List.of(
            new LifeSource(150, 230),
            new LifeSource(600, 230)
        );

        List<Rectangle> forbidden = List.of(rect(start), rect(end));
        List<AcceleratedEnemy> accelerated = buildLevel4Enemies(walls, forbidden);

        return new Level(4, board,
            new ArrayList<>(), new ArrayList<>(),
            coins, List.of(skinCoin), new ArrayList<>(),
            new ArrayList<>(), accelerated, bombs, greenCoin, lifeSources
        );
    }

    public static LevelPvP buildPvPLevel4(Player p1, Player p2) {
        int zoneW = 70, zoneH = 100;
        int zoneY = BH / 2 - zoneH / 2;

        StartZone start1 = new StartZone(WT, zoneY, zoneW, zoneH);
        EndZone   end1   = new EndZone(BW - WT - zoneW, zoneY, zoneW, zoneH);
        StartZone start2 = new StartZone(BW - WT - zoneW, zoneY, zoneW, zoneH);
        EndZone   end2   = new EndZone(WT, zoneY, zoneW, zoneH);

        List<Rectangle> walls = buildLevel4Walls();
        BoardPvP board = new BoardPvP(start1, start2, end1, end2, walls);
        setupPlayerPvP(p1, walls, start1);
        setupPlayerPvP(p2, walls, start2);

        List<Coin> coins = List.of(
            new Coin(110, 150), new Coin(110, 310),
            new Coin(650, 150), new Coin(650, 310),
            new Coin(290, 40),  new Coin(460, 40),
            new Coin(290, 430), new Coin(460, 430),
            new Coin(290, 170), new Coin(460, 170)
        );
        SkinCoin skinCoin   = new SkinCoin(370, 240);
        GreenCoin greenCoin = new GreenCoin(410, 240);

        List<Bomb> bombs = List.of(
            new Bomb(240, 170), new Bomb(520, 170),
            new Bomb(240, 290), new Bomb(520, 290)
        );
        List<LifeSource> lifeSources = List.of(
            new LifeSource(150, 230),
            new LifeSource(600, 230)
        );

        List<Rectangle> forbidden = List.of(rect(start1), rect(start2));
        List<AcceleratedEnemy> accelerated = buildLevel4Enemies(walls, forbidden);

        return new LevelPvP(4, board,
            new ArrayList<>(), new ArrayList<>(),
            coins, List.of(skinCoin), new ArrayList<>(),
            new ArrayList<>(), accelerated, bombs, greenCoin, lifeSources
        );
    }

    public static LevelPvP buildPvMLevel4(Player player, MachinePlayer machine) {
        int zoneW = 70, zoneH = 100;
        int zoneY = BH / 2 - zoneH / 2;

        StartZone start1 = new StartZone(WT, zoneY, zoneW, zoneH);
        EndZone   end1   = new EndZone(BW - WT - zoneW, zoneY, zoneW, zoneH);
        StartZone start2 = new StartZone(BW - WT - zoneW, zoneY, zoneW, zoneH);
        EndZone   end2   = new EndZone(WT, zoneY, zoneW, zoneH);

        List<Rectangle> walls = buildLevel4Walls();
        BoardPvP board = new BoardPvP(start1, start2, end1, end2, walls);
        setupPlayerPvP(player,  walls, start1);
        setupPlayerPvP(machine, walls, start2);

        List<Coin> coins = List.of(
            new Coin(110, 150), new Coin(110, 310),
            new Coin(650, 150), new Coin(650, 310),
            new Coin(290, 40),  new Coin(460, 40),
            new Coin(290, 430), new Coin(460, 430),
            new Coin(290, 170), new Coin(460, 170)
        );
        SkinCoin skinCoin   = new SkinCoin(370, 240);
        GreenCoin greenCoin = new GreenCoin(410, 240);

        List<Bomb> bombs = List.of(
            new Bomb(240, 170), new Bomb(520, 170),
            new Bomb(240, 290), new Bomb(520, 290)
        );
        List<LifeSource> lifeSources = List.of(
            new LifeSource(150, 230),
            new LifeSource(600, 230)
        );

        List<Rectangle> forbidden = List.of(rect(start1), rect(start2));
        List<AcceleratedEnemy> accelerated = buildLevel4Enemies(walls, forbidden);

        return new LevelPvP(4, board,
            new ArrayList<>(), new ArrayList<>(),
            coins, List.of(skinCoin), new ArrayList<>(),
            new ArrayList<>(), accelerated, bombs, greenCoin, lifeSources
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
