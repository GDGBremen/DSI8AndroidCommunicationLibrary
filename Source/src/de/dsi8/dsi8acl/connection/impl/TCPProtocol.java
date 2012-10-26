package de.dsi8.dsi8acl.connection.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;

import org.apache.http.conn.util.InetAddressUtils;

import android.util.Log;

import de.dsi8.dsi8acl.connection.contract.IConnector;
import de.dsi8.dsi8acl.connection.contract.ISocket;
import de.dsi8.dsi8acl.connection.contract.ISocketProtocol;
import de.dsi8.dsi8acl.connection.model.ConnectionParameter;


public class TCPProtocol implements ISocketProtocol {

	private static final String LOG_TAG = "TCPProtocol";

	private final int defaultPort;
	
	private final String connectionParameterURLBase;
	
	public TCPProtocol(String connectionParameterURLBase, int defaultPort) {
		this.defaultPort = defaultPort;
		this.connectionParameterURLBase = connectionParameterURLBase;
	}

	@Override
	public String getProtocolName() {
		return TCP_PROTOCOL;
	}

	@Override
	public ISocket connect(ConnectionParameter parameter) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		return new TCPSocketWrapper(parameter);
	}

	@Override
	public IConnector createConnector() {
		return new TCPSocketConnector(defaultPort);
	}

	public ConnectionParameter getConnectionDetails(String password) {
		ConnectionParameter parameter = new ConnectionParameter(connectionParameterURLBase,  TCP_PROTOCOL, getLocalIpAddress(), password);
		parameter.setParameter(PORT_KEY, String.valueOf(defaultPort));
		return parameter;
	}
	
	/**
	 * Returns the first non-local IPv4 address of the device. 
	 * @return IPv4 address as String or unknown, if no address is found.
	 */
	private static String getLocalIpAddress() {
	    try {
	    	for(NetworkInterface iface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
	    		for (InetAddress address : Collections.list(iface.getInetAddresses())) {
	    			if (!address.isLoopbackAddress() && address.isSiteLocalAddress() && InetAddressUtils.isIPv4Address(address.getHostAddress())) {
	    				return address.getHostAddress().toString();
	    			}
	    		}
	    	}
	    } catch (SocketException ex) {
	        Log.e(LOG_TAG, ex.toString());
	    }
	    return "unknown";
	}
	
	/*
	TODO:
	 * Default port used by the host
	
	private static final int DEFAULT_PORT = 4254;*/
	
	
	public int getDefaultPort() {
		return defaultPort;
	}
	
	private static final String TCP_PROTOCOL = "tcp";
	
	public static final String PORT_KEY = "port";
	
	/**
	 * Port where the Host application listen.
	 * @throws IllegalArgumentException 
	 */
	public static int getPort(ConnectionParameter parameter) throws IllegalArgumentException {
		String val = parameter.getParameter(PORT_KEY);
		if(val == null) {
			throw new IllegalArgumentException("Parameter "+PORT_KEY+" was not found.");
		} else {
			try {
				return Integer.parseInt(val);
			} catch(NumberFormatException ex) {
				throw new IllegalArgumentException("'"+val+"' is not a valid port.");
			}
		}
	}
	
	
}
