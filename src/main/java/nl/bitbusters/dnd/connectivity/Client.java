package nl.bitbusters.dnd.connectivity;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import nl.bitbusters.dnd.Launcher;
import nl.bitbusters.dnd.model.Unit;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Observable;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * Client class that will receive map and unit info etc from the Server.
 * This will be running on the TV view.
 * 
 * @author Bart
 */
public class Client extends Observable implements AutoCloseable {
    
    private static final int LOOP_INTERVAL = 500;
    
    /** default = 30000 ms. */
    private static int readTimeout = 30000;
    
    private boolean connected;
    
    private Socket socket;
    private ObjectInputStream objectInStream;
    private ObjectOutputStream objectOutStream;
    
    private Thread runLoopThread;

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
        
        objectOutStream = new ObjectOutputStream(socket.getOutputStream());
        objectInStream = new ObjectInputStream(socket.getInputStream());
        int num = new Random().nextInt(99999);
        objectOutStream.writeInt(num);
        objectOutStream.flush();
        
        if (objectInStream.readBoolean()) {
            connected = true;
            runLoopThread = new Thread(() -> {
                try {
                    runLoop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, "Client");
            runLoopThread.start();
        } else {
            throw new IOException("Verification on server side failed.");
        }
        
        System.out.println("Client: Connected to server at " + address + ":" + port);
    }
    
    @Override
    public void close() {
        connected = false;
        try {
            objectInStream = null;
            objectOutStream = null;
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
    
    protected ObjectInputStream getObjectInputStream() {
        return objectInStream;
    }
    
    protected Thread getRunLoopThread() {
        return runLoopThread;
    }
    
    protected void runLoop() throws IOException {
        while (connected) {
            if (connected && objectInStream.available() > 0) {
                onContact(objectInStream.readUTF());
            } else {
                try {
                    Thread.sleep(LOOP_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void onContact(String message) throws IOException {
        try {
            switch (message) {
                case "map":
                    onReceiveMap();
                    break;
                case "unit":
                    onReceiveUnit();
                    break;
                default:
                    throw new IOException("Unknown message received: " + message);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private void onReceiveMap() throws ClassNotFoundException, IOException {
        Image image = SwingFXUtils.toFXImage(ImageIO.read(objectInStream), null);
        
        System.out.println("--Excess data in stream after receiving image:--");
        while (objectInStream.available() > 0) {
            System.out.print(objectInStream.read() + " ");
        }
        System.out.println("\n--End excess--");
        
        objectOutStream.writeBoolean(true);
        objectOutStream.flush();
        
        setChanged();
        notifyObservers(image);
    }
    
    private void onReceiveUnit() throws ClassNotFoundException, IOException {
        Unit unit = (Unit) objectInStream.readObject();
        
        System.out.println("--Excess data in stream after receiving Unit:--");
        while (objectInStream.available() > 0) {
            System.out.print(objectInStream.read() + " ");
        }
        System.out.println("\n--End excess--");
        
        objectOutStream.writeBoolean(true);
        objectOutStream.flush();
        
        setChanged();
        notifyObservers(unit);
    }

}
