package de.dsi8.dsi8acl.connection.model;

import java.util.UUID;

public abstract class AbstractCommunicationConfiguration {
	
	/**
	 * Default port used by the host
	 */
	private static final int DEFAULT_PORT = 4254; 
	
	public int getDefaultPort() {
		return DEFAULT_PORT;
	}
	
	public UUID getDefaultUUID() {
		return null; // TODO: Generate a UUID.
	}
	
	public abstract String getURLBase();
	
	public String getDefaultPassword()
	{
		return "";
	}
}
