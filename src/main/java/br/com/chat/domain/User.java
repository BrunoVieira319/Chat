package br.com.chat.domain;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class User {

	private Socket client;
	private String nickname;

	public User(String nickname) {
		try {
			setNickname(nickname);
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
			setNickname("Fulano" + Math.round(Math.random() * 10000));
		}
		connectToServer();
		sendMessages();
		receiveMessages();
	}

	private void connectToServer() {
		try {
			client = new Socket("127.0.0.1", 12345);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendMessages() {
		new Thread(() -> {
			try {
				Scanner input = new Scanner(System.in);
				PrintStream output = new PrintStream(client.getOutputStream());

				// Envia o nick do usuário para o Servidor registrá-lo
				output.println(this.nickname); 

				while (input.hasNextLine()) {
					String message = checkMessage(input.nextLine().trim());
					output.println(message);
					if (message.equals("/sair")) break;
				}

				output.close();
				input.close();
				client.close();
				System.out.println("Você saiu");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	private String checkMessage(String message) {
		if (message.startsWith("/nick ")) {
			try {
				setNickname(message.substring(6));
			} catch (IllegalArgumentException e) {
				System.out.println(e.getMessage());
				return "";
			}
		}
		return message;
	}

	private void receiveMessages() {
		new Thread(() -> {
			try {
				Scanner server = new Scanner(client.getInputStream());

				while (server.hasNextLine()) {
					System.out.println(server.nextLine());
				}
				server.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	private void setNickname(String nickname) {
		if (nickname.contains(" ") || nickname.isEmpty()) {
			throw new IllegalArgumentException("Nickname inválido");
		}
		this.nickname = nickname;
	}
}
