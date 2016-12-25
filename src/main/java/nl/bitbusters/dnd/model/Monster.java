package nl.bitbusters.dnd.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Simple class to represent a monster.
 * 
 * @author Bart
 */
public class Monster extends Unit {

    public Monster(String name, Integer health, Image icon, ImageView sprite) {
        super(name, health, icon, sprite);
    }

    @Override
    public UnitType getType() {
        return UnitType.MONSTER;
    }

}
