package client.net;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Scanner;

public class ServerConnector implements  Runnable{
    private Socket socket;
    private PrintWriter output;
    private Scanner input;
    private static final int portNum = 8080;
    private static InetAddress host;

    private InetSocketAddress serverAddress;
    private SocketChannel socketChannel;
    private Selector selector;
    private LinkedList<String> sendingQueue = new LinkedList<String>();
    private LinkedList<String> receivingQueue = new LinkedList<String>();
    private ByteBuffer bufferFromServer = ByteBuffer.allocateDirect(2048);
    private volatile boolean timeToSend = false;

    public void connectToServer() {
        this.serverAddress = new InetSocketAddress(host, portNum);
        new Thread(this).start();
    }

    public void disconnectFromServer() {
        System.out.println("client is disconnected");
        try {
            this.socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.socketChannel.keyFor(selector).cancel();
    }

    public void addUserMsgToQueue ( String msg){
        synchronized (sendingQueue) {
            sendingQueue.add(msg);
        }

        this.timeToSend = true;
        selector.wakeup();
    }

//    public void sendToServer(String s) throws IOException {
//        output = new PrintWriter(socket.getOutputStream(), true);
//        output.println(s);
//        if (s.contains("End Game")) {
//            disconnect();
//        }
//    }

    public String receiveFromServer() throws IOException {
        input = new Scanner(socket.getInputStream());
        return input.nextLine();
    }

//    private void disconnect() {
//        System.out.println("Disconnecting from server");
//        try {
//            socket.close();
//            System.exit(0);
//        } catch (IOException e) {
//            System.out.println(e);
//        }
//    }

    private void initConnection() throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(serverAddress);
    }

    private void initSelector() throws IOException {
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
    }

    @Override
    public void run() {


        try {
            initConnection();
            initSelector();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true){
            try {

                selector.select();
                if(timeToSend){
                    socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
                    timeToSend = false;
                }

                for (SelectionKey key : selector.selectedKeys()) {
                    selector.selectedKeys().remove(key);
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isConnectable()) {
                        completeConnection(key);
                    } else if (key.isReadable()) {
                        recvFromServer(key);
                    } else if (key.isWritable()) {
                        sendToServer(key);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void completeConnection(SelectionKey key) throws IOException {
        socketChannel.finishConnect();
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private void recvFromServer(SelectionKey key) throws IOException {
        bufferFromServer.clear();
        int numOfReadBytes = socketChannel.read(bufferFromServer);
        if (numOfReadBytes == -1) {
            throw new IOException("Not able to read from server");
        }

        String recvdString = extractMessageFromBuffer();
        System.out.println(recvdString);

        key.interestOps(SelectionKey.OP_WRITE);

    }

    private void sendToServer(SelectionKey key) throws IOException {
        synchronized (sendingQueue){
            while (!sendingQueue.isEmpty()){
                ByteBuffer bufferTosend = ByteBuffer.wrap(sendingQueue.remove().getBytes());
                socketChannel.write(bufferTosend);

                if (bufferTosend.hasRemaining()) return;
            }
        }

        key.interestOps(SelectionKey.OP_READ);

    }


//    private void showOutput (String recvdString){
//        String[] dataToShow = recvdString.split("/");
//        System.out.println("---" + dataToShow[3] + "---" );
//        System.out.println("Score: " + dataToShow[0] + "     Attempts: " + dataToShow[1] + "    Wrong letters: " + dataToShow[4]);
//        System.out.println("Word to guess:   " + dataToShow[2] + "\n");
//
//    }

    private String extractMessageFromBuffer() {
        bufferFromServer.flip();
        byte[] bytes = new byte[bufferFromServer.remaining()];
        bufferFromServer.get(bytes);
        return new String(bytes);
    }
}
