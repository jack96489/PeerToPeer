package Peer.Common;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

public abstract class FileTransfer implements Closeable {
    protected final DatagramSocket socket;
    protected final SocketAddress address;


    public FileTransfer(DatagramSocket socket, SocketAddress address) {
        this.socket = socket;
        this.address = address;
    }


    protected void waitForAck() throws IOException {
        byte[] bufferIN = new byte[1024];
        DatagramPacket ackPacket = new DatagramPacket(bufferIN, bufferIN.length);
        socket.receive(ackPacket);
        if (!address.equals(ackPacket.getSocketAddress()))
            throw new IOException("Puoi ricevere da un solo peer alla volta");
        final String response = new String(ackPacket.getData()).substring(0, ackPacket.getLength());
        //System.out.println("SERVER: " + response);
        if (!response.equals("OK"))
            throw new IOException("ACK non ricevuto correttamente (response: " + response + ")");
    }

    protected void sendAck() throws IOException {
        final byte[] ack = "OK".getBytes();
        final DatagramPacket ackPacket = new DatagramPacket(ack, ack.length, address);
        socket.send(ackPacket);
    }

    @Override
    public void close() {
        socket.close();
    }
}
