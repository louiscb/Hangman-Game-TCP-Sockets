package client.view;

import client.controller.Controller;

import java.io.IOException;
import java.util.Scanner;

public class UserInterface implements Runnable {
    private Controller cont;
    private String[] msgArray;

    public void start() {
        cont = new Controller();
        new Thread(this).start();
    }

    public void run() {
        System.out.println("Type 'Start Game' to play...");
        while (true) {
            Scanner userEntry = new Scanner(System.in);

            String s = userEntry.nextLine();
            try {
                cont.setData(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String messageFromServer = null;
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