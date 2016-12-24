package nl.bitbusters.dnd;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import nl.bitbusters.dnd.gui.GameController;

/**
 * Main launcher for the DnD application.
 * 
 * @author Bart
 */
public class Launcher extends Application {
    
    private static Stage primaryStage;
    private static BorderPane rootLayout;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("DnD Application");
        primaryStage.minWidthProperty().set(1337);
        primaryStage.minHeightProperty().set(800);
        
        rootLayout = FXMLLoader.load(getClass().getResource("view/main.fxml"));
        primaryStage.setScene(new Scene(rootLayout, 1337, 800));
        primaryStage.setOnCloseRequest(e -> close());
        primaryStage.sizeToScene();
        primaryStage.show();
        
        GameController.show();
    }
    
    public void close() {
        Platform.exit();
        System.exit(0);
    }

    public static BorderPane getRootLayout() {
        return rootLayout;
    }

}
