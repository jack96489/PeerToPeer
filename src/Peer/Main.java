package Peer;

import java.net.SocketException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        int porta;
        do {
            System.out.print("Inserisci la porta su cui ascoltare: ");
            porta = scanner.nextInt();
        } while (porta < 1024 || porta > 65535);

        try {
            final Peer peer = new Peer(porta);
            peer.start();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
