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
                selector.select();
                for (SelectionKey key : this.selector.selectedKeys()) {
                    selector.selectedKeys().remove(key);

                    if (!key.isValid()) continue;

                    if (key.isAcceptable()) startConnection(key);
                    else if (key.isReadable()) readClient(key);
                    else if (key.isWritable()) writeToClient(key);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startConnection (SelectionKey key) throws IOException {
        System.out.println("STARTING CONNECTION");
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        ClientHandler handler = new ClientHandler(socketChannel);
        key.attach(handler);
        socketChannel.register(selector, SelectionKey.OP_READ, handler);
    }

    private void readClient(SelectionKey key) throws IOException {
        System.out.println("READING CLIENT");
        ClientHandler handler = (ClientHandler) key.attachment();
        handler.fromClient();

        key.interestOps(SelectionKey.OP_WRITE);
    }

    private void writeToClient(SelectionKey key) {
        System.out.println("WRITING CLIENT");
        ClientHandler clientHandler = (ClientHandler) key.attachment();
        clientHandler.toClient();
        key.interestOps(SelectionKey.OP_READ);
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