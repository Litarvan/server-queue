package fr.litarvan.queue.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class StreamReader
{
	private InputStream in;
	private int readAmount = 0;

	public StreamReader(byte[] bytes)
	{
		this(new ByteArrayInputStream(bytes));
	}

	public StreamReader(InputStream in)
	{
		this.in = in;
	}

	public boolean readBoolean() throws IOException
	{
		return readByte() != 0;
	}

	public byte readByte() throws IOException
	{
		int read = in.read();

		if (read == -1)
		{
			throw new EOFException("Stream closed");
		}

		readAmount++;
		return (byte) read;
	}

	public int readUnsignedByte() throws IOException
	{
		return Byte.toUnsignedInt(readByte());
	}

	public short readShort() throws IOException
	{
		return ByteBuffer.wrap(readBytes(Short.BYTES)).order(ByteOrder.BIG_ENDIAN).getShort();
	}

	public int readUnsignedShort() throws IOException
	{
		return Short.toUnsignedInt(readShort());
	}

	public int readInt() throws IOException
	{
		return ByteBuffer.wrap(readBytes(Integer.BYTES)).order(ByteOrder.BIG_ENDIAN).getInt();
	}

	public long readLong() throws IOException
	{
		return ByteBuffer.wrap(readBytes(Long.BYTES)).order(ByteOrder.BIG_ENDIAN).getLong();
	}

	public float readFloat() throws IOException
	{
		return ByteBuffer.wrap(readBytes(Float.BYTES)).order(ByteOrder.BIG_ENDIAN).getFloat();
	}

	public double readDouble() throws IOException
	{
		return ByteBuffer.wrap(readBytes(Double.BYTES)).order(ByteOrder.BIG_ENDIAN).getDouble();
	}

	public String readString() throws IOException
	{
		return new String(readBytes(readVarInt()));
	}

	public int readVarInt() throws IOException
	{
		int numRead = 0;
		int result = 0;
		byte read;

		do
		{
			read = readByte();

			int value = (read & 0b01111111);
			result |= (value << (7 * numRead));

			numRead++;

			if (numRead > 5)
			{
				throw new RuntimeException("VarInt is too big");
			}
		}
		while ((read & 0b10000000) != 0);

		return result;
	}

	public long readVarLong() throws IOException
	{
		int numRead = 0;
		long result = 0;
		byte read;

		do
		{
			read = readByte();

			int value = (read & 0b01111111);
			result |= (value << (7 * numRead));

			numRead++;

			if (numRead > 10)
			{
				throw new RuntimeException("VarInt is too big");
			}
		}
		while ((read & 0b10000000) != 0);

		return result;
	}

	public byte[] readBytes(int amount) throws IOException
	{
		byte[] bytes = new byte[amount];
		in.read(bytes);

		readAmount += amount;

		return bytes;
	}

	public int getReadAmount()
	{
		return readAmount;
	}
}