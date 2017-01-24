package nl.bitbusters.dnd.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import nl.bitbusters.dnd.model.Unit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for the dialog that allows one to add or edit a unit.
 * 
 * @author Bart
 *
 */
public final class EditUnitDialogController {
    
    @FXML private Button btnOK;
    @FXML private Button btnCancel;
    @FXML private TextField nameField;
    @FXML private TilePane iconTiles;
    
    private GameController gameController;
    private Stage dialogStage;
    private Unit unit;
    
    private boolean okClicked;
    private Image tempImage;
    private ImageView lastSelected;
    
    private List<Image> imageList = new ArrayList<>();

    /**
     * Initialise method used by JavaFX's FXML loader.
     */
    @FXML
    public void initialize() {
        File dir = new File("src/main/resources/sprites");
        File[] images = dir.listFiles();
        for (File file : images) {
            String name = file.getName();
            Image image = new Image("sprites/" + name);
            ImageView spriteView = createIconTile(image, name.substring(0, name.length() - 4));
            
            iconTiles.getChildren().add(spriteView);
            imageList.add(image);
        }
        
        btnOK.setOnAction(event -> {
            save();
            okClicked = true;
            dialogStage.close();
        });
        
        btnCancel.setOnAction(event -> {
            dialogStage.close();
        });
        
        btnCancel.requestFocus();
    }
    
    /**
     * Sets the {@link Stage} for this dialog window.
     * 
     * <p><b>This method <i>MUST</i> be called before the dialog is opened.</b>
     * @param stage the stage this dialog window is owned by.
     */
    public void setDialogStage(Stage stage) {
        dialogStage = stage;
    }
    
    /**
     * Sets the {@link GameController} that is the parent of this dialog window.
     * 
     * <p><b>This method <i>MUST</i> be called before the dialog is opened.</b>
     * @param controller the parent controller of this dialog.
     */
    public void setGameController(GameController controller) {
        gameController = controller;
    }
    
    /**
     * Sets the unit and populates the fields of this view.
     * 
     * <p><b>This method <i>MUST</i> be called before the dialog is opened.</b>
     * @param unit the unit to edit. <b>May not be <code>null</code></b>
     */
    public void setUnit(Unit unit) {
        this.unit = unit;
        lastSelected = null;
        
        if (unit.getIcon() != null) {
            lastSelected = createIconTile(unit.getIcon(), "Custom");
            lastSelected.setEffect(new DropShadow(15, Color.RED));
            iconTiles.getChildren().add(lastSelected);
        }
        
        nameField.setText(unit.getName());
    }
    
    /**
     * Saves the info contained in this window to the actual player.
     * Hooray for Java's easy variable access by reference instead of by value! :P
     */
    public void save() {
        unit.setName(nameField.getText());
        
        if (tempImage != null) {
            ImageView mapSprite = gameController.createMapSprite(tempImage);
            unit.setIcon(tempImage);
            gameController.changeMapSprite(unit, mapSprite);
        }
        
    }
    
    /**
     * Returns true iff the dialog was closed using the OK button.
     * @return true iff the dialog was closed using the OK button.
     */
    public boolean isOkClicked() {
        return okClicked;
    }
    
    /**
     * Creates an ImageView to be used as a tile in {@link #iconTiles}.
     * 
     * @param image Image to create ImageView from
     * @param name Name of the sprite (used to attach a tooltip)
     * @return an icon tile that can now be added to {@linkplain #iconTiles}.
     */
    private ImageView createIconTile(Image image, String name) {
        ImageView spriteView = new ImageView(image);
        spriteView.setOnMouseClicked(event -> {
            if (lastSelected != null) {
                lastSelected.setEffect(null);
            }
            lastSelected = spriteView;
            spriteView.setEffect(new DropShadow(15, Color.RED));
            tempImage = image;
        });
        Tooltip.install(spriteView, new Tooltip(name));
        return spriteView;
    }
    
}
