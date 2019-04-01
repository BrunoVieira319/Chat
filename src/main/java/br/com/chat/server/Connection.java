package br.com.chat.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class Connection implements Runnable {

	private Socket client;
	private String nickname;

	public Connection(Socket client) {
		new Thread(this).start();
		this.client = client;
	}

	public PrintStream getOutputClient() {
		PrintStream output;
		try {
			output = new PrintStream(client.getOutputStream());
		} catch (IOException e) {
			output = null;
			e.printStackTrace();
		}
		return output;
	}

	public Socket getClient() {
		return client;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getNickname() {
		return nickname;
	}

	@Override
	public void run() {
		System.out.println("Nova conexão estabelecida no servidor " + client.getInetAddress().getHostAddress());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((client == null) ? 0 : client.hashCode());
		result = prime * result + ((nickname == null) ? 0 : nickname.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		Connection connection = (Connection) obj;
		if (nickname == connection.getNickname()) {
			if (client.getInetAddress() == connection.getClient().getInetAddress()) {
				if (client.getPort() == connection.getClient().getPort()) {
					return true;
				}
			}
		}
		return false;
	}
}
