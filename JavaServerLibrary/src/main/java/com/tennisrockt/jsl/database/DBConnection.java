package com.tennisrockt.jsl.database;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.tennisrockt.jsl.exceptions.ServerException;

public class DBConnection {
	
	private MongoClient mongoClient = null;
	private final String mongoUrl;
	
	private final String dbName;
	
	public DBConnection(String mongoUrl, String dbName) {
		this.mongoUrl = mongoUrl;
		this.dbName = dbName;
	}
	
	public synchronized void refreshConnection() throws ServerException {
		try {
			close();
			mongoClient = new MongoClient(mongoUrl);
			mongoClient.getDatabase(dbName);
		} catch (Exception e) {
			throw new ServerException(e);
		}
	}
	
	private synchronized void setupConnection() throws ServerException {
		if(mongoClient == null) {
			refreshConnection();
		}
	}
	
	public synchronized void close() {
		if(mongoClient != null) {
			mongoClient.close();
			mongoClient = null;
		}
	}
	
	MongoCollection<Document> getRawCollection(String collectionName) throws ServerException {
		setupConnection();
		return mongoClient.getDatabase(dbName).getCollection(collectionName);
	}
	
	public DBCollection getCollection(String collectionName) throws ServerException {
		return new DBCollection(this, collectionName);
	}

	
}
