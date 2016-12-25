package nl.bitbusters.dnd.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Abstract class to describe the base properties of a unit able to appear on the map.
 * 
 * @author Bart
 */
public abstract class Unit {
    
    private String name;
    private Image icon;
    private ImageView sprite;

    /**
     * Constructor for a Unit. Needs a name, icon image and map sprite.
     * 
     * @param name Name of unit
     * @param icon Icon image of unit
     * @param sprite Map sprite of unit
     */
    public Unit(String name, Image icon, ImageView sprite) {
        this.name = name;
        this.icon = icon;
        this.sprite = sprite;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Image getIcon() {
        return icon;
    }
    
    public void setIcon(Image icon) {
        this.icon = icon;
    }
    
    public ImageView getMapSprite() {
        return sprite;
    }
    
    public void setMapSprite(ImageView sprite) {
        this.sprite = sprite;
    }
    
    public abstract UnitType getType();

}
