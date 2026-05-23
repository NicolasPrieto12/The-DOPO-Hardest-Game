package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase {@link Level}.
 * Verifica la lógica de completado del nivel, el restablecimiento
 * de monedas y jugador, y los métodos de acceso a sus elementos.
 * Los métodos siguen el estándar should/shouldNot para describir
 * el comportamiento esperado de forma clara y legible.
 */
class LevelTest {

    private Level level;
    private Player player;
    private Coin coin;
    private Enemy enemy;

    @BeforeEach
    void setUp() {
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());

        player = new Player(50, 240);
        coin   = new Coin(400, 240);
        enemy  = new Enemy(300, 240, 3, 0, 800, 500);

        level = new Level(1, board, List.of(enemy), List.of(coin));
    }

    /** El nivel no debería estar completado cuando la moneda no ha sido recogida. */
    @Test
    void shouldNotBeCompletedWhenCoinIsNotCollected() {
        assertFalse(level.isCompleted());
    }

    /** El nivel debería marcarse como completado cuando todas las monedas han sido recogidas. */
    @Test
    void shouldBeCompletedWhenAllCoinsAreCollected() {
        coin.collect();
        assertTrue(level.isCompleted());
    }

    /** El nivel debería restablecer las monedas a no recogidas al llamar reset(). */
    @Test
    void shouldResetCoinsToNotCollectedAfterReset() {
        coin.collect();
        level.reset(player);
        assertFalse(coin.isCollected());
    }

    /** El nivel debería reposicionar al jugador dentro del área de la StartZone al llamar reset(). */
    @Test
    void shouldRepositionPlayerInsideStartZoneAfterReset() {
        player.setPosition(600, 300);
        level.reset(player);
        assertTrue(player.getX() >= 20 && player.getX() <= 100);
        assertTrue(player.getY() >= 200 && player.getY() <= 300);
    }

    /** El nivel debería retornar el número correcto al llamar getLevelNumber(). */
    @Test
    void shouldReturnCorrectLevelNumber() {
        assertEquals(1, level.getLevelNumber());
    }

    /** El nivel debería retornar la lista de enemigos con el tamaño correcto. */
    @Test
    void shouldReturnCorrectNumberOfEnemies() {
        assertEquals(1, level.getEnemies().size());
    }

    /** El nivel debería retornar la lista de monedas con el tamaño correcto. */
    @Test
    void shouldReturnCorrectNumberOfCoins() {
        assertEquals(1, level.getCoins().size());
    }

    /** reset() con checkpoint activo deberia respetar el checkpoint y no resetear monedas. */
    @Test
    void shouldRespawnAtCheckpointAndKeepCoinsOnReset() {
        coin.collect();
        player.saveCheckpoint(390, 230);
        level.reset(player);
        assertEquals(390, player.getX());
        assertEquals(230, player.getY());
        assertTrue(coin.isCollected());
    }

    /** fullReset() deberia resetear monedas y checkpoint ignorando checkpoint activo. */
    @Test
    void shouldFullResetIgnoreCheckpointAndResetCoins() {
        coin.collect();
        player.saveCheckpoint(390, 230);
        level.fullReset(player);
        assertFalse(coin.isCollected());
        assertEquals(-1, player.getCheckpointX());
    }

    /** isCompleted() deberia retornar false cuando la lista de monedas esta vacia. */
    @Test
    void shouldNotBeCompletedWhenCoinListIsEmpty() {
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        Level emptyLevel = new Level(1, board, List.of(), List.of());
        assertFalse(emptyLevel.isCompleted());
    }

    /** update() deberia mover los enemigos del nivel. */
    @Test
    void shouldUpdateMoveEnemies() {
        int xBefore = enemy.getBounds().x;
        level.update();
        assertNotEquals(xBefore, enemy.getBounds().x);
    }

    /** updateCheckpoints() deberia activar checkpoint cuando el jugador lo pisa. */
    @Test
    void shouldUpdateCheckpointsActivateWhenPlayerEnters() {
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        CheckpointZone cp = new CheckpointZone(40, 230, 80, 60);
        Level levelWithCp = new Level(1, board,
            new ArrayList<>(), new ArrayList<>(),
            List.of(coin), new ArrayList<>(),
            List.of(cp));
        Player p = new Player(50, 240);
        levelWithCp.updateCheckpoints(p);
        assertTrue(cp.isActivated());
    }

    /** getBoard deberia retornar el tablero del nivel. */
    @Test
    void shouldGetBoardReturnBoard() {
        assertNotNull(level.getBoard());
    }

    /** getPatrolEnemies deberia retornar lista vacia cuando no hay patrulleros. */
    @Test
    void shouldGetPatrolEnemiesReturnEmptyWhenNone() {
        assertTrue(level.getPatrolEnemies().isEmpty());
    }

    /** getBombs deberia retornar lista vacia cuando no hay bombas. */
    @Test
    void shouldGetBombsReturnEmptyWhenNone() {
        assertTrue(level.getBombs().isEmpty());
    }

    /** getCheckpoints deberia retornar lista vacia cuando no hay checkpoints. */
    @Test
    void shouldGetCheckpointsReturnEmptyWhenNone() {
        assertTrue(level.getCheckpoints().isEmpty());
    }

    /** Level con PatrolEnemy deberia moverlo en update(). */
    @Test
    void shouldUpdateMovePatrolEnemy() {
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        PatrolEnemy patrol = new PatrolEnemy(300, 240, 3, new int[][]{{400, 240}, {300, 240}});
        Level levelWithPatrol = new Level(1, board, new ArrayList<>(), List.of(patrol),
            List.of(coin), new ArrayList<>(), new ArrayList<>());
        int xBefore = patrol.getBounds().x;
        levelWithPatrol.update();
        assertNotEquals(xBefore, patrol.getBounds().x);
    }

    /** Level con SliderEnemy deberia moverlo en update(). */
    @Test
    void shouldUpdateMoveSliderEnemy() {
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        SliderEnemy slider = new SliderEnemy(200, 100, 3, 500);
        Level lv = new Level(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(coin), new ArrayList<>(), new ArrayList<>(),
            List.of(slider), new ArrayList<>(), new ArrayList<>(), null);
        int yBefore = slider.getBounds().y;
        lv.update();
        assertNotEquals(yBefore, slider.getBounds().y);
    }

    /** Level con AcceleratedEnemy deberia moverlo en update(). */
    @Test
    void shouldUpdateMoveAcceleratedEnemy() {
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        AcceleratedEnemy ae = new AcceleratedEnemy(200, 200, 1, 0, 800, 500);
        Level lv = new Level(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(coin), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), List.of(ae), new ArrayList<>(), null);
        int xBefore = ae.getBounds().x;
        lv.update();
        assertNotEquals(xBefore, ae.getBounds().x);
    }

    /** Level.reset() deberia resetear SkinCoin cuando esta presente. */
    @Test
    void shouldResetSkinCoinWhenPresent() {
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        SkinCoin sc = new SkinCoin(300, 240);
        Player p = new Player(50, 240);
        sc.collect(p);
        Level lv = new Level(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(new Coin(400, 240)), List.of(sc), new ArrayList<>());
        lv.reset(p);
        assertFalse(sc.isCollected());
    }

    /** Level getSliderEnemies deberia retornar la lista correcta. */
    @Test
    void shouldGetSliderEnemiesReturnCorrectList() {
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        SliderEnemy slider = new SliderEnemy(200, 100, 3, 500);
        Level lv = new Level(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(coin), new ArrayList<>(), new ArrayList<>(),
            List.of(slider), new ArrayList<>(), new ArrayList<>(), null);
        assertEquals(1, lv.getSliderEnemies().size());
    }

    /** Level getAcceleratedEnemies deberia retornar la lista correcta. */
    @Test
    void shouldGetAcceleratedEnemiesReturnCorrectList() {
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        AcceleratedEnemy ae = new AcceleratedEnemy(200, 200, 1, 0, 800, 500);
        Level lv = new Level(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(coin), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), List.of(ae), new ArrayList<>(), null);
        assertEquals(1, lv.getAcceleratedEnemies().size());
    }

    /** Level getLifeSources deberia retornar la lista correcta. */
    @Test
    void shouldGetLifeSourcesReturnCorrectList() {
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        LifeSource ls = new LifeSource(300, 240);
        Level lv = new Level(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(coin), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
            null, List.of(ls));
        assertEquals(1, lv.getLifeSources().size());
    }

    /** Level getGreenCoin deberia retornar la moneda verde cuando esta presente. */
    @Test
    void shouldGetGreenCoinReturnCoinWhenPresent() {
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        GreenCoin gc = new GreenCoin(300, 240);
        Level lv = new Level(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(coin), new ArrayList<>(), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), gc);
        assertSame(gc, lv.getGreenCoin());
    }

    /** Level isCompleted deberia retornar false cuando alguna moneda no esta recogida. */
    @Test
    void shouldNotBeCompletedWhenSomeCoinNotCollected() {
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        Coin c1 = new Coin(300, 240);
        Coin c2 = new Coin(400, 240);
        Level lv = new Level(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(c1, c2), new ArrayList<>(), new ArrayList<>());
        c1.collect();
        assertFalse(lv.isCompleted());
    }

    /** Level fullReset con greenCoin null no deberia lanzar excepcion. */
    @Test
    void shouldFullResetNotThrowWhenGreenCoinIsNull() {
        assertDoesNotThrow(() -> level.fullReset(player));
    }

    /** Level reset con greenCoin null no deberia lanzar excepcion. */
    @Test
    void shouldResetNotThrowWhenGreenCoinIsNull() {
        assertDoesNotThrow(() -> level.reset(player));
    }

    /** Level getSkinCoins deberia retornar lista correcta. */
    @Test
    void shouldGetSkinCoinsReturnCorrectList() {
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        SkinCoin sc = new SkinCoin(300, 240);
        Level lv = new Level(1, board, new ArrayList<>(), new ArrayList<>(),
            List.of(coin), List.of(sc), new ArrayList<>());
        assertEquals(1, lv.getSkinCoins().size());
    }
}