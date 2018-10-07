package com.tennisrockt.jsl.config;

import com.tennisrockt.jsl.exceptions.ServerException;

public class ConfigEntry {
	
	private final ConfigConnection connection;
	private final String keyName;
	private final String defaultValue;
	
	
	
	public ConfigEntry(ConfigConnection connection, String keyName, String defaultValue) {
		this.connection = connection;
		this.keyName = keyName;
		this.defaultValue = defaultValue;
	}



	public String value() throws ServerException {
		if(connection.useDefaultValues()) {
			return defaultValue;
		}
		else {
			return connection.getValue(keyName);
		}
	}
	
	public String defaultValue() {
		return defaultValue;
	}
	
	public String key() {
		return keyName;
	}
	
}
