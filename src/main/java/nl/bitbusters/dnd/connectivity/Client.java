package nl.bitbusters.dnd.connectivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class Client implements AutoCloseable {
    
    private boolean connected;
    
    private Socket socket;
    private DataInputStream dataInStream;
    private DataOutputStream dataOutStream;

    public Client() {
        connected = false;
    }
    
    @Override
    public void close() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    public void connect(String address, int port) throws IOException {
        socket = new Socket(address, port);
        dataInStream = new DataInputStream(socket.getInputStream());
        dataOutStream = new DataOutputStream(socket.getOutputStream());
        int num = new Random().nextInt(99999);
        dataOutStream.writeInt(num);
        connected = dataInStream.readBoolean();
    }

}
