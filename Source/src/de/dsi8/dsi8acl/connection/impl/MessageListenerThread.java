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
package de.dsi8.dsi8acl.connection.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Iterator;

import android.os.Handler;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.dsi8.dsi8acl.connection.model.Message;

/**
 * A {@link Thread}, that wait for data on input stream and converts it to a {@link Message} and tells it to the initiator.
 * @author sven
 */
class MessageListenerThread extends Thread {
	
	/**
	 * The one, that want to know when something happen.
	 */
	private final SocketConnection initiator;

	/**
	 * Handler for posting back received messages.
	 */
	private Handler handler;
	
	/**
	 * Default constructor.
	 * @param initiator The one, that want to know when something happen.
	 * @param handler For post back messages to the {@link SocketConnection} (into the main thread).
	 */
	public MessageListenerThread(SocketConnection initiator, Handler handler) {
		this.initiator = initiator;
		this.handler = handler;
	}
	
	/**
	 * Converts the data from {@link InputStream} of {@link Socket} from
	 * JSON format into a concrete {@link Message}.
	 */
	@Override
	public void run() {
		try {
			InputStream inputStream = initiator.getSocket().getInputStream();
			ObjectMapper objectMapper = initiator.getJsonObjectMapper();
			JsonFactory factory = objectMapper.getJsonFactory();
			
			JsonParser jsonParser = factory.createJsonParser(inputStream);
			Iterator<Message> iterator = jsonParser.readValuesAs(Message.class);
			
			while (!isInterrupted() && iterator.hasNext()) {
				Message msg = iterator.next();
				messageRecived(msg);
			}
			
			if(!isInterrupted()) {
				// The stream was closed but the thread wasn't stopped. That's bad.
				throw new IOException("Unexpected close of InputStream.");
			}
		} catch (Exception e) {
			connectionProblem(e);
		}
	}
	
	/**
	 * {@link Handler#post} a received {@link Message} to the {@link #initiator} (into the main thread). 
	 * @param msg The received {@link Message}
	 */
	private void messageRecived(final Message msg) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				initiator.messageRecived(msg);
			}
		});
	}
	
	/**
	 * {@link Handler#post} to the {@link #initiator} (into the main thread)
	 * that an {@link SocketConnection#connectionProblem} occurred.
	 * @param e The Problem
	 */
	private void connectionProblem(final Exception e) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				initiator.connectionProblem(e);
			}
		});
	}
}
