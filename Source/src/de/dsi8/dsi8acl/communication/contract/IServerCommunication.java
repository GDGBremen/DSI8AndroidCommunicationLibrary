package de.dsi8.dsi8acl.communication.contract;

import de.dsi8.dsi8acl.communication.impl.CommunicationPartner;
import de.dsi8.dsi8acl.connection.model.Message;

public interface IServerCommunication {
	void startListen();
	
	void close();
	
	void sendMessageToAll(Message msg);
	
	void sendMessageToAll(Message msg, CommunicationPartner ignore);
	
	void sendMessageToAll(Message msg, int state);
	
	void sendMessageToAll(Message msg, int state, CommunicationPartner ignore);
}
