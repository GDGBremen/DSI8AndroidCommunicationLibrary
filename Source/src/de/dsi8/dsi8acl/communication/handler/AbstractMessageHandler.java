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
package de.dsi8.dsi8acl.communication.handler;

import java.lang.reflect.ParameterizedType;

import de.dsi8.dsi8acl.communication.impl.CommunicationPartner;
import de.dsi8.dsi8acl.exception.InvalidMessageException;
import de.dsi8.dsi8acl.connection.model.Message;

/**
 * A {@code AbstractMessageHandler} handles received messages to a concrete {@link Message} type.  
 * 
 * Note: The implementors of this interface must have an constructor
 * that accept an implementor of {@link AbstractLogic} as only parameter.
 * @author sven
 *
 * @param <T> The concrete {@link Message} type, that is handled by the implementor.
 */
public abstract class AbstractMessageHandler<T extends Message> {
	
	/**
	 * Get the {@link Class} of the concrete {@link Message} type.
	 * @return {@link Class}
	 */
	@SuppressWarnings("unchecked")
	public Class<T> getMessageType() {
		return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	/**
	 * Handle a received message.
	 * @param message The received message.
	 * @throws InvalidMessageException If the message have invalid data.
	 */
	public abstract void handleMessage(CommunicationPartner partner, T message) throws InvalidMessageException;
}
