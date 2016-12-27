package nl.bitbusters.dnd.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

/**
 * 
 * @author Yannick.
 *
 */
public class NPC extends Unit {

    public NPC(String name, Integer health, List<String> affliction, Image icon, ImageView sprite) {
        super(name, health, affliction, icon, sprite);
    }

    @Override
    public UnitType getType() {
        return UnitType.NPC;
    }

}
