package com.tennisrockt.jsl.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.primitives.Ints;

public class PropertyPath {

	public static final String DEFAULT_SEPERATOR = "::";
	
	public static <T> T getProperty(Class<T> clazz, String path, Map<String, ? extends Object> map) {
		return new PropertyPath(path).getRefProperty(clazz, map);
	}
	public static <T> T getProperty(Class<T> clazz, String path, List<? extends Object> list) {
		return new PropertyPath(path).getRefProperty(clazz, list);
	}
	public static <T> void setProperty(String path, Map<String, ? extends Object> map, T value) {
		new PropertyPath(path).setRefProperty(map, value);
	}
	public static <T> void setProperty(String path, List<? extends Object> list, T value) {
		new PropertyPath(path).setRefProperty(list, value);
	}
	
	private final String path;
	private final List<String> steps;
	private final String seperator;
	
	public PropertyPath(String path, String seperator) {
		this.path = path;
		this.seperator = seperator;
		this.steps = Arrays.asList(path.split(this.seperator));
	}
	public PropertyPath(String path) {
		this(path, DEFAULT_SEPERATOR);
	}
	
	public String getSeperator() {
		return seperator;
	}
	public List<String> getSteps() {
		return new ArrayList<>(steps);
	}
	
	public String getLastStep() {
		return steps.get(steps.size()-1);
	}
	
	@SuppressWarnings("unchecked")
	private Object getRefObjectP(Object mapOrArray, int maxSteps) {
		for(int i=0;i<maxSteps;i++) {
			if(mapOrArray == null) {
				throw new IllegalArgumentException("Invalid path! (path: '"+path+"', property '"+steps.get(i-1)+"' doesn't exist)");
			}
			else if(mapOrArray instanceof Map) {
				mapOrArray = ((Map<Object, Object>)mapOrArray).get(steps.get(i));
			}
			else if(mapOrArray instanceof List) {
				Integer index = Ints.tryParse(steps.get(i));
				if(index == null) {
					throw new IllegalArgumentException("Invalid path! (path: '"+path+"', property '"+steps.get(i-1)+"' is an array, but '"+steps.get(i)+"' is not an integer)");
				}
				mapOrArray = ((List<Object>)mapOrArray).get(index);
			}
			else {
				throw new IllegalArgumentException("Invalid path! (path: '"+path+"', property '"+steps.get(i)+"' doesn't exist)");
			}
		}
		return mapOrArray;
	}
	public Object getRefObject(Map<String, ? extends Object> map, int maxSteps) {
		return getRefObjectP(map, maxSteps);
	}
	public Object getRefObject(List<? extends Object> list, int maxSteps) {
		return getRefObjectP(list, maxSteps);
	}
	
	@SuppressWarnings("unchecked")
	private <T> T getRefObjectP(Class<T> clazz, Object mapOrArray, int maxSteps) {
		Object prop = getRefObjectP(mapOrArray, maxSteps);
		if(prop == null) {
			throw new IllegalArgumentException("Invalid path! (path: '"+path+"')");
		}
		if(clazz.isAssignableFrom(prop.getClass())) {
			return (T) prop;
		}
		else {
			throw new IllegalArgumentException("Referenced property is not of type '"+clazz.getSimpleName()+"'!");
		}
	}
	
	public <T> T getRefObject(Class<T> clazz, Map<String, ? extends Object> map, int maxSteps) {
		return getRefObjectP(clazz, map, maxSteps);
	}
	public <T> T getRefObject(Class<T> clazz, List<? extends Object> list, int maxSteps) {
		return getRefObjectP(clazz, list, maxSteps);
	}
	
	private Object getRefObject(Object mapOrArray) {
		return getRefObjectP(mapOrArray, steps.size()-1);
	}
	
	private <T> T getRefObjectP(Class<T> clazz, Object mapOrArray) {
		return getRefObjectP(clazz, mapOrArray, steps.size()-1);
	}
	
	public <T> T getRefObject(Class<T> clazz, Map<String, ? extends Object> map) {
		return getRefObjectP(clazz, map);
	}
	public <T> T getRefObject(Class<T> clazz, List<? extends Object> list) {
		return getRefObjectP(clazz, list);
	}
	
	private <T> T getRefPropertyP(Class<T> clazz, Object mapOrArray) {
		return getRefObjectP(clazz, mapOrArray, steps.size());
	}
	
	public <T> T getRefProperty(Class<T> clazz, Map<String, ? extends Object> map) {
		return getRefPropertyP(clazz, map);
	}
	public <T> T getRefProperty(Class<T> clazz, List<? extends Object> list) {
		return getRefPropertyP(clazz, list);
	}
	
	@SuppressWarnings("unchecked")
	private void setRefPropertyP(Object mapOrArray, Object value) {
		Object obj = getRefObject(mapOrArray);
		if(obj == null) {
			throw new IllegalArgumentException("Invalid path! (path: '"+path+"')");
		}
		else if(obj instanceof Map) {
			((Map<Object, Object>)obj).put(getLastStep(), value);
		}
		else if(obj instanceof List) {
			Integer index = Ints.tryParse(getLastStep());
			if(index == null) {
				throw new IllegalArgumentException("Invalid path! (path: '"+path+"', property is an array, but '"+getLastStep()+"' is not an integer)");
			}
			((List<Object>)obj).set(index, value);
		}
		else {
			throw new IllegalArgumentException("Invalid path! (path: '"+path+"', property '"+getLastStep()+"' doesn't exist)");
		}
	}
	public void setRefProperty(Map<String, ? extends Object> map, Object value) {
		setRefPropertyP(map, value);
	}
	public void setRefProperty(List<? extends Object> list, Object value) {
		setRefPropertyP(list, value);
	}
	
}