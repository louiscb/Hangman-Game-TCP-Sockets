package server.net;

import server.controller.Controller;
import server.model.GameSetup;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class ClientHandler {
    //private final Socket client;
    private final SocketChannel channel;
    private Scanner input; //from client
    private PrintWriter output; //to client
    private final Controller cont = new Controller();
    private boolean open = true;

    private final ByteBuffer dataFromClient = ByteBuffer.allocateDirect(2048);
    private ByteBuffer dataToClient = ByteBuffer.allocateDirect(2048);

    private LinkedList<String> sendingQueue = new LinkedList<String>();
    private LinkedList<String> receivingQueue = new LinkedList<String>();

    private GameSetup gameSetup = new GameSetup();


    public ClientHandler(SocketChannel client) {
        this.channel = client;
        //receivingQueue.add("Start Game");
    }


    public void gameProcessing() {
        System.out.println("Client Handler");

        // get the strings from the queue and pass them to the GameSetup
        while (!receivingQueue.isEmpty()) {
            String msg = receivingQueue.remove();
            gameSetup.setGameData(msg);
            addToSendingQueue(gameSetup.getGameData());
        }
    }

    private void addToSendingQueue(String msg) {
        synchronized (sendingQueue) {
            sendingQueue.add(msg);
        }
    }

    public void disconnect() {
    /*    open = false;

        System.out.println("Disconnecting client " + client.toString());
        try {
            client.close();
        } catch (IOException e) {
          System.out.println(e);
        }*/
    }

    void fromClient() throws IOException{

        // reading data from the channel
        dataFromClient.clear();
        int numOfReadBytes = channel.read(dataFromClient);
        if (numOfReadBytes == -1) throw new IOException("Client has closed connection.");

        // adding the extracted string to the queue
        receivingQueue.add(extractMessageFromBuffer());
        // calling the run method
        gameProcessing();

    }

    void toClient() {

        synchronized (sendingQueue) {
            while (!sendingQueue.isEmpty()) {
                ByteBuffer message = ByteBuffer.wrap(sendingQueue.remove().getBytes());
                try {
                    channel.write(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String extractMessageFromBuffer() {
        dataFromClient.flip();
        byte[] bytes = new byte[dataFromClient.remaining()];
        dataFromClient.get(bytes);
        return new String(bytes);
    }

}