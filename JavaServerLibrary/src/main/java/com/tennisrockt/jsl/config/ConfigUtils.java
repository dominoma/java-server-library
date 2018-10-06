package com.tennisrockt.jsl.config;

import java.net.URL;

import com.ecwid.consul.v1.ConsulClient;
import com.tennisrockt.jsl.exceptions.ServerException;

class ConfigUtils {
	
	private static URL consulUrl = null;
	
	private static ConsulClient consulClient;
	
	public static synchronized void refreshConnection() throws ServerException {
		if(consulUrl != null) {
			try {
				consulClient = new ConsulClient(consulUrl.toExternalForm());
			} catch (Exception e) {
				throw new ServerException(e);
			}
		}
	}
	
	public static synchronized void setupConnection() throws ServerException {
		if(consulClient == null) {
			refreshConnection();
		}
	}
	
	public static URL getConsulUrl() {
		return consulUrl;
	}
	
	public static synchronized void setConsulUrl(URL url) {
		if(consulClient != null) {
			consulClient = null;
		}
		consulUrl = url;
	}
	
	public static boolean useDefaultValues() {
		return consulUrl == null;
	}
	
	public static String getValue(String keyName) throws ServerException {
		setupConnection();
		return consulClient.getKVValue(keyName).getValue().getDecodedValue();
	}
}
