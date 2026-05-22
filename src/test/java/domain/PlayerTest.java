package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase {@link Player}.
 * Verifica el estado inicial, el movimiento en las cuatro direcciones,
 * la colisión con paredes, el respawn y la detección de colisiones.
 * Los métodos siguen el estándar should/shouldNot para describir
 * el comportamiento esperado de forma clara y legible.
 */
class PlayerTest {

    /** Jugador usado en cada prueba, creado en la posición (100, 100). */
    private Player player;

    /**
     * Crea un jugador nuevo antes de cada prueba para garantizar
     * que cada test parte de un estado limpio e independiente.
     */
    @BeforeEach
    void setUp() {
        player = new Player(100, 100);
    }

    /**
     * El jugador debería iniciar en estado vivo al ser creado.
     */
    @Test
    void shouldStartAlive() {
        assertTrue(player.isAlive());
    }

    /**
     * El jugador debería iniciar en las coordenadas indicadas en el constructor.
     */
    @Test
    void shouldStartAtGivenPosition() {
        assertEquals(100, player.getX());
        assertEquals(100, player.getY());
    }

    /**
     * El jugador debería actualizar su posición correctamente al llamar {@code setPosition()}.
     */
    @Test
    void shouldUpdatePositionWhenSetPositionIsCalled() {
        player.setPosition(200, 300);
        assertEquals(200, player.getX());
        assertEquals(300, player.getY());
    }

    /**
     * El jugador debería volver a su posición inicial y estar vivo después de {@code respawn()}.
     */
    @Test
    void shouldReturnToStartPositionAfterRespawn() {
        player.setPosition(400, 400);
        player.respawn();
        assertEquals(100, player.getX());
        assertEquals(100, player.getY());
        assertTrue(player.isAlive());
    }

    /**
     * El jugador debería detectar colisión con otro jugador en la misma posición.
     */
    @Test
    void shouldDetectCollisionWithPlayerAtSamePosition() {
        Player otro = new Player(100, 100);
        assertTrue(player.collidesWith(otro));
    }

    /**
     * El jugador no debería detectar colisión con un jugador que está lejos.
     */
    @Test
    void shouldNotDetectCollisionWithPlayerFarAway() {
        Player otro = new Player(500, 500);
        assertFalse(player.collidesWith(otro));
    }

    /**
     * Los límites del jugador deberían reflejar su posición y tamaño correctos.
     */
    @Test
    void shouldReturnCorrectBounds() {
        assertEquals(100, player.getBounds().x);
        assertEquals(100, player.getBounds().y);
        assertEquals(20,  player.getBounds().width);
        assertEquals(20,  player.getBounds().height);
    }

    /**
     * El jugador debería reducir su coordenada Y al moverse hacia arriba.
     */
    @Test
    void shouldDecreaseYWhenMovingUp() {
        player.setMovingUp(true);
        player.move();
        assertTrue(player.getY() < 100);
    }

    /**
     * El jugador debería aumentar su coordenada Y al moverse hacia abajo.
     */
    @Test
    void shouldIncreaseYWhenMovingDown() {
        player.setMovingDown(true);
        player.move();
        assertTrue(player.getY() > 100);
    }

    /**
     * El jugador debería reducir su coordenada X al moverse hacia la izquierda.
     */
    @Test
    void shouldDecreaseXWhenMovingLeft() {
        player.setMovingLeft(true);
        player.move();
        assertTrue(player.getX() < 100);
    }

    /**
     * El jugador debería aumentar su coordenada X al moverse hacia la derecha.
     */
    @Test
    void shouldIncreaseXWhenMovingRight() {
        player.setMovingRight(true);
        player.move();
        assertTrue(player.getX() > 100);
    }

    /**
     * El jugador no debería moverse si no hay ninguna tecla de dirección activa.
     */
    @Test
    void shouldNotMoveWhenNoKeyIsPressed() {
        player.move();
        assertEquals(100, player.getX());
        assertEquals(100, player.getY());
    }

    /**
     * El jugador no debería atravesar una pared al moverse hacia arriba.
     * Verifica que la colisión con paredes bloquee el movimiento correctamente.
     */
    @Test
    void shouldNotMoveThroughWallAbove() {
        java.util.List<java.awt.Rectangle> walls = java.util.List.of(
            new java.awt.Rectangle(0, 90, 800, 20)
        );
        player.setWalls(walls);
        player.setMovingUp(true);
        player.move();
        assertEquals(100, player.getY());
    }

    /** El jugador no deberia atravesar una pared al moverse hacia abajo. */
    @Test
    void shouldNotMoveThroughWallBelow() {
        java.util.List<java.awt.Rectangle> walls = java.util.List.of(
            new java.awt.Rectangle(0, 110, 800, 20)
        );
        player.setWalls(walls);
        player.setMovingDown(true);
        player.move();
        assertEquals(100, player.getY());
    }

    /** El jugador no deberia atravesar una pared al moverse hacia la izquierda. */
    @Test
    void shouldNotMoveThroughWallToTheLeft() {
        java.util.List<java.awt.Rectangle> walls = java.util.List.of(
            new java.awt.Rectangle(90, 0, 20, 800)
        );
        player.setWalls(walls);
        player.setMovingLeft(true);
        player.move();
        assertEquals(100, player.getX());
    }

    /** El jugador no deberia atravesar una pared al moverse hacia la derecha. */
    @Test
    void shouldNotMoveThroughWallToTheRight() {
        java.util.List<java.awt.Rectangle> walls = java.util.List.of(
            new java.awt.Rectangle(110, 0, 20, 800)
        );
        player.setWalls(walls);
        player.setMovingRight(true);
        player.move();
        assertEquals(100, player.getX());
    }

    /** respawnAtCheckpoint() deberia reposicionar al jugador en el checkpoint guardado. */
    @Test
    void shouldRespawnAtSavedCheckpoint() {
        player.saveCheckpoint(300, 200);
        player.setPosition(500, 500);
        player.respawnAtCheckpoint();
        assertEquals(300, player.getX());
        assertEquals(200, player.getY());
    }

    /** respawnAtCheckpoint() sin checkpoint deberia volver al inicio. */
    @Test
    void shouldRespawnAtStartWhenNoCheckpointSaved() {
        player.setPosition(500, 500);
        player.respawnAtCheckpoint();
        assertEquals(100, player.getX());
        assertEquals(100, player.getY());
    }

    /** applyType() deberia actualizar velocidad y tamano al tipo BLUE. */
    @Test
    void shouldApplyTypeBlueUpdateSpeedAndSize() {
        player.applyType(PlayerType.BLUE);
        assertEquals(PlayerType.BLUE, player.getType());
        assertEquals(PlayerType.BLUE.speed, player.getSpeed());
        assertEquals(PlayerType.BLUE.size, player.getSize());
    }

    /** setStartPosition() deberia actualizar la posicion de inicio. */
    @Test
    void shouldSetStartPositionUpdateRespawnPoint() {
        player.setStartPosition(200, 300);
        player.respawn();
        assertEquals(200, player.getX());
        assertEquals(300, player.getY());
    }
}
