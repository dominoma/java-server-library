package com.tennisrockt.jsl.openidrequest;

import org.json.JSONObject;

import com.tennisrockt.jsl.exceptions.ServerException;

public interface IOpenIdRequestManager {
	
	public String getConfigUrl();
	public String getUsername();
	public String getPassword();
	
	public default JSONObject doRequest(String url) throws ServerException {
		return doRequest(url, null);
	}
	public default JSONObject doRequest(String url, JSONObject body) throws ServerException {
		return TokensManagerUtils.doRequest(getConfigUrl(), getUsername(), getPassword(), url, body);
	}
}
