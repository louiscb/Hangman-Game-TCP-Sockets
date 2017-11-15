package Game.Server.net;

import Game.Server.controller.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private Scanner input;
    private PrintWriter output;
    private final Controller contr = new Controller();


    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {

        while(true){
            try {
                input = new Scanner(clientSocket.getInputStream());
                output =  new PrintWriter(clientSocket.getOutputStream(), true);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            // receiving data from network
            String received = input.nextLine();
            contr.setInput(received);

            // sending data to network
            output.println(contr.getOutput());
        }

    }
}
