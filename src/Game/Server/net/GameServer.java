package Game.Server.net;

import Game.Server.controller.Controller;

import javax.xml.soap.SOAPException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class GameServer {

    private static final int LINGER_TIME = 5000;
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private static final int PORT_NO = 8080;

    public static void main (String [] args){
        GameServer server = new GameServer();
        server.init();
    }

    private void init() {
        try{
            ServerSocket socket = new ServerSocket(PORT_NO);
            while (true) {

                Socket link = socket.accept();
                startConnection(link);
            }

        }
        catch (IOException ioe)
        { System.out.println("No connection"); }

    }

    private void startConnection ( Socket link) throws SocketException
    {
        ClientHandler handler = new ClientHandler(link);
        Thread threadHandler = new Thread(handler);
        threadHandler.start();
    }

}
