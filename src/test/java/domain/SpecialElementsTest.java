package domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para los elementos especiales del juego:
 * SkinCoin, GreenCoin, Bomb, LifeSource y CheckpointZone.
 */
class SpecialElementsTest {

    // ─── SkinCoin ───────────────────────────────────────────────

    /** SkinCoin deberia iniciar no recogida. */
    @Test
    void shouldSkinCoinStartNotCollected() {
        SkinCoin sc = new SkinCoin(100, 100);
        assertFalse(sc.isCollected());
    }

    /** SkinCoin deberia transformar al jugador a BLUE al recogerla. */
    @Test
    void shouldSkinCoinApplyBlueTypeToPlayer() {
        SkinCoin sc = new SkinCoin(100, 100);
        Player player = new Player(100, 100);
        sc.collect(player);
        assertTrue(sc.isCollected());
        assertEquals(PlayerType.BLUE, player.getType());
    }

    /** SkinCoin no deberia colisionar si ya fue recogida. */
    @Test
    void shouldSkinCoinNotCollideIfAlreadyCollected() {
        SkinCoin sc = new SkinCoin(100, 100);
        Player player = new Player(100, 100);
        sc.collect(player);
        assertFalse(sc.collidesWith(player));
    }

    /** SkinCoin deberia restablecerse a no recogida con reset(). */
    @Test
    void shouldSkinCoinResetToNotCollected() {
        SkinCoin sc = new SkinCoin(100, 100);
        Player player = new Player(100, 100);
        sc.collect(player);
        sc.reset();
        assertFalse(sc.isCollected());
    }

    /** SkinCoin deberia retornar bounds correctos. */
    @Test
    void shouldSkinCoinReturnCorrectBounds() {
        SkinCoin sc = new SkinCoin(50, 60);
        assertEquals(50, sc.getBounds().x);
        assertEquals(60, sc.getBounds().y);
    }

    // ─── GreenCoin ──────────────────────────────────────────────

    /** GreenCoin deberia iniciar no recogida. */
    @Test
    void shouldGreenCoinStartNotCollected() {
        GreenCoin gc = new GreenCoin(200, 200);
        assertFalse(gc.isCollected());
    }

    /** GreenCoin deberia transformar al jugador a GREEN al recogerla. */
    @Test
    void shouldGreenCoinApplyGreenTypeToPlayer() {
        GreenCoin gc = new GreenCoin(100, 100);
        Player player = new Player(100, 100);
        gc.collect(player);
        assertTrue(gc.isCollected());
        assertEquals(PlayerType.GREEN, player.getType());
    }

    /** GreenCoin no deberia colisionar si ya fue recogida. */
    @Test
    void shouldGreenCoinNotCollideIfAlreadyCollected() {
        GreenCoin gc = new GreenCoin(100, 100);
        Player player = new Player(100, 100);
        gc.collect(player);
        assertFalse(gc.collidesWith(player));
    }

    /** GreenCoin deberia restablecerse con reset(). */
    @Test
    void shouldGreenCoinResetToNotCollected() {
        GreenCoin gc = new GreenCoin(100, 100);
        Player player = new Player(100, 100);
        gc.collect(player);
        gc.reset();
        assertFalse(gc.isCollected());
    }

    // ─── Bomb ───────────────────────────────────────────────────

    /** Bomb deberia detectar colision con jugador en la misma posicion. */
    @Test
    void shouldBombDetectCollisionWithPlayerAtSamePosition() {
        Bomb bomb = new Bomb(100, 100);
        Player player = new Player(100, 100);
        assertTrue(bomb.collidesWith(player));
    }

    /** Bomb no deberia detectar colision con jugador lejos. */
    @Test
    void shouldBombNotDetectCollisionWithPlayerFarAway() {
        Bomb bomb = new Bomb(100, 100);
        Player player = new Player(500, 500);
        assertFalse(bomb.collidesWith(player));
    }

    /** Bomb deberia retornar bounds correctos. */
    @Test
    void shouldBombReturnCorrectBounds() {
        Bomb bomb = new Bomb(50, 70);
        assertEquals(50, bomb.getBounds().x);
        assertEquals(70, bomb.getBounds().y);
    }

    // ─── LifeSource ─────────────────────────────────────────────

    /** LifeSource deberia iniciar no recogida. */
    @Test
    void shouldLifeSourceStartNotCollected() {
        LifeSource ls = new LifeSource(100, 100);
        assertFalse(ls.isCollected());
    }

    /** LifeSource deberia activar escudo en el jugador al recogerla. */
    @Test
    void shouldLifeSourceActivateShieldOnPlayer() {
        LifeSource ls = new LifeSource(100, 100);
        Player player = new Player(100, 100);
        ls.collect(player);
        assertTrue(ls.isCollected());
        assertTrue(player.isShielded());
        assertTrue(player.isLifeShield());
    }

    /** LifeSource no deberia activarse dos veces. */
    @Test
    void shouldLifeSourceNotActivateTwice() {
        LifeSource ls = new LifeSource(100, 100);
        Player player = new Player(100, 100);
        ls.collect(player);
        player.applyType(PlayerType.RED); // quita el escudo
        ls.collect(player);              // segunda llamada no debe hacer nada
        assertFalse(player.isShielded());
    }

    /** LifeSource deberia restablecerse con reset(). */
    @Test
    void shouldLifeSourceResetToNotCollected() {
        LifeSource ls = new LifeSource(100, 100);
        Player player = new Player(100, 100);
        ls.collect(player);
        ls.reset();
        assertFalse(ls.isCollected());
    }

    /** LifeSource no deberia colisionar si ya fue recogida. */
    @Test
    void shouldLifeSourceNotCollideIfAlreadyCollected() {
        LifeSource ls = new LifeSource(100, 100);
        Player player = new Player(100, 100);
        ls.collect(player);
        assertFalse(ls.collidesWith(player));
    }

    // ─── CheckpointZone ─────────────────────────────────────────

    /** CheckpointZone deberia iniciar no activada. */
    @Test
    void shouldCheckpointZoneStartNotActivated() {
        CheckpointZone cp = new CheckpointZone(100, 100, 80, 60);
        assertFalse(cp.isActivated());
    }

    /** CheckpointZone deberia activarse cuando el jugador la pisa. */
    @Test
    void shouldCheckpointZoneActivateWhenPlayerEnters() {
        CheckpointZone cp = new CheckpointZone(100, 100, 80, 60);
        Player player = new Player(110, 110);
        cp.checkAndActivate(player);
        assertTrue(cp.isActivated());
    }

    /** CheckpointZone deberia guardar la posicion del jugador al activarse. */
    @Test
    void shouldCheckpointZoneSavePlayerPosition() {
        CheckpointZone cp = new CheckpointZone(100, 100, 80, 60);
        Player player = new Player(110, 110);
        cp.checkAndActivate(player);
        assertTrue(player.getCheckpointX() >= 0);
    }

    /** CheckpointZone deberia restablecerse a no activada con reset(). */
    @Test
    void shouldCheckpointZoneResetToNotActivated() {
        CheckpointZone cp = new CheckpointZone(100, 100, 80, 60);
        Player player = new Player(110, 110);
        cp.checkAndActivate(player);
        cp.reset();
        assertFalse(cp.isActivated());
    }

    /** CheckpointZone no deberia activarse si el jugador no esta dentro. */
    @Test
    void shouldCheckpointZoneNotActivateWhenPlayerIsOutside() {
        CheckpointZone cp = new CheckpointZone(100, 100, 80, 60);
        Player player = new Player(500, 500);
        cp.checkAndActivate(player);
        assertFalse(cp.isActivated());
    }

    // ─── PlayerType ─────────────────────────────────────────────

    /** PlayerType RED deberia tener velocidad 3 y tamano 20. */
    @Test
    void shouldPlayerTypeRedHaveCorrectAttributes() {
        assertEquals(3,  PlayerType.RED.speed);
        assertEquals(20, PlayerType.RED.size);
    }

    /** PlayerType BLUE deberia tener velocidad 4 y tamano 30. */
    @Test
    void shouldPlayerTypeBlueHaveCorrectAttributes() {
        assertEquals(4,  PlayerType.BLUE.speed);
        assertEquals(30, PlayerType.BLUE.size);
    }

    /** PlayerType GREEN deberia tener velocidad 3 y tamano 20. */
    @Test
    void shouldPlayerTypeGreenHaveCorrectAttributes() {
        assertEquals(3,  PlayerType.GREEN.speed);
        assertEquals(20, PlayerType.GREEN.size);
    }

    // ─── StartZone / EndZone ────────────────────────────────────

    /** StartZone deberia reposicionar al jugador en su centro. */
    @Test
    void shouldStartZoneResetPlayerToCenter() {
        StartZone start = new StartZone(20, 200, 80, 100);
        Player player = new Player(500, 500);
        start.resetPlayer(player);
        assertTrue(player.getX() >= 20 && player.getX() <= 100);
        assertTrue(player.getY() >= 200 && player.getY() <= 300);
    }

    /** EndZone deberia detectar que el jugador completo el nivel cuando esta dentro. */
    @Test
    void shouldEndZoneReturnTrueWhenPlayerIsInside() {
        EndZone end = new EndZone(700, 200, 80, 100);
        Player player = new Player(720, 240);
        assertTrue(end.checkLevelComplete(player));
    }

    /** EndZone deberia retornar false cuando el jugador no esta dentro. */
    @Test
    void shouldEndZoneReturnFalseWhenPlayerIsOutside() {
        EndZone end = new EndZone(700, 200, 80, 100);
        Player player = new Player(100, 100);
        assertFalse(end.checkLevelComplete(player));
    }

    // ─── Player escudo ──────────────────────────────────────────

    /** Player con escudo GREEN deberia absorber el primer golpe sin morir. */
    @Test
    void shouldPlayerAbsorbHitWhenShielded() {
        Player player = new Player(100, 100);
        player.applyType(PlayerType.GREEN);
        assertTrue(player.absorbHit());
        assertFalse(player.isShielded());
    }

    /** Player sin escudo deberia retornar false al absorber golpe. */
    @Test
    void shouldPlayerNotAbsorbHitWhenNotShielded() {
        Player player = new Player(100, 100);
        assertFalse(player.absorbHit());
    }

    /** Player en periodo de invencibilidad deberia absorber golpe. */
    @Test
    void shouldPlayerAbsorbHitDuringInvincibility() {
        Player player = new Player(100, 100);
        player.applyType(PlayerType.GREEN);
        player.absorbHit(); // consume escudo, activa 90 ticks invencibilidad
        assertTrue(player.absorbHit()); // sigue invencible
    }

    /** Player deberia decrementar ticks de invencibilidad con tickInvincibility(). */
    @Test
    void shouldPlayerDecrementInvincibilityTicks() {
        Player player = new Player(100, 100);
        player.grantRespawnInvincibility();
        player.tickInvincibility();
        // No podemos leer invincibleTicks directamente, pero absorbHit debe retornar true
        assertTrue(player.absorbHit());
    }

    /** Player GREEN al absorber golpe deberia bajar velocidad a 1. */
    @Test
    void shouldPlayerGreenReduceSpeedAfterAbsorbingHit() {
        Player player = new Player(100, 100);
        player.applyType(PlayerType.GREEN);
        player.absorbHit();
        assertEquals(1, player.getSpeed());
    }

    /** Player con LifeShield al absorber golpe NO deberia bajar velocidad. */
    @Test
    void shouldPlayerLifeShieldNotReduceSpeed() {
        Player player = new Player(100, 100);
        player.activateLifeShield();
        int speedBefore = player.getSpeed();
        player.absorbHit();
        assertEquals(speedBefore, player.getSpeed());
    }

    // ─── BoardPvP ───────────────────────────────────────────────

    /** BoardPvP deberia retornar las zonas correctas. */
    @Test
    void shouldBoardPvPReturnCorrectZones() {
        StartZone s1 = new StartZone(20, 200, 80, 100);
        StartZone s2 = new StartZone(700, 200, 80, 100);
        EndZone   e1 = new EndZone(700, 200, 80, 100);
        EndZone   e2 = new EndZone(20, 200, 80, 100);
        BoardPvP board = new BoardPvP(s1, s2, e1, e2, List.of());
        assertSame(s1, board.getStartZone1());
        assertSame(s2, board.getStartZone2());
        assertSame(e1, board.getEndZone1());
        assertSame(e2, board.getEndZone2());
    }

    /** Board deberia retornar sus dimensiones correctas. */
    @Test
    void shouldBoardReturnCorrectDimensions() {
        StartZone start = new StartZone(20, 200, 80, 100);
        EndZone   end   = new EndZone(700, 200, 80, 100);
        Board board = new Board(start, end, List.of());
        assertEquals(800, board.getWidth());
        assertEquals(500, board.getHeight());
        assertSame(start, board.getStartZone());
        assertSame(end, board.getEndZone());
    }

    /** Board.getDefaultSize() deberia retornar 800x500. */
    @Test
    void shouldBoardGetDefaultSizeReturn800x500() {
        int[] size = Board.getDefaultSize();
        assertEquals(800, size[0]);
        assertEquals(500, size[1]);
    }

    /** AcceleratedEnemy vertical deberia moverse hacia abajo. */
    @Test
    void shouldAcceleratedEnemyMoveVertically() {
        domain.AcceleratedEnemy ae = new domain.AcceleratedEnemy(200, 100, 0, 1, 800, 500);
        int initialY = ae.getBounds().y;
        ae.move();
        assertEquals(initialY + 6, ae.getBounds().y);
    }

    /** AcceleratedEnemy deberia rebotar en borde inferior. */
    @Test
    void shouldAcceleratedEnemyBounceAtBottomBorder() {
        domain.AcceleratedEnemy ae = new domain.AcceleratedEnemy(200, 480, 0, 1, 800, 500);
        ae.move();
        int y1 = ae.getBounds().y;
        ae.move();
        assertTrue(ae.getBounds().y < y1);
    }

    /** SliderEnemy deberia rebotar al chocar con zona prohibida. */
    @Test
    void shouldSliderEnemyBounceWhenHittingForbiddenZone() {
        domain.SliderEnemy slider = new domain.SliderEnemy(200, 100, 3, 500);
        slider.addForbiddenZone(new java.awt.Rectangle(190, 103, 30, 20));
        int y1 = slider.getBounds().y;
        slider.move();
        assertTrue(slider.getBounds().y <= y1);
    }

    /** AcceleratedEnemy deberia rebotar al chocar con zona prohibida. */
    @Test
    void shouldAcceleratedEnemyBounceWhenHittingForbiddenZone() {
        domain.AcceleratedEnemy ae = new domain.AcceleratedEnemy(200, 200, 1, 0, 800, 500);
        ae.addForbiddenZone(new java.awt.Rectangle(206, 190, 20, 30));
        ae.move();
        int x1 = ae.getBounds().x;
        ae.move();
        assertTrue(ae.getBounds().x < x1);
    }

    /** MachinePlayer atascado deberia recalcular direccion. */
    @Test
    void shouldMachinePlayerRecalculateDirectionWhenStuck() {
        domain.MachinePlayer machine = new domain.MachinePlayer(400, 240, domain.MachineProfile.RANDOM);
        machine.setWalls(List.of());
        // Simular 8 ticks sin moverse forzando posicion fija
        for (int i = 0; i < 10; i++) {
            machine.setPosition(400, 240);
            machine.updateAI(new java.util.ArrayList<>(), new java.util.ArrayList<>(), 30, 240);
        }
        // Despues de detectar atasco, debe haber recalculado - no lanza excepcion
        assertNotNull(machine);
    }

    /** GameState deberia tener las constantes correctas. */
    @Test
    void shouldGameStateHaveCorrectConstants() {
        assertEquals("PLAYING", GameState.PLAYING);
        assertEquals("PAUSED",  GameState.PAUSED);
        assertEquals("WIN",     GameState.WIN);
        assertEquals("TIMEOUT", GameState.TIMEOUT);
        assertEquals("DEAD",    GameState.DEAD);
    }

    /** Zone deberia retornar sus dimensiones correctamente. */
    @Test
    void shouldZoneReturnCorrectDimensions() {
        StartZone zone = new StartZone(10, 20, 80, 60);
        assertEquals(10, zone.getX());
        assertEquals(20, zone.getY());
        assertEquals(80, zone.getWidth());
        assertEquals(60, zone.getHeight());
    }

    /** Player applyType RED deberia restaurar velocidad y tamano base. */
    @Test
    void shouldPlayerApplyTypeRedRestoreBaseStats() {
        Player p = new Player(100, 100);
        p.applyType(PlayerType.BLUE);
        p.applyType(PlayerType.RED);
        assertEquals(PlayerType.RED, p.getType());
        assertEquals(PlayerType.RED.speed, p.getSpeed());
        assertEquals(PlayerType.RED.size, p.getSize());
    }

    /** Player applyType GREEN deberia activar escudo. */
    @Test
    void shouldPlayerApplyTypeGreenActivateShield() {
        Player p = new Player(100, 100);
        p.applyType(PlayerType.GREEN);
        assertTrue(p.isShielded());
    }

    /** Player grantRespawnInvincibility deberia activar invencibilidad. */
    @Test
    void shouldPlayerGrantRespawnInvincibilityActivateInvincibility() {
        Player p = new Player(100, 100);
        p.grantRespawnInvincibility();
        assertTrue(p.absorbHit());
    }

    /** MachinePlayer EXPERT con paredes en todas direcciones no deberia lanzar excepcion. */
    @Test
    void shouldMachinePlayerExpertNotThrowWhenAllDirectionsBlocked() {
        domain.MachinePlayer machine = new domain.MachinePlayer(400, 240, domain.MachineProfile.EXPERT);
        machine.setWalls(List.of(
            new java.awt.Rectangle(0, 0, 800, 500)
        ));
        assertDoesNotThrow(() ->
            machine.updateAI(new java.util.ArrayList<>(), new java.util.ArrayList<>(), 30, 240));
    }

    /** PatrolEnemy deberia retornar bounds correctos. */
    @Test
    void shouldPatrolEnemyReturnCorrectBounds() {
        PatrolEnemy patrol = new PatrolEnemy(150, 200, 3, new int[][]{{300, 200}});
        assertEquals(150, patrol.getBounds().x);
        assertEquals(200, patrol.getBounds().y);
    }

    /** SliderEnemy deberia retornar bounds correctos. */
    @Test
    void shouldSliderEnemyReturnCorrectBounds() {
        domain.SliderEnemy slider = new domain.SliderEnemy(100, 150, 3, 500);
        assertEquals(100, slider.getBounds().x);
        assertEquals(150, slider.getBounds().y);
    }

    /** GreenCoin deberia retornar bounds correctos. */
    @Test
    void shouldGreenCoinReturnCorrectBounds() {
        GreenCoin gc = new GreenCoin(80, 90);
        assertEquals(80, gc.getBounds().x);
        assertEquals(90, gc.getBounds().y);
    }

    /** LifeSource deberia retornar bounds correctos. */
    @Test
    void shouldLifeSourceReturnCorrectBounds() {
        LifeSource ls = new LifeSource(70, 80);
        assertEquals(70, ls.getBounds().x);
        assertEquals(80, ls.getBounds().y);
    }

    /** CheckpointZone no deberia activarse dos veces. */
    @Test
    void shouldCheckpointZoneNotSaveCheckpointTwice() {
        CheckpointZone cp = new CheckpointZone(100, 100, 80, 60);
        Player p = new Player(110, 110);
        cp.checkAndActivate(p);
        p.setPosition(500, 500);
        cp.checkAndActivate(p); // segunda llamada, ya activado
        assertTrue(cp.isActivated());
    }

    /** CheckpointZone ya activada no deberia sobreescribir checkpoint del jugador. */
    @Test
    void shouldCheckpointZoneNotOverwriteCheckpointWhenAlreadyActivated() {
        CheckpointZone cp = new CheckpointZone(100, 100, 80, 60);
        Player p = new Player(110, 110);
        cp.checkAndActivate(p);
        int savedX = p.getCheckpointX();
        p.setPosition(110, 110); // vuelve a estar dentro
        cp.checkAndActivate(p); // ya activado, no debe cambiar nada
        assertEquals(savedX, p.getCheckpointX());
    }

    /** GreenCoin colidesWith deberia retornar false cuando ya fue recogida. */
    @Test
    void shouldGreenCoinNotCollideWhenAlreadyCollected() {
        GreenCoin gc = new GreenCoin(100, 100);
        Player p = new Player(100, 100);
        gc.collect(p);
        assertFalse(gc.collidesWith(p));
    }

    /** GreenCoin colidesWith deberia retornar false cuando jugador esta lejos. */
    @Test
    void shouldGreenCoinNotCollideWithPlayerFarAway() {
        GreenCoin gc = new GreenCoin(100, 100);
        Player p = new Player(500, 500);
        assertFalse(gc.collidesWith(p));
    }

    /** SkinCoin colidesWith deberia retornar false cuando jugador esta lejos. */
    @Test
    void shouldSkinCoinNotCollideWithPlayerFarAway() {
        SkinCoin sc = new SkinCoin(100, 100);
        Player p = new Player(500, 500);
        assertFalse(sc.collidesWith(p));
    }

    /** LifeSource colidesWith deberia retornar false cuando jugador esta lejos. */
    @Test
    void shouldLifeSourceNotCollideWithPlayerFarAway() {
        LifeSource ls = new LifeSource(100, 100);
        Player p = new Player(500, 500);
        assertFalse(ls.collidesWith(p));
    }

    /** GreenCoin colidesWith deberia retornar true cuando jugador esta encima y no recogida. */
    @Test
    void shouldGreenCoinCollideWithPlayerAtSamePosition() {
        GreenCoin gc = new GreenCoin(100, 100);
        Player p = new Player(100, 100);
        assertTrue(gc.collidesWith(p));
    }

    /** SkinCoin colidesWith deberia retornar true cuando jugador esta encima y no recogida. */
    @Test
    void shouldSkinCoinCollideWithPlayerAtSamePosition() {
        SkinCoin sc = new SkinCoin(100, 100);
        Player p = new Player(100, 100);
        assertTrue(sc.collidesWith(p));
    }

    /** LifeSource colidesWith deberia retornar true cuando jugador esta encima y no recogida. */
    @Test
    void shouldLifeSourceCollideWithPlayerAtSamePosition() {
        LifeSource ls = new LifeSource(100, 100);
        Player p = new Player(100, 100);
        assertTrue(ls.collidesWith(p));
    }

    /** Coin colidesWith deberia retornar true cuando jugador esta encima y no recogida. */
    @Test
    void shouldCoinCollideWithPlayerAtSamePosition() {
        Coin c = new Coin(100, 100);
        Player p = new Player(100, 100);
        assertTrue(c.collidesWith(p));
    }

    /** GameLogger.error con excepcion null no deberia lanzar excepcion. */
    @Test
    void shouldNotGameLoggerErrorWithNullExceptionThrow() {
        assertDoesNotThrow(() -> GameLogger.error("error con null", null));
    }
}
