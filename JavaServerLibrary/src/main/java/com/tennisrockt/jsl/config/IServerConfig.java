package com.tennisrockt.jsl.config;

import com.tennisrockt.jsl.exceptions.ServerException;

public interface IServerConfig {
	
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
