package de.dsi8.dsi8acl.connection.impl;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import de.dsi8.dsi8acl.connection.model.ConnectionParameter;

public class WiFiChecker {
	
	private final Context context; 
	
	public WiFiChecker(Context context) {
		this.context = context;
	}

	private final static String WIFI_AP_KEY = "wifiap";
	
	public ConnectionParameter addSSIDToConnectionDetails(ConnectionParameter parameter) {
		parameter.setParameter(WIFI_AP_KEY, getSSID());
		return parameter;
	}
	
	public String getSSID() {
		WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		return manager.getConnectionInfo().getSSID();
	}
	
	public boolean inSameNetwork(ConnectionParameter parameter) {
		String currentSSID = getSSID();
		String remoteSSID = parameter.getParameter(WIFI_AP_KEY);
		return currentSSID != null && remoteSSID != null &&
			   currentSSID.equals(remoteSSID);
	}
}