package com.tennisrockt.jsl.database;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.tennisrockt.jsl.config.ValueSupplier;

public class DBConnection {
	
	private MongoClient mongoClient = null;
	private final ValueSupplier<String> mongoUrl;
	
	private final String dbName;
	
	private final Logger logger = LoggerFactory.getLogger(DBConnection.class);
	
	public DBConnection(ValueSupplier<String> mongoUrl, String dbName) {
		this.mongoUrl = mongoUrl;
		this.dbName = dbName;
	}
	
	public synchronized void refreshConnection() {
		close();
		logger.info("Connecting to database...");
		mongoClient = new MongoClient(mongoUrl.value());
		mongoClient.getDatabase(dbName);
		logger.info("Connected!");
		
	}
	
	private synchronized void setupConnection() {
		if(mongoClient == null) {
			refreshConnection();
		}
	}
	
	public synchronized void close() {
		if(mongoClient != null) {
			mongoClient.close();
			mongoClient = null;
			logger.info("Connection closed.");
		}
	}
	
	MongoCollection<Document> getRawCollection(String collectionName) {
		setupConnection();
		return mongoClient.getDatabase(dbName).getCollection(collectionName);
	}
	
	public DBCollection getCollection(String collectionName) {
		return new DBCollection(this, collectionName);
	}

	
}
