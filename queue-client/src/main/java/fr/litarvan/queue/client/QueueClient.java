package fr.litarvan.queue.client;

import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;

public class QueueClient
{
	private String address;
	private int port;

	private Consumer<Integer> onPositionUpdate;
	private Runnable onConnect;

	public QueueClient(String address, int port)
	{
		this.address = address;
		this.port = port;
	}

	public Thread startInSeparatedThread()
	{
		Thread t = new Thread(() -> {
			try {
				start();
			} catch (IOException e) {
				System.err.println("Exception during queue, aborting");
				e.printStackTrace();
			}
		});
		t.start();

		return t;
	}

	public void start() throws IOException
	{
		Socket socket = new Socket(address, port);
		StreamReader in = new StreamReader(socket.getInputStream());

		while (true) {
			int position = in.readVarInt();

			if (position == -1) {
				onConnect.run();

				try {
					socket.close();
				} catch (Exception ignored) {
				}

				break;
			}

			onPositionUpdate.accept(position);
		}
	}

	public void setOnPositionUpdate(Consumer<Integer> onPositionUpdate)
	{
		this.onPositionUpdate = onPositionUpdate;
	}

	public void setOnConnect(Runnable onConnect)
	{
		this.onConnect = onConnect;
	}
}
