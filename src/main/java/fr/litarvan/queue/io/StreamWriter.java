package fr.litarvan.queue.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class StreamWriter
{
	private OutputStream out;

	public StreamWriter(OutputStream out)
	{
		this.out = out;
	}

	public void writeBoolean(boolean value) throws IOException
	{
		writeByte(value ? (byte) 0x01 : (byte) 0x00);
	}

	public void writeByte(byte value) throws IOException
	{
		out.write(value);
	}

	public void writeUnsignedByte(int value) throws IOException
	{
		writeByte((byte) value);
	}

	public void writeShort(short value) throws IOException
	{
		writeBytes(ByteBuffer.allocate(Short.BYTES).putShort(value).array());
	}

	public void writeUnsignedShort(int value) throws IOException
	{
		writeShort((short) value);
	}

	public void writeInt(int value) throws IOException
	{
		writeBytes(ByteBuffer.allocate(Integer.BYTES).putInt(value).array());
	}

	public void writeLong(long value) throws IOException
	{
		writeBytes(ByteBuffer.allocate(Long.BYTES).putLong(value).array());
	}

	public void writeFloat(float value) throws IOException
	{
		writeBytes(ByteBuffer.allocate(Float.BYTES).putFloat(value).array());
	}

	public void writeDouble(double value) throws IOException
	{
		writeBytes(ByteBuffer.allocate(Double.BYTES).putDouble(value).array());
	}

	public void writeString(String value) throws IOException
	{
		byte[] bytes = value.getBytes();

		writeVarInt(bytes.length);
		writeBytes(bytes);
	}

	public void writeVarInt(int value) throws IOException
	{
		do
		{
			byte temp = (byte) (value & 0b01111111);

			value >>>= 7;

			if (value != 0)
			{
				temp |= 0b10000000;
			}

			writeByte(temp);
		}
		while (value != 0);
	}

	public void writeVarLong(long value) throws IOException
	{
		do
		{
			byte temp = (byte) (value & 0b01111111);

			value >>>= 7;

			if (value != 0)
			{
				temp |= 0b10000000;
			}

			writeByte(temp);
		}
		while (value != 0);
	}

	public void writeBytes(byte[] value) throws IOException
	{
		for (byte b : value)
		{
			writeByte(b);
		}
	}
}