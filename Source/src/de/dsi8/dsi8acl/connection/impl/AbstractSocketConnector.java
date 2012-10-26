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

import java.net.ServerSocket;

import android.os.AsyncTask;
import android.util.Log;
import de.dsi8.dsi8acl.common.utils.AsyncTaskResult;
import de.dsi8.dsi8acl.connection.contract.IConnector;
import de.dsi8.dsi8acl.connection.contract.IConnectorListener;
import de.dsi8.dsi8acl.connection.contract.ISocket;

/**
 * The socket connector listens for a connecting client application.  
 * @author sven
 */
public abstract class AbstractSocketConnector<TSocket extends ISocket> implements IConnector {
	
	private ListenTask listenTask;
	
	/**
	 * The {@link IHostDependencyContainer}
	 */
	private IConnectorListener socketConnectorListener;
	
	/**
	 * Used as Log Tag.
	 * @see Log
	 */
	private static final String LOG_TAG = "AbstractSocketConnector";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean listen() throws IllegalStateException {
		if(socketConnectorListener == null) {
			throw new IllegalStateException("Set a IConnectorListener before!");
		}
		if(!isListening()) {
			startNewListenTask();
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cancel() {
		if(isListening()) {
			listenTask.cancel(true);
		}
	}
	
	/**
	 * Starts a new listenTask.
	 * This method don't check if there is a running task!
	 */
	private void startNewListenTask() {
		listenTask = new ListenTask();
		listenTask.execute((Object)null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isListening() {
		return !(listenTask == null || listenTask.getStatus() == AsyncTask.Status.FINISHED);
	}
	
	private class ListenTask extends AsyncTask<Object, Object, AsyncTaskResult<TSocket>> {
		/**
		 * Start to listen on the {@link ServerSocket}.
		 * @return A socket on success or a exception on fail. 
		 */
		@Override
		protected AsyncTaskResult<TSocket> doInBackground(Object... params) {
			try {
				TSocket socket = doListening();
				return new AsyncTaskResult<TSocket>(socket);
			} catch(Exception ex) {
				return new AsyncTaskResult<TSocket>(ex);
			}
		}
		
		/**
		 * A connection is established or an error occurred.
		 */
		@Override
		protected void onPostExecute(AsyncTaskResult<TSocket> result) {
			boolean newListenTask;
			
			if(result.getError() != null) {
				newListenTask = socketConnectorListener.error(result.getError());
			} else {
				SocketConnection connection = new SocketConnection(result.getResult());
				newListenTask = socketConnectorListener.connectionEstablished(connection);
			}
			
			if(newListenTask) {
				startNewListenTask();
			}
		}
	}
	
	protected abstract TSocket doListening() throws Exception;

	@Override
	public void setListener(IConnectorListener listener) throws IllegalArgumentException {
		if(listener == null) {
			throw new IllegalArgumentException("listener is null");
		}
		this.socketConnectorListener = listener;
	}
}