package nl.bitbusters.dnd.model;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * Abstract class to describe the base properties of a unit able to appear on the map.
 * 
 * @author Bart
 */
public abstract class Unit implements Serializable {
    
    private String name;
    private Integer health;
    private List<String> affliction;
    private transient Image icon;
    private transient ImageView sprite;
    
    /**
     * Simple constructor for a Unit. This initialises its variables as follows: 
     * <ul>
     *   <li>{@link #name}: empty string
     *   <li>{@link #health}: 0
     *   <li>{@link #affliction}: an empty list
     *   <li>{@link #icon}: <code>null</code>
     *   <li>{@link #sprite}: <code>null</code>
     * </ul>
     */
    public Unit() {
        this.name = "";
        this.health = 0;
        this.affliction = new ArrayList<>();
    }

    /**
     * Constructor for a Unit. Needs a name, icon image and map sprite.
     * 
     * @param name Name of unit
     * @param health Initial health of the unit
     * @param affliction Initial status affects of the unit
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
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        ImageIO.write(SwingFXUtils.fromFXImage(icon, null), "png", out);
    }
    
    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        icon = SwingFXUtils.toFXImage(ImageIO.read(in), null);
    }
}
