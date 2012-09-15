/*******************************************************************************
 * Copyright (C) 2012 Henrik Voß und Sven Nobis
 * 
 * This file is part of DSI8AndroidCommunicationLibrary
 * (https://github.com/SvenTo/DSI8AndroidCommunicationLibrary)
 * 
 * DSI8AndroidCommunicationLibrary is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ******************************************************************************/
package de.dsi8.dsi8acl.communication.contract;

import de.dsi8.dsi8acl.communication.handler.AbstractMessageHandler;
import de.dsi8.dsi8acl.connection.model.Message;

/**
 * Common access to the logic.
 * @author sven
 *
 */
public interface ICommunicationPartner {
	
	int getId();
	
	int getState();

	void setState(int state);
	
	void initialized();
	
	/**
	 * Register a new {@link AbstractMessageHandler} that should handle messages.
	 * @param messageHandlerClass {@link Class} of {@link AbstractMessageHandler} that should be added
	 * @param <T> Concrete Type of {@link AbstractMessageHandler}
	 * 
	 */
	void registerMessageHandler(AbstractMessageHandler messageHandler);
	
	/**
	 * Removes all registered {@link AbstractMessageHandler}s.
	 */
	void clearMessageHandlers();
	
	/**
	 * Close the connection.
	 */
	void close();
	
	/**
	 * Sends a message to the remote Host/Client.
	 * @param message The message
	 */
	void sendMessage(Message message);
	
	/**
	 * Will be called on an error.
	 * Closes the connection and logs the Exception {@code ex}.
	 * @param ex The error
	 */
	void handleError(Exception ex);
	
	/**
	 * Will be called on an error.
	 * Closes the connection and logs the Exception {@code ex}.
	 * @param ex The error
	 * @param logMessage Additional message for logging
	 */
	void handleError(Exception ex, String logMessage);
}
