package de.dsi8.dsi8acl.communication.contract;

import de.dsi8.dsi8acl.communication.impl.CommunicationPartner;
import de.dsi8.dsi8acl.exception.ConnectionProblemException;

public interface ICommunicationListener {
	void newPartner(ICommunicationPartner partner);
	void connectionLost(ICommunicationPartner partner, ConnectionProblemException ex);
	void socketListenerProblem(Exception ex);
}
