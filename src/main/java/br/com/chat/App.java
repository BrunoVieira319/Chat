package br.com.chat;

import java.util.Scanner;

import br.com.chat.domain.User;

public class App {
	@SuppressWarnings({ "resource", "unused" })
	public static void main(String[] args) {
		
		System.out.println("Digite seu nickname");
		User user = new User(new Scanner(System.in).nextLine());

	}
}
