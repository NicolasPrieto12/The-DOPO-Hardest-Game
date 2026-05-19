package domain;

import java.util.List;
import java.util.Random;

/**
 * Jugador controlado por la máquina en el modo Player vs Machine.
 * Soporta dos perfiles:
 * <ul>
 *   <li>{@link MachineProfile#RANDOM}: se mueve aleatoriamente pero con
 *       tendencia hacia las monedas y la meta.</li>
 *   <li>{@link MachineProfile#EXPERT}: va directamente a cada moneda
 *       y luego a la meta por la ruta más corta.</li>
 * </ul>
 */
public class MachinePlayer extends Player {

    /** Perfil de comportamiento de la máquina. */
    private final MachineProfile profile;

    /** Generador de números aleatorios para el perfil RANDOM. */
    private final Random random = new Random();

    /** Contador de ticks para cambiar dirección aleatoria. */
    private int randomTick = 0;

    /** Dirección actual en modo aleatorio. 0=arriba,1=abajo,2=izq,3=der */
    private int randomDir = 2;

    /**
     * Crea un jugador máquina con el perfil indicado.
     *
     * @param startX  Posición X inicial.
     * @param startY  Posición Y inicial.
     * @param profile Perfil de comportamiento (RANDOM o EXPERT).
     */
    public MachinePlayer(int startX, int startY, MachineProfile profile) {
        super(startX, startY, PlayerType.RED);
        this.profile = profile;
    }

    /**
     * Actualiza la dirección de la máquina y ejecuta el movimiento.
     * Primero intenta recoger monedas pendientes, luego va a la meta.
     *
     * @param coins     Lista de monedas del nivel.
     * @param skinCoins Lista de SkinCoins del nivel.
     * @param targetX   X del centro de la EndZone de la máquina.
     * @param targetY   Y del centro de la EndZone de la máquina.
     */
    public void updateAI(List<Coin> coins, List<SkinCoin> skinCoins,
                         int targetX, int targetY) {
        // Buscar la moneda más cercana no recogida
        int[] nearestCoin = findNearestCoin(coins, skinCoins);

        if (profile == MachineProfile.RANDOM) {
            moveRandom(nearestCoin, targetX, targetY);
        } else {
            moveExpert(nearestCoin, targetX, targetY);
        }
        move();
    }

    /**
     * Encuentra la moneda no recogida más cercana a la máquina.
     *
     * @return int[]{x, y} de la moneda más cercana, o null si no hay.
     */
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

    /**
     * Movimiento aleatorio con tendencia hacia el objetivo.
     * Cada 20 ticks elige aleatoriamente entre ir al objetivo o moverse random.
     */
    private void moveRandom(int[] nearestCoin, int targetX, int targetY) {
        randomTick++;
        if (randomTick >= 20) {
            randomTick = 0;
            // 60% de probabilidad de ir hacia el objetivo, 40% aleatorio
            if (random.nextInt(10) < 6) {
                int[] goal = nearestCoin != null ? nearestCoin : new int[]{targetX, targetY};
                randomDir = directionTo(goal[0], goal[1]);
            } else {
                randomDir = random.nextInt(4);
            }
        }
        setMovingUp(false); setMovingDown(false);
        setMovingLeft(false); setMovingRight(false);
        switch (randomDir) {
            case 0 -> setMovingUp(true);
            case 1 -> setMovingDown(true);
            case 2 -> setMovingLeft(true);
            case 3 -> setMovingRight(true);
        }
    }

    /**
     * Movimiento experto: va directamente a la moneda más cercana,
     * y cuando no hay monedas va a la meta.
     */
    private void moveExpert(int[] nearestCoin, int targetX, int targetY) {
        int goalX = nearestCoin != null ? nearestCoin[0] : targetX;
        int goalY = nearestCoin != null ? nearestCoin[1] : targetY;
        moveToward(goalX, goalY);
    }

    /**
     * Mueve la máquina hacia el punto (gx, gy) priorizando el eje con mayor distancia.
     */
    private void moveToward(int gx, int gy) {
        int diffX = gx - getX();
        int diffY = gy - getY();
        setMovingUp(false); setMovingDown(false);
        setMovingLeft(false); setMovingRight(false);

        if (Math.abs(diffX) >= Math.abs(diffY)) {
            if      (diffX < 0) setMovingLeft(true);
            else if (diffX > 0) setMovingRight(true);
            else if (diffY < 0) setMovingUp(true);
            else if (diffY > 0) setMovingDown(true);
        } else {
            if      (diffY < 0) setMovingUp(true);
            else if (diffY > 0) setMovingDown(true);
            else if (diffX < 0) setMovingLeft(true);
            else if (diffX > 0) setMovingRight(true);
        }
    }

    /** Retorna la dirección (0-3) hacia el punto dado. */
    private int directionTo(int gx, int gy) {
        int diffX = gx - getX();
        int diffY = gy - getY();
        if (Math.abs(diffX) >= Math.abs(diffY)) {
            return diffX < 0 ? 2 : 3;
        } else {
            return diffY < 0 ? 0 : 1;
        }
    }

    /** @return El perfil de comportamiento de la máquina. */
    public MachineProfile getProfile() { return profile; }
}
