package server.net;

import server.controller.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private final Socket client;
    private Scanner input; //from client
    private PrintWriter output; //to client
    private final Controller cont = new Controller();

    public ClientHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        System.out.println("Found client");
        while (true) {
            try {
                input = new Scanner(client.getInputStream());
                output = new PrintWriter(client.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String received = input.nextLine();
            cont.setInput(received);

            output.println(cont.getOutput());
        }
    }
}
