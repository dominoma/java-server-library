package com.tennisrockt.jsl.database;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.tennisrockt.jsl.exceptions.NotFoundException;
import com.tennisrockt.jsl.exceptions.ServerException;

class DBConnectionUtils {
	
	private static MongoClient mongoClient = null;
	
	public static final String DB_NAME = "TennisRockt";
	
	
	public static synchronized void refreshConnection(String url) throws ServerException {
		try {
			mongoClient = new MongoClient(url);
			mongoClient.getDatabase(DB_NAME);
		} catch (Exception e) {
			throw new ServerException(e);
		}
	}
	
	private static synchronized void setupConnection(String url) throws ServerException {
		if(mongoClient == null) {
			refreshConnection(url);
		}
	}
	
	public static synchronized void close() {
		if(mongoClient != null) {
			mongoClient.close();
			mongoClient = null;
		}
	}
	
	public static MongoCollection<Document> get(String url, String collectionName) throws ServerException {
		setupConnection(url);
		return mongoClient.getDatabase(DB_NAME).getCollection(collectionName);
	}
	
	public static boolean exists(String url, String collectionName, ObjectId id) throws ServerException {
		return get(url, collectionName).countDocuments(new Document("_id", id)) > 0;
	}

	public static void insert(String url, String collectionName, Document obj) throws ServerException {
		get(url, collectionName).insertOne(obj);
	}
	
	public static Document findByID(String url, String collectionName, ObjectId id) throws ServerException {
		Document doc = get(url, collectionName).find(new Document("_id", id)).first();
		if(doc == null) {
			throw new NotFoundException("Object with id '"+id+"' doesn't exist in '"+collectionName+"'!");
		}
		return doc;
	}

	public static void updateByID(String url, String collectionName, ObjectId id, Document obj) throws ServerException {
		if(!exists(url, collectionName, id)) {
			throw new NotFoundException("Object with id '"+id+"' doesn't exist in '"+collectionName+"'!");
		}
		get(url, collectionName).updateOne(new Document("_id", id), obj);
	}
	
	public static void removeByID(String url, String collectionName, ObjectId id) throws ServerException {
		if(!exists(url, collectionName, id)) {
			throw new NotFoundException("Object with id '"+id+"' doesn't exist in '"+collectionName+"'!");
		}
		get(url, collectionName).deleteOne(new Document("_id", id));
	}
}
