package nl.bitbusters.dnd.connectivity;

import nl.bitbusters.dnd.Launcher;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements AutoCloseable {
    
    private ServerSocket serverSocket;
    private Socket clientSocket;
    
    private DataInputStream dataInStream;
    private DataOutputStream dataOutStream;
    
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
     * Waits for a connection from a client, and accepts the incoming connection.
     * @throws IOException if an I/O error occurs waiting for a connection.
     */
    public void start() throws IOException {
        clientSocket = serverSocket.accept();
        dataInStream = new DataInputStream(clientSocket.getInputStream());
        dataOutStream = new DataOutputStream(clientSocket.getOutputStream());
        dataOutStream.writeBoolean(Launcher.askVerifyConnection(dataInStream.readInt()));
        running = true;
    }
    
    /**
     * Closes the server and a possible connection.
     * @throws IOException if an I/O error occurs while closing the sockets.
     */
    @Override
    public void close() throws IOException {
        if (clientSocket != null) {
            clientSocket.close();
        }
        serverSocket.close();
        running = false;
    }
    
    /**
     * Returns true iff the server is running.
     * @return true iff the server is running.
     */
    public boolean isRunning() {
        return running;
    }

}
