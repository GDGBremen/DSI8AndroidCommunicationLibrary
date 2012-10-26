package de.dsi8.dsi8acl.connection.impl;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import de.dsi8.dsi8acl.connection.model.AbstractCommunicationConfiguration;
import de.dsi8.dsi8acl.connection.model.ConnectionParameter;

public class BluetoothConnector extends AbstractSocketConnector<BluetoothSocketWrapper> {

	@Override
	protected BluetoothSocketWrapper doListening() throws Exception {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		AbstractCommunicationConfiguration config = ConnectionParameter.getStaticCommunicationConfiguration();
		BluetoothServerSocket serverSocket = adapter.listenUsingInsecureRfcommWithServiceRecord(config.getName(), config.getUUID());
		BluetoothSocket socket = serverSocket.accept();
		serverSocket.close();
		return new BluetoothSocketWrapper(socket);
	}

}
