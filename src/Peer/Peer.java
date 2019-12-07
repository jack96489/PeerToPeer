package Peer;

import Peer.Client.Client;
import Peer.Server.Server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

public class Peer {
    private final Client client;
    private final Server server;

    public Peer(int porta) throws SocketException {
        //final SocketAddress serverAddress = new InetSocketAddress("localhost", porta);
        this.client = new Client(/*serverAddress*/);
        this.server = new Server(porta);
    }

    public Peer() throws SocketException {
        //final SocketAddress serverAddress = new InetSocketAddress("localhost", 6789);
        this.client = new Client(/*serverAddress*/);
        this.server = new Server(6789);
    }

    public void start() {
        server.start();
        client.start();


        //quando si chiude il client termino il server
        try {
            client.join();
            server.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void stop() {
        client.interrupt();
        server.interrupt();
    }

}
