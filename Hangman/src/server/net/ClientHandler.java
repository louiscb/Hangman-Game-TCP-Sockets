package server.net;

import server.controller.Controller;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

public class ClientHandler {
    private final SocketChannel channel;
    private final ByteBuffer dataFromClient = ByteBuffer.allocateDirect(2048);
    private LinkedList<String> sendingQueue = new LinkedList<String>();
    private LinkedList<String> receivingQueue = new LinkedList<String>();
    private Controller controller = new Controller();

    public ClientHandler(SocketChannel client) {
        this.channel = client;
    }

    void fromClient() throws IOException{
        //reading data from the channel
        dataFromClient.clear();
        int numOfReadBytes = channel.read(dataFromClient);
        if (numOfReadBytes == -1){
            System.out.println("Client has closed connection.");
            channel.close();
        }

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

    public void gameProcessing() {
        // get the strings from the queue and pass them to the GameSetup
        while (!receivingQueue.isEmpty()) {
            String msg = receivingQueue.remove();
            controller.setInput(msg);
            addToSendingQueue(controller.getOutput());
        }
    }

    private void addToSendingQueue(String msg) {
        synchronized (sendingQueue) {
            sendingQueue.add(msg);
        }
    }

    private String extractMessageFromBuffer() {
        dataFromClient.flip();
        byte[] bytes = new byte[dataFromClient.remaining()];
        dataFromClient.get(bytes);
        return new String(bytes);
    }

}