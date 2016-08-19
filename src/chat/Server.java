package chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {

	private int port;
	private List<PrintStream> clients;
	private ServerSocket server;

	public static void main(String[] args) throws IOException {
		new Server(12345).run();
	}

	public Server(int port) {
		this.port = port;
		this.clients = new ArrayList<PrintStream>();
	}

	public void run() throws IOException {
		server = new ServerSocket(port) {
			protected void finalize() throws IOException {
				this.close();
			}
		};
		System.out.println("Porta " + port + " está aberta."); 

		while (true) {
			// aceita um novo cliente
			Socket client = server.accept();
			System.out.println("Cria uma nova conexao c/ o cliente: " + client.getInetAddress().getHostAddress());
			
			// adiciona o cliente a lista
			this.clients.add(new PrintStream(client.getOutputStream()));
			
			// cria uma nova thread para um cliente
			new Thread(new ClienteHandler(this, client.getInputStream())).start();
		}
	}

	void broadcastMessages(String msg) {
		for (PrintStream client : this.clients) {
			client.println(msg);
		}
	}
}

class ClienteHandler implements Runnable {

	private Server server;
	private InputStream client;

	public ClienteHandler(Server server, InputStream client) {
		this.server = server;
		this.client = client;
	}

	@Override
	public void run() {
		String message;
		
		// quando houver uma nova mensagem, manda para todos
		Scanner sc = new Scanner(this.client);
		while (sc.hasNextLine()) {
			message = sc.nextLine();
			server.broadcastMessages(message);
		}
		sc.close();
	}
}
