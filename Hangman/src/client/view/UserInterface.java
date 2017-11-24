package client.view;

import client.controller.Controller;

import java.io.IOException;
import java.util.Scanner;

public class UserInterface implements Runnable {
    private Controller cont;
    private String[] msgArray;

    public void start() {
        System.out.println("Type 'connect' to play...");
        cont = new Controller();
        new Thread(this).start();
    }

    @Override
    public void run() {
        while(true){
            try{
                Scanner userEntry = new Scanner(System.in);
                String userMsg = userEntry.nextLine();

                if (userMsg.equalsIgnoreCase("connect")){
                    cont.connect();
                }
                else if (userMsg.equalsIgnoreCase("disconnect")){
                    cont.disconnect();
                }
                else{
                    cont.addUserMsgToQueue(userMsg);
                }

            }catch (IllegalArgumentException e){
                System.out.println(e);
            }
        }
    }
}