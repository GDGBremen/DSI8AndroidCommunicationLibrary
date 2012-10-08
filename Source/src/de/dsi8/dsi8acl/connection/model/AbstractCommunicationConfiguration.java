package de.dsi8.dsi8acl.connection.model;

public abstract class AbstractCommunicationConfiguration {
	
	/**
	 * Default port used by the host
	 */
	private static final int DEFAULT_PORT = 4254; 
	
	public int getDefaultPort() {
		return DEFAULT_PORT;
	}
	
	public abstract String getURLBase();
	
	public String getDefaultPassword()
	{
		return "";
	}
}
