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
package de.dsi8.dsi8acl.connection.contract;

/**
 * The socket connector listens for a connecting client application.  
 * @author sven
 */
public interface IConnector {
	
	/**
	 * Start listen for a connecting client application.
	 * @return If listening was started, if not, the connector already listening.
	 * @throws IllegalStateException If no {@link IConnectorListener} was set.
	 */
	boolean listen() throws IllegalStateException;
	
	/**
	 * Cancel listening, if listening is running.
	 */
	void cancel();
	
	boolean isListening();
	
	void setListener(IConnectorListener listener) throws IllegalArgumentException;
}
