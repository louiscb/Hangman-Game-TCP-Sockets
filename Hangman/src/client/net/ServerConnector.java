package client.net;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ServerConnector {
    private Socket socket;
    private PrintWriter output;
    private Scanner input;
    private static final int portNum = 8080;
    private static InetAddress host;

    public void connectToServer() {
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            System.out.println("Unknown host");
        }

        try {
            socket = new Socket(host, portNum);
        } catch (IOException e) {
            System.out.println("Couldn't connect to host");
        }
    }

    public void sendToServer(String s) throws IOException {
        output = new PrintWriter(socket.getOutputStream(), true);
        output.println(s);
        if (s.contains("End Game")) {
            disconnect();
        }
    }

    public String receiveFromServer() throws IOException {
        input = new Scanner(socket.getInputStream());
        return input.nextLine();
    }

    private void disconnect() {
        System.out.println("Disconecting from server");
        try {
            socket.close();
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
