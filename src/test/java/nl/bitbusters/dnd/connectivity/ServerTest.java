package nl.bitbusters.dnd.connectivity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerTest {
    
    private static final int TEST_PORT = 1337;

    @Test
    public void testServer() throws IOException {
        Server server = new Server(TEST_PORT);
        new Thread(() -> {
            try {
                Socket client = new Socket("localhost", TEST_PORT);
                new DataOutputStream(client.getOutputStream()).writeInt(65656);
                assertTrue(new DataInputStream(client.getInputStream()).readBoolean());
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        server.start();
        assertTrue(server.isRunning());
        server.close();
        assertFalse(server.isRunning());
    }

}
