package com.tennisrockt.jsl.database;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;
import com.tennisrockt.jsl.exceptions.NotFoundException;
import com.tennisrockt.jsl.exceptions.ServerException;

public class DBCollection {
	
	private final DBConnection connection;
	private final String collectionName;
	
	DBCollection(DBConnection connection, String collectionName) {
		super();
		this.connection = connection;
		this.collectionName = collectionName;
	}

	public MongoCollection<Document> get() {
		return connection.getRawCollection(collectionName);
	}
	
	public boolean exists(ObjectId id) {
		return get().countDocuments(new Document("_id", id)) > 0;
	}

	public void insert(Document obj) throws ServerException {
		get().insertOne(obj);
	}
	
	public Document findByID(ObjectId id) {
		Document doc = get().find(new Document("_id", id)).first();
		if(doc == null) {
			throw new NotFoundException("Object with id '"+id+"' doesn't exist in '"+collectionName+"'!");
		}
		return doc;
	}

	public void updateByID(ObjectId id, Document obj) {
		if(!exists(id)) {
			throw new NotFoundException("Object with id '"+id+"' doesn't exist in '"+collectionName+"'!");
		}
		get().updateOne(new Document("_id", id), obj);
	}
	
	public void removeByID(ObjectId id) {
		if(!exists(id)) {
			throw new NotFoundException("Object with id '"+id+"' doesn't exist in '"+collectionName+"'!");
		}
		get().deleteOne(new Document("_id", id));
	}
}
