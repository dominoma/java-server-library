package com.tennisrockt.jsl.keymanager;

import java.security.Key;

import com.tennisrockt.jsl.exceptions.ServerException;

import io.jsonwebtoken.SigningKeyResolver;

public interface IKeyManager {
	
	public String getUpdateURL();
	
	public default void updateKeys() throws ServerException {
		KeyManagerUtils.updateKeys(getUpdateURL());
	}
	
	public default void refreshKeys() throws ServerException {
		KeyManagerUtils.refreshKeys(getUpdateURL());
	}
	
	public default boolean hasKey(String keyID) {
		return KeyManagerUtils.hasKey(keyID);
	}
	
	public default Key getKey(String keyID) throws ServerException {
		return KeyManagerUtils.getKey(getUpdateURL(), keyID);
	}
	
	public default SigningKeyResolver getKeyResolver() {
		return KeyManagerUtils.getKeyResolver(getUpdateURL());
	}
	
}
