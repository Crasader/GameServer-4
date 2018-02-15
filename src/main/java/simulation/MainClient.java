package simulation;

import java.net.Socket;

public class MainClient {

    public static void main(String [] args) {
        CommandLineGame commandLineGame = new CommandLineGame();
        System.out.println("Starting the game...");
        try{
            commandLineGame.start();
        } catch(Exception e) {
            System.out.println(e.toString());
        }
    }

}
