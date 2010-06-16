package network;

import java.net.InetAddress;
import java.util.Map;

public interface ConnectionManager
{
	public Map<InetAddress, Connection> getConnections();
}
