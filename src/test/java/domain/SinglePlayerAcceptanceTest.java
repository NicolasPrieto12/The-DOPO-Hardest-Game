package domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de aceptación para el modo un jugador.
 * Verifican flujos completos del juego desde la perspectiva del usuario:
 * recoger monedas, completar niveles, morir, usar checkpoint y agotar el tiempo.
 */
class SinglePlayerAcceptanceTest {

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
        game.start();
    }

    @AfterEach
    void tearDown() {
        Game.resetInstance();
    }

    /**
     * Escenario: el jugador recoge la moneda y llega a la meta.
     * Dado que el jugador recoge todas las monedas
     * Y llega a la zona final
     * Entonces el nivel debe completarse y el juego pasa a WIN.
     */
    @Test
    void shouldCompleteGameWhenPlayerCollectsAllCoinsAndReachesEndZone() {
        coin.collect();
        player.setPosition(720, 240);
        game.update();
        assertEquals(GameState.WIN, game.getState());
    }

    /**
     * Escenario: el jugador intenta pasar sin recoger monedas.
     * Dado que el jugador llega a la zona final sin recoger monedas
     * Entonces el nivel NO debe completarse.
     */
    @Test
    void shouldNotCompleteGameWhenPlayerReachesEndZoneWithoutCoins() {
        player.setPosition(720, 240);
        game.update();
        assertNotEquals(GameState.WIN, game.getState());
    }

    /**
     * Escenario: el jugador muere y reaparece en el inicio.
     * Dado que el jugador colisiona con un enemigo
     * Entonces debe reaparecer en la zona de inicio y sumar una muerte.
     */
    @Test
    void shouldRespawnAtStartAndIncrementDeathsWhenPlayerDies() {
        player.setPosition(enemy.getBounds().x, enemy.getBounds().y);
        game.update();
        assertEquals(1, game.getDeaths());
        assertTrue(player.getX() >= startZone.getX() &&
                   player.getX() <= startZone.getX() + startZone.getWidth());
    }

    /**
     * Escenario: el jugador muere y las monedas se restablecen.
     * Dado que el jugador recogió una moneda y luego muere
     * Entonces la moneda debe volver a estar disponible.
     */
    @Test
    void shouldResetCoinsWhenPlayerDiesWithoutCheckpoint() {
        coin.collect();
        assertTrue(coin.isCollected());
        game.checkDeath();
        assertFalse(coin.isCollected());
    }

    /**
     * Escenario: el jugador usa el checkpoint.
     * Dado que el jugador activa un checkpoint y luego muere
     * Entonces debe reaparecer en el checkpoint, no en el inicio.
     */
    @Test
    void shouldRespawnAtCheckpointAfterDyingWhenCheckpointIsActivated() {
        player.saveCheckpoint(390, 230);
        game.checkDeath();
        assertEquals(390, player.getX());
        assertEquals(230, player.getY());
    }

    /**
     * Escenario: el tiempo se agota.
     * Dado que pasan 3 minutos sin completar el nivel
     * Entonces el estado debe cambiar a TIMEOUT.
     */
    @Test
    void shouldSetTimeoutStateWhenTimeLimitIsReached() {
        for (int i = 0; i < 180 * 60; i++) game.update();
        assertEquals(GameState.TIMEOUT, game.getState());
    }

    /**
     * Escenario: el jugador reinicia la partida.
     * Dado que el jugador murió varias veces y reinicia
     * Entonces las muertes deben volver a cero y el estado a PLAYING.
     */
    @Test
    void shouldResetAllProgressWhenPlayerRestartsGame() {
        game.checkDeath();
        game.checkDeath();
        game.restart();
        assertEquals(0, game.getDeaths());
        assertEquals(GameState.PLAYING, game.getState());
        assertEquals(180, game.getSecondsLeft());
    }

    /**
     * Escenario: el jugador pausa y reanuda el juego.
     * Dado que el jugador pausa el juego
     * Entonces el estado debe ser PAUSED y al volver debe ser PLAYING.
     */
    @Test
    void shouldPauseAndResumeGameCorrectly() {
        game.pause();
        assertEquals(GameState.PAUSED, game.getState());
        game.pause();
        assertEquals(GameState.PLAYING, game.getState());
    }
}
