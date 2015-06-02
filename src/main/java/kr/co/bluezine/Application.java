package kr.co.bluezine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

	Vector<Chat> users = new Vector<Chat>();

	private static final Logger logger = LoggerFactory
			.getLogger(Application.class);

	public static void main(String[] args) {

		logger.debug("---------> Application Stating...");
		SpringApplication.run(Application.class, args);
		logger.debug("---------> End Application");
	}

	@Override
	public void run(String... args) throws Exception {
		logger.debug("Server Initializing...");
		ServerSocket serverSocket = new ServerSocket(65000);
		logger.debug("Server Start!");
		while (true) {
			Socket socket = serverSocket.accept();
			Chat c = new Chat(socket);
			c.start();
		}
	}

	class Chat extends Thread {
		Socket socket;
		BufferedReader reader;
		PrintWriter writer;

		public Chat(Socket socket) throws IOException {
			reader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream());
			this.socket = socket;
			users.add(this);
			logger.debug("Connected!");
		}

		@Override
		public void run() {
			String msg;
			String name = null;
			try {
				name = reader.readLine();
				while ((msg = reader.readLine()) != null) {
					logger.debug(name + " : " + msg);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			users.remove(this);
			logger.debug(name + " 퇴장");
			logger.debug("남은 호스트 : " + users.size());
		}
	}
}
