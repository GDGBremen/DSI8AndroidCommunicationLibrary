package de.dsi8.dsi8acl.connection.impl;

import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

public class BluetoothConnector extends AbstractSocketConnector<BluetoothSocketWrapper> {

	private final UUID uuid;
	private final String name;
	
	public BluetoothConnector(String name, UUID uuid) {
		this.name = name;
		this.uuid = uuid;
	}
	
	@Override
	protected BluetoothSocketWrapper doListening() throws Exception {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothServerSocket serverSocket = adapter.listenUsingInsecureRfcommWithServiceRecord(name, uuid);
		BluetoothSocket socket = serverSocket.accept();
		serverSocket.close();
		return new BluetoothSocketWrapper(socket);
	}

}
