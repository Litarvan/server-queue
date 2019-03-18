package fr.litarvan.queue;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import fr.litarvan.queue.ping.ServerInfos;
import fr.litarvan.queue.ping.ServerPinger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Prend l'habitude de séparer le Main de tout le reste, comme ça si jamais tu veux réutiliser ton code pour autre chose, t'es pas obligé de prendre le code de lancement
public class Main
{
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	public static final String VERSION = "1.0.0";

	public static void main(String[] args)
	{
		int port, mcPort;
		String mcAddress;

		try {
			port = Integer.parseInt(args[0]);
			mcAddress = args[1];
			mcPort = Integer.parseInt(args[2]);
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
			System.err.println("Usage : <port> <mc server address> <mc server port>");
			return;
		}

		start(port, mcAddress, mcPort);
	}

	protected static void start(int port, String mcAddress, int mcPort)
	{
		ScheduledExecutorService updatePool = Executors.newScheduledThreadPool(3); // 3 max threads for updating queue

		logger.info("Starting Server-Queue v{}", VERSION);

		ServerPinger pinger = new ServerPinger(mcAddress, mcPort);

		try {
			ServerInfos infos = pinger.ping();
			logger.info("Bound to Minecraft server on {}:{}, version {} (protocol {}) ", mcAddress, mcPort, infos.version.name, infos.version.protocol);
		} catch (IOException e) {
			logger.error("Unable to connect to Minecraft server on {}:{}, aborting", mcAddress, mcPort);
			logger.error("Exception during ping request", e);

			return;
		}

		ClientQueue queue = new ClientQueue(pinger);
		updatePool.scheduleAtFixedRate(() -> {
			try {
				queue.update();
			} catch (IOException e) {
				logger.error("Exception during queue update", e);
			}
		}, 0, 5, TimeUnit.SECONDS); // Updating queue very 5 seconds

		ClientListener listener;

		try {
			listener = new ClientListener(port, queue);
			listener.listen();
		} catch (IOException e) {
			logger.error("Unable to start server on port {}, aborting", e);
			logger.error("Exception during server starting", e);

			System.exit(1);
		}
	}
}
