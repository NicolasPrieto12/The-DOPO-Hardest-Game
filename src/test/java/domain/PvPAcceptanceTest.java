package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de aceptación para el modo Player vs Player.
 * Verifican flujos completos: victoria del primero en llegar,
 * monedas independientes por jugador y checkpoint.
 */
class PvPAcceptanceTest {

    private GamePvP game;
    private Player player1;
    private Player player2;
    private Coin coin;
    private StartZone start1;
    private StartZone start2;
    private EndZone end1;
    private EndZone end2;

    @BeforeEach
    void setUp() {
        start1 = new StartZone(20, 200, 80, 100);
        end1   = new EndZone(700, 200, 80, 100);
        start2 = new StartZone(700, 200, 80, 100);
        end2   = new EndZone(20, 200, 80, 100);

        BoardPvP board = new BoardPvP(start1, start2, end1, end2, List.of());

        player1 = new Player(50, 240);
        player2 = new Player(730, 240);
        coin    = new Coin(400, 240);

        LevelPvP level = new LevelPvP(1, board,
            new ArrayList<>(), new ArrayList<>(),
            List.of(coin), new ArrayList<>()
        );

        game = new GamePvP(player1, player2, List.of(level));
        game.start();
    }

    /**
     * Escenario: el jugador 1 llega primero a la meta.
     * Dado que el jugador 1 recoge la moneda y llega a su EndZone
     * Entonces debe ganar inmediatamente sin esperar al jugador 2.
     */
    @Test
    void shouldDeclarePlayer1WinnerWhenReachesEndZoneFirst() {
        coin.collect();
        player1.setPosition(720, 240);
        game.update();
        assertEquals("Jugador 1", game.getWinner());
    }

    /**
     * Escenario: el jugador 2 llega primero a la meta.
     * Dado que el jugador 2 recoge la moneda y llega a su EndZone
     * Entonces debe ganar inmediatamente sin esperar al jugador 1.
     */
    @Test
    void shouldDeclarePlayer2WinnerWhenReachesEndZoneFirst() {
        coin.collect();
        player2.setPosition(30, 240);
        game.update();
        assertEquals("Jugador 2", game.getWinner());
    }

    /**
     * Escenario: el jugador 1 muere pero el jugador 2 conserva sus monedas.
     * Dado que el jugador 2 recogió una moneda
     * Y el jugador 1 muere
     * Entonces la moneda del jugador 2 NO debe restablecerse.
     */
    @Test
    void shouldNotResetPlayer2CoinsWhenPlayer1Dies() {
        coin.collect();
        game.getCurrentLevel().resetPlayer(player1, start1);
        assertTrue(coin.isCollected());
    }

    /**
     * Escenario: el jugador 2 muere pero el jugador 1 conserva sus monedas.
     * Dado que el jugador 1 recogió una moneda
     * Y el jugador 2 muere
     * Entonces la moneda del jugador 1 NO debe restablecerse.
     */
    @Test
    void shouldNotResetPlayer1CoinsWhenPlayer2Dies() {
        coin.collect();
        game.getCurrentLevel().resetPlayer(player2, start2);
        assertTrue(coin.isCollected());
    }

    /**
     * Escenario: ningún jugador puede ganar sin recoger monedas.
     * Dado que ambos jugadores están en sus EndZones pero sin monedas
     * Entonces el juego no debe terminar.
     */
    @Test
    void shouldNotWinWithoutCollectingCoins() {
        player1.setPosition(720, 240);
        player2.setPosition(30, 240);
        game.update();
        assertNotEquals(GameState.WIN, game.getState());
    }

    /**
     * Escenario: el tiempo se agota en modo PvP.
     * Dado que pasan 3 minutos sin que nadie complete el nivel
     * Entonces el estado debe cambiar a TIMEOUT.
     */
    @Test
    void shouldSetTimeoutWhenTimeLimitIsReachedInPvP() {
        for (int i = 0; i < 180 * 60; i++) game.update();
        assertEquals(GameState.TIMEOUT, game.getState());
    }

    /**
     * Escenario: reinicio de partida PvP.
     * Dado que ambos jugadores murieron varias veces y se reinicia
     * Entonces las muertes deben volver a cero.
     */
    @Test
    void shouldResetAllDeathsWhenPvPGameIsRestarted() {
        game.restart();
        assertEquals(0, game.getDeaths1());
        assertEquals(0, game.getDeaths2());
        assertEquals(GameState.PLAYING, game.getState());
    }
}
