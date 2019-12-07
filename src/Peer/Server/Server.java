package Peer.Server;

import Peer.Common.Downloader;
import Peer.Common.Uploader;
import Peer.Common.Binary.BinaryFileDownloader;
import Peer.Common.Binary.BinaryFileUploader;
import Peer.Common.Command;
import Peer.Common.Text.TextFileDownloader;
import Peer.Common.Text.TextFileUploader;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    private DatagramSocket socket;

    public Server(int port) throws SocketException {
        socket = new DatagramSocket(port);
        //socket.setSoTimeout(2000);
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                byte[] bufferIN = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(bufferIN, bufferIN.length);

                socket.receive(receivePacket);

                //Contenuto pacchetto:
                // 1. Server Upload
                // 2. Server Downloaad
                // 3. Server binary Upload
                // 4. Server binary Downloaad
                final byte com = bufferIN[0];
                final Command comando = Command.fromBytes(com).getOpposite();

                final String fileName = new String(receivePacket.getData()).substring(1, receivePacket.getLength());
                final SocketAddress address = receivePacket.getSocketAddress();
                switch (comando) {
                    case UploadTextFile:
                        final Uploader uploader1 = new TextFileUploader(socket, address);
                        uploader1.uploadFile(fileName);
                        break;
                    case DownloadTextFile:
                        final Downloader downloader1 = new TextFileDownloader(socket, address);
                        downloader1.downloadFile("uploaded/" + fileName);
                        break;
                    case UploadBinaryFile:
                        final Uploader uploader2 = new BinaryFileUploader(socket, address);
                        uploader2.uploadFile(fileName);
                        break;
                    case DownloadBinaryFile:
                        final Downloader downloader2 = new BinaryFileDownloader(socket, address);
                        downloader2.downloadFile("uploaded/" + fileName);
                        break;
                    case GetAvailableFiles:
                        throw new NotImplementedException();
//                        final List<String> files = listFilesAndFolders("share");
//                        for (String s : files) {
//                            final DatagramPacket sendPacket = new DatagramPacket(s.getBytes(), s.length(), address);
//                            socket.send(sendPacket);
//                        }
//                        final DatagramPacket sendPacket = new DatagramPacket(new byte[]{0}, 1, address);
//                        socket.send(sendPacket);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * List all the files and folders from a directory
     *
     * @param directoryName to be listed
     */
    private List<String> listFilesAndFolders(String directoryName) {
        final File directory = new File(directoryName); //get all the files from a directory//
        final File[] fList = directory.listFiles();
        final List<String> lista = new ArrayList<>();
        if (fList != null) {
            for (File file : fList) {
                if (file.isDirectory())
                    lista.add(file.getName() + "; DIRECTORY");
                else if (file.isFile())
                    lista.add(file.getName() + "; FILE");
            }
        }
        return lista;
    }

    /**
     * List all the files under a directory
     *
     * @param directoryName to be listed
     */
    private void listFiles(String directoryName) {
        File directory = new File(directoryName); //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                System.out.println(file.getName());
            }
        }
    }

    /**
     * List all the folder under a directory
     *
     * @param directoryName to be listed
     */
    private void listFolders(String directoryName) {
        File directory = new File(directoryName); //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isDirectory()) {
                System.out.println(file.getName());
            }
        }
    }

    /**
     * List all files from a directory and its subdirectories
     *
     * @param directoryName to be listed
     */
    private void listFilesAndFilesSubDirectories(String directoryName) {
        File directory = new File(directoryName); //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                System.out.println(file.getAbsolutePath());
            } else if (file.isDirectory()) {
                listFilesAndFilesSubDirectories(file.getAbsolutePath());
            }
        }
    }


    @Override
    public void interrupt() {
        super.interrupt();
        socket.close();
    }
}
