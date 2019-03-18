package fr.litarvan.queue.ping;

public class ServerInfos
{
	public final ServerVersion version;
	public final ServerPlayers players;

	public ServerInfos(ServerVersion version, ServerPlayers players)
	{
		this.version = version;
		this.players = players;
	}

	public static class ServerVersion
	{
		public final String name;
		public final String protocol;

		public ServerVersion(String name, String protocol)
		{
			this.name = name;
			this.protocol = protocol;
		}
	}

	public static class ServerPlayers
	{
		public final int online;
		public final int max;

		public ServerPlayers(int online, int max)
		{
			this.online = online;
			this.max = max;
		}
	}
}
