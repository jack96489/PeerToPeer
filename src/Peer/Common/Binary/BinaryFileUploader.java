package Peer.Common.Binary;

import Peer.Common.FileTransfer;
import Peer.Common.Uploader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class BinaryFileUploader extends FileTransfer implements Uploader {

    public BinaryFileUploader(DatagramSocket socket, SocketAddress address) {
        super(socket, address);
    }

    @Override
    public void uploadFile(String fileName) throws IOException {

        final File file = new File("share/" + fileName);
        final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        final int packetSize = 1024;

        //mando la lunghezza del file
        final long nPackets = (long) Math.ceil((double) file.length() / (packetSize - Long.BYTES));
        System.out.println((double) file.length() / (packetSize - Long.BYTES));
//        final String n = Integer.toString(nPackets);
        final long totalLength = file.length() + Long.BYTES * nPackets;
        byte[] bufferOUT = ByteBuffer.allocate(Long.BYTES).putLong(totalLength).array();
        final DatagramPacket packetNumber = new DatagramPacket(bufferOUT, bufferOUT.length, address);
        socket.send(packetNumber);
        System.out.println(ByteBuffer.wrap(bufferOUT).getLong());

        try {       //aspetto che il client sia pronto
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        bufferOUT = new byte[packetSize];
        DatagramPacket dp = new DatagramPacket(bufferOUT, bufferOUT.length, address);
        for (long sentPackets = 0; sentPackets < nPackets; sentPackets++) {
            if (sentPackets == nPackets - 1) {           //Se è l'ultimo è più corto
                bufferOUT = new byte[(int) (totalLength - packetSize * sentPackets)];
                dp = new DatagramPacket(bufferOUT, bufferOUT.length, address);
            }
            byte[] temp = ByteBuffer.allocate(Long.BYTES).putLong(sentPackets).array();
            System.arraycopy(temp, 0, bufferOUT, 0, temp.length);
            bis.read(bufferOUT, Long.BYTES, bufferOUT.length - Long.BYTES);
            socket.send(dp);

//            StringBuilder sb = new StringBuilder(bufferOUT.length * 2);
//            for (byte b : bufferOUT)
//                sb.append(String.format("%02x", b)).append(" ");
//
//            System.out.println(sb.toString() + "\n\n" + sentPackets);

            //Sleep di 1 ms ogni 10 pacchetti perchè altrimenti spariscono i pacchetti
            if (sentPackets % 10 == 0)
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            if (sentPackets % 1000 == 0)
                System.out.println("Inviato pacchetto " + sentPackets);
        }
        bis.close();
        //aspetto l'ack
        waitForAck();
    }
}
