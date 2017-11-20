package server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class GameServer {

    private static final int portNum = 8080;

    public static void main(String[] args) {
        GameServer server = new GameServer(); //create an object of itself because this class is not being explicity called,we have to create the object ourselves
        server.start();
    }

    private void start() {
        try {
            ServerSocket socket = new ServerSocket(portNum);
            System.out.println("Connected to port " + portNum);
            while (true) {
                System.out.println("Looking for client...");
                Socket client = socket.accept();
                startConnection(client);
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void startConnection (Socket client) throws SocketException {
        ClientHandler handler =  new ClientHandler(client);

        Thread threadHandler = new Thread(handler);
        threadHandler.start();
    }
}
