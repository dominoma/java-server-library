package com.tennisrockt.jsl.keymanager;

import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tennisrockt.jsl.exceptions.ServerException;
import com.tennisrockt.jsl.utils.ServerUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.SigningKeyResolverAdapter;

public class KeyManager {
	
	private final Map<String, Key> keys = new HashMap<>();
	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private final Lock readLock = readWriteLock.readLock();
	private final Lock writeLock = readWriteLock.writeLock();
	
	private final String updateUrl;
	
	public KeyManager(String updateUrl) {
		this.updateUrl = updateUrl;
	}
	
	public Key convertToRSAKey(String modulus, String exponent) throws ServerException {
		BigInteger dmodulus = new BigInteger(1, Base64.getUrlDecoder().decode(modulus));
        BigInteger dexponent = new BigInteger(1, Base64.getUrlDecoder().decode(exponent));
        try {
			return KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(dmodulus, dexponent));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			throw new ServerException(e);
		}
	}
	
	private void insertKeys(JSONArray keysJSON) throws ServerException {
		writeLock.lock();
		try {
			for(Object keyObj : keysJSON) {
				JSONObject keyJSON = (JSONObject) keyObj;
				String keyID = (String) keyJSON.get("kid");
				String modulus = (String) keyJSON.get("n");
				String exponent = (String) keyJSON.get("e");
				
				keys.put(keyID, convertToRSAKey(modulus, exponent));
			}
		}
		finally {
			writeLock.unlock();
		}
	}
	
	public void updateKeys() throws ServerException {
		try {
			URL url = new URL(updateUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			JSONArray keysJSON = (JSONArray) ServerUtils.parseJSON(con.getInputStream()).get("keys");
			insertKeys(keysJSON);
		} catch (IOException e) {
			throw new ServerException(e);
		}
	}
	
	public synchronized void refreshKeys() throws ServerException {
		keys.clear();
		updateKeys();
	}
	
	public boolean hasKey(String keyID) {
		return keys.containsKey(keyID);
	}
	
	public Key getKey(String keyID) throws ServerException {
		readLock.lock();
		try {
			if(!hasKey(keyID)) {
				updateKeys();
				if(!hasKey(keyID)) {
					throw new IllegalArgumentException("Key with id '"+keyID+"' doesn't exist!");
				}
			}
			return keys.get(keyID);
		}
		finally {
			readLock.unlock();
		}
	}
	
	public SigningKeyResolver getKeyResolver() {
		return new SigningKeyResolverAdapter() {
		    public Key resolveSigningKey(@SuppressWarnings("rawtypes") JwsHeader jwsHeader, Claims claims) {
		        try {
					return getKey(jwsHeader.getKeyId());
				} catch (ServerException e) {
					e.printStackTrace();
					return null;
				}
		    }
		};
	}
	
}
