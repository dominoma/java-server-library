package com.tennisrockt.jsl.keymanager;

import java.security.Key;

import com.tennisrockt.jsl.exceptions.ServerException;

import io.jsonwebtoken.SigningKeyResolver;

public interface IKeyManager {
	
	public String getUpdateURL() throws ServerException;
	
	public default void updateKeys() throws ServerException {
		KeyManagerUtils.updateKeys(getUpdateURL());
	}
	
	public default void refreshKeys() throws ServerException {
		KeyManagerUtils.refreshKeys(getUpdateURL());
	}
	
	public default boolean hasKey(String keyID) throws ServerException {
		return KeyManagerUtils.hasKey(keyID);
	}
	
	public default Key getKey(String keyID) throws ServerException {
		return KeyManagerUtils.getKey(getUpdateURL(), keyID);
	}
	
	public default SigningKeyResolver getKeyResolver() throws ServerException {
		return KeyManagerUtils.getKeyResolver(getUpdateURL());
	}
	
}
