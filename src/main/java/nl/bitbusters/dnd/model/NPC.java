package nl.bitbusters.dnd.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * 
 * @author Yannick.
 *
 */
public class NPC extends Unit {

    public NPC(String name, Integer health, String affliction, Image icon, ImageView sprite) {
        super(name, health, affliction, icon, sprite);
    }

    @Override
    public UnitType getType() {
        return UnitType.NPC;
    }

}
