package nl.bitbusters.dnd.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

/**
 * Simple class representing a player.
 * 
 * @author Bart
 */
public class Player extends Unit {

    public Player(String name, Integer health, List<String> affliction, Image icon, ImageView sprite) {
        super(name, health, affliction, icon, sprite);
    }

    @Override
    public UnitType getType() {
        return UnitType.PLAYER;
    }

}
