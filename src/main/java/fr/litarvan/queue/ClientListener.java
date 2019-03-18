package fr.litarvan.queue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import fr.litarvan.queue.io.StreamReader;
import fr.litarvan.queue.io.StreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientListener
{
	private static final Logger logger = LoggerFactory.getLogger(ClientListener.class);

	private ClientQueue queue;
	private ServerSocket socket;

	public ClientListener(int port, ClientQueue queue) throws IOException
	{
		this.queue = queue;
		this.socket = new ServerSocket(port);
	}

	public void listen() throws IOException
	{
		logger.info("Listening for connections on port {}", socket.getLocalPort());

		while (true)
		{
			Socket socket = this.socket.accept();
			logger.info("New connection received from {}", socket.getInetAddress().getHostAddress());

			Client client = new Client(socket, new StreamReader(socket.getInputStream()), new StreamWriter(socket.getOutputStream()));
			queue.add(client);

			client.setPosition(queue.size());
			client.out.writeVarInt(client.getPosition());

			queue.update();
		}
	}
}
