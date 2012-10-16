package de.dsi8.dsi8acl.connection.impl;

import java.io.IOException;

import de.dsi8.dsi8acl.communication.impl.CommunicationPartner;
import de.dsi8.dsi8acl.connection.contract.IRemoteConnection;
import de.dsi8.dsi8acl.connection.contract.IRemoteConnectionListener;
import de.dsi8.dsi8acl.connection.model.Message;

public class InternalConnection implements IRemoteConnection {

	private CommunicationPartner communicationPartner;
	
	private InternalConnector connector;
	
	private IRemoteConnectionListener listener;
	
	public IRemoteConnectionListener getListener() {
		return listener;
	}

	public InternalConnection(InternalConnector connector) {
		this.connector = connector;
	}
	
	@Override
	public void startMessageListener() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendMessage(Message message) throws IOException {
	 this.connector.sendMessage(this, message);
	}

	@Override
	public void setListener(IRemoteConnectionListener listener) {
		this.listener = listener;
	}

}
