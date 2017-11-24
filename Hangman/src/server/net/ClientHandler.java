package server.net;

import server.controller.Controller;
import server.model.GameSetup;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class ClientHandler implements Runnable {
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
    }

    @Override
    public void run() {
        System.out.println("Connected");

        // get the strings from the queue and pass them to the GameSetup
        while (!receivingQueue.isEmpty()) {
            String msg = receivingQueue.remove();
            gameSetup.setGameData(msg);
            sendResponseToClient(gameSetup.getGameData());

//            try {
//                input = new Scanner(client.getInputStream());
//                output = new PrintWriter(client.getOutputStream(), true);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            String received = input.nextLine();
//
//            if (received.contains("End Game")) {
//                disconnect();
//            } else {
//                cont.setInput(received);
//                output.println(cont.getOutput());
//            }
        }
    }

    private void sendResponseToClient(String msg) {


        synchronized (sendingQueue) {
            sendingQueue.add(msg);
        }
//        gameServer.addMessageToWritingQueue(this.selectionKey);
//        gameServer.wakeupSelector();
    }

    public void addtoSendingQueue (){
        sendingQueue.add(gameSetup.getGameData());
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
        ForkJoinPool.commonPool().execute(this);

//        dataFromClient.clear();
//        int numBytes = channel.read(dataFromClient);
//        String fromClientMsg =  extractMessageFromBuffer();
//        cont.setInput(fromClientMsg);
////        System.out.println(fromClientMsg);
//        toClient();
//

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
//        dataToClient.flip();
//        String fromServer = cont.getOutput();
//
//        dataToClient = ByteBuffer.wrap((fromServer.getBytes(Charset.defaultCharset())));
//
//        try {
//            channel.write(dataToClient);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private String extractMessageFromBuffer() {
        dataFromClient.flip();
        byte[] bytes = new byte[dataFromClient.remaining()];
        dataFromClient.get(bytes);
        return new String(bytes);
    }

}