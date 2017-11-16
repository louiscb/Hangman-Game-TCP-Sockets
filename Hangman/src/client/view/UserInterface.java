package client.view;

import client.controller.Controller;

import java.io.IOException;
import java.util.Scanner;

public class UserInterface implements Runnable {
    private Controller cont;

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

            try {
                System.out.println(cont.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
