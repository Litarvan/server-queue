package fr.litarvan.queue;

import java.net.Socket;

import fr.litarvan.queue.io.StreamReader;
import fr.litarvan.queue.io.StreamWriter;

public class Client
{
	public final Socket socket;
	public final StreamReader in;
	public final StreamWriter out;
	private int position;

	public Client(Socket socket, StreamReader in, StreamWriter out)
	{
		this.socket = socket;
		this.in = in;
		this.out = out;
		this.position = 0;
	}

	public int getPosition()
	{
		return position;
	}

	public void setPosition(int position)
	{
		this.position = position;
	}

	public void close()
	{
		try {
			socket.close();
		} catch (Exception ignored) {
		}
	}
}
