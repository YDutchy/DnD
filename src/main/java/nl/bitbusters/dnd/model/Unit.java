package nl.bitbusters.dnd.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

/**
 * Abstract class to describe the base properties of a unit able to appear on the map.
 * 
 * @author Bart
 */
public abstract class Unit {
    
    private String name;
    private Integer health;
    private List<String> affliction;
    private Image icon;
    private ImageView sprite;

    /**
     * Constructor for a Unit. Needs a name, icon image and map sprite.
     * 
     * @param name Name of unit
     * @param icon Icon image of unit
     * @param sprite Map sprite of unit
     */
    public Unit(String name, Integer health, List<String> affliction, Image icon, ImageView sprite) {
        this.name = name;
        this.health = health;
        this.affliction = affliction;
        this.icon = icon;
        this.sprite = sprite;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getHealth() {
        return health;
    }
    
    public void setHealth(Integer health) {
        this.health = health;
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

    public List<String> getAffliction() {
        return affliction;
    }

    public void setAffliction(List<String> affliction) {
        this.affliction = affliction;
    }
}
