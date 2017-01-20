package nl.bitbusters.dnd.connectivity;

import nl.bitbusters.dnd.Launcher;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

/**
 * Client class that will receive map and unit info etc from the Server.
 * This will be running on the TV view.
 * 
 * @author Bart
 */
public class Client implements AutoCloseable {
    
    /** default = 30000 ms. */
    private static int readTimeout = 30000;
    
    private boolean connected;
    
    private Socket socket;
    private ObjectInputStream objectInStream;
    private ObjectOutputStream objectOutStream;

    public Client() {
        connected = false;
    }
    
    /**
     * Sets the read timeout in milliseconds on this server's underlying socket.
     * @param milliseconds time in milliseconds until the server should time out.
     */
    public void setReadTimeout(int milliseconds) {
        readTimeout = milliseconds;
        if (socket != null) {
            try {
                socket.setSoTimeout(readTimeout);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Connects the client to a server running on the specified address and a port. 
     * 
     * <p>The client and server have a simple three-way handshake:
     * First, the server needs to be running and waiting for a connection. The client
     * may then connect using this method and it will send a 5-digit positive integer.
     * The server then asks the {@link Launcher#askVerifyConnection(int)} method to
     * verify the number with the user. The server then sends back whether this was
     * correct or not. Only if this was correct and within {@value #readTimeout} ms,
     * then the connection is accepted. Otherwise, an IOException is thrown.
     * 
     * @param address address of the server.
     * @param port port on which the server is listening.
     * @throws IOException in case something goes wrong in connecting to the
     *      specified address and port, or in case verification on the server's side is
     *      not correctly executed or times out.
     */
    public void connect(String address, int port) throws IOException {
        socket = new Socket(address, port);
        socket.setSoTimeout(readTimeout);
        
        objectInStream = new ObjectInputStream(socket.getInputStream());
        objectOutStream = new ObjectOutputStream(socket.getOutputStream());
        int num = new Random().nextInt(99999);
        objectOutStream.writeInt(num);
        
        if (objectInStream.readBoolean()) {
            connected = true;
        } else {
            throw new IOException("Verification on server side failed.");
        }
    }
    
    @Override
    public void close() {
        connected = false;
        try {
            if (objectInStream != null) {
                objectInStream.close();
                objectInStream = null;
            }
            if (objectOutStream != null) {
                objectOutStream.close();
                objectOutStream = null;
            }

            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Resets the client to before the {@link #connect(String, int)} method was called.
     * Implementation-wise, this just comes down to calling {@link #close()}.
     */
    public void reset() {
        close();
    }
    
    public boolean isConnected() {
        return connected;
    }

}
