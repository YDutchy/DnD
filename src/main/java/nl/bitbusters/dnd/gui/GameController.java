package nl.bitbusters.dnd.gui;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import nl.bitbusters.dnd.Launcher;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Controller showing the main game view of the application. This includes the board
 * 
 * @author Bart
 */
public class GameController {
    
    @FXML private BorderPane board;
    @FXML private Button btnLoadMap;
    
    @FXML @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void initialize() {
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
                    
                    board.setCenter(imageView);
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
