package com.tennisrockt.jsl.config;

import com.ecwid.consul.v1.ConsulClient;
import com.tennisrockt.jsl.exceptions.ServerException;

public class ConfigConnection {

private static ConsulClient consulClient;
	
	private String consulUrl;
	
	public ConfigConnection(String consulUrl) {
		super();
		this.consulUrl = consulUrl;
	}
	public ConfigConnection() {
		
	}

	public String getConsulUrl() {
		return consulUrl;
	}
	public void setConsulUrl(String consulUrl) {
		this.consulUrl = consulUrl;
	}
	public synchronized void refreshConnection() {
		
		try {
			consulClient = new ConsulClient(consulUrl);
		} catch (Exception e) {
			throw new ServerException(e);
		}
		
	}
	
	public synchronized void setupConnection() {
		if(consulClient == null) {
			refreshConnection();
		}
	}
	
	public boolean useDefaultValues() {
		return consulUrl == null;
	}
	
	String getValue(String keyName) {
		setupConnection();
		return consulClient.getKVValue(keyName).getValue().getDecodedValue();
	}
	
	public ConfigEntry getEntry(String keyName, String defaultValue) {
		return new ConfigEntry(this, keyName, defaultValue);
	}
	
}
