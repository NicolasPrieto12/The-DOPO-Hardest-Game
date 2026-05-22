package domain;

import org.junit.jupiter.api.Test;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para los tipos de enemigos avanzados:
 * PatrolEnemy, SliderEnemy, AcceleratedEnemy y MachinePlayer.
 */
class AdvancedEnemiesTest {

    // ─── PatrolEnemy ────────────────────────────────────────────

    /** PatrolEnemy deberia moverse hacia el primer waypoint. */
    @Test
    void shouldPatrolEnemyMoveTowardFirstWaypoint() {
        PatrolEnemy patrol = new PatrolEnemy(100, 100, 3, new int[][]{
            {200, 100}, {200, 200}, {100, 200}, {100, 100}
        });
        int initialX = patrol.getBounds().x;
        patrol.move();
        assertTrue(patrol.getBounds().x > initialX);
    }

    /** PatrolEnemy deberia detectar colision con jugador en la misma posicion. */
    @Test
    void shouldPatrolEnemyDetectCollisionWithPlayer() {
        PatrolEnemy patrol = new PatrolEnemy(100, 100, 3, new int[][]{{200, 100}});
        Player player = new Player(100, 100);
        assertTrue(patrol.collidesWith(player));
    }

    /** PatrolEnemy no deberia detectar colision con jugador lejos. */
    @Test
    void shouldPatrolEnemyNotDetectCollisionWithPlayerFarAway() {
        PatrolEnemy patrol = new PatrolEnemy(100, 100, 3, new int[][]{{200, 100}});
        Player player = new Player(500, 500);
        assertFalse(patrol.collidesWith(player));
    }

    /** PatrolEnemy deberia avanzar al siguiente waypoint al llegar al actual. */
    @Test
    void shouldPatrolEnemyAdvanceToNextWaypointWhenReached() {
        // Waypoint muy cercano para que lo alcance en un tick
        PatrolEnemy patrol = new PatrolEnemy(100, 100, 10, new int[][]{
            {102, 100}, {200, 100}
        });
        patrol.move(); // llega al waypoint {102,100}
        patrol.move(); // ahora va hacia {200,100}
        assertTrue(patrol.getBounds().x > 102);
    }

    /** PatrolEnemy no deberia entrar en zona prohibida. */
    @Test
    void shouldPatrolEnemyNotEnterForbiddenZone() {
        // Enemigo en (100,100) size=16, bounds=(100,100,16,16)
        // Zona prohibida en x=120..170, y=95..125 - NO solapa con posicion inicial
        PatrolEnemy patrol = new PatrolEnemy(100, 100, 3, new int[][]{{200, 100}});
        patrol.addForbiddenZone(new Rectangle(120, 95, 50, 30));
        patrol.move();
        // El enemigo salta el waypoint al detectar zona prohibida, no entra en ella
        assertFalse(new Rectangle(120, 95, 50, 30).intersects(patrol.getBounds()));
    }

    // ─── SliderEnemy ────────────────────────────────────────────

    /** SliderEnemy deberia moverse verticalmente. */
    @Test
    void shouldSliderEnemyMoveVertically() {
        SliderEnemy slider = new SliderEnemy(200, 100, 3, 500);
        int initialY = slider.getBounds().y;
        slider.move();
        assertNotEquals(initialY, slider.getBounds().y);
    }

    /** SliderEnemy deberia rebotar al llegar al borde inferior. */
    @Test
    void shouldSliderEnemyBounceAtBottomBorder() {
        SliderEnemy slider = new SliderEnemy(200, 480, 3, 500);
        slider.move();
        int y1 = slider.getBounds().y;
        slider.move();
        assertTrue(slider.getBounds().y < y1);
    }

    /** SliderEnemy deberia rebotar al llegar al borde superior. */
    @Test
    void shouldSliderEnemyBounceAtTopBorder() {
        SliderEnemy slider = new SliderEnemy(200, 2, -3, 500);
        slider.move();
        int y1 = slider.getBounds().y;
        slider.move();
        assertTrue(slider.getBounds().y > y1);
    }

    /** SliderEnemy deberia detectar colision con jugador. */
    @Test
    void shouldSliderEnemyDetectCollisionWithPlayer() {
        SliderEnemy slider = new SliderEnemy(100, 100, 3, 500);
        Player player = new Player(100, 100);
        assertTrue(slider.collidesWith(player));
    }

    /** SliderEnemy deberia rebotar al chocar con pared. */
    @Test
    void shouldSliderEnemyBounceWhenHittingWall() {
        SliderEnemy slider = new SliderEnemy(200, 100, 3, 500);
        slider.setWalls(List.of(new Rectangle(0, 103, 800, 20)));
        int y1 = slider.getBounds().y;
        slider.move();
        // Despues de rebotar, debe moverse hacia arriba
        assertTrue(slider.getBounds().y <= y1);
    }

    // ─── AcceleratedEnemy ───────────────────────────────────────

    /** AcceleratedEnemy deberia moverse a velocidad 6. */
    @Test
    void shouldAcceleratedEnemyMoveAtSpeed6() {
        AcceleratedEnemy ae = new AcceleratedEnemy(200, 200, 1, 0, 800, 500);
        int initialX = ae.getBounds().x;
        ae.move();
        assertEquals(initialX + 6, ae.getBounds().x);
    }

    /** AcceleratedEnemy deberia rebotar en el borde derecho. */
    @Test
    void shouldAcceleratedEnemyBounceAtRightBorder() {
        AcceleratedEnemy ae = new AcceleratedEnemy(780, 200, 1, 0, 800, 500);
        ae.move();
        int x1 = ae.getBounds().x;
        ae.move();
        assertTrue(ae.getBounds().x < x1);
    }

    /** AcceleratedEnemy deberia detectar colision con jugador. */
    @Test
    void shouldAcceleratedEnemyDetectCollisionWithPlayer() {
        AcceleratedEnemy ae = new AcceleratedEnemy(100, 100, 1, 0, 800, 500);
        Player player = new Player(100, 100);
        assertTrue(ae.collidesWith(player));
    }

    /** AcceleratedEnemy deberia rebotar al chocar con pared. */
    @Test
    void shouldAcceleratedEnemyBounceWhenHittingWall() {
        AcceleratedEnemy ae = new AcceleratedEnemy(200, 200, 1, 0, 800, 500);
        ae.setWalls(List.of(new Rectangle(206, 190, 20, 30)));
        ae.move();
        int x1 = ae.getBounds().x;
        ae.move();
        assertTrue(ae.getBounds().x < x1);
    }

    // ─── MachinePlayer ──────────────────────────────────────────

    /** MachinePlayer EXPERT deberia moverse hacia la moneda mas cercana. */
    @Test
    void shouldMachinePlayerExpertMoveTowardCoin() {
        MachinePlayer machine = new MachinePlayer(400, 240, MachineProfile.EXPERT);
        machine.setWalls(List.of());
        Coin coin = new Coin(100, 240);
        int initialX = machine.getX();
        machine.updateAI(List.of(coin), new ArrayList<>(), 30, 240);
        assertTrue(machine.getX() < initialX);
    }

    /** MachinePlayer deberia retornar su perfil correctamente. */
    @Test
    void shouldMachinePlayerReturnCorrectProfile() {
        MachinePlayer machine = new MachinePlayer(400, 240, MachineProfile.EXPERT);
        assertEquals(MachineProfile.EXPERT, machine.getProfile());
    }

    /** MachinePlayer RANDOM deberia moverse con lista de monedas vacia. */
    @Test
    void shouldMachinePlayerRandomMoveWithNoCoins() {
        MachinePlayer machine = new MachinePlayer(400, 240, MachineProfile.RANDOM);
        machine.setWalls(List.of());
        int initialX = machine.getX();
        int initialY = machine.getY();
        for (int i = 0; i < 5; i++) {
            machine.updateAI(new ArrayList<>(), new ArrayList<>(), 30, 240);
        }
        assertTrue(machine.getX() != initialX || machine.getY() != initialY);
    }

    /** MachinePlayer EXPERT deberia ir al objetivo cuando no hay monedas. */
    @Test
    void shouldMachinePlayerExpertMoveToTargetWhenNoCoins() {
        MachinePlayer machine = new MachinePlayer(400, 240, MachineProfile.EXPERT);
        machine.setWalls(List.of());
        int initialX = machine.getX();
        machine.updateAI(new ArrayList<>(), new ArrayList<>(), 30, 240);
        assertTrue(machine.getX() < initialX);
    }

    /** MachinePlayer deberia detectar colision con otro jugador. */
    @Test
    void shouldMachinePlayerDetectCollisionWithPlayer() {
        MachinePlayer machine = new MachinePlayer(100, 100, MachineProfile.RANDOM);
        Player player = new Player(100, 100);
        assertTrue(machine.collidesWith(player));
    }

    /** MachinePlayer deberia respetar paredes al moverse. */
    @Test
    void shouldMachinePlayerRespectWalls() {
        MachinePlayer machine = new MachinePlayer(400, 240, MachineProfile.EXPERT);
        // Pared que bloquea el movimiento a la izquierda
        machine.setWalls(List.of(new Rectangle(0, 0, 405, 500)));
        Coin coin = new Coin(100, 240);
        machine.updateAI(List.of(coin), new ArrayList<>(), 30, 240);
        // Con la pared bloqueando izquierda, debe elegir otra direccion
        assertTrue(machine.getX() >= 0);
    }
}
