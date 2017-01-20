package nl.bitbusters.dnd.connectivity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import nl.bitbusters.dnd.Launcher;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

/**
 * Simple integration test for Server and Client.
 * 
 * @author Bart
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Launcher.class)
@SuppressWarnings("PMD.TooManyStaticImports")
public class ConnectivityTest {
    
    private static final int TEST_PORT = 1337;

    @Test
    public void testConnect() throws IOException {
        Server server = new Server(TEST_PORT);
        Client client = new Client();
        
        new Thread(() -> {
            try {
                mockStatic(Launcher.class);
                when(Launcher.askVerifyConnection(anyInt())).thenReturn(true);
                
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        client.connect("localhost", TEST_PORT);
        
        assertTrue(server.isConnected());
        assertTrue(client.isConnected());
        
        server.close();
        client.close();
        
        assertFalse(server.isConnected());
        assertFalse(client.isConnected());
    }

}
