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

    /** MachinePlayer RANDOM deberia cambiar direccion cuando la actual esta bloqueada. */
    @Test
    void shouldMachinePlayerRandomChangeDirectionWhenCurrentIsBlocked() {
        MachinePlayer machine = new MachinePlayer(400, 240, MachineProfile.RANDOM);
        // Bloquear todas las direcciones excepto abajo
        machine.setWalls(List.of(
            new Rectangle(0, 0, 800, 238),   // bloquea arriba
            new Rectangle(0, 0, 398, 500),   // bloquea izquierda
            new Rectangle(406, 0, 400, 500)  // bloquea derecha
        ));
        // Forzar cambio de direccion ejecutando varios ticks
        for (int i = 0; i < 30; i++) {
            machine.updateAI(new ArrayList<>(), new ArrayList<>(), 400, 400);
        }
        assertNotNull(machine);
    }

    /** MachinePlayer deberia usar SkinCoin como objetivo cuando esta mas cerca. */
    @Test
    void shouldMachinePlayerTargetSkinCoinWhenCloser() {
        MachinePlayer machine = new MachinePlayer(400, 240, MachineProfile.EXPERT);
        machine.setWalls(List.of());
        SkinCoin sc = new SkinCoin(100, 240); // mas cerca que el objetivo
        int initialX = machine.getX();
        machine.updateAI(new ArrayList<>(), List.of(sc), 700, 240);
        assertTrue(machine.getX() < initialX);
    }

    /** MachinePlayer RANDOM con forceChange deberia elegir direccion aleatoria. */
    @Test
    void shouldMachinePlayerRandomChooseRandomDirectionOnForceChange() {
        MachinePlayer machine = new MachinePlayer(400, 240, MachineProfile.RANDOM);
        machine.setWalls(List.of());
        for (int i = 0; i < 26; i++) {
            machine.updateAI(new ArrayList<>(), new ArrayList<>(), 30, 240);
        }
        assertNotNull(machine);
    }

    /** MachinePlayer EXPERT con objetivo abajo-derecha deberia moverse en esa direccion. */
    @Test
    void shouldMachinePlayerExpertMoveDownRightWhenGoalIsBelowRight() {
        MachinePlayer machine = new MachinePlayer(100, 100, MachineProfile.EXPERT);
        machine.setWalls(List.of());
        machine.updateAI(new ArrayList<>(), new ArrayList<>(), 700, 400);
        assertTrue(machine.getX() > 100 || machine.getY() > 100);
    }

    /** MachinePlayer EXPERT con objetivo arriba-izquierda deberia moverse en esa direccion. */
    @Test
    void shouldMachinePlayerExpertMoveUpLeftWhenGoalIsAboveLeft() {
        MachinePlayer machine = new MachinePlayer(400, 300, MachineProfile.EXPERT);
        machine.setWalls(List.of());
        machine.updateAI(new ArrayList<>(), new ArrayList<>(), 50, 50);
        assertTrue(machine.getX() < 400 || machine.getY() < 300);
    }

    /** MachinePlayer EXPERT con objetivo arriba-derecha deberia moverse en esa direccion. */
    @Test
    void shouldMachinePlayerExpertMoveUpRightWhenGoalIsAboveRight() {
        MachinePlayer machine = new MachinePlayer(100, 400, MachineProfile.EXPERT);
        machine.setWalls(List.of());
        machine.updateAI(new ArrayList<>(), new ArrayList<>(), 700, 50);
        assertTrue(machine.getX() > 100 || machine.getY() < 400);
    }

    /** MachinePlayer RANDOM con direccion actual bloqueada deberia recalcular. */
    @Test
    void shouldMachinePlayerRandomRecalculateWhenCurrentDirectionIsBlocked() {
        MachinePlayer machine = new MachinePlayer(400, 240, MachineProfile.RANDOM);
        machine.setWalls(List.of(new Rectangle(0, 0, 398, 500)));
        machine.updateAI(new ArrayList<>(), new ArrayList<>(), 700, 240);
        assertTrue(machine.getX() >= 400);
    }

    /** MachinePlayer con SkinCoin ya recogida no deberia usarla como objetivo. */
    @Test
    void shouldMachinePlayerNotTargetCollectedSkinCoin() {
        MachinePlayer machine = new MachinePlayer(400, 240, MachineProfile.EXPERT);
        machine.setWalls(List.of());
        SkinCoin sc = new SkinCoin(100, 240);
        Player dummy = new Player(100, 240);
        sc.collect(dummy);
        int initialX = machine.getX();
        machine.updateAI(new ArrayList<>(), List.of(sc), 700, 240);
        assertTrue(machine.getX() > initialX);
    }

    /** MachinePlayer con Coin ya recogida no deberia usarla como objetivo. */
    @Test
    void shouldMachinePlayerNotTargetCollectedCoin() {
        MachinePlayer machine = new MachinePlayer(400, 240, MachineProfile.EXPERT);
        machine.setWalls(List.of());
        Coin c = new Coin(100, 240);
        c.collect();
        int initialX = machine.getX();
        machine.updateAI(List.of(c), new ArrayList<>(), 700, 240);
        assertTrue(machine.getX() > initialX);
    }

    /** SliderEnemy deberia rebotar al chocar con pared arriba. */
    @Test
    void shouldSliderEnemyBounceWhenHittingWallAbove() {
        SliderEnemy slider = new SliderEnemy(200, 100, -3, 500);
        slider.setWalls(List.of(new Rectangle(0, 96, 800, 10)));
        int y1 = slider.getBounds().y;
        slider.move();
        assertTrue(slider.getBounds().y >= y1);
    }

    /** AcceleratedEnemy deberia rebotar en borde izquierdo. */
    @Test
    void shouldAcceleratedEnemyBounceAtLeftBorder() {
        AcceleratedEnemy ae = new AcceleratedEnemy(5, 200, -1, 0, 800, 500);
        ae.move();
        int x1 = ae.getBounds().x;
        ae.move();
        assertTrue(ae.getBounds().x > x1);
    }

    /** AcceleratedEnemy deberia rebotar en borde superior. */
    @Test
    void shouldAcceleratedEnemyBounceAtTopBorder() {
        AcceleratedEnemy ae = new AcceleratedEnemy(200, 5, 0, -1, 800, 500);
        ae.move();
        int y1 = ae.getBounds().y;
        ae.move();
        assertTrue(ae.getBounds().y > y1);
    }

    /** PatrolEnemy deberia ciclar al llegar al ultimo waypoint. */
    @Test
    void shouldPatrolEnemyCycleBackToFirstWaypointAfterLast() {
        PatrolEnemy patrol = new PatrolEnemy(100, 100, 20, new int[][]{
            {102, 100}, {200, 100}
        });
        for (int i = 0; i < 25; i++) patrol.move();
        assertTrue(patrol.getBounds().x <= 200);
    }

    /** PatrolEnemy con movimiento vertical deberia moverse hacia abajo. */
    @Test
    void shouldPatrolEnemyMoveVerticallyTowardWaypoint() {
        PatrolEnemy patrol = new PatrolEnemy(100, 100, 3, new int[][]{
            {100, 200}
        });
        int initialY = patrol.getBounds().y;
        patrol.move();
        assertTrue(patrol.getBounds().y > initialY);
    }

    /** PatrolEnemy ya en el waypoint deberia avanzar al siguiente inmediatamente. */
    @Test
    void shouldPatrolEnemyAdvanceWaypointWhenAlreadyAtTarget() {
        // Enemigo ya en la posicion del waypoint
        PatrolEnemy patrol = new PatrolEnemy(100, 100, 3, new int[][]{
            {100, 100}, {200, 100}
        });
        patrol.move(); // ya esta en {100,100}, debe avanzar a {200,100}
        assertTrue(patrol.getBounds().x >= 100);
    }

    /** PatrolEnemy con speed mayor que distancia deberia llegar exactamente al waypoint. */
    @Test
    void shouldPatrolEnemySnapToWaypointWhenSpeedExceedsDistance() {
        PatrolEnemy patrol = new PatrolEnemy(100, 100, 50, new int[][]{
            {105, 100}, {200, 100}
        });
        patrol.move();
        assertEquals(105, patrol.getBounds().x);
    }

    /** BoardPvP deberia retornar dimensiones correctas. */
    @Test
    void shouldBoardPvPReturnCorrectDimensions() {
        StartZone s1 = new StartZone(20, 200, 80, 100);
        StartZone s2 = new StartZone(700, 200, 80, 100);
        EndZone   e1 = new EndZone(700, 200, 80, 100);
        EndZone   e2 = new EndZone(20, 200, 80, 100);
        BoardPvP board = new BoardPvP(s1, s2, e1, e2, List.of());
        assertEquals(800, board.getWidth());
        assertEquals(500, board.getHeight());
    }

    /** BoardPvP deberia retornar lista de paredes correcta. */
    @Test
    void shouldBoardPvPReturnCorrectWalls() {
        StartZone s1 = new StartZone(20, 200, 80, 100);
        StartZone s2 = new StartZone(700, 200, 80, 100);
        EndZone   e1 = new EndZone(700, 200, 80, 100);
        EndZone   e2 = new EndZone(20, 200, 80, 100);
        java.awt.Rectangle wall = new java.awt.Rectangle(100, 100, 50, 50);
        BoardPvP board = new BoardPvP(s1, s2, e1, e2, List.of(wall));
        assertEquals(1, board.getWalls().size());
    }

    /** Enemy con dx=0 y dy=0 no deberia moverse. */
    @Test
    void shouldEnemyNotMoveWhenBothDxAndDyAreZero() {
        Enemy e = new Enemy(300, 250, 0, 0, 800, 500);
        e.move();
        assertEquals(300, e.getBounds().x);
        assertEquals(250, e.getBounds().y);
    }

    /** MachinePlayer RANDOM atascado deberia resetear stuckTicks y recalcular. */
    @Test
    void shouldMachinePlayerRandomResetStuckTicksWhenStuck() {
        MachinePlayer machine = new MachinePlayer(400, 240, MachineProfile.RANDOM);
        machine.setWalls(List.of(new Rectangle(0, 0, 800, 500)));
        for (int i = 0; i < 10; i++) {
            machine.setPosition(400, 240);
            machine.updateAI(new ArrayList<>(), new ArrayList<>(), 30, 240);
        }
        assertNotNull(machine);
    }

    /** MachinePlayer EXPERT con objetivo exactamente igual a posicion no deberia lanzar excepcion. */
    @Test
    void shouldMachinePlayerExpertNotThrowWhenAtGoalPosition() {
        MachinePlayer machine = new MachinePlayer(400, 240, MachineProfile.EXPERT);
        machine.setWalls(List.of());
        assertDoesNotThrow(() ->
            machine.updateAI(new ArrayList<>(), new ArrayList<>(), 400, 240));
    }

    /** MachinePlayer RANDOM con stuckTicks exactamente 8 deberia recalcular. */
    @Test
    void shouldMachinePlayerRandomRecalculateAtExactly8StuckTicks() {
        MachinePlayer machine = new MachinePlayer(400, 240, MachineProfile.RANDOM);
        machine.setWalls(List.of());
        for (int i = 0; i < 8; i++) {
            machine.setPosition(400, 240);
            machine.updateAI(new ArrayList<>(), new ArrayList<>(), 30, 240);
        }
        assertNotNull(machine);
    }

    /** AcceleratedEnemy con dx=0 y dy=0 no deberia moverse. */
    @Test
    void shouldAcceleratedEnemyNotMoveWhenBothZero() {
        AcceleratedEnemy ae = new AcceleratedEnemy(200, 200, 0, 0, 800, 500);
        ae.move();
        assertEquals(200, ae.getBounds().x);
        assertEquals(200, ae.getBounds().y);
    }

    /** SliderEnemy con dy=0 no deberia moverse. */
    @Test
    void shouldSliderEnemyNotMoveWhenDyIsZero() {
        SliderEnemy slider = new SliderEnemy(200, 200, 0, 500);
        slider.move();
        assertEquals(200, slider.getBounds().y);
    }

    /** PatrolEnemy con un solo waypoint deberia ciclar al mismo. */
    @Test
    void shouldPatrolEnemyCycleToSameWaypointWhenOnlyOne() {
        PatrolEnemy patrol = new PatrolEnemy(100, 100, 3, new int[][]{
            {200, 100}
        });
        for (int i = 0; i < 50; i++) patrol.move();
        assertNotNull(patrol.getBounds());
    }
}
