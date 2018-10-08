package com.tennisrockt.jsl.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.google.common.collect.Lists;
import com.tennisrockt.jsl.exceptions.ServerException;
import com.tennisrockt.jsl.utils.PropertyPath;
import com.tennisrockt.jsl.utils.ServerUtils;

public class DBQueryLoader {

	private static final Map<String, Document> aggregations = new HashMap<>();
	
	private static Document getParsedAggregation(String location, String name) {
		if(aggregations.containsKey(name)) {
			return aggregations.get(name);
		}
		else {
			InputStream in = DBQueryLoader.class.getResourceAsStream(location + name + ".json");
			Document aggregation = null;
			try {
				String json = ServerUtils.toStringInputStream(in);
				aggregation = Document.parse(json);
			} catch (IOException e) {
				throw new ServerException(e);
			}
			aggregations.put(name, aggregation);
			return aggregation;
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void setVars(Document aggregation, Object...arguments) {
		if(arguments.length%2 != 0) {
			throw new IllegalArgumentException("arguments need to be in a key->value pair");
		}
		List<Object> args = Arrays.asList(arguments);
		List<Document> vars = (List<Document>) aggregation.get("vars");
		for(Object varObj : vars) {
			Document var = (Document) varObj;
			String varName = var.getString("name");
			if(args.indexOf(varName)%2 != 0) {
				throw new IllegalArgumentException("var "+varName+" is missing!");
			}
			Object varValue = args.get(args.indexOf(varName)+1);
			for(String pathStr : (Iterable<String>)var.get("path")) {
				PropertyPath.setProperty(pathStr, aggregation, varValue);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void setImports(String location, Document aggregation) {
		List<Document> imports = (List<Document>) aggregation.get("imports");
		for(Object importObj : imports) {
			Document dbimport = (Document) importObj;
			List<Document> calls = (List<Document>) dbimport.get("calls");
			for(Object callObj : calls) {
				Document call = (Document) callObj;
				Document callvars = (Document) call.get("vars");
				List<Object> callargs = new ArrayList<>();
				callvars.forEach((k, v) -> {
					callargs.add(k);
					callargs.add(v);
				});
				List<Document> callquery = (List<Document>) getAggregation(location, dbimport.getString("name"), callargs.toArray()).get("query");
				PropertyPath path = new PropertyPath(call.getString("path"));
				
				Object refObj = path.getRefObject(Object.class, aggregation);
				if(refObj instanceof List) {
					((List<Document>)refObj).addAll(Integer.valueOf(path.getLastStep()), callquery);
				}
				else if(refObj instanceof Document){
					((Document)refObj).put(path.getLastStep(), callquery);
				}
				else {
					throw new IllegalArgumentException("Property path '"+call.getString("path")+"' doesn't lead to a JSON object/array! ("+refObj+")");
				}
			}
		}
	}
	
	public static Document getAggregation(String location, String name, Object... arguments) {
		Document aggregation = getParsedAggregation(location, name);
		if(aggregation.containsKey("vars")) {
			setVars(aggregation, arguments);
		}
		if(aggregation.containsKey("imports")) {
			setImports(location, aggregation);
		}
		return aggregation;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Document> execAggregation(String location, DBCollection collection, String name, Object... arguments) {
		Document aggregation = getAggregation(location, name, arguments);
		List<Document> pipe = (List<Document>) aggregation.get("query");
		List<Document> result = Lists.newArrayList(collection.get().aggregate(pipe));
		return result;
	}
	
}