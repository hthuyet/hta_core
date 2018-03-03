/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hta.ws.mongo;

import com.hta.ws.common.Properties;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author thuyetlv
 */
public class ConnectToMongo {

    private final Logger logger = Logger.getLogger(ConnectToMongo.class);

    static ConnectToMongo _instance;

    public synchronized static ConnectToMongo getInstace() {
        if (_instance == null) {
            _instance = new ConnectToMongo();
        }
        return _instance;
    }

    ConnectToMongo() {
        init();
    }

    MongoClient mongo;
    MongoDatabase database;
    DBCollection coll;
    MongoCollection mongoCollection;

    String col_history_ctrl;
    String col_history_irr;

    private void init() {
        String url = Properties.getMongoUrl();
        int port = Properties.getMongoPort();
        String user = Properties.getMongoUser();
        String pass = Properties.getMongoPass();
        String db = Properties.getMongoDb();
        col_history_ctrl = Properties.getMongoCollectionHis();
        col_history_irr = Properties.getMongoCollectionHisIrr();
        // Creating a Mongo client 
        mongo = new MongoClient(url, port);

        // Creating Credentials 
        MongoCredential credential;
        credential = MongoCredential.createCredential(user, db, pass.toCharArray());
        logger.info("Connected to the database successfully");

        // Accessing the database 
        database = mongo.getDatabase(db);

        logger.info("Credentials ::" + credential);

//        coll = mongo.getDB(db).getCollection(col);
        mongoCollection = database.getCollection(col_history_ctrl);
    }

    public void insertHis(Object object) {
        mongoCollection = database.getCollection(col_history_ctrl);
        mongoCollection.insertOne(object);
    }

    public void insertHis(List list) {
        mongoCollection = database.getCollection(col_history_ctrl);
        mongoCollection.bulkWrite(list);
    }

    public void insertHisIrr(Object object) {
        mongoCollection = database.getCollection(col_history_irr);
        mongoCollection.insertOne(object);
    }

    public void insertHisIrr(List list) {
        mongoCollection = database.getCollection(col_history_irr);
        mongoCollection.bulkWrite(list);
    }
}
