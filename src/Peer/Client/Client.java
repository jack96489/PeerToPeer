package Peer.Client;

import Peer.Common.Binary.BinaryFileDownloader;
import Peer.Common.Binary.BinaryFileUploader;
import Peer.Common.Downloader;
import Peer.Common.Text.TextFileDownloader;
import Peer.Common.Text.TextFileUploader;
import Peer.Common.Command;
import Peer.Common.Uploader;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Client extends Thread {
    private final DatagramSocket socket;
    private static final Scanner sc = new Scanner(System.in);
    //private final SocketAddress serverAddress;

    public Client(/*SocketAddress serverAddress*/) throws SocketException {
        //this.serverAddress = serverAddress;
        socket = new DatagramSocket();
        //socket.setSoTimeout(5000);  //timeout di 5 secondi
    }

    @Override
    public void run() {
        System.out.println("PEER TO PEER FILE MANAGER\n");
        int scelta;
        do {
            System.out.println("Premere:");
            System.out.println("1. Download file");
            System.out.println("2. Upload file");
            System.out.println("3. Download binary file");
            System.out.println("4. Upload binary file");
//            System.out.println("5. Get list of available files");
            System.out.println("0. Esci");
            scelta = sc.nextInt();
            sc.nextLine();  //ignoring the \n

            if (scelta <= 0 || scelta > 4)
                continue;

            final SocketAddress serverAddress = readServerAddress();
            final String fileName = getFileName();
            switch (scelta) {
                case 1:
                    try {
                        final Downloader downloader = new TextFileDownloader(socket, serverAddress);
                        sendStartPacket(fileName, serverAddress, Command.DownloadTextFile);
                        downloadFile(downloader, "downloaded/" + fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        final Uploader fileUploader = new TextFileUploader(socket, serverAddress);
                        sendStartPacket(fileName, serverAddress, Command.UploadTextFile);
                        uploadFile(fileUploader, fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    try {
                        final Downloader downloader = new BinaryFileDownloader(socket, serverAddress);
                        sendStartPacket(fileName, serverAddress, Command.DownloadBinaryFile);
                        downloadFile(downloader, "downloaded/" + fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    try {
                        final Uploader fileUploader = new BinaryFileUploader(socket, serverAddress);
                        sendStartPacket(fileName, serverAddress, Command.UploadBinaryFile);
                        uploadFile(fileUploader, fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
//                case 5:
//                    try {
//                        sendStartPacket("", serverAddress, Command.GetAvailableFiles);
//                        System.out.println("File disponibili: ");
//                        byte[] bufferIN = new byte[1024];
//                        String ricevuto;
//                        DatagramPacket receivePacket = new DatagramPacket(bufferIN, bufferIN.length);
//                        while (true) {
//                            socket.receive(receivePacket);
//                            if (receivePacket.getLength() == 1 && bufferIN[0] == 0)
//                                break;
//                            ricevuto = new String(receivePacket.getData()).substring(0, receivePacket.getLength());
//                            System.out.println(ricevuto);
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                default:
                    break;
            }
            System.out.println("Premere invio per contiuare");
            sc.nextLine();
            clearConsole();
        } while (scelta != 0 && !isInterrupted());

        socket.close();

    }

    private SocketAddress readServerAddress() {
        System.out.print("Inserisci l'indirizzo IP del server: ");
        final String IP = sc.nextLine();
        System.out.print("Inserisci la porta del server: ");
        final int porta = sc.nextInt();
        sc.nextLine();
        return new InetSocketAddress(IP, porta);
    }

    private void uploadFile(Uploader uploader, String fileName) {
        try {
            uploader.uploadFile(fileName);
            System.out.println("File caricato correttamente");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Errore durante il caricamento del file");
        }
    }


    private void downloadFile(Downloader downloader, String fileName) {
        try {
            downloader.downloadFile(fileName);
            System.out.println("File scaricato correttamente");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Errore durante lo scaricamento del file");
        }
    }

    private String getFileName() {
        System.out.print("Inserisci il nome del file: ");
        return sc.nextLine();
    }

    private void sendStartPacket(String fileName, SocketAddress serverAddress, Command command) throws IOException {
        //PACCHETTO INIZIALE
        //Primo byte: comando ->    1 in caso di download file di testo
        //                          2 in caso di upload file di testo
        //                          3 in caso di download file binari
        //                          4 in caso di upload file binari
        //Altri byte: nome del file

        final byte[] buffer = new byte[fileName.length() + 1];
        buffer[0] = command.getBytes();
        System.arraycopy(fileName.getBytes(), 0, buffer, 1, fileName.length());
        final DatagramPacket startPacket = new DatagramPacket(buffer, buffer.length, serverAddress);
        socket.send(startPacket);
    }

    private void clearConsole() {
        //Clears Screen in java
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        socket.close();
    }
}
