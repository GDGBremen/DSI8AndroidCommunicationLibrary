/*******************************************************************************
 * Copyright (C) 2012 Sven Nobis
 * 
 * This file is part of AndroidRCCar (http://androidrccar.sven.to)
 * 
 * AndroidRCCar is free software; you can redistribute it and/or modify
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
package de.dsi8.dsi8acl.communication.impl;

import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import de.dsi8.dsi8acl.communication.contract.ICommunicationPartner;
import de.dsi8.dsi8acl.communication.contract.ICommunicationPartnerListener;
import de.dsi8.dsi8acl.communication.handler.IFacadeForMessageHandler;
import de.dsi8.dsi8acl.communication.handler.IMessageHandler;
import de.dsi8.dsi8acl.connection.contract.IRemoteConnection;
import de.dsi8.dsi8acl.connection.contract.IRemoteConnectionListener;
import de.dsi8.dsi8acl.connection.model.Message;
import de.dsi8.dsi8acl.exception.ConnectionProblemException;
import de.dsi8.dsi8acl.exception.UnsupportedMessageException;

import android.util.Log;

public abstract class CommunicationPartner implements ICommunicationPartner, IFacadeForMessageHandler, IRemoteConnectionListener {
	/**
	 * Was closed called?
	 */
	private boolean closed = false; 
	
	/**
	 * Used as Log Tag.
	 * @see Log
	 */
	private static final String LOG_TAG = "CommunicationPartner";
	
	/**
	 * Used for the Communication with the Client/Host.
	 * @see IRemoteCommunication
	 */
	private final IRemoteConnection remoteCommunication;
	
	/**
	 * Contains the canonical names and the instances of all {@link IMessageHandler} that 
	 * should handle {@link Message}s in the current state of this class.
	 */
	private final SortedMap<String,IMessageHandler<?>> messageHandlers = 
			new TreeMap<String,IMessageHandler<?>>();
	
	/**
	 * The one that that want to know, when something happen here (Most likely an Activity)
	 */
	private final ICommunicationPartnerListener listener;
	
	/**
	 * Initializes the {@link AbstractLogic}
	 * @param container A {@link IDependencyContainer} for dependency injection
	 */
	public CommunicationPartner(ICommunicationPartnerListener listener, IRemoteConnection remoteConnection) {
		this.listener = listener;
		this.remoteCommunication = remoteConnection;
	}
	
	/**
	 * This method should called, when the concrete logic is initialized.
	 * Starts the receiving of messages.
	 */
	protected void initialized() {
		remoteCommunication.startMessageListener();
	}
	
	/**
	 * Called from the {@link #remoteCommunication} when a message is received.
	 * Searches for an matching {@link IMessageHandler} and calls it with the message.
	 * If no matching handler is found, {@link #handleUnsupportedMessage(Message)} is called.
	 */
	@Override
	public void messageReceived(Message message) {
		String messageType = message.getClass().getCanonicalName();
		IMessageHandler<?> messageHandler = messageHandlers.get(messageType);
		if(messageHandler != null) {
			handleMessage(message, messageHandler);
		} else {
			handleUnsupportedMessage(message);
		}
	}
	
	/**
	 * Call the {@code messageHandler} with the given {@code message}. 
	 * @param <T> The type of message
	 * @param message Message to handle
	 * @param messageHandler Handler, that should handle the message
	 */
	@SuppressWarnings("unchecked") // Only called with the correct handler 
	private <T extends Message> void handleMessage(Message message, IMessageHandler<T> messageHandler) {
		try {
			messageHandler.handleMessage(this, (T)message);
		} catch(Exception e) {
			handleError(e); // TODO: Test thiz.
		}
	}
	
	/**
	 * Handles message that are not supported by the current state of the object.
	 * @param message {@link Message} that is not supported.
	 */
	private void handleUnsupportedMessage(Message message) {
		handleError(new UnsupportedMessageException(message));
	}
	
	/**
	 * Register a new {@link IMessageHandler} that should handle messages.
	 * @param messageHandler The {@link IMessageHandler} to register
	 */
	public void registerMessageHandler(IMessageHandler messageHandler) {
		messageHandlers.put(messageHandler.getMessageType().getCanonicalName(), messageHandler);
	}
	
	/**
	 * Removes all registered {@link IMessageHandler}s.
	 */
	@Override
	public void clearMessageHandlers() {
		messageHandlers.clear();
	}

	/**
	 * Called from the {@link #remoteCommunication} when a problem with the exception occurs.
	 * @param ex The Problem
	 */
	@Override
	public void connectionProblem(Exception ex) {
		handleError(ex, "connectionProblem");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		try {
			closed = true;
			// The connections should closed, so no we expect no messages anymore:
			messageHandlers.clear();
			remoteCommunication.close(); // TODO: Implicit tested this?
			
		} catch(IOException ex) {
			Log.w(LOG_TAG, "close", ex);
		}
	}
	
	/**
	 * Sends a message to the {@link #remoteCommunication}.
	 * @param message The message
	 */
	@Override
	public void sendMessage(Message message) {
		try {
			Log.d(LOG_TAG, message.getClass().getSimpleName()); // TODO: rm Debug
			remoteCommunication.sendMessage(message);
		}
		catch(IOException ex)
		{
			handleError(ex, "sendMessage");
		}
	}
	
	/**
	 * Will be called on an error.
	 * Closes the connection and logs the Exception {@code ex}.
	 * @param ex The error
	 */
	@Override
	public void handleError(Exception ex) {
		handleError(ex, null);
	}
	
	/**
	 * Will be called on an error.
	 * Closes the connection and logs the Exception {@code ex}.
	 * @param ex The error
	 * @param logMessage Additional message for logging
	 */
	@Override
	public void handleError(Exception ex, String logMessage) {
		Log.e(LOG_TAG, logMessage, ex);
		
		if(!closed) {
			close();
			ConnectionProblemException newEx = (logMessage == null)?
					new ConnectionProblemException(ex):
					new ConnectionProblemException(logMessage, ex);
			listener.connectionLost(this, newEx);
		}
	}
}
