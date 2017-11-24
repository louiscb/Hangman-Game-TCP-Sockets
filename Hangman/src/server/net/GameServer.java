package server.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class GameServer {

    private static final int portNum = 8080;
    private static ServerSocketChannel serverSocketChannel;
    private Selector selector;

    public static void main(String[] args) {
        GameServer server = new GameServer(); //create an object of itself because this class is not being explicity called,we have to create the object ourselves
        server.start();
    }

    private void start() {
        ServerSocket serverSocket;
        try {
            System.out.println("Starting Server");

            //init server socket channel
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocket = serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(portNum));
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("waiting for client");

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
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        ClientHandler handler = new ClientHandler(socketChannel);
        key.attach(handler);
        System.out.println("client is connected " + socketChannel.socket());
        socketChannel.register(selector, SelectionKey.OP_READ, handler);
    }

    private void readClient(SelectionKey key) throws IOException {
        ClientHandler handler = (ClientHandler) key.attachment();
        handler.fromClient();
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private void writeToClient(SelectionKey key) {
        ClientHandler clientHandler = (ClientHandler) key.attachment();
        clientHandler.toClient();
        key.interestOps(SelectionKey.OP_READ);
    }

}