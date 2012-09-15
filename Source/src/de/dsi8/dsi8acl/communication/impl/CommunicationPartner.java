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
package de.dsi8.dsi8acl.communication.impl;

import java.io.IOException;
import java.net.Socket;
import java.util.SortedMap;
import java.util.TreeMap;

import de.dsi8.dsi8acl.communication.contract.ICommunicationPartner;
import de.dsi8.dsi8acl.communication.contract.ICommunicationPartnerListener;
import de.dsi8.dsi8acl.communication.handler.AbstractMessageHandler;
import de.dsi8.dsi8acl.connection.contract.IRemoteConnection;
import de.dsi8.dsi8acl.connection.contract.IRemoteConnectionListener;
import de.dsi8.dsi8acl.connection.impl.TCPConnection;
import de.dsi8.dsi8acl.connection.model.Message;
import de.dsi8.dsi8acl.exception.ConnectionProblemException;
import de.dsi8.dsi8acl.exception.UnsupportedMessageException;

import android.util.Log;

public class CommunicationPartner implements ICommunicationPartner, IRemoteConnectionListener {
	
	private static int partnerCount = 0;
	
	private final int id;
	
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
	 * Used for the ServerCommunication with the Client/Host.
	 * @see IRemoteCommunication
	 */
	private final IRemoteConnection remoteCommunication;
	
	/**
	 * Contains the canonical names and the instances of all {@link AbstractMessageHandler} that 
	 * should handle {@link Message}s in the current state of this class.
	 */
	private final SortedMap<String,AbstractMessageHandler<?>> messageHandlers = 
			new TreeMap<String,AbstractMessageHandler<?>>();
	
	/**
	 * The one that that want to know, when something happen here (Most likely an Activity)
	 */
	private final ICommunicationPartnerListener listener;
	
	/**
	 * Initializes the {@link AbstractLogic}
	 * @param container A {@link IDependencyContainer} for dependency injection
	 */
	public CommunicationPartner(ICommunicationPartnerListener listener, Socket socket) {
		this.id = partnerCount++;
		this.listener = listener;
		this.remoteCommunication = new TCPConnection(socket, this);
	}
	
	/**
	 * This method should called, when the concrete logic is initialized.
	 * Starts the receiving of messages.
	 */
	public void initialized() {
		remoteCommunication.startMessageListener();
	}
	
	/**
	 * Called from the {@link #remoteCommunication} when a message is received.
	 * Searches for an matching {@link AbstractMessageHandler} and calls it with the message.
	 * If no matching handler is found, {@link #handleUnsupportedMessage(Message)} is called.
	 */
	@Override
	public void messageReceived(Message message) {
		String messageType = message.getClass().getCanonicalName();
		AbstractMessageHandler<?> messageHandler = messageHandlers.get(messageType);
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
	private <T extends Message> void handleMessage(Message message, AbstractMessageHandler<T> messageHandler) {
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
	 * Register a new {@link AbstractMessageHandler} that should handle messages.
	 * @param messageHandler The {@link AbstractMessageHandler} to register
	 */
	public void registerMessageHandler(AbstractMessageHandler messageHandler) {
		messageHandlers.put(messageHandler.getMessageType().getCanonicalName(), messageHandler);
	}
	
	/**
	 * Removes all registered {@link AbstractMessageHandler}s.
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
			//Log.d(LOG_TAG, message.getClass().getSimpleName()); // TODO: rm Debug
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

	@Override
	public int getId() {
		return id;
	}
	
	private int state;

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
