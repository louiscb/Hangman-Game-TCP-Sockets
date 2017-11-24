package client.controller;

import client.net.ServerConnector;

import java.io.IOException;

public class Controller {

    private ServerConnector connector = new ServerConnector();

    public void connect() {
        connector.connectToServer();
    }

    public void disconnect() {
        connector.disconnectFromServer();
    }

    public void addUserMsgToQueue ( String msg){
        connector.addUserMsgToQueue(msg);
    }

//    public void setData(String s) throws IOException{
//        connector.sendToServer(s);
//    }
//
//    public String getData() throws IOException{
//        return connector.receiveFromServer();
//    }
}
