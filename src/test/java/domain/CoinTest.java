package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase {@link Coin}.
 * Verifica el comportamiento de recolección, restablecimiento
 * y detección de colisiones de la moneda.
 * Los métodos siguen el estándar should/shouldNot para describir
 * el comportamiento esperado de forma clara y legible.
 */
class CoinTest {

    /** Moneda usada en cada prueba, creada en la posición (100, 100). */
    private Coin coin;

    /**
     * Crea una moneda nueva antes de cada prueba para garantizar
     * que cada test parte de un estado limpio e independiente.
     */
    @BeforeEach
    void setUp() {
        coin = new Coin(100, 100);
    }

    /**
     * Una moneda recién creada debería iniciar en estado no recogida.
     * Verifica que {@code isCollected()} retorne false al crear la moneda.
     */
    @Test
    void shouldStartAsNotCollected() {
        assertFalse(coin.isCollected());
    }

    /**
     * Una moneda debería marcarse como recogida al llamar {@code collect()}.
     * Verifica que {@code isCollected()} retorne true después de recogerla.
     */
    @Test
    void shouldBeCollectedAfterCollectIsCalled() {
        coin.collect();
        assertTrue(coin.isCollected());
    }

    /**
     * Una moneda debería restablecerse a no recogida al llamar {@code reset()}.
     * Verifica que después de recogerla y resetearla vuelva al estado inicial.
     */
    @Test
    void shouldResetToNotCollectedAfterReset() {
        coin.collect();
        coin.reset();
        assertFalse(coin.isCollected());
    }

    /**
     * Una moneda debería detectar colisión cuando el jugador está en la misma posición.
     * Verifica que {@code collidesWith()} retorne true con un jugador superpuesto.
     */
    @Test
    void shouldDetectCollisionWithPlayerAtSamePosition() {
        Player player = new Player(100, 100);
        assertTrue(coin.collidesWith(player));
    }

    /**
     * Una moneda ya recogida no debería generar colisión aunque el jugador esté encima.
     * Verifica que una moneda recogida no interactúe con el jugador.
     */
    @Test
    void shouldNotCollideIfAlreadyCollected() {
        Player player = new Player(100, 100);
        coin.collect();
        assertFalse(coin.collidesWith(player));
    }

    /**
     * Una moneda no debería detectar colisión cuando el jugador está lejos.
     * Verifica que {@code collidesWith()} retorne false con un jugador distante.
     */
    @Test
    void shouldNotCollideWithPlayerFarAway() {
        Player player = new Player(500, 500);
        assertFalse(coin.collidesWith(player));
    }

    /**
     * Los límites de la moneda deberían reflejar su posición y tamaño correctos.
     * Verifica que {@code getBounds()} retorne x=100, y=100, width=12, height=12.
     */
    @Test
    void shouldReturnCorrectBounds() {
        assertEquals(100, coin.getBounds().x);
        assertEquals(100, coin.getBounds().y);
        assertEquals(12,  coin.getBounds().width);
        assertEquals(12,  coin.getBounds().height);
    }
}
