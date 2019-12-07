package Peer.Common.Text;

import Peer.Common.Downloader;
import Peer.Common.FileTransfer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;

public class TextFileDownloader extends FileTransfer implements Downloader {
    private static final String endCommunicationSequence = "endoffile";

    public TextFileDownloader(DatagramSocket socket,SocketAddress serverAddress) {
        super(socket, serverAddress);
    }

    @Override
    public void downloadFile(String fileName) throws IOException {

        final File file = new File("share/" + fileName);
        file.getParentFile().mkdirs();
        final PrintWriter fileWriter = new PrintWriter(file);

        //download file
        byte[] bufferIN = new byte[1024];
        String ricevuto;
        do {
            // definizione del datagramma
            DatagramPacket receivePacket = new DatagramPacket(bufferIN, bufferIN.length);

            // attesa della ricezione dato dal client
            socket.receive(receivePacket);

            if (!address.equals(receivePacket.getSocketAddress()))
                throw new IOException("Puoi ricevere da un solo peer alla volta");

            // analisi del pacchetto ricevuto
            ricevuto = new String(receivePacket.getData()).substring(0, receivePacket.getLength());
            if (!ricevuto.equals(endCommunicationSequence)) {
                //System.out.println("RICEVUTO: " + ricevuto);
                fileWriter.println(ricevuto);
            } else {
                fileWriter.close();
                sendAck();
            }

        } while (!ricevuto.equals(endCommunicationSequence));
    }

}
