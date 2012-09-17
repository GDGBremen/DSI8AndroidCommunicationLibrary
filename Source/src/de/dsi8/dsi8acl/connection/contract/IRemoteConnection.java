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

import java.io.Closeable;
import java.io.IOException;

import de.dsi8.dsi8acl.connection.contract.IRemoteConnectionListener;
import de.dsi8.dsi8acl.connection.model.Message;

/**
 * This interface handles the communication with the other Android device (Host or Client).
 * It converts also 
 * @author sven
 *
 */
public interface IRemoteConnection extends Closeable {

	/**
	 * Start the {@link Thread} that wait for messages from the Host/Client.
	 */
	void startMessageListener();

	/**
	 * Closes the connection to the partner and
	 * if the message listener {@link Thread} is running, it will be stopped, too.
	 * The {@link IRemoteConnectionListener#connectionProblem} callback will not executed.
	 */
	@Override
	void close() throws IOException;

	/**
	 * Encodes the {@link Message} as JSON and sends it to the partner.
	 * @param message {@link Message} to Encode
	 * @throws IOException Thrown if sending failed
	 */
	void sendMessage(Message message) throws IOException;

	
	void setListener(IRemoteConnectionListener listener);
}
