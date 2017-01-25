package nl.bitbusters.dnd.gui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nl.bitbusters.dnd.Launcher;
import nl.bitbusters.dnd.connectivity.Client;
import nl.bitbusters.dnd.model.Unit;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * Controller for the TV view of the application. Basically this is just
 * a worked down version of the DM view with a client attached to it...
 * 
 * @author Bart
 */
public class TVViewController implements Observer {
    
    private static Client client;
    
    /**
     * Top pane.
     */
    @FXML private Button btnClientConnect;
    @FXML private Label lblClientStatus;
    
    /**
     * Centre pane.
     */
    @FXML private AnchorPane board;
    @FXML private Rectangle scaleLine; //scaleLine's width is 30 ft on the map
    @FXML private Rectangle scaleBound;
    
    /**
     * Left pane.
     */
    @FXML private TableView<Unit> playerTable;
    @FXML private TableColumn<Unit, String> playerTableName;
    @FXML private TableColumn<Unit, VBox> playerTableAffliction;
    @FXML private TableColumn<Unit, ImageView> playerTableIcon;

    @FXML @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void initialize() {
        initPlayerTable();
        initClientSection();
    }
    
    private void initClientSection() {
        btnClientConnect.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(Launcher.class.getResource("view/connectClientView.fxml"));
                
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Connect to DM");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(Launcher.getStage());
                dialogStage.setScene(new Scene(loader.load()));
                
                ConnectClientController controller = loader.getController();
                controller.setTVViewController(this);
                controller.setDialogStage(dialogStage);
                
                dialogStage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    
    private void initPlayerTable() {
        playerTableName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        
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
        
        playerTable.setFocusTraversable(false);
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("Cool, I got an update: " + arg.toString());
    }
    
    /**
     * Deletes this class as observer from the current client, sets
     * the given client as the client and registers this class as its
     * observer.
     * 
     * @param c the client to be set.
     */
    public void setClient(Client c) {
        if (client != null) {
            client.deleteObserver(this);
        }
        client = c;
        if (client != null) {
            client.addObserver(this);
        }
    }
    
    public void setConnected(boolean status) {
        lblClientStatus.setText(status ? "Connected!" : "Disconnected!");
    }
    
    public static Client getClient() {
        return client;
    }
    
    /**
     * Shows the TV view in the middle of the root layout.
     */
    public static void show() {
        try {
            BorderPane tvView = FXMLLoader.load(Launcher.class.getResource("view/tvView.fxml"));
            Launcher.getRootLayout().setCenter(tvView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
