package br.com.chat;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server implements Runnable {

	private List<PrintStream> clientsOutput;
	private List<Connection> clients;
	private ServerSocket server;

	public Server() {
		clientsOutput = new ArrayList<>();
		clients = new ArrayList<>();
		new Thread(this).start();
	}

	@Override
	public void run() {
		try {
			server = new ServerSocket(12345);
			System.out.println("Servidor iniciado na porta " + server.getLocalPort());

			waitConnections();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void listenToClient(Connection client) {
		new Thread(() -> {
			try {
				Scanner input = new Scanner(client.getClient().getInputStream());

				client.setNickname(input.nextLine());

				while (input.hasNextLine()) {
					String message = input.nextLine();
					message = checkMessage(message, client);
					
					if (message == null) continue; 
						
					shareMessage(client.getNickname(), message);
				}

				input.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	private String checkMessage(String message, Connection client) {
		if (message.startsWith("/")) {
			
			if (message.startsWith("/nick ")) {
				String previousNickname = client.getNickname();
				client.setNickname(message.substring(6));
				return previousNickname + " alterou seu nome para " + client.getNickname();
				
			} else if (message.startsWith("/nomes")) {
				sendUserList(client);
				return null;
				
			} else if (message.startsWith("/msg ")) {
				String[] messageParts = message.split(" ", 3);
				String targetUser = messageParts[1];
				String msg = messageParts[2];
				sendPrivateMessage(msg, targetUser, client.getNickname());
				return null;
				
			}
		}
		return message;
	}

	private void sendPrivateMessage(String msg, String targetUser, String senderUser) {
		Connection targetClient = null;
		for (Connection client : clients) {
			if (client.getNickname().equals(targetUser)) {
				targetClient = client;
				break;
			}
		}
		
		if (targetClient == null) {
			throw new IllegalArgumentException("Usuário não encontrado");
		} else {
			PrintStream output = new PrintStream(targetClient.getOutputClient());
			output.println("<" + senderUser + "> (privado) " + msg);
		}
	}

	private void sendUserList(Connection client) {
		PrintStream output = new PrintStream(client.getOutputClient());
		output.println("Usuários online no chat:  ");
		
		for (Connection user : clients) {
			output.println(user.getNickname());
		}
	}

	private void shareMessage(String nicknameClient, String message) {
		System.out.println("<" + nicknameClient + "> " + message);

		for (PrintStream client : clientsOutput) {
			client.println("<" + nicknameClient + "> " + message);
		}
	}

	private void waitConnections() throws IOException {
		while (true) {
			Connection connection = new Connection(server.accept());

			clients.add(connection);
			listenToClient(connection);
			clientsOutput.add(connection.getOutputClient());
		}
	}
}
