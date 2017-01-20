package nl.bitbusters.dnd.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.Serializable;
import java.util.List;

/**
 * Simple class to represent a monster.
 * 
 * @author Bart
 */
public class Monster extends Unit implements Serializable {

    public Monster(String name, Integer health, List<String> affliction, Image icon, ImageView sprite) {
        super(name, health, affliction, icon, sprite);
    }

    @Override
    public UnitType getType() {
        return UnitType.MONSTER;
    }

}
