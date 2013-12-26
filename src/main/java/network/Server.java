package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Petter Hannevold
 */
public class Server {

	private static ArrayList<Peer> peers;

	private static final int PORT = 53999;

	public static void listen() {

		peers = new ArrayList<>();

		new Thread(
			new Runnable() {
			@Override
			public void run() {
				try (ServerSocket serverSocket = new ServerSocket(PORT);) {

					while(true) {
						System.out.println("listening");
						Socket socket = serverSocket.accept();
						Peer peer = new Peer(socket);
						peers.add(peer);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		).start();
	}

	public static List<Peer> getPeers() {
		return peers;
	}
}
