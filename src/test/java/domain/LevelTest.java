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
        Level levelWithCp = new Level(1, board, List.of(), List.of(coin),
            List.of(cp));
        Player p = new Player(50, 240);
        levelWithCp.updateCheckpoints(p);
        assertTrue(cp.isActivated());
    }
