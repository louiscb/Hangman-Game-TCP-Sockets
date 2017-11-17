package client.view;

import client.controller.Controller;

import java.io.IOException;
import java.util.Scanner;

public class UserInterface  {
    private Controller cont;
    private String[] msgArray;

    public void start() {
        //create one controller object for each client
        cont = new Controller();

        System.out.println("Type 'Start Game' to play...");
        runLoop();
    }

    public void runLoop() {
        while (true) {
            Scanner userEntry = new Scanner(System.in);
            String s = userEntry.nextLine();

            //We create a thread for every input from the client, which sends to input to the server and outputs whats recieved from the server
            Thread thread = new Thread(new inputThread(s));
            thread.start();
        }
    }

    class inputThread implements Runnable {
        private String inputToSend;

        inputThread (String input) {
            inputToSend = input;
        }

        public void run() {
            //sending data to server
            try {
                cont.setData(inputToSend);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String messageFromServer = null;

            //Recieving data from server
            try {
                messageFromServer = cont.getData();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (messageFromServer!=null && messageFromServer.contains("/")) {
                msgArray = messageFromServer.split("/");
                StringBuilder sb = new StringBuilder();
                sb.append(msgArray[3]);
                sb.append("\n");
                sb.append("Score: ");
                sb.append(msgArray[0]);
                sb.append("\n");
                sb.append("Attempts left: ");
                sb.append(msgArray[1]);
                sb.append("\n");
                sb.append("Word: ");
                sb.append("\n");

                for (char c: msgArray[2].toCharArray()) {
                    sb.append(c);
                    sb.append(" ");
                }

                sb.append("\n");

                messageFromServer = sb.toString();
            }

            System.out.println(messageFromServer);
        }
    }
}