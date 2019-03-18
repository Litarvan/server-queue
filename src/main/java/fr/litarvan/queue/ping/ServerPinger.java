package fr.litarvan.queue.ping;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.litarvan.queue.io.StreamReader;
import fr.litarvan.queue.io.StreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerPinger
{
	private static final Logger logger = LoggerFactory.getLogger(ServerPinger.class);
	private static final Gson gson = new GsonBuilder().create();

	private String address;
	private int port;

	public ServerPinger(String address, int port)
	{
		this.address = address;
		this.port = port;
	}

	public ServerInfos ping() throws IOException
	{
		logger.info("Pinging Minecraft server {}:{}", address, port);
		long time = System.currentTimeMillis();

		Socket socket = new Socket(address, port);
		StreamReader in = new StreamReader(socket.getInputStream());
		StreamWriter out = new StreamWriter(socket.getOutputStream());

		sendPacket(0x00, out, writer -> { // Handshake packet
			writer.writeVarInt(-1); // Protocol version, -1 means we want to know server version
			writer.writeString("127.0.0.1"); // Address, unused
			writer.writeUnsignedShort(25565); // Port, unused
			writer.writeVarInt(1); // Next state, 1 = status
		});

		sendPacket(0x00, out, writer -> {}); // Request packet

		int length = in.readVarInt();
		byte[] packet = in.readBytes(length);

		StreamReader reader = new StreamReader(new ByteArrayInputStream(packet));
		int id = reader.readVarInt();

		if (id != 0) {
			logger.warn("Received unknown packet id {} instead of 0, pinging may fail", id);
		}

		String status = reader.readString();
		ServerInfos result = gson.fromJson(status, ServerInfos.class);

		if (result == null || result.version == null) {
			throw new IllegalStateException("Received unparsable infos from server pinging");
		}

		logger.info("Pinged successfully in {}ms", System.currentTimeMillis() - time);

		return result;
	}

	protected void sendPacket(int id, StreamWriter out, PacketWriter writer) throws IOException
	{
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		StreamWriter stream = new StreamWriter(output);

		stream.writeVarInt(id);
		writer.write(stream);

		byte[] packet = output.toByteArray();
		out.writeVarInt(packet.length);
		out.writeBytes(packet);
	}

	@FunctionalInterface
	protected interface PacketWriter
	{
		void write(StreamWriter writer) throws IOException;
	}
}
