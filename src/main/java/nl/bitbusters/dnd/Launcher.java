package nl.bitbusters.dnd;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nl.bitbusters.dnd.gui.GameController;
import nl.bitbusters.dnd.gui.TVViewController;

import java.io.IOException;

/**
 * Main launcher for the DnD application.
 * 
 * @author Bart
 */
public class Launcher extends Application {
    
    private static final int PORT = 1337;
    
    private static Stage primaryStage;
    private static Stage launcherStage;
    private static BorderPane rootLayout;
    
    private static Mode mode = Mode.NONE;
    
    @FXML private Button btnDM;
    @FXML private Button btnTV;
    @FXML private Button btnExit;

    public static void main(String[] args) {
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
        
        showLauncher();
        
        if (mode == Mode.DM) {
            primaryStage.show();
            GameController.show();
        } else if (mode == Mode.TV) {
            primaryStage.show();
            TVViewController.show();
        }
    }
    
    /**
     * Opens the launcher dialog, allowing the user to choose between DM or TV mode.
     */
    public void showLauncher() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("view/launcher.fxml"));
            
            launcherStage = new Stage();
            launcherStage.setTitle("DnD App Launcher");
            launcherStage.initModality(Modality.WINDOW_MODAL);
            launcherStage.initOwner(primaryStage);
            launcherStage.setScene(new Scene(loader.load()));
            launcherStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void initialize() {
        btnExit.setOnAction(event -> {
            launcherStage.close();
            primaryStage.close();
        });
        
        btnDM.setOnAction(event -> {
            mode = Mode.DM;
            launcherStage.close();
        });
        
        btnTV.setOnAction(event -> {
            mode = Mode.TV;
            launcherStage.close();
        });
    }
    
    public static boolean askVerifyConnection(int num) {
        //insert alert dialog for a verifying an incoming connection from a
        //client with a certain 5 digit integer code.
        //I actually rather want to make this Launcher be an actual launcher,
        //so you can select the correct mode, connect to server / client etc.
        return true;
    }
    
    /**
     * Closes the application, including any server or client where present.
     */
    public static void close() {
        if (GameController.getServer() != null) {
            GameController.getServer().close();
        }
        if (TVViewController.getClient() != null) {
            TVViewController.getClient().close();
        }
        
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
