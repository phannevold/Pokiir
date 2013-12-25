package network;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author Petter Hannevold
 */
public class Peer {

	private BufferedOutputStream writer;
	private BufferedReader reader;

	private ArrayList<String> messageLog;
	private int lastIndexRead;

	private static final String END = "";

	public Peer(Socket socket) {
		try {
			writer = new BufferedOutputStream(socket.getOutputStream());
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		messageLog = new ArrayList<>();
		lastIndexRead = -1;

		new Thread(new Reciever()).start();
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

	private class Sender implements Runnable {

		String message;

		private Sender(String message) {
			this.message = message;
		}

		@Override
		public void run() {
			try {
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
					line = reader.readLine();
					if (END.equals(line)) {
						writeMessage(message.toString());
						message = new StringBuilder();
					} else {
						message.append(line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void writeMessage(String message) {
			messageLog.add(message);
		}
	}
}