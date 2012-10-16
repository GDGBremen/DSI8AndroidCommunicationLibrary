package de.dsi8.dsi8acl.connection.impl;

import java.io.IOException;

import de.dsi8.dsi8acl.connection.model.Message;


public class InternalConnector {
	private InternalConnection firstConnection = new InternalConnection(this);
	
	private InternalConnection secondConnection = new InternalConnection(this);
	
	
	public void sendMessage(InternalConnection from, Message message) throws IOException {
		if (this.firstConnection != from) {
			this.firstConnection.getListener().messageReceived(message);
		} else {
			this.secondConnection.getListener().messageReceived(message);
		}
	}


	public InternalConnection getFirstConnection() {
		return firstConnection;
	}


	public InternalConnection getSecondConnection() {
		return secondConnection;
	}
}
