package chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	private String host;
	private int port;
	private String nickname;

	public static void main(String[] args) throws UnknownHostException, IOException {
		new Client("127.0.0.1", 12345).run();
	}

	public Client(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void run() throws UnknownHostException, IOException {
		// conecta o cliente no servidor
		Socket client = new Socket(host, port);
		System.out.println("Cliente conectado com sucesso!");

		new Thread(new ReceivedMessagesHandler(client.getInputStream())).start();

		Scanner sc = new Scanner(System.in);
		System.out.print("Insira um nickname: ");
		nickname = sc.nextLine();

		// le a mensagem do teclado e envia para o servidor
		System.out.println("Envia mensagem: ");
		PrintStream output = new PrintStream(client.getOutputStream());
		while (sc.hasNextLine()) {
			output.println(nickname + ": " + sc.nextLine());
		}
		
		output.close();
		sc.close();
		client.close();
	}
}

class ReceivedMessagesHandler implements Runnable {

	private InputStream server;

	public ReceivedMessagesHandler(InputStream server) {
		this.server = server;
	}

	@Override
	public void run() {
		// recebe a mensagem do servidor e printa na tela
		Scanner s = new Scanner(server);
		while (s.hasNextLine()) {
			System.out.println(s.nextLine());
		}
		s.close();
	}
}
