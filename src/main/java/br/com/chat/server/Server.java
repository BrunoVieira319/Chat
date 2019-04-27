package br.com.chat.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {

	private List<PrintStream> clientOutputs;
	private List<Connection> clients;
	private ServerSocket server;

	public Server() {
		clientOutputs = new ArrayList<>();
		clients = new ArrayList<>();
		run();
	}

	private void run() {
		try {
			server = new ServerSocket(12345);
			System.out.println("Servidor iniciado na porta " + server.getLocalPort());

			waitConnections();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void waitConnections() throws IOException {
		while (true) {
			Connection connection = new Connection(server.accept());

			clients.add(connection);
			clientOutputs.add(connection.getOutputClient());
			listenToClient(connection);
		}
	}

	private void listenToClient(Connection client) {
		new Thread(() -> {
			try {
				Scanner input = new Scanner(client.getClient().getInputStream());
				
				String nickName = input.nextLine();
				client.setNickname(nickName);
				notifyUsersThatHasNewUserConnected(nickName, input);

				while (input.hasNextLine()) {
					String message = input.nextLine();
					message = checkMessage(message, client);

					if (message.isEmpty()) continue;

					shareMessage(client.getNickname(), message);
				}
				input.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	private void notifyUsersThatHasNewUserConnected(String nickName, Scanner input) {
		String newUserMsg = nickName + " entrou no chat!";
		shareMessage("Server", newUserMsg);
	}

	private String checkMessage(String message, Connection client) {
		if (message.startsWith("/")) {

			if (message.startsWith("/nick ")) {
				String previousNickname = client.getNickname();
				client.setNickname(message.substring(6));
				return previousNickname + " alterou seu nome para " + client.getNickname();

			} else if (message.equals("/nomes")) {
				sendUserList(client);
				return "";

			} else if (message.startsWith("/msg ")) {
				String[] messageParts = message.split(" ", 3);
				String targetUser = messageParts[1];
				String msg = messageParts[2];
				sendPrivateMessage(msg, targetUser, client);
				return "";

			} else if (message.equals("/sair")) {
				new Thread(() -> {
					try {
						for (int i = 0; i < 10; i++) {
							Thread.sleep(350);
							if (client.getClient().isClosed()) {
								shareMessage("Server", client.getNickname() + " saiu do chat!");
								clients.remove(client);
								break;
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}).start();
				return "";

			} else {
				new PrintStream(client.getOutputClient()).println("Comando inexistente!");
				return "";
			}
		}
		return message;
	}

	private void sendUserList(Connection client) {
		PrintStream output = new PrintStream(client.getOutputClient());
		output.println("Usuários online no chat:  ");

		for (Connection user : clients) {
			if (client.equals(user)) {
				output.println("*" + user.getNickname());
			} else {
				output.println(user.getNickname());
			}
		}
	}

	private void sendPrivateMessage(String msg, String targetUser, Connection senderUser) {
		Connection targetClient = findClientByNickname(targetUser);

		PrintStream outputSender = new PrintStream(senderUser.getOutputClient());
		try {
			if (targetClient == null) {
				throw new IllegalArgumentException("Usuário não encontrado");
			}
			PrintStream outputTarget = new PrintStream(targetClient.getOutputClient());
			outputTarget.println("<" + senderUser.getNickname() + "> (privado) " + msg);
			outputSender.println("<" + senderUser.getNickname() + "> (privado) " + msg);

		} catch (IllegalArgumentException e) {
			outputSender.println(e.getMessage());
		}
	}

	private Connection findClientByNickname(String targetUser) {
		Connection targetClient = null;
		for (Connection client : clients) {
			if (client.getNickname().equals(targetUser)) {
				targetClient = client;
				break;
			}
		}
		return targetClient;
	}

	private void shareMessage(String nicknameClient, String message) {
		System.out.println("<" + nicknameClient + "> " + message);

		for (PrintStream client : clientOutputs) {
			client.println("<" + nicknameClient + "> " + message);
		}
	}

}
