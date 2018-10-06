package com.tennisrockt.jsl.database.builders;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tennisrockt.jsl.exceptions.RequestException;
import com.tennisrockt.jsl.exceptions.ServerException;

public class DBObjectBuilder {
	
	private final JSONObject json;
	private final Document dbobj = new Document();
	private final String scope;

	public DBObjectBuilder(JSONObject json) {
		this.json = json;
		scope = "";
	}
	
	DBObjectBuilder(JSONObject json, String scope) {
		this.json = json;
		this.scope = scope+".";
	}
	
	public Document getDBObject() {
		return dbobj;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(String key, Class<T> clazz) throws RequestException {
		checkValue(key, clazz);
		return (T) json.get(key);
	}
	
	public void checkValue(String key) throws RequestException {
		if(!json.has(key)) {
			throw new RequestException("Key '"+scope+key+"' is missing!");
		}
	}
	public void checkValue(String key, Class<?>... classTypes) throws RequestException {
		checkValue(key);
		Object obj = json.get(key);
		if(obj == null) {
			throw new RequestException("Key '"+scope+key+"' must not be null!");
		}
		List<String> classStrs = new ArrayList<>();
		for(Class<?> clazz : classTypes) {
			if(clazz.isAssignableFrom(obj.getClass())) {
				return;
			}
			classStrs.add(clazz.getSimpleName());
		}
		throw new RequestException("Key '"+scope+key+"' has the wrong type! (Type: "+obj.getClass().getSimpleName()+", Allowed: "+String.join(", ", classStrs)+")");
	}
	public void checkValue(String key, CheckCondition<Object> condition) throws RequestException {
		checkValue(key);
		try {
			condition.check(json.get(key));
		} catch(JSONException | ServerException e) {
			throw new RequestException("Error at '"+scope+"."+key+"': "+e.getMessage());
		}
	}
	public void applyValue(String key) throws RequestException {
		checkValue(key);
		dbobj.append(key, json.get(key));
	}
	public void applyValue(String key, Class<?>... classTypes) throws RequestException {
		checkValue(key, classTypes);
		dbobj.append(key, json.get(key));
	}
	public void applyValue(String key, InsertCondition<Object> condition,  InsertCallback<Object> setValue) throws RequestException {
		checkValue(key);
		try {
			if(condition == null || condition.check(json.get(key))) {
				if(setValue == null) {
					dbobj.append(key, json.get(key));
				}
				else {
					dbobj.append(key, setValue.parse(json.get(key)));
				}
			}
		} catch(JSONException | ServerException e) {
			throw new RequestException("Error at '"+scope+"."+key+"': "+e.getMessage());
		}
	}
	@SuppressWarnings("unchecked")
	public <T> void applyValue(String key, Class<T> clazz, InsertCondition<T> condition,  InsertCallback<T> setValue) throws RequestException {
		checkValue(key, clazz);
		applyValue(key,(InsertCondition<Object>) condition,(InsertCallback<Object>) setValue);
	}
	public void applyObject(String key, BuilderCallback<DBObjectBuilder> callback) throws RequestException {
		checkValue(key, JSONObject.class);
		DBObjectBuilder builder = new DBObjectBuilder((JSONObject) json.get(key), scope+key);
		callback.build(builder);
		dbobj.append(key, builder.getDBObject());
	}
	public void applyArray(String key, BuilderCallback<DBListBuilder> callback) throws RequestException {
		checkValue(key, JSONArray.class);
		DBListBuilder builder = new DBListBuilder((JSONArray) json.get(key), scope+key);
		callback.build(builder);
		dbobj.append(key, builder.getDBList());
	}
	
	public void applyValue(boolean required, String key) throws RequestException {
		if(required || json.has(key)) {
			applyValue(key);
		}
	}
	public void applyValue(boolean required, String key, Class<?>... classTypes) throws RequestException {
		if(required || json.has(key)) {
			applyValue(key, classTypes);
		}
	}
	public void applyValue(boolean required, String key, InsertCondition<Object> condition,  InsertCallback<Object> setValue) throws RequestException {
		if(required || json.has(key)) {
			applyValue(key, condition, setValue);
		}
	}
	public <T> void applyValue(boolean required, String key, Class<T> clazz, InsertCondition<T> condition,  InsertCallback<T> setValue) throws RequestException {
		if(required || json.has(key)) {
			applyValue(key, clazz, condition, setValue);
		}
	}
	public void applyObject(boolean required, String key, BuilderCallback<DBObjectBuilder> callback) throws RequestException {
		if(required || json.has(key)) {
			applyObject(key, callback);
		}
	}
	public void applyArray(boolean required, String key, BuilderCallback<DBListBuilder> callback) throws RequestException {
		if(required || json.has(key)) {
			applyArray(key, callback);
		}
	}

	public boolean containsKey(String key) {
		return json.has(key);
	}
	
}