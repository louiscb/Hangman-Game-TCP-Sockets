package Game.Client.view;


import Game.Client.controller.Controller;

import java.io.IOException;
import java.util.Scanner;

public class UserInterface implements Runnable {
    private Controller contr;


    public void start(){
        contr = new Controller();
        new Thread(this).start();
    }

    @Override
    public void run() {

        while(true){
            System.out.println("send/receive");
            Scanner userInput = new Scanner(System.in);
            String str = userInput.nextLine();

            switch (str){
                case "send" :
                    String s = userInput.nextLine();
                    try {
                        contr.sendToController(s);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case "receive" :
                    try {
                        System.out.println(contr.receiveFromController());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ;
                    break;

                default:
                    break;
            }

        }

    }
}
