package simulation;

import auth.FireBaseLoginAuth;
import auth.LoginAuth;
import builder.SchemaBuilder;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import schema.*;
import server.app.PlayerManager;
import sun.security.ssl.Debug;
import util.DebugUtil;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CommandLineGame {

    private enum GameState {
        JOINING_ROOM,
        JOINED_ROOM,
    }
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8080;
    private static final String SINGAPORE = "Singapore";

    private LoginAuth loginAuth;
    private Socket socket;
    private static Thread thread;
    private GameState gameState;
    private List<PlayerInfo> players;
    private String email;

    public void start() throws Exception {
        this.setUpFireBase();
        System.out.println("Done setup the firebase!");
        loginAuth = new FireBaseLoginAuth();
        socket = new Socket(HOST, PORT);
        Thread thread = new Thread(() -> {
            while(true) {
                try {
                    Message result = this.readMessage();
                    if (result!=null) {
                        handleRecieved(result);
                    }
                } catch(Exception e) {
                    print(e.toString());
                }
            }
        });
        thread.start();
        System.out.println("Starting listening to the server!");
        email = "nghiank@hotmail.com";
        Scanner keyboard = new Scanner(System.in);
        print("Enter email:(default=" + email +"):");
        String overrideEmail = keyboard.nextLine();
        if (!overrideEmail.isEmpty()) {
            email = overrideEmail;
        }
        print("Current email = " + email);
        while(true) {
            printInstruction();
            print("Enter command:");
            int command = keyboard.nextInt();
            if (command == 0) {
                print("Quit the game!");
                break;
            }
            try{
                handleCommand(command);
            } catch(Exception e) {
                print(e.toString());
            }
        }
    }

    private void printInstruction() {
        System.out.println("0. Quit the game");
        System.out.println("1. Login and Join room");
        System.out.println("2. Leave the room");
    }

    private void print(String s) {
        System.out.println(s);
    }

    private void handleCommand(int command) throws Exception{
        Scanner keyboard = new Scanner(System.in);
        if (command == 1) {
            print("Login in --> email:" + email);
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmailAsync(email).get();
            String fakeToken = userRecord.getUid();
            print("Join room --> ");
            gameState = GameState.JOINING_ROOM;
            userJoinRoom(fakeToken, SINGAPORE);
        }
    }

    private void setUpFireBase() throws Exception {
        FileInputStream serviceAccount =
                new FileInputStream("/Users/nghiaround/Desktop/key.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                .setDatabaseUrl("https://pokerg-bf08c.firebaseio.com")
                .build();
        FirebaseApp.initializeApp(options);
    }

    private void userJoinRoom(String token, String roomId) throws Exception {
        // send data
        OutputStream outputStream = socket.getOutputStream();
        byte[] joinbuf = SchemaBuilder.buildJoinCommand(roomId, token).sizedByteArray();
        ByteBuf lengthBuffer = Unpooled.buffer(2);
        lengthBuffer.writeShort(joinbuf.length);
        outputStream.write(lengthBuffer.array());
        outputStream.write(joinbuf);
        outputStream.flush();
    }

    private Message readMessage() throws Exception {
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

    private void handleRecieved(Message m) {
        print("Received message type :" + DebugUtil.toString(m));

        if (gameState == GameState.JOINING_ROOM) {
            if (m.dataType() == Data.RoomInfo) {
                gameState = GameState.JOINED_ROOM;
                players = new ArrayList<>();
                RoomInfo roomInfo = (RoomInfo)m.data(new RoomInfo());
                for(int i = 0; i < roomInfo.playersLength(); ++i) {
                    players.add(roomInfo.players(i));
                }
                print(DebugUtil.toString(roomInfo));
            } else {
                print("!!!!!!!!!Game State is joining but received wrong message");
            }
            return;
        }

        if (gameState == GameState.JOINED_ROOM) {
            if (m.dataType() == Data.PlayerUpdate) {
                PlayerUpdate update = (PlayerUpdate)m.data(new PlayerUpdate());
                if (update.status() == PlayerStatus.Arrive) {
                    players.add(update.player());
                } else if (update.status() == PlayerStatus.Left) {
                    for(int i = 0; i < players.size(); ++i) {
                        if (players.get(i).userId() == update.player().userId()) {
                            players.remove(i);
                            break;
                        }
                    }
                    for(int i = 0; i < players.size(); ++i) {
                        print(DebugUtil.toString(players.get(i)));
                    }
                }
            }
        }
    }
}
