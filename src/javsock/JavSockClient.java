package javsock;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;
import java.security.PublicKey;

public class JavSockClient {

	private Socket socket;
	private ClientThread clientThread;
	private boolean connected;
	private JavSock server;
	private PublicKey publicKey;

	public JavSockClient(JavSock server, Socket socket) {
		this.server = server;
		this.socket = socket;
	}

	public void run() {
		if (clientThread == null) {
			connected = true;
			clientThread = new ClientThread();
			clientThread.run();
		}
	}

	public Socket getSocket() {
		return socket;
	}

	public void read(byte[] bytes) {

	}

	public void close() {
		try {
			server.getClients().remove(this);
			if (!socket.isClosed())
				socket.close();
			clientThread.interrupt();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class ClientThread extends Thread {
		public void run() {
			while (connected && !interrupted() && !socket.isClosed()) {
				synchronized (this) {
					try {
						System.out.println("..");
						final int bufferSize = 1024;
						final char[] buffer = new char[bufferSize];
						Reader in = new InputStreamReader(socket.getInputStream());
						StringBuilder out = new StringBuilder();
						for (;;) {
							int rsz = in.read(buffer, 0, buffer.length);
							if (rsz < 0)
								break;
							System.out.println(rsz);
							out.append(buffer, 0, rsz);
						}
						System.out.println("recv: " + out.toString());
					} catch (Exception e) {
						e.printStackTrace();
						break;
					}
				}
			}
			close();
		}
	}
}
