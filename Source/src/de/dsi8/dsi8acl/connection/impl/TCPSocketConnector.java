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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;

import org.apache.http.conn.util.InetAddressUtils;

import de.dsi8.dsi8acl.R;
import de.dsi8.dsi8acl.common.utils.AsyncTaskResult;
import de.dsi8.dsi8acl.connection.contract.IConnector;
import de.dsi8.dsi8acl.connection.contract.IConnectorListener;

import de.dsi8.dsi8acl.connection.model.ConnectionParameter;
import android.os.AsyncTask;
import android.util.Log;

/**
 * The socket connector listens for a connecting client application.  
 * @author sven
 */
public class TCPSocketConnector extends AsyncTask<Object, Object, AsyncTaskResult<Socket>> 
							 implements IConnector {
	
	/**
	 * The {@link IHostDependencyContainer}
	 */
	private final IConnectorListener socketConnectorListener;
	
	/**
	 * Used as Log Tag.
	 * @see Log
	 */
	private static final String LOG_TAG = "TCPSocketConnector";
	
	/**
	 * The port that is used by the server socket.
	 */
	private final int port;

	private String password;
	
	/**
	 * Default Constructor 
	 */
	public TCPSocketConnector(IConnectorListener socketConnectorListener, int port, String password) {
		this.socketConnectorListener = socketConnectorListener;
		this.port = port;
		this.password = password;
	}

	/**
	 * {@inheritDoc}
	 */
	public static ConnectionParameter getDefaultConnectionDetails() {
		return new ConnectionParameter(getLocalIpAddress(),
									   ConnectionParameter.DEFAULT_PORT,
									   ConnectionParameter.DEFAULT_PASSWORD);
	}
	
	/**
	 * Returns the first non-local IPv4 address of the device. 
	 * @return IPv4 address as String or unknown, if no address is found.
	 */
	private static String getLocalIpAddress() {
	    try {
	    	for(NetworkInterface iface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
	    		for (InetAddress address : Collections.list(iface.getInetAddresses())) {
	    			if (!address.isLoopbackAddress() && InetAddressUtils.isIPv4Address(address.getHostAddress())) {
	    				return address.getHostAddress().toString();
	    			}
	    		}
	    	}
	    } catch (SocketException ex) {
	        Log.e(LOG_TAG, ex.toString());
	    }
	    return "unknown";
	}
	
	/**
	 * Retrieves the {@link #port} from the configuration.
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	/**
	 * Start to listen on the {@link ServerSocket}.
	 */
	@Override
	protected AsyncTaskResult<Socket> doInBackground(Object... params) {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			Socket socket = serverSocket.accept();
			serverSocket.close();
			return new AsyncTaskResult<Socket>(socket);
		} catch(Exception ex) {
			return new AsyncTaskResult<Socket>(ex);
		}
	}
	
	/**
	 * A connection is established or an error occurred.
	 */
	@Override
	protected void onPostExecute(AsyncTaskResult<Socket> result) {
		if(result.getError() != null) {
			socketConnectorListener.error(result.getError());
		} else {
			socketConnectorListener.connectionEstablished(new TCPConnection(result.getResult()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void listen() {
		execute((Object)null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cancel() {
		cancel(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean finishedListening() {
		return getStatus() == AsyncTask.Status.FINISHED;
	}
}