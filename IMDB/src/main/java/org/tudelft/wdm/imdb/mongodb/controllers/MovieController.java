package org.tudelft.wdm.imdb.mongodb.controllers;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.tudelft.wdm.imdb.models.Actor;
import org.tudelft.wdm.imdb.models.Movie;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
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
    private static DBCollection seriesCollection = null;
    private static DBCollection moviesGenresCollection = null;
    private static DBCollection genresCollection = null;
    private static DBCollection moviesKeywordsCollection = null;
    private static DBCollection keywordsCollection = null;
    private static DBCollection actedInCollection = null;
    private static DBCollection actorsCollection = null;

    private MovieController() {}

    private static void initMongoDB() {
        try {
            mongoClient = new MongoClient();
            imdbDatabase = mongoClient.getDB("imdb");
            moviesCollection = imdbDatabase.getCollection("movies");
            seriesCollection = imdbDatabase.getCollection("series");
            moviesGenresCollection = imdbDatabase.getCollection("movies_genres");
            genresCollection = imdbDatabase.getCollection("genres");
            moviesKeywordsCollection = imdbDatabase.getCollection("movies_keywords");
            keywordsCollection = imdbDatabase.getCollection("keywords");
            actedInCollection = imdbDatabase.getCollection("acted_in");
            actorsCollection = imdbDatabase.getCollection("actors");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    
    private static Movie createMovieObject(long id) {
    	initMongoDB();
    	
    	BasicDBObject query = new BasicDBObject("idmovies", id);
        DBObject document = moviesCollection.findOne(query);
        
        if (document == null) {
            return null;
        } else {
        	// Base data
            Movie movie = new Movie(
                (Integer) document.get("idmovies"), 
                (String) document.get("title"),
                document.get("year") != null && document.get("year") instanceof Integer ? (Integer) document.get("year") : null
            );
            
            // Add name of the series - if any
            DBObject seriesDocument = seriesCollection.findOne(new BasicDBObject("idmovies", id));
            
            if (seriesDocument != null) {
            	movie.setSeriesName((String) seriesDocument.get("name"));
            }
            
            // Add list of genre labels
            try (DBCursor genreCursor = moviesGenresCollection.find(new BasicDBObject("idmovies", id))) {
            	while (genreCursor.hasNext()) {
            		DBObject genreDocument = genresCollection.findOne(new BasicDBObject("idgenres", genreCursor.next().get("idgenres")));
            		movie.AddGenreLabel((String) genreDocument.get("genre"));
            	}
            }
            
            // Add list of keywords
            try (DBCursor keywordCursor = moviesKeywordsCollection.find(new BasicDBObject("idmovies", id))) {
            	while (keywordCursor.hasNext()) {
            		DBObject keywordDocument = keywordsCollection.findOne(new BasicDBObject("idkeywords", keywordCursor.next().get("idkeywords")));
            		movie.AddKeyword((String) keywordDocument.get("keyword"));
            	}
            }
            
            // Add list of actors and roles
            Map<Integer, Actor> actorBillings = new TreeMap<Integer, Actor>();
            try (DBCursor actorCursor = actedInCollection.find(new BasicDBObject("idmovies", id))) {
            	while (actorCursor.hasNext()) {
            		DBObject actedInDocument = actorCursor.next();
            		DBObject actorDocument = actorsCollection.findOne(new BasicDBObject("idactors", actedInDocument.get("idactors")));
            		
            		// Gender in the MongoDB database is a string if female or the integer 1 if male
            		String gender = "female";
            		if (actorDocument.get("gender") instanceof Integer) {
            			gender = "male";
            		}
            		
            		Actor actor = new Actor(
            			null,
            			(String) actorDocument.get("fname"),
            			(String) actorDocument.get("lname"),
            			gender
            		);
            		
            		if (actedInDocument.get("character") instanceof String && ((String) actedInDocument.get("character")).length() > 0) {
            			actor.SetRole((String) actedInDocument.get("character"));
            		}
            		
            		if (actedInDocument.get("billing_position") instanceof Integer) {
            			actorBillings.put((Integer) actedInDocument.get("billing_position"), actor);
            		} else {
            			actorBillings.put(Integer.MAX_VALUE, actor);
            		}
            	}
            }
            
            // Add the actors to the final list in order of billing position
            for (Map.Entry<Integer, Actor> entry : actorBillings.entrySet()) {
            	movie.AddActor(entry.getValue());
            }
            
            return movie;
        }
    }

    public static Movie getMovieById(long id) {
        return createMovieObject(id);
    }
    
    public static List<Movie> getMoviesByTitleYear(String title, Integer year) {
    	initMongoDB();
    	
    	Pattern expression = Pattern.compile(title, Pattern.CASE_INSENSITIVE | Pattern.LITERAL);
    	BasicDBObject query = new BasicDBObject("title", new BasicDBObject("$regex", expression)).append("type", 3);
    	
    	if (year != null) {
    		query.append("year", year);
    	}
    	
    	List<Movie> movies = new ArrayList<Movie>();
    	
    	try (DBCursor movieCursor = moviesCollection.find(query)) {
    		while (movieCursor.hasNext()) {
    			movies.add(createMovieObject((Integer) movieCursor.next().get("idmovies")));
    		}
    	}
    	
    	return movies;
    }
}
