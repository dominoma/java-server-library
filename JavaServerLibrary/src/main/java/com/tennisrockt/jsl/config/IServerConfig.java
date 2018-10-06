package com.tennisrockt.jsl.config;

import java.net.URL;

import com.tennisrockt.jsl.exceptions.ServerException;

public interface IServerConfig {
	
	public static void setConsulUrl(URL url) {
		ConfigUtils.setConsulUrl(url);
	}
	
	public static URL getConsulUrl() {
		return ConfigUtils.getConsulUrl();
	}
	
	public static void refreshConnection() throws ServerException {
		ConfigUtils.refreshConnection();
	}
	
	public default String value() throws ServerException {
		if(ConfigUtils.useDefaultValues()) {
			return defaultValue();
		}
		else {
			return ConfigUtils.getValue(key());
		}
	};
	
	public String key() throws ServerException;
	
	public String defaultValue() throws ServerException;
}
