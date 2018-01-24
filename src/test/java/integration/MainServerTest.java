package integration;

import builder.SchemaBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import schema.Data;
import schema.Message;
import schema.ReconnectKey;
import server.MainServer;

import java.io.DataInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

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
        Thread.sleep(1000);
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
        System.out.println("inside test");
        try {
            Socket socket = new Socket(HOST, PORT);
            // send data
            OutputStream outputStream = socket.getOutputStream();
            String token = "ijk";
            byte[] msg = SchemaBuilder.buildCredentialToken(token).sizedByteArray();

            ByteBuf lengthBuffer = Unpooled.buffer(2);
            lengthBuffer.writeShort(msg.length);

            outputStream.write(lengthBuffer.array());
            outputStream.write(msg);
            outputStream.flush();

            Thread.sleep(2000);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());

            if(inputStream.available()>0) {
                int length = inputStream.readShort();
                byte[] b = new byte[length];
                inputStream.read(b, 0, length);
                java.nio.ByteBuffer buf = java.nio.ByteBuffer.wrap(b);
                Message mm = Message.getRootAsMessage(buf);
                if (mm.dataType() == Data.ReconnectKey) {
                    ReconnectKey reconnectKey = (ReconnectKey)mm.data(new ReconnectKey());
                    System.out.println("Value of msg:" + reconnectKey.key());
                }
            }

            //inputStream.read(buf, 0, length);
            //System.out.println("Debugg = " + Arrays.toString(buf));
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}