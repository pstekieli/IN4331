package org.tudelft.wdm.imdb.mongodb.controllers;

import java.net.UnknownHostException;

import org.tudelft.wdm.imdb.models.Movie;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * @author Alexander Overvoorde
 *
 * Controllers for the MongoDB movie APIs.
 */
public class MovieController {
    private static MongoClient mongoClient = null;
    private static DB imdbDatabase = null;
    private static DBCollection moviesCollection = null;

    private MovieController() {}

    private static void initMongoDB() {
        try {
            mongoClient = new MongoClient();
            imdbDatabase = mongoClient.getDB("imdb");
            moviesCollection = imdbDatabase.getCollection("movies");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static Movie getMovieById(long id) {
        initMongoDB();

        BasicDBObject query = new BasicDBObject("idmovies", id);
        DBObject document = moviesCollection.findOne(query);

        if (document == null) {
            return null;
        } else {
            return new Movie(
                (Integer) document.get("idmovies"), 
                (String) document.get("title"),
                (Integer) document.get("year")
            );
        }
    }
}
