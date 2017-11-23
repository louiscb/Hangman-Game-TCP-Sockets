package server.net;

import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.security.Key;
import java.util.Iterator;

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

            while (true) {
                System.out.println("Waiting for client...");
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (key.isAcceptable()) {
                        startConnection(key);
                        System.out.println("hello");
                    } else if (key.isReadable()) {
                        readClient(key);
                        System.out.println("yooo");
                    } else if (key.isWritable()) {
                        System.out.println("IM WRITING HERE");
                        //DOnt know what to write here? THis isnt getting called by the program
                        writeClient(key);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startConnection (SelectionKey key) throws IOException {
        SocketChannel socketChannel;
        socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);

        System.out.println("Connection with " + socketChannel);

        Client client = new Client(socketChannel);

        key.attach(client);

        socketChannel.register(selector, SelectionKey.OP_READ, client);
    }

    private void readClient(SelectionKey key) {
        Client client = (Client)key.attachment();

        try {
            client.handler.fromClient();
            key.interestOps(key.OP_WRITE);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    void writeClient(SelectionKey key) {
        Client client = (Client)key.attachment();

        try {
            client.handler.toClient();
            key.interestOps(key.OP_READ);
        } catch (IOException e) {
            System.out.println(e);
        }
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