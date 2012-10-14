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
package de.dsi8.dsi8acl.connection.model;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.util.InetAddressUtils;

import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.net.UrlQuerySanitizer.ParameterValuePair;
import android.util.Log;

/**
 * Data object that contains the information for connecting to the Host.
 * 
 * The instances of this class are immutable.
 * @author sven
 *
 */
public class ConnectionParameter {
	
	/**
	 * Used as Log Tag.
	 * @see Log
	 */
	private static final String LOG_TAG = "ConnectionParameter";
	
	private static final String PROTOCOL_KEY = "protocol";
	private static final String HOST_KEY = "host";
	private static final String PORT_KEY = "port";
	private static final String PASSWORD_KEY = "password";
	
	public static final String TCP_PROTOCOL = "tcp";
	public static final String RFCOMM_PROTOCOL = "rfcomm";
	
	// TODO: comment
	private static AbstractCommunicationConfiguration staticConfig;
	
	
	private Map<String, String> parameters = new HashMap<String, String>();
	
	public ConnectionParameter() {
		
	}
	
	/**
	 * Creates a new instance of {@link ConnectionParameter}
	 * @param host {@link #host}
	 * @param port {@link #port}
	 * @param password {@link #password}
	 */
	public ConnectionParameter(String protocol, String host, int port, String password) {
		setParameter(PROTOCOL_KEY, protocol);
		setParameter(HOST_KEY, host);
		setParameter(PORT_KEY, Integer.toString(port));
		setParameter(PASSWORD_KEY, password);
	}
	
	/**
	 * Builds the connection parameters from a connection URL, like it can receive in a intent.
	 * @param connectionURL connection parameters encoded in a URL
	 */
	public ConnectionParameter(String connectionURL) {
		UrlQuerySanitizer query = new UrlQuerySanitizer(connectionURL);
		
		for (ParameterValuePair entry : query.getParameterList()) {
			setParameter(entry.mParameter, entry.mValue);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public static ConnectionParameter getDefaultTCPConnectionDetails() {
		return new ConnectionParameter(TCP_PROTOCOL,
									   getLocalIpAddress(),
									   staticConfig.getDefaultPort(),
									   staticConfig.getDefaultPassword());
	}
	
	// TODO: This not here?
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
	
	public String getParameter(String key) {
		return parameters.get(key);
	}
	
	public void setParameter(String key, String value) {
		parameters.put(key, value);
	}
	
	/**
	 * IP/Hostname of the Host
	 */
	public String getHost() {
		return getParameter(HOST_KEY);
	}
	
	/**
	 * Port where the Host application listen.
	 */
	public int getPort() {
		int port = staticConfig.getDefaultPort();
		try {
			String val = getParameter(PORT_KEY);
			if(val != null) {
				port = Integer.parseInt(val);				
			}
		} catch(NumberFormatException ex) {
		}
		return port;
	}
	
	/**
	 * Password, for authentication on the host.
	 */
	public final String getPassword() {
		return getParameter(PASSWORD_KEY);
	}

	public final String getProtocol() {
		return getParameter(PROTOCOL_KEY);
	}
	
	/**
	 * Returns the IP/Hostname with port (if it's not the default port).
	 */
	@Override
	public String toString() {
		return toConnectionURL();
	}
	
	/**
	 * Returns the connection parameters as a URL
	 * that can received by client activity intent filter
	 * to connect to the host.
	 * @return Formatted {@link #URL_FORMAT}. 
	 */
	public String toConnectionURL() {
		StringBuilder builder = new StringBuilder(staticConfig.getURLBase());
		builder.append("?");
		
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			builder.append(Uri.encode(entry.getKey()) + "=" + Uri.encode(entry.getValue()) + "&");
		}
		
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}
	
	public static void setStaticCommunicationConfiguration(AbstractCommunicationConfiguration configuration) {
		staticConfig = configuration;
	}

	public static AbstractCommunicationConfiguration getStaticCommunicationConfiguration() {
		return staticConfig;
	}
}
