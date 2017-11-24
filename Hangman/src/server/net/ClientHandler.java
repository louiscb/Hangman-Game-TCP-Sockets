package server.net;

import server.controller.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    //private final Socket client;
    private final SocketChannel channel;
    private Scanner input; //from client
    private PrintWriter output; //to client
    private final Controller cont = new Controller();
    private boolean open = true;

    private final ByteBuffer dataFromClient = ByteBuffer.allocateDirect(2048);
    private ByteBuffer dataToClient = ByteBuffer.allocateDirect(2048);

    public ClientHandler(SocketChannel client) {
        this.channel = client;
    }

    @Override
    public void run() {
        System.out.println("Connected");

        /*while (open) {
            try {
                input = new Scanner(client.getInputStream());
                output = new PrintWriter(client.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String received = input.nextLine();

            if (received.contains("End Game")) {
                disconnect();
            } else {
                cont.setInput(received);
                output.println(cont.getOutput());
            }
        }*/
    }

    private void disconnect() {
    /*    open = false;

        System.out.println("Disconnecting client " + client.toString());
        try {
            client.close();
        } catch (IOException e) {
          System.out.println(e);
        }*/
    }

    void fromClient() throws IOException{
        dataFromClient.clear();
        int numBytes = channel.read(dataFromClient);
        String fromClientMsg =  extractMessageFromBuffer();
        cont.setInput(fromClientMsg);
//        System.out.println(fromClientMsg);
        toClient();
    }

    void toClient() {
        dataToClient.flip();
        String fromServer = cont.getOutput();

        dataToClient = ByteBuffer.wrap((fromServer.getBytes(Charset.defaultCharset())));

        try {
            channel.write(dataToClient);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String extractMessageFromBuffer() {
        dataFromClient.flip();
        byte[] bytes = new byte[dataFromClient.remaining()];
        dataFromClient.get(bytes);
        return new String(bytes);
    }
}