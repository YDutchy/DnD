package nl.bitbusters.dnd.connectivity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import nl.bitbusters.dnd.Launcher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Simple test for Server.
 * 
 * @author Bart
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Launcher.class)
@SuppressWarnings("PMD.TooManyStaticImports")
public class ServerTest {
    
    private static final int TEST_PORT = 1337;
    
    private Server server;
    
    @Before
    public void setUp() throws IOException {
        server = new Server(TEST_PORT);
    }
    
    @After
    public void tearDown() throws IOException {
        server.close();
    }

    @Test
    public void testServer() throws IOException {
        mockStatic(Launcher.class);
        when(Launcher.askVerifyConnection(anyInt())).thenReturn(true);
        
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
        assertTrue(server.isConnected());
        server.close();
        assertFalse(server.isConnected());
    }
    
    @Test(expected = IOException.class)
    public void testServerWrongVerification() throws IOException {
        mockStatic(Launcher.class);
        when(Launcher.askVerifyConnection(anyInt())).thenReturn(false);
        
        new Thread(() -> {
            try {
                Socket client = new Socket("localhost", TEST_PORT);
                new DataOutputStream(client.getOutputStream()).writeInt(65656);
                assertFalse(new DataInputStream(client.getInputStream()).readBoolean());
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        
        server.start();
        
        server.close();
    }
    
    @Test(expected = SocketTimeoutException.class)
    public void testServerNoVerificationNumber() throws IOException {
        server.setReadTimeout(300);
        new Thread(() -> {
            try {
                Socket client = new Socket("localhost", TEST_PORT);
                Thread.sleep(300);
                client.close();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        
        server.start();
        
        server.close();
    }

}
