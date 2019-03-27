package br.com.chat;

import java.util.Scanner;

public class App {
	public static void main(String[] args) {
		
		System.out.println("Digite seu nickname");
		Scanner scanner = new Scanner(System.in);
		
        User user = new User(scanner.nextLine());

        
	}
}
