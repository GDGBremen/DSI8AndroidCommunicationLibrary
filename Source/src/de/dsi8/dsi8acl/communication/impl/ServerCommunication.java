package de.dsi8.dsi8acl.communication.impl;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import de.dsi8.dsi8acl.communication.contract.IServerCommunication;
import de.dsi8.dsi8acl.communication.contract.ICommunicationListener;
import de.dsi8.dsi8acl.communication.contract.ICommunicationPartner;
import de.dsi8.dsi8acl.communication.contract.ICommunicationPartnerListener;
import de.dsi8.dsi8acl.connection.contract.ISocketConnector;
import de.dsi8.dsi8acl.connection.contract.ISocketConnectorListener;
import de.dsi8.dsi8acl.connection.impl.SocketConnector;
import de.dsi8.dsi8acl.connection.model.ConnectionParameter;
import de.dsi8.dsi8acl.connection.model.Message;
import de.dsi8.dsi8acl.exception.ConnectionProblemException;

public class ServerCommunication implements IServerCommunication, ICommunicationPartnerListener {
	
	private List<ICommunicationPartner> partners = new ArrayList<ICommunicationPartner>();
	private final int maxPlayers;
	private ISocketConnector connector;
	private final ICommunicationListener listener;
	
	public ServerCommunication(ICommunicationListener listener, int maxPlayer) {
		this.listener = listener;
		this.maxPlayers = maxPlayer; 
	}
	
	public void startListen() {
		if(partners.size() < maxPlayers && (connector == null || connector.finishedListening())) {
			connector = new SocketConnector(socketConnectorListener,
											ConnectionParameter.DEFAULT_PORT,
											ConnectionParameter.DEFAULT_PASSWORD);
			connector.listen();
		}
	}
	
	public void close() {
		for(ICommunicationPartner partner : partners) {
			partner.close();
		}
		if(connector != null) {
			connector.cancel();
		}
	}
	
	public void sendMessageToAll(Message msg) {
		sendMessageToAll(msg, -1, null);
	}
	
	public void sendMessageToAll(Message msg, CommunicationPartner ignore) {
		sendMessageToAll(msg, -1, ignore);
	}
	
	public void sendMessageToAll(Message msg, int state) {
		sendMessageToAll(msg, state, null);
	}
	
	public void sendMessageToAll(Message msg, int state, CommunicationPartner ignore) {
		for(ICommunicationPartner partner : partners) {
			if(partner != ignore &&
			   (state == -1 || state == partner.getState())) {
				partner.sendMessage(msg);
			}
		}
	}

	@Override
	public void connectionLost(CommunicationPartner partner,
			ConnectionProblemException ex) {
		partners.remove(partner);
		listener.connectionLost(partner, ex);
	}
	
	private final ISocketConnectorListener socketConnectorListener = new ISocketConnectorListener() {

		@Override
		public void connectionEstablished(Socket socket) {
			ICommunicationPartner partner = new CommunicationPartner(ServerCommunication.this, socket);
			partners.add(partner);
			
			listener.newPartner(partner);
			
			partner.initialized();
			
			startListen();
		}

		@Override
		public void error(Exception ex) {
			listener.socketListenerProblem(ex);
		}
		
	};
}
