package com.tennisrockt.jsl.database.builders;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tennisrockt.jsl.exceptions.RequestException;
import com.tennisrockt.jsl.exceptions.ServerException;

public class DBListBuilder {

	private final JSONArray json;
	private final List<Object> dblist = new ArrayList<Object>();
	private final String scope;

	public DBListBuilder(JSONArray json) {
		this.json = json;
		scope = "";
	}
	
	DBListBuilder(JSONArray json, String scope) {
		this.json = json;
		this.scope = scope;
	}
	
	public List<Object> getDBList() {
		return dblist;
	}
	
	public int size() {
		return json.length();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(int index, Class<T> clazz) throws RequestException {
		checkValue(index, clazz);
		return (T) json.get(index);
	}
	
	public void checkSize(int min, int max) throws RequestException {
		if(min < 0) {
			throw new IllegalArgumentException("Minimum is less than zero! (Value: "+String.valueOf(min)+")");
		}
		if(json.length() < min || (json.length() > max && max > 0)) {
			throw new RequestException("Array '"+scope+"' is out of bounds! (size: "+String.valueOf(json.length())+", min: "+String.valueOf(min)+", max: "+String.valueOf(max)+")");
		}
	}
	
	public void checkValue(int index) throws RequestException {
		if(index < 0) {
			throw new IllegalArgumentException("Index is less than zero! (Value: "+String.valueOf(index)+")");
		}
		if(json.length() <= index) {
			throw new RequestException("Index '"+scope+"."+String.valueOf(index)+"' is out of bounds!");
		}
	}
	public void checkValue(int index, Class<?>... classTypes) throws RequestException {
		checkValue(index);
		Object obj = json.get(index);
		if(obj == null) {
			throw new RequestException("Object at '"+scope+"."+String.valueOf(index)+"' must not be null!");
		}
		List<String> classStrs = new ArrayList<>();
		for(Class<?> clazz : classTypes) {
			if(clazz.isAssignableFrom(obj.getClass())) {
				return;
			}
			classStrs.add(clazz.getSimpleName());
		}
		throw new RequestException("Object at '"+scope+"."+String.valueOf(index)+"' has the wrong type! (Type: "+obj.getClass().getSimpleName()+", Allowed: "+String.join(", ", classStrs)+")");
	}
	public void checkValue(int index, CheckCondition<Object> condition) throws RequestException {
		checkValue(index);
		try {
			condition.check(json.get(index));
		} catch(ServerException e) {
			throw new RequestException("Error at '"+scope+"."+String.valueOf(index)+"': "+e.getMessage());
		}
	}
	@SuppressWarnings("unchecked")
	public <T> void checkValue(int index, Class<T> clazz, CheckCondition<T> condition) throws RequestException {
		checkValue(index, clazz);
		checkValue(index, (CheckCondition<Object>) condition);
	}
	public void addValue(int index) throws RequestException {
		checkValue(index);
		dblist.add(json.get(index));
	}
	public void addValue(int index, Class<?>... classTypes) throws RequestException {
		checkValue(index, classTypes);
		dblist.add(index, json.get(index));
	}
	public void addValue(int index, InsertCondition<Object> condition,  InsertCallback<Object> setValue) throws RequestException {
		checkValue(index);
		try {
			if(condition == null || condition.check(json.get(index))) {
				if(setValue == null) {
					dblist.add(index, json.get(index));
				}
				else {
					dblist.add(index, setValue.parse(json.get(index)));
				}
			}
		} catch(JSONException | ServerException e) {
			throw new RequestException("Error at '"+scope+"."+String.valueOf(index)+"': "+e.getMessage());
		}
	}
	@SuppressWarnings("unchecked")
	public <T> void addValue(int index, Class<T> clazz, InsertCondition<T> condition,  InsertCallback<T> setValue) throws RequestException {
		checkValue(index, clazz);
		addValue(index, (InsertCondition<Object>) condition, (InsertCallback<Object>) setValue);
	}
	public void addObject(int index, BuilderCallback<DBObjectBuilder> callback) throws RequestException {
		checkValue(index, JSONObject.class);
		DBObjectBuilder builder = new DBObjectBuilder((JSONObject) json.get(index), scope+"."+String.valueOf(index));
		callback.build(builder);
		dblist.add(builder.getDBObject());
	}
	public void addArray(int index, BuilderCallback<DBListBuilder> callback) throws RequestException {
		checkValue(index, JSONArray.class);
		DBListBuilder builder = new DBListBuilder((JSONArray) json.get(index), scope+"."+String.valueOf(index));
		callback.build(builder);
		dblist.add(builder.getDBList());
	}
	public void addValues() throws RequestException {
		for(int i=0;i<json.length();i++) {
			addValue(i);
		}
	}
	public void addValues(Class<?>... classTypes) throws RequestException {
		for(int i=0;i<json.length();i++) {
			addValue(i, classTypes);
		}
	}
	public void addValues(InsertCondition<Object> condition,  InsertCallback<Object> setValue) throws RequestException {
		for(int i=0;i<json.length();i++) {
			addValue(i, condition, setValue);
		}
	}
	public <T> void addValues(Class<T> clazz, InsertCondition<T> condition,  InsertCallback<T> setValue) throws RequestException {
		for(int i=0;i<json.length();i++) {
			addValue(i, clazz, condition, setValue);
		}
	}
	public void addObjects(BuilderCallback<DBObjectBuilder> callback) throws RequestException {
		for(int i=0;i<json.length();i++) {
			addObject(i, callback);
		}
	}
	public void addArrays(BuilderCallback<DBListBuilder> callback) throws RequestException {
		for(int i=0;i<json.length();i++) {
			addArray(i, callback);
		}
	}
	
	public void checkValues() throws RequestException {
		for(int i=0;i<json.length();i++) {
			checkValue(i);
		}
	}
	public void checkValues(Class<?>... classTypes) throws RequestException {
		for(int i=0;i<json.length();i++) {
			checkValue(i, classTypes);
		}
	}
	public void checkValues(CheckCondition<Object> condition) throws RequestException {
		for(int i=0;i<json.length();i++) {
			checkValue(i, condition);
		}
	}
	
}