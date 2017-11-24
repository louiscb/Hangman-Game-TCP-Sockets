package server.net;

import javax.xml.bind.TypeConstraintException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.Key;
import java.util.Iterator;
import java.util.LinkedList;

public class GameServer {

    private static final int portNum = 8080;
    private static ServerSocketChannel serverSocketChannel;

    private final LinkedList<SelectionKey> sendingQueue = new LinkedList<SelectionKey>();
    private Selector selector;

    public static void main(String[] args) {
        GameServer server = new GameServer(); //create an object of itself because this class is not being explicity called,we have to create the object ourselves
        server.start();
    }

    private void start() {
        ServerSocket serverSocket;
        System.out.println("START METHOD CALLED");

        try {
            System.out.println("Starting Server");

            //init server socket channel
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocket = serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(portNum));
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);



            while (true) {

                while (!sendingQueue.isEmpty()) {
                    sendingQueue.remove().interestOps(SelectionKey.OP_WRITE);
                }
                System.out.println("before");
                selector.select();
                System.out.println("after");
                for (SelectionKey key : this.selector.selectedKeys()) {
                    if (!key.isValid()) continue;

                    if (key.isAcceptable()) {
                        startConnection(key);

                    }
                    else if (key.isReadable()) readClient(key);
                    else if (key.isWritable()) writeToClient(key);

                    selector.selectedKeys().remove(key);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
//
//                while (iterator.hasNext()) {
//                    SelectionKey key = iterator.next();
//                    iterator.remove();
//
//                    if (key.isAcceptable()) {
//                        startConnection(key);
//                    } else if (key.isWritable()) {
//                        //DOnt know what to write here? THis isnt getting called by the program
//                       //writeClient(key);
//                    } else if (key.isReadable()) {
//                        readClient(key);
//                    }
//                }
//            }
    }

    private void writeToClient(SelectionKey key) {
        ClientHandler clientHandler = (ClientHandler) key.attachment();
        clientHandler.toClient();
        key.interestOps(SelectionKey.OP_READ);
    }

    private void startConnection (SelectionKey key) throws IOException {
        SocketChannel socketChannel;
        socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);

        System.out.println("Connection with " + socketChannel);

        Client client = new Client(socketChannel);
        key.attach(client);
        socketChannel.register(selector, SelectionKey.OP_READ, client);
        System.out.println("reading passed");
    }

    private void readClient(SelectionKey key) throws IOException {
        Client client = (Client)key.attachment();

        try {
            client.handler.fromClient();
        } catch (IOException clientHasClosedConnection) {
            removeClient(key);
        }

        //ECHOS BACK TO CLIENT

//        ByteBuffer buffer = ByteBuffer.allocate(2048);
//
//        client.clientChannel = (SocketChannel)key.channel();
//
//        buffer.clear();
//
//        try {
//            int numBytes = client.clientChannel.read(buffer);
//
//            System.out.println(numBytes + " bytes read bitch");
//
//            if (numBytes != -1) {
//                buffer.flip();
//                while (buffer.remaining()>0)
//                    client.clientChannel.write(buffer);
//            }
//        } catch (IOException e ) {
//            e.printStackTrace();
//        }


    }

    private void removeClient(SelectionKey clientKey) throws IOException {
        Client client = (Client) clientKey.attachment();
        client.handler.disconnect();
        clientKey.cancel();
    }

    private class Client {
        private final ClientHandler handler;
        private SocketChannel clientChannel ;

        Client (SocketChannel channel) {
            this.clientChannel = channel;
            handler = new ClientHandler(this.clientChannel);
        }
    }
}