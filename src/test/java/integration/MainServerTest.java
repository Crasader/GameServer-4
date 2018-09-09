package integration;

import builder.SchemaBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import schema.*;
import server.MainServer;
import server.netty.util.NettyUtils;

import java.io.DataInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import static java.lang.Thread.sleep;
import static junit.framework.TestCase.assertEquals;

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
    private static String roomId;
    private static String token1, token2;


    @BeforeClass
    public static void setUp() throws Exception {
        token1 = "userId1";
        token2 = "userId2";
        roomId = "Singapore";
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
        sleep(3000);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        sleep(1000);
        thread.interrupt();
        while (thread.isAlive()) {
            sleep(250);
        }
    }

    private Socket userJoinRoom(String token, String roomId) throws Exception {
        Socket socket = new Socket(HOST, PORT);
        // send data
        OutputStream outputStream = socket.getOutputStream();
        byte[] joinbuf = SchemaBuilder.buildJoinCommand(roomId, token).sizedByteArray();
        ByteBuf lengthBuffer = Unpooled.buffer(2);
        lengthBuffer.writeShort(joinbuf.length);
        outputStream.write(lengthBuffer.array());
        outputStream.write(joinbuf);
        outputStream.flush();
        sleep(2000);
        return socket;
    }

    private Message readMessage(Socket socket) throws Exception {
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        if(inputStream.available()>0) {
            int length = inputStream.readShort();
            byte[] b = new byte[length];
            inputStream.read(b, 0, length);
            java.nio.ByteBuffer buf = java.nio.ByteBuffer.wrap(b);
            Message mm = Message.getRootAsMessage(buf);
            return mm;
        }
        return null;
    }

    @Test
    public void userJoinedRoomSuccessfullyAndLeave() throws Exception {
        Socket socket1 = userJoinRoom(token1, roomId);
        Socket socket2 = userJoinRoom(token2, roomId);

        Message msg1 = readMessage(socket1);
        Message msg2 = readMessage(socket2);
        assertEquals(msg1.dataType(), Data.RoomInfo);
        RoomInfo room1 = (RoomInfo)msg1.data(new RoomInfo());
        assertEquals(room1.playersLength(), 1);
        verifyPlayer(room1.players(0), "userId1", "Nghia_userId1");

        assertEquals(msg2.dataType(), Data.RoomInfo);
        RoomInfo room2 = (RoomInfo)msg2.data(new RoomInfo());
        assertEquals(room2.playersLength(), 2);
        verifyPlayer(room2.players(0), "userId1", "Nghia_userId1");
        verifyPlayer(room2.players(1), "userId2", "Nghia_userId2");

        Message msg3 = readMessage(socket1);
        assertEquals(msg3.dataType(), Data.PlayerUpdate);
        PlayerUpdate player = (PlayerUpdate) msg3.data(new PlayerUpdate());
        assertEquals(player.player().userId(), "userId2");
        assertEquals(player.player().name(), "Nghia_userId2");
        assertEquals(player.status(), PlayerStatus.Arrive);
        socket1.close();

        sleep(1000);
        Message msg4 = readMessage(socket2);
        assertEquals(msg4.dataType(), Data.PlayerUpdate);
        PlayerUpdate playerLeft = (PlayerUpdate) msg4.data(new PlayerUpdate());
        assertEquals(playerLeft.player().userId(), "userId1");
        assertEquals(playerLeft.player().name(), "Nghia_userId1");
        assertEquals(playerLeft.status(), PlayerStatus.Left);
    }

    private void verifyPlayer(PlayerInfo player, String userId, String displayName) {
        assertEquals(player.userId(),  userId);
        assertEquals(player.name(),  displayName);
    }

}