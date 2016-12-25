package nl.bitbusters.dnd.model;

/**
 * Simple enumeration to differentiate between the subclasses of Unit without
 * an ugly <code>instanceof</code>.
 * 
 * @author Bart
 */
public enum UnitType {

    PLAYER,
    MONSTER,
    NPC;
}
