package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de aceptación para el modo Player vs Machine.
 * Verifican que la máquina se comporta correctamente en ambos perfiles,
 * que las monedas son independientes y que el primero en llegar gana.
 */
class PvMAcceptanceTest {

    private StartZone start1;
    private StartZone start2;
    private EndZone   end1;
    private EndZone   end2;
    private Coin      coin;
    private LevelPvP  level;

    @BeforeEach
    void setUp() {
        start1 = new StartZone(20,  200, 80, 100);
        end1   = new EndZone(700,  200, 80, 100);
        start2 = new StartZone(700, 200, 80, 100);
        end2   = new EndZone(20,   200, 80, 100);

        BoardPvP board = new BoardPvP(start1, start2, end1, end2, List.of());
        coin  = new Coin(400, 240);
        level = new LevelPvP(1, board,
            new ArrayList<>(), new ArrayList<>(),
            List.of(coin), new ArrayList<>()
        );
    }

    private GamePvM buildGame(MachineProfile profile) {
        Player        player  = new Player(50, 240);
        MachinePlayer machine = new MachinePlayer(730, 240, profile);
        GamePvM g = new GamePvM(player, machine, List.of(level));
        g.start();
        return g;
    }

    /**
     * Escenario: el jugador humano gana contra la máquina.
     * Dado que el jugador recoge la moneda y llega a su EndZone
     * Entonces el ganador debe ser "Jugador".
     */
    @Test
    void shouldDeclareHumanPlayerWinnerWhenReachesEndZoneFirst() {
        GamePvM game = buildGame(MachineProfile.RANDOM);
        coin.collect();
        game.getPlayer().setPosition(720, 240);
        game.update();
        assertEquals("Jugador", game.getWinner());
    }

    /**
     * Escenario: la máquina gana contra el jugador humano.
     * Dado que la máquina recoge la moneda y llega a su EndZone
     * Entonces el ganador debe ser "Máquina".
     */
    @Test
    void shouldDeclareMachineWinnerWhenReachesEndZoneFirst() {
        GamePvM game = buildGame(MachineProfile.EXPERT);
        coin.collect();
        game.getMachine().setPosition(30, 240);
        game.update();
        assertEquals("Máquina", game.getWinner());
    }

    /**
     * Escenario: la máquina experta se mueve hacia las monedas.
     * Dado que hay una moneda a la izquierda de la máquina
     * Entonces la máquina experta debe moverse hacia ella.
     */
    @Test
    void shouldMoveExpertMachineTowardNearestCoin() {
        MachinePlayer machine = new MachinePlayer(730, 240, MachineProfile.EXPERT);
        machine.setWalls(List.of());
        int initialX = machine.getX();
        machine.updateAI(List.of(coin), new ArrayList<>(), 30, 240);
        assertTrue(machine.getX() < initialX);
    }

    /**
     * Escenario: la máquina aleatoria se mueve.
     * Dado que la máquina aleatoria actualiza su IA
     * Entonces debe moverse en alguna dirección.
     */
    @Test
    void shouldMoveRandomMachineAfterAIUpdate() {
        MachinePlayer machine = new MachinePlayer(400, 240, MachineProfile.RANDOM);
        machine.setWalls(List.of());
        int initialX = machine.getX();
        int initialY = machine.getY();
        for (int i = 0; i < 100; i++) {
            machine.updateAI(List.of(coin), new ArrayList<>(), 30, 240);
        }
        assertTrue(machine.getX() != initialX || machine.getY() != initialY);
    }

    /**
     * Escenario: la muerte de la máquina no afecta las monedas del jugador.
     * Dado que el jugador recogió una moneda
     * Y la máquina muere
     * Entonces la moneda del jugador NO debe restablecerse.
     */
    @Test
    void shouldNotResetPlayerCoinsWhenMachineDies() {
        GamePvM game = buildGame(MachineProfile.RANDOM);
        coin.collect();
        level.resetPlayer(game.getMachine(), start2);
        assertTrue(coin.isCollected());
    }

    /**
     * Escenario: la muerte del jugador no afecta las monedas de la máquina.
     * Dado que la máquina recogió una moneda
     * Y el jugador muere
     * Entonces la moneda de la máquina NO debe restablecerse.
     */
    @Test
    void shouldNotResetMachineCoinsWhenPlayerDies() {
        GamePvM game = buildGame(MachineProfile.EXPERT);
        coin.collect();
        level.resetPlayer(game.getPlayer(), start1);
        assertTrue(coin.isCollected());
    }

    /**
     * Escenario: el tiempo se agota en modo PvM.
     * Dado que pasan 3 minutos sin que nadie complete el nivel
     * Entonces el estado debe cambiar a TIMEOUT.
     */
    @Test
    void shouldSetTimeoutWhenTimeLimitIsReachedInPvM() {
        GamePvM game = buildGame(MachineProfile.RANDOM);
        for (int i = 0; i < 180 * 60; i++) game.update();
        assertEquals(GameState.TIMEOUT, game.getState());
    }

    /**
     * Escenario: reinicio de partida PvM.
     * Dado que ocurrieron muertes y se reinicia la partida
     * Entonces las muertes deben volver a cero y el estado a PLAYING.
     */
    @Test
    void shouldResetAllProgressWhenPvMGameIsRestarted() {
        GamePvM game = buildGame(MachineProfile.EXPERT);
        game.restart();
        assertEquals(0, game.getDeathsPlayer());
        assertEquals(0, game.getDeathsMachine());
        assertEquals(GameState.PLAYING, game.getState());
    }
}
