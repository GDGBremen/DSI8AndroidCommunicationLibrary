package de.dsi8.dsi8acl.connection.contract;

import java.io.IOException;
import java.net.UnknownHostException;

import de.dsi8.dsi8acl.connection.model.ConnectionParameter;

public interface ISocketProtocol {
	String getProtocolName();
	ISocket connect(ConnectionParameter parameter) throws UnknownHostException, IOException;
	IConnector createConnector();
	ConnectionParameter getConnectionDetails(String password);
}
