package com.tennisrockt.jsl.config;

public class ConfigEntry implements ValueSupplier<String> {
	
	private final ConfigConnection connection;
	private final String keyName;
	private final String defaultValue;
	
	
	
	public ConfigEntry(ConfigConnection connection, String keyName, String defaultValue) {
		this.connection = connection;
		this.keyName = keyName;
		this.defaultValue = defaultValue;
	}



	public String value() {
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
