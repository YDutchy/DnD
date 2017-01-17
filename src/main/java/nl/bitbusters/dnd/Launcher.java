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
    
    private static final int PORT = 1337;
    
    private static Stage primaryStage;
    private static BorderPane rootLayout;
    
    private static Mode mode;

    public static void main(String[] args) {
        mode = Mode.DM; //<-- set by analysing args or something idk.
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("DnD Application");
        primaryStage.minWidthProperty().set(1337);
        primaryStage.minHeightProperty().set(900);
        
        rootLayout = FXMLLoader.load(getClass().getResource("view/main.fxml"));
        primaryStage.setScene(new Scene(rootLayout, 1337, 900));
        primaryStage.setOnCloseRequest(e -> close());
        primaryStage.sizeToScene();
        primaryStage.show();
        
        GameController.show();
    }
    
    public static boolean askVerifyConnection(int num) {
        //insert alert dialog for a verifying an incoming connection from a
        //client with a certain 5 digit integer code.
        //I actually rather want to make this Launcher be an actual launcher,
        //so you can select the correct mode, connect to server / client etc.
        return true;
    }
    
    public static void close() {
        Platform.exit();
        System.exit(0);
    }
    
    public static Mode getMode() {
        return mode;
    }
    
    public static int getPort() {
        return PORT;
    }

    public static BorderPane getRootLayout() {
        return rootLayout;
    }
    
    public static Stage getStage() {
        return primaryStage;
    }

}
