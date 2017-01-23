package nl.bitbusters.dnd.connectivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import nl.bitbusters.dnd.Launcher;
import nl.bitbusters.dnd.model.Player;
import nl.bitbusters.dnd.model.Unit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Simple integration test for Server and Client.
 * 
 * @author Bart
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Launcher.class)
@SuppressWarnings("PMD.TooManyStaticImports")
public class ConnectivityTest extends Application {
    
    private static final int TEST_PORT = 1337;
    
    private volatile Throwable error;
    
    private Client client;
    private Server server;
    
    /**
     * Initialises the JavaFX runtime for these tests, so images can be loaded.
     */
    @BeforeClass
    public static void setUpClass() {
        new Thread(() -> launch()).start();
    }
    
    /**
     * Opens a new server and client, resets the error.
     * @throws IOException due to Server.
     */
    @Before
    public void setUp() throws IOException {
        server = new Server(TEST_PORT);
        client = new Client();
        error = null;
    }
    
    /**
     * Closes down the server and client, resets the error.
     * @throws IOException due to server close method.
     */
    @After
    public void tearDown() throws IOException {
        server.close();
        client.close();
        error = null;
    }
    
    /**
     * Exits the JavaFX runtime.
     */
    @AfterClass
    public static void tearDownClass() {
        Platform.exit();
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void testConnectClose() throws IOException, InterruptedException {
        launchServer(true);
        client.connect("localhost", TEST_PORT);
        
        assertTrue(server.isConnected());
        assertTrue(client.isConnected());
        
        Thread clientThread = client.getRunLoopThread(); //check thread is alive
        assertNotNull(clientThread);
        assertTrue(clientThread.isAlive());
        
        server.close();
        client.close();
        
        assertFalse(server.isConnected());
        assertFalse(client.isConnected());
        clientThread.join(); //client thread should be closed down by close method, this is to make sure.
    }
    
    @Test
    public void testSendMap() throws Throwable {
        launchServer(true);
        client.connect("localhost", TEST_PORT);
        
        Image testImage = new Image("testImage.jpg");
        client.addObserver((obs, arg) -> { //check whether image sent is equal to image received.
            try {
                assertFXImageEquals(testImage, arg);
                assertEquals(0, client.getObjectInputStream().available());
            } catch (Throwable e) { //run on separate thread, so error must be passed to JUnit main thread.
                error = e;
            }
            client.close();
        });
        
        server.sendMap(testImage);
        
        client.getRunLoopThread().join(); //wait for client thread to finish due to close() or with error / exception.
        if (error != null) {
            throw error;
        }
    }
    
    @Test
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    public void testSendUnit() throws Throwable {
        launchServer(true);
        client.connect("localhost", TEST_PORT);
        
        Image testSprite = new Image("testImage.jpg");
        Unit testUnit = new Player("Test", 42, new ArrayList<>(), testSprite, new ImageView(testSprite));
        client.addObserver((obs, arg) -> { //check whether image sent is equal to image received.
            try {
                assertTrue(arg instanceof Unit);
                Unit received = (Unit) arg;
                assertEquals(testUnit.getName(), received.getName());
                assertEquals(testUnit.getAffliction(), received.getAffliction());
                assertEquals(testUnit.getHealth(), received.getHealth());
                assertFXImageEquals(testUnit.getIcon(), received.getIcon());
                assertEquals(0, client.getObjectInputStream().available());
            } catch (Throwable e) { //run on separate thread, so error must be passed to JUnit main thread.
                error = e;
            }
            client.close();
        });
        
        server.sendUnit(testUnit);
        
        client.getRunLoopThread().join(); //wait for client thread to finish due to close() or with error / exception.
        if (error != null) {
            throw error;
        }
    }
    
    /**
     * Asserts whether two JavaFX images are the same image by asserting equality on size and individual pixels.
     * @param expected the expected Image
     * @param actual the actual object that will be compared to the expected image.
     */
    public static void assertFXImageEquals(Image expected, Object actual) {
        assertTrue(actual instanceof Image);
        Image received = (Image) actual;
        assertEquals(expected.getWidth(), received.getWidth(), 0.1);
        assertEquals(expected.getHeight(), received.getHeight(), 0.1);
        
        for (int x = 0; x < expected.getWidth(); x++) {
            for (int y = 0; y < expected.getHeight(); y++) {
                assertEquals(expected.getPixelReader().getArgb(x, y), received.getPixelReader().getArgb(x, y));
            }
        }
    }
    
    /**
     * Launches the server in a new thread, with a certain response from the
     * {@linkplain Launcher#askVerifyConnection(int)} method, achieved using mocking with PowerMockito.
     * @param launcherResponse response from launcher when asking for verification.
     */
    public void launchServer(boolean launcherResponse) {
        new Thread(() -> {
            try {
                mockStatic(Launcher.class);
                when(Launcher.askVerifyConnection(anyInt())).thenReturn(launcherResponse);
                
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "Server").start();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //used to initialise JavaFX engine so Images can be read.
    }
}
