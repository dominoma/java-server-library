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

import org.json.JSONArray;
import org.json.JSONObject;

import com.tennisrockt.jsl.exceptions.ServerException;
import com.tennisrockt.jsl.utils.ServerUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.SigningKeyResolverAdapter;

class KeyManagerUtils {
	
	private static final Map<String, Key> keys = new HashMap<>();
	
	public static Key convertToRSAKey(String modulus, String exponent) throws ServerException {
		BigInteger dmodulus = new BigInteger(1, Base64.getUrlDecoder().decode(modulus));
        BigInteger dexponent = new BigInteger(1, Base64.getUrlDecoder().decode(exponent));
        try {
			return KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(dmodulus, dexponent));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			throw new ServerException(e);
		}
	}
	
	private static synchronized void insertKeys(JSONArray keysJSON) throws ServerException {
		for(Object keyObj : keysJSON) {
			JSONObject keyJSON = (JSONObject) keyObj;
			String keyID = (String) keyJSON.get("kid");
			String modulus = (String) keyJSON.get("n");
			String exponent = (String) keyJSON.get("e");
			
			keys.put(keyID, convertToRSAKey(modulus, exponent));
		}
	}
	
	public static void updateKeys(String updateUrl) throws ServerException {
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
	
	public static synchronized void refreshKeys(String updateUrl) throws ServerException {
		keys.clear();
		updateKeys(updateUrl);
	}
	
	public static boolean hasKey(String keyID) {
		return keys.containsKey(keyID);
	}
	
	public static synchronized Key getKey(String updateUrl, String keyID) throws ServerException {
		if(!hasKey(keyID)) {
			updateKeys(updateUrl);
			if(!hasKey(keyID)) {
				throw new IllegalArgumentException("Key with id '"+keyID+"' doesn't exist!");
			}
		}
		return keys.get(keyID);
	}
	
	public static SigningKeyResolver getKeyResolver(String updateUrl) {
		return new SigningKeyResolverAdapter() {
		    public Key resolveSigningKey(@SuppressWarnings("rawtypes") JwsHeader jwsHeader, Claims claims) {
		        try {
					return KeyManagerUtils.getKey(updateUrl, jwsHeader.getKeyId());
				} catch (ServerException e) {
					e.printStackTrace();
					return null;
				}
		    }
		};
	}
	
}
