package nl.bitbusters.dnd.gui;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import nl.bitbusters.dnd.Launcher;
import nl.bitbusters.dnd.connectivity.Client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;


/**
 * Simple controller for the view that allows the user to let the client
 * connect to a DM server.
 * 
 * @author Bart
 */
public class ConnectClientController {

    @FXML private Button btnCancel;
    @FXML private Button btnConnect;
    @FXML private Label lblStatus;
    
    @FXML private TextField addressField;
    @FXML private TextField portField;
    @FXML private TextArea exceptionArea;
    
    private Stage dialogStage;
    private TVViewController tvController;
    
    private Task<Void> connectTask;
    
    @FXML @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void initialize() {
        btnConnect.setOnAction(event -> {
            btnConnect.setDisable(true);
            lblStatus.setText("Connecting");
            lblStatus.setVisible(true);
            
            if (addressField.getText().equals("")) {
                addressField.setText("localhost");
            }
            
            connectClient();
        });
        
        btnCancel.setOnAction(event -> {
            if (connectTask != null && connectTask.isRunning()) {
                connectTask.cancel();
            }
            dialogStage.close();
        });
        
        portField.setText("" + Launcher.getPort());
        portField.textProperty().addListener((ov, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) { //force numeric input
                portField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
    
    /**
     * Tries to create a client and connect it to the address and port specified
     * in {@link #addressField} and {@link #portField}. If this is successful,
     * then the client is set as the TVViewController's client and the dialog is
     * closed. Else, the IOException is caught off and shown as an error message.
     */
    @SuppressWarnings("resource") //'client' will be closed when necessary.
    private void connectClient() {
        Client client = new Client();
        final String address = addressField.getText();
        final int port = Integer.parseInt(portField.getText());
        
        connectTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                client.connect(address, port);
                return null;
            }
        };
        
        connectTask.setOnSucceeded(event -> {
            lblStatus.setText("Connected!");
            exceptionArea.setVisible(false);
            tvController.setClient(client);
            tvController.setConnected(true);
            btnConnect.setDisable(false);
            
            dialogStage.close();
        });
        
        connectTask.setOnFailed(event -> {
            lblStatus.setText("Could not connect:");
            
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            PrintStream printer = new PrintStream(stream);
            connectTask.getException().printStackTrace(printer);
            printer.flush();
            
            exceptionArea.setText(stream.toString());
            exceptionArea.setVisible(true);
            btnConnect.setDisable(false);
            
            client.close();
        });
        
        connectTask.setOnCancelled(event -> {
            client.close();
        });
        
        new Thread(connectTask).start();
    }
    
    public void setDialogStage(Stage stage) {
        dialogStage = stage;
        dialogStage.setOnCloseRequest(event -> btnCancel.fire());
    }
    
    public void setTVViewController(TVViewController cont) {
        tvController = cont;
    }
    
}
