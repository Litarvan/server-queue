package fr.litarvan.queue;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import fr.litarvan.queue.ping.ServerInfos;
import fr.litarvan.queue.ping.ServerPinger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientQueue
{
	private static final Logger logger = LoggerFactory.getLogger(ClientQueue.class);

	private Queue<Client> queue;
	private ServerPinger pinger;

	public ClientQueue(ServerPinger pinger)
	{
		this.queue = new LinkedBlockingQueue<>();
		this.pinger = pinger;
	}

	public void add(Client client)
	{
		queue.offer(client);
	}

	// Synchronized permet que si la fonction est en train d'être executée par un thread, et qu'un autre veut l'executer aussi
	// Ca va attendre que le premier thread ait fini de l'executer avant que le second puisse le faire
	public synchronized void update() throws IOException
	{
		ServerInfos infos = pinger.ping();
		logger.info("Server infos updated, current players : {}/{}", infos.players.online, infos.players.max);

		int room = infos.players.max - infos.players.online - 1; // Je laisse une marge de 1 joueur au cas où

		for (int i = 0; i < room; i++) { // For every client there is room for
			Client client = queue.poll();

			logger.info("Authorizing connection of {}", client.socket.getInetAddress().getHostAddress());

			client.out.writeVarInt(-1); // -1 means client can connect
			client.close();

			queue.forEach(c -> c.setPosition(c.getPosition() - 1)); // Every other clients goes further in queue
		}

		if (room != 0) { // If position changed, sending new position to everyone
			for (Client c : queue) {
				c.out.writeVarInt(c.getPosition());
			}

			logger.info("Remaining clients in queue : {}", queue.size());
		}
	}

	public int size()
	{
		return queue.size();
	}
}
