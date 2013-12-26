package network;

import game.Game;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Petter Hannevold
 */
public class Peer {

	private UUID uuid;

	private BufferedOutputStream writer;
	private BufferedReader reader;

	private ArrayList<String> messageLog;
	private int lastIndexRead;

	private static final String END = "%end";

	public Peer(Socket socket) {
		try {
			writer = new BufferedOutputStream(socket.getOutputStream());
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		new Thread(new Reciever()).start();

		uuid = fetchUUID(reader);
		sendMessage(Game.me.getId().toString());

		messageLog = new ArrayList<>();
		lastIndexRead = -1;

	}

	public String getLastMessage() {
		String message = null;
		if (lastIndexRead < messageLog.size() - 1) {
			message = messageLog.get(++lastIndexRead);
		}
		return  message;
	}

	public void sendMessage(String message) {
		Sender sender = new Sender(message);
		new Thread(sender).start();
	}

	public UUID getUuid() {
		return uuid;
	}

	public boolean hasRecievedNewMessage() {
		return lastIndexRead < messageLog.size();
	}

	private UUID fetchUUID(BufferedReader reader) {
		UUID uuid = null;
		try {
			while (true) {
				if (reader.ready()) {
					String uuidString = reader.readLine();
					uuid = UUID.fromString(uuidString);
					break;
				} else {
					Thread.sleep(100);
				}
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return uuid;
	}

	private class Sender implements Runnable {

		String message;

		private Sender(String message) {
			this.message = message;
		}

		@Override
		public void run() {
			try {
				System.out.println("writing " + message);
				writer.write(message.getBytes());
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class Reciever implements Runnable {

		@Override
		public void run() {
			StringBuilder message = new StringBuilder();
			String line;
			while(true) {
				try {
					if (reader.ready()) {
						line = reader.readLine();
						System.out.println("read: " + message);
						if (END.equals(line)) {
							writeMessage(message.toString());
							message = new StringBuilder();
						} else {
							message.append(line);
						}
					} else {
						Thread.sleep(300);
					}
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		private void writeMessage(String message) {
			messageLog.add(message);
		}
	}
}