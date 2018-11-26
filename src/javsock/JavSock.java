package javsock;

import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.ArrayList;

public class JavSock {

	private int port;
	private ServerSocket socket;
	private PrivateKey privateKey;
	private PublicKey publicKey;
	private boolean running = false;
	private ClientHandler clientHandler;
	private ArrayList<JavSockClient> clients;

	public JavSock(int port) throws Exception {
		this.port = port;
		this.socket = new ServerSocket(port);
		this.clients = new ArrayList<JavSockClient>();

		// generate private/public keys
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		keyGen.initialize(1024, random);
		KeyPair pair = keyGen.generateKeyPair();
		privateKey = pair.getPrivate();
		publicKey = pair.getPublic();
		running = true;
		clientHandler = new ClientHandler();
		clientHandler.run();
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public ArrayList<JavSockClient> getClients() {
		return clients;
	}

	public ServerSocket getSocket() {
		return socket;
	}

	public int getPort() {
		return port;
	}

	public void close() {
		try {
			running = false;
			for (int i = 0; i < clients.size(); i++) {
				clients.get(i).close();
			}
			if (!socket.isClosed())
				socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class ClientHandler extends Thread {
		public void run() {
			while (running && !interrupted()) {
				synchronized (this) {
					try {
						Socket clientSocket = socket.accept();
						JavSockClient client = new JavSockClient(JavSock.this, clientSocket);
						clients.add(client);
						client.run();
						System.out.println("new conn");
					} catch (Exception e) {
						e.printStackTrace();
						close();
					}
				}
			}
		}
	}
}
