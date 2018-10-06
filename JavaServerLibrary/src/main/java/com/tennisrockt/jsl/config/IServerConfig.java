package com.tennisrockt.jsl.config;

import java.net.URL;

import com.tennisrockt.jsl.exceptions.ServerException;

public interface IServerConfig {
	
	public URL getConsulUrl() throws ServerException;
	
	public default void refreshConnection() throws ServerException {
		ConfigUtils.refreshConnection(getConsulUrl());
	}
	
	public default String value() throws ServerException {
		if(ConfigUtils.useDefaultValues(getConsulUrl())) {
			return defaultValue();
		}
		else {
			return ConfigUtils.getValue(getConsulUrl(), key());
		}
	};
	
	public String key() throws ServerException;
	
	public String defaultValue() throws ServerException;
}
