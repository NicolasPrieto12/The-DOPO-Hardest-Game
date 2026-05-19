package domain;

/**
 * Define los perfiles de comportamiento disponibles para la máquina
 * en el modo Player vs Machine.
 */
public enum MachineProfile {

    /** La máquina se mueve aleatoriamente, cambiando de dirección cada cierto tiempo. */
    RANDOM,

    /** La máquina usa la estrategia más directa para llegar a su meta. */
    EXPERT
}
