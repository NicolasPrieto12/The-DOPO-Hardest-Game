package domain;

/**
 * Define los perfiles de comportamiento disponibles para la máquina
 * en el modo Player vs Machine.
 *
 * <p>Perfiles disponibles:</p>
 * <ul>
 *   <li>{@link #RANDOM}: cambia de dirección cada 25 ticks, 65% probabilidad hacia el objetivo</li>
 *   <li>{@link #EXPERT}: siempre elige la dirección libre más cercana al objetivo</li>
 * </ul>
 *
 * <p>Ambos perfiles detectan atasco (8 ticks sin moverse) y recalculan dirección.</p>
 */
public enum MachineProfile {

    /** La máquina se mueve aleatoriamente, cambiando de dirección cada cierto tiempo. */
    RANDOM,

    /** La máquina usa la estrategia más directa para llegar a su meta. */
    EXPERT
}
