package de.dsi8.dsi8acl.connection.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import de.dsi8.dsi8acl.connection.contract.ISocket;
import de.dsi8.dsi8acl.connection.model.ConnectionParameter;

public class BluetoothSocketWrapper implements ISocket {

	private final BluetoothSocket socket;
	private boolean isOpen = true;
	
	public BluetoothSocketWrapper(ConnectionParameter connectionParameter) throws IOException {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothDevice device = adapter.getRemoteDevice(connectionParameter.getHost());
		socket = device.createInsecureRfcommSocketToServiceRecord(null); // TODO: uuid
		socket.connect();
	}
	
	public BluetoothSocketWrapper(BluetoothSocket socket) {
		this.socket = socket; 
	}
	
	@Override
	public void close() throws IOException {
		socket.close();
		isOpen = false;
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
		return !isOpen; // TODO: This maybe better?
	}

}
