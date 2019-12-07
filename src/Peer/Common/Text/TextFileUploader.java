package Peer.Common.Text;

import Peer.Common.Uploader;
import Peer.Common.FileTransfer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

public class TextFileUploader extends FileTransfer implements Uploader {

    public TextFileUploader(DatagramSocket socket, SocketAddress serverAddress) {
        super(socket, serverAddress);
        //System.out.println("client pronto");
    }


    @Override
    public void uploadFile(String fileName) throws IOException {

        byte[] bufferOUT;
        final BufferedReader input = new BufferedReader(new FileReader("share/" + fileName));
        String daSpedire;
        do {
            daSpedire = input.readLine();
            if (daSpedire != null) {
                // predisposizione del messaggio da spedire
                bufferOUT = daSpedire.getBytes();
                // trasmissione del dato al server
                final DatagramPacket sendPacket = new DatagramPacket(bufferOUT, bufferOUT.length, address);
                socket.send(sendPacket);
            }
        } while (daSpedire != null);

        final byte[] endComunicationSequence = "endoffile".getBytes();
        final DatagramPacket sendPacket = new DatagramPacket(endComunicationSequence, endComunicationSequence.length, address);
        socket.send(sendPacket);

        waitForAck();
    }

}
