package nl.bitbusters.dnd.model;

import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Simple class representing a player.
 * 
 * @author Bart
 */
public class Player extends Unit {

    public Player(String name, Integer health, ArrayList<String> affliction, Image icon, ImageView sprite) {
        super(name, health, affliction, icon, sprite);
    }

    @Override
    public UnitType getType() {
        return UnitType.PLAYER;
    }

}
