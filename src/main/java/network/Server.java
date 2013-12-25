package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author Petter Hannevold
 */
public class Server {

	public static void listen() {

		ArrayList<Socket> clientSockets = new ArrayList<>();
		ServerSocket serverSocket;

		try {
			serverSocket = new ServerSocket(53999);

			while(true) {
				Socket socket = serverSocket.accept();
				clientSockets.add(socket);
				Peer peer = new Peer(socket);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
