package nl.bitbusters.dnd.model;

import java.io.Serializable;

/**
 * Simple enumeration to differentiate between the subclasses of Unit without
 * an ugly <code>instanceof</code>.
 * 
 * @author Bart
 */
public enum UnitType implements Serializable {

    PLAYER,
    MONSTER,
    NPC;
    
}
