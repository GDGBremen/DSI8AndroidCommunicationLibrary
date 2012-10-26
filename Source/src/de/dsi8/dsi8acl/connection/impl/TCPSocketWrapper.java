package de.dsi8.dsi8acl.connection.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import de.dsi8.dsi8acl.connection.contract.ISocket;
import de.dsi8.dsi8acl.connection.model.ConnectionParameter;

public class TCPSocketWrapper implements ISocket {

	private final Socket socket;
	
	public TCPSocketWrapper(ConnectionParameter parameter) throws UnknownHostException, IOException {
		socket = new Socket(parameter.getHost(), TCPProtocol.getPort(parameter));
	}
	
	public TCPSocketWrapper(Socket socket) {
		this.socket = socket;  
	}
	
	@Override
	public void close() throws IOException {
		socket.close();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return socket.getOutputStream();
	}

	@Override
	public boolean isClosed() {
		return socket.isClosed();
	}

}
