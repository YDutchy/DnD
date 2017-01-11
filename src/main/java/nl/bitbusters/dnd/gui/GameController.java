package nl.bitbusters.dnd.gui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nl.bitbusters.dnd.Launcher;
import nl.bitbusters.dnd.model.Player;
import nl.bitbusters.dnd.model.Unit;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Controller showing the main game view of the application. This includes the board
 * 
 * @author Bart
 */
public class GameController {

    /**
     * Centre pane.
     */
    @FXML private AnchorPane board;
    @FXML private Button btnLoadMap;

    /**
     * Left pane.
     */
    @FXML private Button btnAddPlayer;
    @FXML private Button btnChangeFire;
    @FXML private Button btnChangeCold;
    @FXML private Button btnChangePoison;
    @FXML private Button btnChangeProne;
    @FXML private Button btnChangeStun;
    @FXML private Button btnChangeNecro;
    @FXML private TableView<Unit> playerTable;
    @FXML private TableColumn<Unit, String> playerTableName;
    @FXML private TableColumn<Unit, VBox> playerTableAffliction;
    @FXML private TableColumn<Unit, ImageView> playerTableIcon;

    @FXML @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void initialize() {
        initPlayerTable();
        initMap();
        initKeyBoardControl();
    }

    /**
     * Initialises the key bindings that allows the user to control the currently selected player
     * with the arrow keys.
     */
    private void initKeyBoardControl() {
        Launcher.getRootLayout().setOnKeyPressed(event -> {
            Unit selectedUnit = playerTable.getSelectionModel().getSelectedItem();
            if (selectedUnit != null && selectedUnit.getMapSprite() != null) {
                ImageView selected = selectedUnit.getMapSprite();
                switch (event.getCode()) {
                    case LEFT:
                        moveOnMap(selected, -8, 0);
                        break;
                    case RIGHT:
                        moveOnMap(selected, 8, 0);
                        break;
                    case UP:
                        moveOnMap(selected, 0, -8);
                        break;
                    case DOWN:
                        moveOnMap(selected, 0, 8);
                        break;
                    default:
                        break;
                }
                selected.setFocusTraversable(true);
            }
            event.consume();
        });
    }

    /**
     * Initialises the button with which a map image can be loaded.
     */
    private void initMap() {
        btnLoadMap.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            final ExtensionFilter extFilterJPG = new ExtensionFilter("JPG files (*.jpg)", "*.JPG");
            final ExtensionFilter extFilterPNG = new ExtensionFilter("PNG files (*.png)", "*.PNG");
            fileChooser.getExtensionFilters().addAll(extFilterPNG, extFilterJPG);

            try {
                File file = fileChooser.showOpenDialog(Launcher.getStage());
                if (file != null) {
                    Image image = SwingFXUtils.toFXImage(ImageIO.read(file), null);
                    ImageView imageView = new ImageView(image);

                    board.getChildren().add(imageView);
                    imageView.setPreserveRatio(true);
                    imageView.fitWidthProperty().bind(board.widthProperty());
                    imageView.fitHeightProperty().bind(board.heightProperty());
                    imageView.toBack();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Initialises the table that contains the names and icons of all units in the game,
     * and the Add Player button found below it.
     */
    private void initPlayerTable() {
        playerTable.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            if (oldValue != null && oldValue.getMapSprite() != null) {
                oldValue.getMapSprite().setEffect(null);
            }
            if (newValue != null && newValue.getMapSprite() != null) {
                newValue.getMapSprite().setEffect(new DropShadow(15, Color.RED));
            }
        });
        
        playerTableName.setCellFactory(TextFieldTableCell.forTableColumn());
        playerTableName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        
        playerTableName.setOnEditCommit(event -> {
            event.getRowValue().setName(event.getNewValue());
            playerTable.setFocusTraversable(false);
        });
        
        playerTableAffliction.setCellValueFactory(cellData -> {
            VBox box = new VBox();
            for (String effect : cellData.getValue().getAffliction()) {
                box.getChildren().add(new Label(effect));
            }
            return new SimpleObjectProperty<VBox>(box);
        });

        playerTableIcon.setCellValueFactory(cellData -> {
            final ImageView icon = new ImageView(cellData.getValue().getIcon());
            icon.setFitHeight(20);
            icon.setFitWidth(20);
            return new SimpleObjectProperty<ImageView>(icon);
        });

        btnAddPlayer.setOnAction(event -> {
            Player player = new Player();
            showEditUnitDialog(player);
            playerTable.getItems().add(player);
        });

        btnChangeCold.setOnAction(event -> setEffectOnSelected("Frozen"));
        btnChangeFire.setOnAction(event -> setEffectOnSelected("Burned"));
        btnChangeNecro.setOnAction(event -> setEffectOnSelected("Necro"));
        btnChangePoison.setOnAction(event -> setEffectOnSelected("Poisoned"));
        btnChangeProne.setOnAction(event -> setEffectOnSelected("Prone"));
        btnChangeStun.setOnAction(event -> setEffectOnSelected("Stunned"));
        
        playerTable.setFocusTraversable(false);
    }
    
    /**
     * Creates a map sprite with the given image and sets the proper
     * size and event handlers. It does not add this to the map though.
     * 
     * @param sprite image to be shown as sprite
     * @return an ImageView ready to add to the map.
     */
    public ImageView createMapSprite(Image sprite) {
        ImageView mapSprite = new ImageView(sprite);
        mapSprite.setFitHeight(30);
        mapSprite.setFitWidth(30);
        
        mapSprite.setOnMouseDragged(mouseEvent -> { //dragging
            mapSprite.setX(mouseEvent.getX());
            mapSprite.setY(mouseEvent.getY());
        });
        
        mapSprite.setOnMouseReleased(mouseEvent -> { //dropping
            if (mouseEvent.getX() < 0) {
                mapSprite.setX(0);
            } else if (mouseEvent.getX() > board.getWidth() - mapSprite.getFitWidth()) {
                mapSprite.setX(board.getWidth() - mapSprite.getFitWidth());
            }
            
            if (mouseEvent.getY() < 0) {
                mapSprite.setY(0);
            } else if (mouseEvent.getY() > board.getHeight() - mapSprite.getFitHeight()) {
                mapSprite.setY(board.getHeight() - mapSprite.getFitHeight());
            }
        });
        
        return mapSprite;
    }
    
    /**
     * Changes the current map sprite of the given unit to the given new sprite.
     * If the unit does not have a map sprite (i.e. it is <code>null</code>), then
     * the new sprite is simply set ('added') as its map sprite.
     * 
     * @param unit the unit to have its map sprite changed
     * @param newSprite the new map sprite of the given unit.
     */
    public void changeMapSprite(Unit unit, ImageView newSprite) {
        if (unit.getMapSprite() != null) {
            board.getChildren().remove(unit.getMapSprite());
        }
        board.getChildren().add(newSprite);
        
        unit.setMapSprite(newSprite);
        playerTable.refresh();
    }
    
    /**
     * Toggles an effect on the selected Unit in the player table.
     * @param effect the effect to be toggled.
     */
    private void setEffectOnSelected(String effect) {
        if (playerTable.getSelectionModel().getSelectedItem() != null) {
            if (playerTable.getSelectionModel().getSelectedItem().getAffliction().contains(effect)) {
                playerTable.getSelectionModel().getSelectedItem().getAffliction().remove(effect);
            } else {
                playerTable.getSelectionModel().getSelectedItem().getAffliction().add(effect);
            }
            playerTable.refresh();
        }
    }

    /**
     * Moves the ImageView sprite across the map (a.k.a. {@link #board}), adhering
     * to its boundaries.
     * @param sprite Sprite to move
     * @param offsetX offset in X direction
     * @param offsetY offset in Y direction
     */
    private void moveOnMap(ImageView sprite, int offsetX, int offsetY) {
        assert board.equals(sprite.getParent());

        if (sprite.getX() + sprite.getFitWidth() + offsetX > board.getWidth()) {
            sprite.setX(board.getWidth() - sprite.getFitWidth());
        } else if (sprite.getX() + offsetX < 0) {
            sprite.setX(0);
        } else {
            sprite.setX(sprite.getX() + offsetX);
        }

        if (sprite.getY() + sprite.getFitHeight() + offsetY > board.getHeight()) {
            sprite.setY(board.getHeight() - sprite.getFitHeight());
        } else if (sprite.getY() + offsetY < 0) {
            sprite.setY(0);
        } else {
            sprite.setY(sprite.getY() + offsetY);
        }
    }
    
    /**
     * Shows the menu to add or edit a unit.
     * @param unit the unit to edit. Since {@link Unit} cannot simply be instantiated, this <b>cannot</b>
     *      be <code>null</code>
     */
    public void showEditUnitDialog(Unit unit) {
        assert unit != null;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Launcher.class.getResource("view/editUnitDialogView.fxml"));
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add / edit unit");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(Launcher.getStage());
            dialogStage.setScene(new Scene(loader.load()));
            
            EditUnitDialogController controller = loader.getController();
            controller.setGameController(this);
            controller.setDialogStage(dialogStage);
            controller.setUnit(unit);
            
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the application settings view in the middle of the root layout.
     */
    public static void show() {
        try {
            BorderPane gameView = FXMLLoader.load(Launcher.class.getResource("view/gameView.fxml"));
            Launcher.getRootLayout().setCenter(gameView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
