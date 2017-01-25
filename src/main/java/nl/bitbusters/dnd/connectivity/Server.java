package nl.bitbusters.dnd.connectivity;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import nl.bitbusters.dnd.Launcher;
import nl.bitbusters.dnd.model.Unit;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.imageio.ImageIO;

/**
 * Server class that will send map and unit info etc to the client.
 * This will be running on the DM view.
 * 
 * @author Bart
 */
public class Server implements AutoCloseable {

    /** default = 30000 ms. */
    private static int readTimeout = 30000;
    
    private ServerSocket serverSocket;
    private Socket clientSocket;
    
    private ObjectInputStream objectInStream;
    private ObjectOutputStream objectOutStream;
    
    private boolean connected;

    /**
     * Initialises a server on the given port.
     * @param port port
     * @throws IOException if an I/O error occurs opening the server socket.
     */
    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }
    
    /**
     * Sets the read timeout in milliseconds on this server's underlying socket.
     * @param milliseconds time in milliseconds until the server should time out.
     */
    public void setReadTimeout(int milliseconds) {
        readTimeout = milliseconds;
        if (clientSocket != null) {
            try {
                clientSocket.setSoTimeout(readTimeout);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Waits for a connection from a client, and accepts the incoming connection.
     * 
     * <p>The client and server have a simple three-way handshake:
     * First, the server needs to be running and waiting for a connection. The client
     * may then connect using this method and it will send a 5-digit positive integer.
     * The server then asks the {@link Launcher#askVerifyConnection(int)} method to
     * verify the number with the user. The server then sends back whether this was
     * correct or not. Only if this was correct and within {@link #readTimeout} ms,
     * then the connection is accepted. Otherwise, an IOException is thrown.
     * 
     * @throws IOException if an I/O error occurs waiting for a connection.
     */
    public void start() throws IOException {
        clientSocket = serverSocket.accept();
        clientSocket.setSoTimeout(readTimeout);
        objectOutStream = new ObjectOutputStream(clientSocket.getOutputStream());
        objectInStream = new ObjectInputStream(clientSocket.getInputStream());
        
        int veriNum = objectInStream.readInt();
        if (Launcher.askVerifyConnection(veriNum)) {
            objectOutStream.writeBoolean(true);
            objectOutStream.flush();
        } else {
            objectOutStream.writeBoolean(false);
            objectOutStream.flush();
            throw new IOException("Verification with user failed. Reset the server to try again.");
        }
        
        connected = true;
        System.out.println("Server: Connected to client!");
    }
    
    @Override
    public void close() {
        connected = false;
        try {
            objectInStream = null;
            objectOutStream = null;
            if (clientSocket != null) {
                clientSocket.close();
                clientSocket = null;
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Resets the server to before the {@link #start()} method was called.
     */
    public void reset() throws IOException {
        close();
        serverSocket = new ServerSocket(serverSocket.getLocalPort());
    }
    
    /**
     * Returns true iff the server is running.
     * @return true iff the server is running.
     */
    public boolean isConnected() {
        return connected;
    }
    
    /**
     * Sends a map image to the connected client.
     * Has no effect if the server is not connected.
     * @param map the map image to send.
     * @throws IOException if something goes wrong in sending the map.
     */
    public void sendMap(Image map) throws IOException {
        if (connected) {
            objectOutStream.writeUTF("map");
            ImageIO.write(SwingFXUtils.fromFXImage(map, null), "png", objectOutStream);
            objectOutStream.flush();
            
            if (!objectInStream.readBoolean()) {
                throw new IOException("Map image not correctly sent!");
            }
        }
    }
    
    /**
     * Sends a Unit to the connected client.
     * Has no effect if the server is not connected.
     * @param unit the unit to send.
     * @throws IOException if something goes wrong in sending the unit.
     */
    public void sendUnit(Unit unit) throws IOException {
        if (connected) {
            objectOutStream.writeUTF("unit");
            objectOutStream.writeObject(unit);
            objectOutStream.flush();
            
            if (!objectInStream.readBoolean()) {
                throw new IOException("Unit not correctly sent!");
            }
        }
    }

}
