package nl.bitbusters.dnd.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * 
 * @author Yannick.
 *
 */
public class NPC extends Unit {

    public NPC(String name, Integer health, Image icon, ImageView sprite) {
        super(name, health, icon, sprite);
    }

    @Override
    public UnitType getType() {
        return UnitType.NPC;
    }

}
