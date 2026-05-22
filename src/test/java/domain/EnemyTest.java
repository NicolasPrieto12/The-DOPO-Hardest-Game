package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Rectangle;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase {@link Enemy}.
 * Verifica el desplazamiento lineal, el rebote en los bordes del tablero,
 * el respeto de zonas prohibidas y la detección de colisiones con el jugador.
 * Los métodos siguen el estándar should/shouldNot para describir
 * el comportamiento esperado de forma clara y legible.
 */
class EnemyTest {

    /** Enemigo usado en cada prueba, moviéndose hacia la derecha desde (300, 250). */
    private Enemy enemy;

    /** Ancho del tablero de prueba. */
    private static final int BW = 800;

    /** Alto del tablero de prueba. */
    private static final int BH = 500;

    /**
     * Crea un enemigo nuevo antes de cada prueba para garantizar
     * que cada test parte de un estado limpio e independiente.
     */
    @BeforeEach
    void setUp() {
        enemy = new Enemy(300, 250, 3, 0, BW, BH);
    }

    /**
     * El enemigo debería avanzar 3 píxeles hacia la derecha en cada tick.
     */
    @Test
    void shouldMoveRightBySpeedEachTick() {
        enemy.move();
        assertEquals(303, enemy.getBounds().x);
    }

    /**
     * El enemigo debería invertir su dirección al alcanzar el borde derecho del tablero.
     */
    @Test
    void shouldBounceWhenReachingRightBorder() {
        Enemy e = new Enemy(BW - 20, 250, 3, 0, BW, BH);
        e.move();
        int xAntes = e.getBounds().x;
        e.move();
        assertTrue(e.getBounds().x < xAntes);
    }

    /**
     * El enemigo debería invertir su dirección al alcanzar el borde izquierdo del tablero.
     */
    @Test
    void shouldBounceWhenReachingLeftBorder() {
        Enemy e = new Enemy(2, 250, -3, 0, BW, BH);
        e.move();
        int xAntes = e.getBounds().x;
        e.move();
        assertTrue(e.getBounds().x > xAntes);
    }

    /**
     * El enemigo no debería entrar en una zona prohibida,
     * invirtiendo su dirección antes de alcanzarla.
     */
    @Test
    void shouldNotEnterForbiddenZone() {
        Rectangle zonaProhibida = new Rectangle(290, 240, 80, 80);
        enemy.addForbiddenZone(zonaProhibida);
        int xAntes = enemy.getBounds().x;
        enemy.move();
        assertNotEquals(xAntes + 3, enemy.getBounds().x);
    }

    /**
     * El enemigo debería detectar colisión cuando el jugador está en la misma posición.
     */
    @Test
    void shouldDetectCollisionWithPlayerAtSamePosition() {
        Player player = new Player(300, 250);
        assertTrue(enemy.collidesWith(player));
    }

    /**
     * El enemigo no debería detectar colisión cuando el jugador está lejos.
     */
    @Test
    void shouldNotDetectCollisionWithPlayerFarAway() {
        Player player = new Player(600, 400);
        assertFalse(enemy.collidesWith(player));
    }

    /**
     * Los límites del enemigo deberían reflejar su posición inicial correctamente.
     */
    @Test
    void shouldReturnCorrectInitialBounds() {
        assertEquals(300, enemy.getBounds().x);
        assertEquals(250, enemy.getBounds().y);
    }

    /** El enemigo deberia rebotar al chocar con una pared. */
    @Test
    void shouldBounceWhenHittingWall() {
        // Enemigo en (300,250) moviendose a la derecha (dx=3)
        // Pared lejos de la posicion inicial para evitar solapamiento
        enemy.setWalls(java.util.List.of(new java.awt.Rectangle(320, 240, 20, 30)));
        // Despues de varios ticks llega a la pared y rebota
        int prevX;
        int bouncedX = -1;
        for (int i = 0; i < 10; i++) {
            prevX = enemy.getBounds().x;
            enemy.move();
            if (enemy.getBounds().x < prevX) {
                bouncedX = enemy.getBounds().x;
                break;
            }
        }
        assertTrue(bouncedX >= 0);
    }

    /** El enemigo vertical deberia moverse hacia abajo. */
    @Test
    void shouldMoveDownWhenDyIsPositive() {
        Enemy e = new Enemy(300, 100, 0, 3, BW, BH);
        e.move();
        assertEquals(103, e.getBounds().y);
    }

    /** El enemigo deberia rebotar al llegar al borde superior. */
    @Test
    void shouldBounceWhenReachingTopBorder() {
        Enemy e = new Enemy(300, 2, 0, -3, BW, BH);
        e.move();
        int y1 = e.getBounds().y;
        e.move();
        assertTrue(e.getBounds().y > y1);
    }

    /** El enemigo deberia rebotar al llegar al borde inferior. */
    @Test
    void shouldBounceWhenReachingBottomBorder() {
        Enemy e = new Enemy(300, BH - 20, 0, 3, BW, BH);
        e.move();
        int y1 = e.getBounds().y;
        e.move();
        assertTrue(e.getBounds().y < y1);
    }
}
