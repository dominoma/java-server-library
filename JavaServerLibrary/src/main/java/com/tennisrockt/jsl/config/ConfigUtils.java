package com.tennisrockt.jsl.config;

import java.net.URL;

import com.ecwid.consul.v1.ConsulClient;
import com.tennisrockt.jsl.exceptions.ServerException;

class ConfigUtils {
	
	
	private static ConsulClient consulClient;
	
	public static synchronized void refreshConnection(URL consulUrl) throws ServerException {
		if(consulUrl != null) {
			try {
				consulClient = new ConsulClient(consulUrl.toExternalForm());
			} catch (Exception e) {
				throw new ServerException(e);
			}
		}
	}
	
	public static synchronized void setupConnection(URL consulUrl) throws ServerException {
		if(consulClient == null) {
			refreshConnection(consulUrl);
		}
	}
	
	public static boolean useDefaultValues(URL consulUrl) {
		return consulUrl == null;
	}
	
	public static String getValue(URL consulUrl, String keyName) throws ServerException {
		setupConnection(consulUrl);
		return consulClient.getKVValue(keyName).getValue().getDecodedValue();
	}
}
