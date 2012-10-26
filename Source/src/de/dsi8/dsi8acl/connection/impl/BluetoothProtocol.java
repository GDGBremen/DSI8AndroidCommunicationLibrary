package de.dsi8.dsi8acl.connection.impl;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.UUID;

import de.dsi8.dsi8acl.connection.contract.IConnector;
import de.dsi8.dsi8acl.connection.contract.ISocket;
import de.dsi8.dsi8acl.connection.contract.ISocketProtocol;
import de.dsi8.dsi8acl.connection.model.ConnectionParameter;

public class BluetoothProtocol implements ISocketProtocol {

	@Override
	public String getProtocolName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISocket connect(ConnectionParameter parameter)
			throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConnector createConnector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConnectionParameter getConnectionDetails(String password) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private UUID getUUID()
	{
		return null; // TODO: Generate a UUID.
	}
	
	private String getName()
	{
		return "Unnammed"; // TODO: Generate a UUID.
	}
	
	private static final String RFCOMM_PROTOCOL = "rfcomm";
}
