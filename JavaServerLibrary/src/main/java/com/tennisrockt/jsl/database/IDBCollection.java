package com.tennisrockt.jsl.database;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;
import com.tennisrockt.jsl.exceptions.ServerException;

public interface IDBCollection {
	
	public String getCollectionName();
	public String getMongoUrl();
	
	public default MongoCollection<Document> get() throws ServerException {
		return DBConnectionUtils.get(getMongoUrl(), getCollectionName());
	}
	
	public default boolean exists(ObjectId id) throws ServerException {
		return DBConnectionUtils.exists(getMongoUrl(), getCollectionName(), id);
	}

	public default void insert(Document obj) throws ServerException {
		DBConnectionUtils.insert(getMongoUrl(), getCollectionName(), obj);
	}
	
	public default Document findByID(ObjectId id) throws ServerException {
		return DBConnectionUtils.findByID(getMongoUrl(), getCollectionName(), id);
	}

	public default void updateByID(ObjectId id, Document obj) throws ServerException {
		DBConnectionUtils.updateByID(getMongoUrl(), getCollectionName(), id, obj);
	}
	
	public default void removeByID(ObjectId id) throws ServerException {
		DBConnectionUtils.removeByID(getMongoUrl(), getCollectionName(), id);
	}
	
	public static void close() {
		DBConnectionUtils.close();
	}
	
}
