package nl.bitbusters.dnd.gui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
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
    @FXML private TableView<Unit> playerTable;
    @FXML private TableColumn<Unit, String> playerTableName;
    @FXML private TableColumn<Unit, String> playerTableAffliction;
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
            if (playerTable.getSelectionModel().getSelectedItem() != null) {
                ImageView selected = playerTable.getSelectionModel().getSelectedItem().getMapSprite();
                switch (event.getCode()) {
                    case LEFT:
                        selected.setX(selected.getX() - 8);
                        break;
                    case RIGHT:
                        selected.setX(selected.getX() + 8);
                        break;
                    case UP:
                        selected.setY(selected.getY() - 8);
                        break;
                    case DOWN:
                        selected.setY(selected.getY() + 8);
                        break;
                    default:
                        break;
                }
                selected.setFocusTraversable(true);
                selected.requestFocus();
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
        playerTableName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        
        playerTableAffliction.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAffliction()));
        
        playerTableIcon.setCellValueFactory(cellData -> {
            final ImageView icon = new ImageView(cellData.getValue().getIcon());
            icon.setFitHeight(20);
            icon.setFitWidth(20);
            return new SimpleObjectProperty<ImageView>(icon);
        });
        
        btnAddPlayer.setOnAction(event -> {
            Image sprite = new Image("http://orig07.deviantart.net/f9d0/f/2009/219/8/0/contra_sprite_player_1_by_ink_geckos.jpg");
            ImageView mapSprite = new ImageView(sprite);
            mapSprite.setFitHeight(30);
            mapSprite.setFitWidth(30);
            
            playerTable.getItems().add(new Player("Blaze", 420, "none", sprite, mapSprite));
            board.getChildren().add(mapSprite);
        });
        
        playerTable.setFocusTraversable(false);
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
