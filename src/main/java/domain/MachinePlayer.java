package domain;

import java.awt.Rectangle;
import java.util.List;
import java.util.Random;

/**
 * Jugador controlado por la máquina en el modo Player vs Machine.
 * IA mejorada: detecta paredes bloqueantes y elige direcciones alternativas
 * para evitar quedarse atascada. Soporta perfil RANDOM y EXPERT.
 */
public class MachinePlayer extends Player {

    private final MachineProfile profile;
    private final Random random = new Random();

    /** Dirección actual. 0=arriba, 1=abajo, 2=izq, 3=der */
    private int currentDir = 2;

    /** Ticks desde el último cambio de dirección. */
    private int dirTick = 0;

    /** Posición X en el tick anterior, para detectar si está atascada. */
    private int lastX = -1;
    private int lastY = -1;

    /** Ticks consecutivos sin moverse (atascada). */
    private int stuckTicks = 0;

    public MachinePlayer(int startX, int startY, MachineProfile profile) {
        super(startX, startY, PlayerType.RED);
        this.profile = profile;
    }

    public void updateAI(List<Coin> coins, List<SkinCoin> skinCoins,
                         int targetX, int targetY) {
        int[] nearestCoin = findNearestCoin(coins, skinCoins);
        int goalX = nearestCoin != null ? nearestCoin[0] : targetX;
        int goalY = nearestCoin != null ? nearestCoin[1] : targetY;

        // Detectar si está atascada (no se movió en el tick anterior)
        if (getX() == lastX && getY() == lastY) {
            stuckTicks++;
        } else {
            stuckTicks = 0;
        }
        lastX = getX();
        lastY = getY();

        if (profile == MachineProfile.RANDOM) {
            moveRandom(goalX, goalY);
        } else {
            moveExpert(goalX, goalY);
        }
        move();
    }

    // ─────────────────────────────────────────────
    // PERFIL EXPERTO
    // ─────────────────────────────────────────────

    /**
     * Va hacia el objetivo eligiendo la dirección libre más cercana al goal.
     * Si está atascada, prueba las 4 direcciones en orden de preferencia.
     */
    private void moveExpert(int goalX, int goalY) {
        int[] preferred = preferredDirs(goalX, goalY);
        int chosen = chooseUnblockedDir(preferred);
        applyDir(chosen);
    }

    // ─────────────────────────────────────────────
    // PERFIL ALEATORIO
    // ─────────────────────────────────────────────

    /**
     * Cambia de dirección cada 25 ticks o cuando está atascada.
     * 65% de probabilidad de ir hacia el objetivo, 35% aleatorio.
     */
    private void moveRandom(int goalX, int goalY) {
        dirTick++;
        boolean forceChange = stuckTicks >= 8 || dirTick >= 25;

        if (forceChange) {
            dirTick   = 0;
            stuckTicks = 0;
            if (random.nextInt(10) < 7) {
                int[] preferred = preferredDirs(goalX, goalY);
                currentDir = chooseUnblockedDir(preferred);
            } else {
                // Dirección aleatoria que no esté bloqueada
                int[] shuffled = shuffledDirs();
                currentDir = chooseUnblockedDir(shuffled);
            }
        } else if (isBlocked(currentDir)) {
            // Si la dirección actual está bloqueada, recalcular
            int[] preferred = preferredDirs(goalX, goalY);
            currentDir = chooseUnblockedDir(preferred);
            dirTick = 0;
        }

        applyDir(currentDir);
    }

    // ─────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────

    /**
     * Retorna las 4 direcciones ordenadas por preferencia hacia (gx, gy).
     * Las dos primeras son las más cercanas al objetivo, las otras dos son alternativas.
     */
    private int[] preferredDirs(int gx, int gy) {
        int diffX = gx - getX();
        int diffY = gy - getY();

        int primary, secondary, alt1, alt2;
        if (Math.abs(diffX) >= Math.abs(diffY)) {
            primary   = diffX < 0 ? 2 : 3;
            secondary = diffY < 0 ? 0 : 1;
        } else {
            primary   = diffY < 0 ? 0 : 1;
            secondary = diffX < 0 ? 2 : 3;
        }
        // Las otras dos direcciones opuestas como último recurso
        alt1 = opposite(primary);
        alt2 = opposite(secondary);
        return new int[]{primary, secondary, alt1, alt2};
    }

    /**
     * De la lista de direcciones en orden de preferencia, retorna la primera
     * que no esté bloqueada por una pared. Si todas están bloqueadas, retorna la primera.
     */
    private int chooseUnblockedDir(int[] dirs) {
        for (int d : dirs) {
            if (!isBlocked(d)) return d;
        }
        return dirs[0];
    }

    /**
     * Verifica si moverse en la dirección dada chocaría con una pared
     * en los próximos pasos.
     */
    private boolean isBlocked(int dir) {
        int step = getSpeed() + 2;
        int nx = getX(), ny = getY();
        switch (dir) {
            case 0 -> ny -= step;
            case 1 -> ny += step;
            case 2 -> nx -= step;
            case 3 -> nx += step;
        }
        Rectangle next = new Rectangle(nx, ny, getSize(), getSize());
        List<Rectangle> walls = getWallsForAI();
        return walls.stream().anyMatch(w -> w.intersects(next));
    }

    /** Aplica la dirección dada activando los flags de movimiento. */
    private void applyDir(int dir) {
        setMovingUp(false); setMovingDown(false);
        setMovingLeft(false); setMovingRight(false);
        switch (dir) {
            case 0 -> setMovingUp(true);
            case 1 -> setMovingDown(true);
            case 2 -> setMovingLeft(true);
            case 3 -> setMovingRight(true);
        }
    }

    /** Retorna la dirección opuesta. */
    private int opposite(int dir) {
        return switch (dir) {
            case 0 -> 1;
            case 1 -> 0;
            case 2 -> 3;
            default -> 2;
        };
    }

    /** Retorna las 4 direcciones en orden aleatorio. */
    private int[] shuffledDirs() {
        int[] dirs = {0, 1, 2, 3};
        for (int i = 3; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int tmp = dirs[i]; dirs[i] = dirs[j]; dirs[j] = tmp;
        }
        return dirs;
    }

    /**
     * Accede a las paredes del jugador (heredadas de Player) para la IA.
     * Usa reflexión sobre el campo walls de Player a través del método setWalls.
     * Como Player no expone getWalls(), usamos un campo propio cacheado.
     */
    private List<Rectangle> cachedWalls = List.of();

    @Override
    public void setWalls(java.util.List<Rectangle> walls) {
        super.setWalls(walls);
        this.cachedWalls = walls;
    }

    private List<Rectangle> getWallsForAI() {
        return cachedWalls;
    }

    private int[] findNearestCoin(List<Coin> coins, List<SkinCoin> skinCoins) {
        int cx = getX(), cy = getY();
        int bestDist = Integer.MAX_VALUE;
        int[] best = null;

        for (Coin c : coins) {
            if (!c.isCollected()) {
                int d = Math.abs(c.getBounds().x - cx) + Math.abs(c.getBounds().y - cy);
                if (d < bestDist) { bestDist = d; best = new int[]{c.getBounds().x, c.getBounds().y}; }
            }
        }
        for (SkinCoin sc : skinCoins) {
            if (!sc.isCollected()) {
                int d = Math.abs(sc.getBounds().x - cx) + Math.abs(sc.getBounds().y - cy);
                if (d < bestDist) { bestDist = d; best = new int[]{sc.getBounds().x, sc.getBounds().y}; }
            }
        }
        return best;
    }

    public MachineProfile getProfile() { return profile; }
}
