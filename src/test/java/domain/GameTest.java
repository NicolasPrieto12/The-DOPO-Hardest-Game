package domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase {@link Game}.
 * Verifica el estado inicial, la lógica de muerte, victoria, pausa,
 * reinicio, progresión de niveles, tiempo agotado y el patrón Singleton.
 * Los métodos siguen el estándar should/shouldNot para describir
 * el comportamiento esperado de forma clara y legible.
 */
class GameTest {

    private Game game;
    private Player player;
    private Coin coin;
    private Enemy enemy;
    private StartZone startZone;
    private EndZone endZone;

    @BeforeEach
    void setUp() {
        Game.resetInstance();

        startZone = new StartZone(20, 200, 80, 100);
        endZone   = new EndZone(700, 200, 80, 100);
        Board board = new Board(startZone, endZone, List.of());

        player = new Player(50, 240);
        coin   = new Coin(400, 240);
        enemy  = new Enemy(600, 400, 3, 0, 800, 500);

        Level level = new Level(1, board, List.of(enemy), List.of(coin));
        game = Game.getInstance(player, List.of(level));
    }

    @AfterEach
    void tearDown() {
        Game.resetInstance();
    }

    /** El juego debería iniciar con estado PLAYING. */
    @Test
    void shouldStartWithPlayingState() {
        assertEquals(GameState.PLAYING, game.getState());
    }

    /** El juego debería iniciar con cero muertes. */
    @Test
    void shouldStartWithZeroDeaths() {
        assertEquals(0, game.getDeaths());
    }

    /** El juego debería iniciar con 180 segundos (3 minutos) de tiempo. */
    @Test
    void shouldStartWithThreeMinutesOfTime() {
        assertEquals(180, game.getSecondsLeft());
    }

    /** El contador de muertes debería incrementarse en 1 al llamar checkDeath(). */
    @Test
    void shouldIncrementDeathCountWhenCheckDeathIsCalled() {
        game.checkDeath();
        assertEquals(1, game.getDeaths());
    }

    /** Las monedas deberían restablecerse a no recogidas al llamar checkDeath(). */
    @Test
    void shouldResetCoinsAfterCheckDeath() {
        coin.collect();
        game.checkDeath();
        assertFalse(coin.isCollected());
    }

    /** El jugador debería reposicionarse en la StartZone al llamar checkDeath(). */
    @Test
    void shouldRepositionPlayerInStartZoneAfterCheckDeath() {
        player.setPosition(600, 300);
        game.checkDeath();
        assertTrue(player.getX() >= 20 && player.getX() <= 100);
    }

    /** checkWin() debería retornar false cuando no se han recogido monedas. */
    @Test
    void shouldNotWinWhenNoCoinsAreCollected() {
        assertFalse(game.checkWin());
    }

    /** checkWin() debería retornar false cuando las monedas están recogidas pero el jugador no está en la EndZone. */
    @Test
    void shouldNotWinWhenCoinsCollectedButPlayerNotInEndZone() {
        coin.collect();
        assertFalse(game.checkWin());
    }

    /** checkWin() debería retornar true cuando todas las monedas están recogidas y el jugador está en la EndZone. */
    @Test
    void shouldWinWhenAllCoinsCollectedAndPlayerInEndZone() {
        coin.collect();
        player.setPosition(720, 240);
        assertTrue(game.checkWin());
    }

    /** El juego debería alternar entre PLAYING y PAUSED al llamar pause(). */
    @Test
    void shouldToggleBetweenPlayingAndPausedWhenPauseIsCalled() {
        game.pause();
        assertEquals(GameState.PAUSED, game.getState());
        game.pause();
        assertEquals(GameState.PLAYING, game.getState());
    }

    /** El juego debería reiniciar muertes y tiempo a sus valores iniciales al llamar restart(). */
    @Test
    void shouldResetDeathsAndTimeAfterRestart() {
        game.checkDeath();
        game.checkDeath();
        game.restart();
        assertEquals(0, game.getDeaths());
        assertEquals(180, game.getSecondsLeft());
    }

    /** El juego debería cambiar a estado PLAYING al llamar restart() sin importar el estado anterior. */
    @Test
    void shouldReturnToPlayingStateAfterRestart() {
        game.pause();
        game.restart();
        assertEquals(GameState.PLAYING, game.getState());
    }

    /** El juego debería cambiar a estado WIN al llamar nextLevel() cuando no hay más niveles. */
    @Test
    void shouldSetWinStateWhenNoMoreLevelsExist() {
        game.nextLevel();
        assertEquals(GameState.WIN, game.getState());
    }

    /** El juego debería cambiar a estado TIMEOUT cuando el tiempo del nivel se agota. */
    @Test
    void shouldSetTimeoutStateWhenTimerReachesZero() {
        for (int i = 0; i < 180 * 60; i++) {
            game.update();
        }
        assertEquals(GameState.TIMEOUT, game.getState());
    }

    /** getInstance() debería retornar siempre la misma instancia (patrón Singleton). */
    @Test
    void shouldReturnSameInstanceEveryTime() {
        Game mismaInstancia = Game.getInstance();
        assertSame(game, mismaInstancia);
    }
}
