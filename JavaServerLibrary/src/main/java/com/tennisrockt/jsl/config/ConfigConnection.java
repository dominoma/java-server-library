package com.tennisrockt.jsl.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ecwid.consul.v1.ConsulClient;

public class ConfigConnection {

	private static ConsulClient consulClient;
	
	private final Logger logger = LoggerFactory.getLogger(ConfigConnection.class);
	
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
		logger.info("Connecting to Consul...");
		consulClient = new ConsulClient(consulUrl);
		logger.info("Connected.");
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
