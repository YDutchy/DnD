package nl.bitbusters.dnd.gui;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import nl.bitbusters.dnd.Launcher;
import nl.bitbusters.dnd.model.Unit;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

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
    @FXML private ImageView spriteView;
    
    private GameController gameController;
    private Stage dialogStage;
    private Unit unit;
    
    private boolean okClicked;
    private Image tempImage;

    /**
     * Initialise method used by JavaFX's FXML loader.
     */
    @FXML
    public void initialize() {
        btnOK.setOnAction(event -> {
            save();
            okClicked = true;
            dialogStage.close();
        });
        
        btnCancel.setOnAction(event -> {
            dialogStage.close();
        });
        
        spriteView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() >= 2) {
                FileChooser fileChooser = new FileChooser();
                final ExtensionFilter extFilterJPG = new ExtensionFilter("JPG files (*.jpg)", "*.JPG");
                final ExtensionFilter extFilterPNG = new ExtensionFilter("PNG files (*.png)", "*.PNG");
                fileChooser.getExtensionFilters().addAll(extFilterPNG, extFilterJPG);

                try {
                    File file = fileChooser.showOpenDialog(Launcher.getStage());
                    if (file != null) {
                        tempImage = SwingFXUtils.toFXImage(ImageIO.read(file), null);
                        spriteView.setImage(tempImage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
        if (unit.getIcon() == null) {
            tempImage = new Image("sprites/default.jpg");
            unit.setIcon(tempImage);
        }
        
        nameField.setText(unit.getName());
        spriteView.setImage(unit.getIcon());
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

}
