package Game.Client.controller;

import Game.Client.net.ServerConnector;

import java.io.IOException;

public class Controller {
    private ServerConnector connector = new ServerConnector();

    public Controller (){
        connect();
    }

    public void sendToController(String str) throws IOException {
        connector.sendToServer(str);
    }

    public String receiveFromController () throws IOException {
        return connector.receiveFromServer();
    }

    private void connect (){
        connector.connectToServer();
    }
}
