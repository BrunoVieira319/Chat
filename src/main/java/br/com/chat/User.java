package br.com.chat;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class User implements Runnable {

	private Socket client;
	private String nickname;

	public User(String nickname) {
		setNickname(nickname);
		new Thread(this).start();
	}

	public void connectToServer () {
		try {
			client = new Socket("127.0.0.1", 12345);
			new PrintStream(client.getOutputStream()).println(nickname);
			new PrintStream(client.getOutputStream()).println(nickname + " entrou no chat!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setNickname(String nickname) {
		if (nickname.contains(" ")) {
			throw new IllegalArgumentException("Nickname não pode conter espaços");
		}
		this.nickname = nickname;
	}

	private void sendMessages() {
		new Thread(() -> {
			try {
				Scanner input = new Scanner(System.in);
				PrintStream output = new PrintStream(client.getOutputStream());

				while (input.hasNextLine()) {
					String message = checkMessage(input.nextLine());
					output.println(message);
				}

				output.close();
				input.close();
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	private String checkMessage(String message) {
		if (String.valueOf(message.charAt(0)).equals("/")) {
			if (message.startsWith("/nick ")) {
				setNickname(message.substring(6));
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
	
	public String getNickname() {
		return nickname;
	}

	@Override
	public void run() {
		connectToServer();
		sendMessages();
		receiveMessages();
	}

}
