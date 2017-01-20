package nl.bitbusters.dnd.connectivity;

import nl.bitbusters.dnd.Launcher;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

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
    
    private DataInputStream dataInStream;
    private DataOutputStream dataOutStream;
    private ObjectInputStream objectInStream;
    private ObjectOutputStream objectOutStream;
    
    private boolean running;

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
        dataInStream = new DataInputStream(clientSocket.getInputStream());
        dataOutStream = new DataOutputStream(clientSocket.getOutputStream());
        objectInStream = new ObjectInputStream(clientSocket.getInputStream());
        objectOutStream = new ObjectOutputStream(clientSocket.getOutputStream());
        
        if (Launcher.askVerifyConnection(dataInStream.readInt())) {
            dataOutStream.writeBoolean(true);
        } else {
            dataOutStream.writeBoolean(false);
            throw new IOException("Verification with user failed. Reset the server to try again.");
        }
        
        running = true;
    }
    
    @Override
    public void close() throws IOException {
        running = false;
        try {
            if (dataInStream != null) {
                dataInStream.close();
                dataInStream = null;
            }
            if (dataOutStream != null) {
                dataOutStream.close();
                dataOutStream = null;
            }
            if (objectInStream != null) {
                objectInStream.close();
                objectInStream = null;
            }
            if (objectOutStream != null) {
                objectOutStream.close();
                objectOutStream = null;
            }
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
    public boolean isRunning() {
        return running;
    }

}
