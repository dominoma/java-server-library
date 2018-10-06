package com.tennisrockt.jsl.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;
import com.tennisrockt.jsl.exceptions.JSONFormatException;
import com.tennisrockt.jsl.exceptions.RequestException;
import com.tennisrockt.jsl.exceptions.ServerException;

public class ServerUtils {
	public static String toStringInputStream(InputStream is) throws IOException {
		InputStreamReader reader = new InputStreamReader(is, Charsets.UTF_8);
		try {
			return CharStreams.toString(reader);
		}
		finally {
			Closeables.close(reader, true);
		}
	}

	public static JSONObject parseJSON(InputStream body) throws ServerException {
		
		try {
			return new JSONObject(toStringInputStream(body));
		} catch (JSONException e) {
			throw new JSONFormatException(e.getMessage());
		} catch (IOException e) {
			throw new ServerException(e);
		}
		
	}
	
	public static ObjectId parseObjectID(String id) throws RequestException {
		if(id == null || id.equals("")) {
			throw new RequestException("ID is missing!");
		}
		try {
			return new ObjectId(id);
		}
		catch(IllegalArgumentException e) {
			throw new RequestException("Invalid ID!");
		}
	}
	
}
