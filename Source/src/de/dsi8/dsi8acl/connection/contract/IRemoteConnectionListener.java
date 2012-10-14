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

import de.dsi8.dsi8acl.connection.impl.SocketConnection;
import de.dsi8.dsi8acl.connection.model.Message;

/**
 * The {@link SocketConnection} uses this listener to inform the implementor of this interface 
 * that something happens. (Usually {@link AbstractLogic})
 * @author sven
 *
 */
public interface IRemoteConnectionListener {
	
	/**
	 * Called from the {@link SocketConnection} when a message is received.
	 * @param message The message.
	 */
	void messageReceived(Message message);
	
	/**
	 * Called from the {@link SocketConnection} when a problem with the exception occurs.
	 * @param ex The Problem
	 */
	void connectionProblem(Exception ex);
}
