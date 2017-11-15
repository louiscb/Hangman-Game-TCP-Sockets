package Game.Client.net;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ServerConnector {
    private Socket socket;
    private Scanner input;
    private PrintWriter output;
    private static InetAddress host;
    private static final int PORT_NO = 8080;

    public void connectToServer (){

        try{
            host = InetAddress.getLocalHost();
        }
        catch (UnknownHostException uhEX){
            System.out.println("Host ID not found!");
            System.exit(1);
        }

        try { // heck this out one more time
            socket = new Socket(host, PORT_NO);
        }
        catch (IOException ioe){
            System.out.println("couldn't connect to host");
        }
    }

    public void sendToServer ( String sentData) throws IOException {
        output = new PrintWriter(socket.getOutputStream(), true);
        output.println(sentData);
    }

    public String receiveFromServer () throws IOException {
        input = new Scanner(socket.getInputStream());
        System.out.println("did it to network");
        return input.nextLine();
    }

}
