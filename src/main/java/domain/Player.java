package domain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa a un jugador controlado por el usuario.
 * Soporta múltiples tipos ({@link PlayerType}) con velocidad y tamaño variables.
 * Puede transformarse al recoger una {@link SkinCoin}.
 * Implementa {@link IMovable}, {@link ICollidable} e {@link IRenderable}.
 */
public class Player implements IMovable, ICollidable, IRenderable {

    /** Posición horizontal actual del jugador. */
    private int x;

    /** Posición vertical actual del jugador. */
    private int y;

    /** Tipo actual del jugador, determina color, velocidad y tamaño. */
    private PlayerType type;

    /** Tamaño actual del lado del cuadrado en píxeles. */
    private int size;

    /** Velocidad actual de desplazamiento en píxeles por tick. */
    private int speed;

    /** Indica si el jugador está vivo. */
    private boolean alive;

    /** Flags de dirección de movimiento activos según las teclas presionadas. */
    private boolean movingUp, movingDown, movingLeft, movingRight;

    /** Posición X de inicio, usada para reaparecer tras una muerte. */
    private int startX;

    /** Posición Y de inicio, usada para reaparecer tras una muerte. */
    private int startY;

    /** Lista de paredes del tablero para detectar colisiones antes de moverse. */
    private List<Rectangle> walls = new ArrayList<>();

    /** Posición X del último checkpoint alcanzado. -1 si no hay checkpoint. */
    private int checkpointX = -1;

    /** Posición Y del último checkpoint alcanzado. -1 si no hay checkpoint. */
    private int checkpointY = -1;

    /** Indica si el jugador tiene el escudo de Clyde activo (primer golpe absorbido). */
    private boolean shielded = false;

    /** Indica si el escudo activo proviene de una LifeSource (no baja velocidad al absorber). */
    private boolean lifeShield = false;

    /** Ticks de invencibilidad restantes tras absorber un golpe (evita doble hit en el mismo frame). */
    private int invincibleTicks = 0;

    /**
     * Crea un jugador del tipo indicado en la posición de inicio dada.
     *
     * @param startX Posición X inicial.
     * @param startY Posición Y inicial.
     * @param type   Tipo de jugador (RED o BLUE).
     */
    public Player(int startX, int startY, PlayerType type) {
        this.startX = startX;
        this.startY = startY;
        this.x      = startX;
        this.y      = startY;
        this.type   = type;
        this.size   = type.size;
        this.speed  = type.speed;
        this.alive  = true;
    }

    /**
     * Crea un jugador rojo (RED) en la posición de inicio dada.
     * Constructor simplificado usado principalmente en los tests.
     *
     * @param startX Posición X inicial.
     * @param startY Posición Y inicial.
     */
    public Player(int startX, int startY) {
        this(startX, startY, PlayerType.RED);
    }

    /**
     * Establece las paredes del tablero para que el jugador no pueda atravesarlas.
     *
     * @param walls Lista de rectángulos que representan las paredes.
     */
    public void setWalls(List<Rectangle> walls) {
        this.walls = walls;
    }

    /**
     * Mueve al jugador en las direcciones activas píxel a píxel.
     * Cada eje se mueve de forma independiente usando la posición actualizada.
     */
    @Override
    public void move() {
        if (movingUp) {
            for (int i = 0; i < speed; i++) {
                if (!collidesWithWall(x, y - 1)) y--; else break;
            }
        }
        if (movingDown) {
            for (int i = 0; i < speed; i++) {
                if (!collidesWithWall(x, y + 1)) y++; else break;
            }
        }
        if (movingLeft) {
            for (int i = 0; i < speed; i++) {
                if (!collidesWithWall(x - 1, y)) x--; else break;
            }
        }
        if (movingRight) {
            for (int i = 0; i < speed; i++) {
                if (!collidesWithWall(x + 1, y)) x++; else break;
            }
        }
    }

    /**
     * Verifica si el jugador colisionaría con alguna pared al moverse a la posición indicada.
     *
     * @param nx Nueva posición X a verificar.
     * @param ny Nueva posición Y a verificar.
     * @return true si hay colisión con alguna pared, false en caso contrario.
     */
    private boolean collidesWithWall(int nx, int ny) {
        Rectangle next = new Rectangle(nx, ny, size, size);
        return walls.stream().anyMatch(w -> w.intersects(next));
    }

    /**
     * Transforma al jugador al tipo indicado, actualizando velocidad y tamaño.
     * Se llama al recoger una {@link SkinCoin}.
     *
     * @param newType El nuevo tipo del jugador.
     */
    public void applyType(PlayerType newType) {
        this.type            = newType;
        this.size            = newType.size;
        this.speed           = newType.speed;
        this.shielded        = (newType == PlayerType.GREEN);
        this.lifeShield      = false;
        this.invincibleTicks = 0;
    }

    /**
     * Guarda la posición actual como checkpoint.
     *
     * @param cx Posición X del checkpoint.
     * @param cy Posición Y del checkpoint.
     */
    public void saveCheckpoint(int cx, int cy) {
        this.checkpointX = cx;
        this.checkpointY = cy;
    }

    /**
     * Reposiciona al jugador en el último checkpoint guardado.
     * Si no hay checkpoint, lo devuelve a la posición de inicio.
     */
    public void respawnAtCheckpoint() {
        if (checkpointX >= 0) {
            x = checkpointX;
            y = checkpointY;
        } else {
            x = startX;
            y = startY;
        }
        alive = true;
    }

    /** Activa o desactiva el movimiento hacia arriba. */
    public void setMovingUp(boolean v)    { movingUp = v; }

    /** Activa o desactiva el movimiento hacia abajo. */
    public void setMovingDown(boolean v)  { movingDown = v; }

    /** Activa o desactiva el movimiento hacia la izquierda. */
    public void setMovingLeft(boolean v)  { movingLeft = v; }

    /** Activa o desactiva el movimiento hacia la derecha. */
    public void setMovingRight(boolean v) { movingRight = v; }

    /** Marca al jugador como muerto. */
    protected void die() { alive = false; }

    /**
     * Devuelve al jugador a su posición de inicio original y lo marca como vivo.
     */
    public void respawn() {
        x = startX;
        y = startY;
        alive = true;
    }

    /**
     * Establece la posición del jugador directamente.
     *
     * @param x Nueva posición X.
     * @param y Nueva posición Y.
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Actualiza la posición de inicio del jugador.
     * Se usa al cambiar de nivel o al cargar una partida.
     *
     * @param x Nueva posición X de inicio.
     * @param y Nueva posición Y de inicio.
     */
    public void setStartPosition(int x, int y) {
        this.startX = x;
        this.startY = y;
    }

    /** Resetea el checkpoint guardado. */
    public void resetCheckpoint() {
        checkpointX = -1;
        checkpointY = -1;
    }

    /**
     * Intenta absorber un golpe con el escudo de Clyde.
     * Si el escudo está activo, lo consume, reduce la velocidad a 1 y activa invencibilidad.
     * Si está en periodo de invencibilidad, ignora el golpe.
     * Si no hay escudo ni invencibilidad, retorna false (debe morir normalmente).
     */
    public boolean absorbHit() {
        if (invincibleTicks > 0) return true;
        if (shielded) {
            shielded        = false;
            if (!lifeShield) speed = 1;  // Clyde baja velocidad, LifeSource no
            lifeShield      = false;
            invincibleTicks = 90;
            return true;
        }
        return false;
    }

    /** Descuenta un tick de invencibilidad. Llamar una vez por tick en el game loop. */
    public void tickInvincibility() {
        if (invincibleTicks > 0) invincibleTicks--;
    }

    /**
     * Activa el escudo de vida (otorgado por una LifeSource).
     * No cambia el tipo ni la velocidad, solo activa el escudo.
     */
    public void activateLifeShield() {
        shielded   = true;
        lifeShield = true;
    }

    /** Otorga invencibilidad temporal al respawnear (evita muerte inmediata al reaparecer). */
    public void grantRespawnInvincibility() {
        invincibleTicks = 60;
    }

    /** @return true si el escudo está activo. */
    public boolean isShielded() { return shielded; }

    /** @return true si el escudo activo es de LifeSource. */
    public boolean isLifeShield() { return lifeShield; }

    /** {@inheritDoc} */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }

    /** {@inheritDoc} */
    @Override
    public boolean collidesWith(ICollidable other) {
        return getBounds().intersects(other.getBounds());
    }

    /**
     * Dibuja al jugador como un cuadrado del color de su tipo con borde gris oscuro.
     *
     * @param g El objeto Graphics usado para dibujar.
     */
    @Override
    public void render(Graphics g) {
        g.setColor(type.color);
        g.fillRect(x, y, size, size);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(x, y, size, size);
    }

    /** @return true si el jugador está vivo. */
    public boolean isAlive()        { return alive; }

    /** @return La posición X actual. */
    public int getX()               { return x; }

    /** @return La posición Y actual. */
    public int getY()               { return y; }

    /** @return El tamaño del lado del jugador en píxeles. */
    public int getSize()            { return size; }

    /** @return La velocidad actual del jugador. */
    public int getSpeed()           { return speed; }

    /** @return El tipo actual del jugador. */
    public PlayerType getType()     { return type; }

    /** @return La posición X del checkpoint, o -1 si no hay. */
    public int getCheckpointX()     { return checkpointX; }

    /** @return La posición Y del checkpoint, o -1 si no hay. */
    public int getCheckpointY()     { return checkpointY; }
}
