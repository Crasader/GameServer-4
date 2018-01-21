package integration;

import builder.SchemaBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import server.MainServer;

import java.io.DataInputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Integration test for the server.
 */
public class MainServerTest
{
    private static final Logger logger;
    static {
        logger = LoggerFactory.getLogger(MainServerTest.class);
    }

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8081;


    private static Thread thread;

    @BeforeClass
    public static void setUp() throws Exception {
        thread = new Thread(() -> {
            try {
                new MainServer(PORT, MainServer.Stage.DEV).run();
            } catch (InterruptedException ie) {
                logger.info("Shutting down Server");
            } catch (Exception ex) {
                logger.error("Could not start Server for tests", ex);
            }
        });
        thread.start();
        // make sure server gets off the ground
        Thread.sleep(2000);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        Thread.sleep(1000);
        thread.interrupt();
        while (thread.isAlive()) {
            Thread.sleep(250);
        }
    }

    @Test
    public void userLoginSuccessfully() {
        try {
            Socket socket = new Socket(HOST, PORT);
            // send data
            OutputStream outputStream = socket.getOutputStream();
            String token = "abcd";
            byte[] msg = SchemaBuilder.buildCredentialToken(token);
            outputStream.write(msg.length);
            outputStream.write(msg);
            outputStream.flush();

            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            int bufLength = inputStream.readInt();
            System.out.println(String.format("Buffer length {}", bufLength));
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}