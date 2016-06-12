/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tudelft.wdm.imdb.neo4j.controllers;

import java.util.ArrayList;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Statement;
import org.neo4j.driver.v1.StatementResult;
import org.tudelft.wdm.imdb.models.Actor;
import org.tudelft.wdm.imdb.models.Movie;

/**
 *
 * @author Tom
 */
public class ActorController {
    
    /**
     * This function will retrieve only the basic movie data.
     * It is required for the statement to retrieve the id, fname, lname and gender
     * using those exact names.
     * @param query
     * @return 
     */
    public static ArrayList<Actor> getActors(Statement query) {
        StatementResult sr = Controller.query(query);
        ArrayList<Actor> actors = new ArrayList<>();
        while (sr.hasNext()){
            Record r = sr.next();
            Actor a = new Actor(
                    r.get("id").asLong(),
                    r.get("fname").asString(),
                    r.get("lname").asString(),
                    r.get("gender").isNull() ? null : ""+r.get("gender").asInt()
            );
            actors.add(a);
        }
        Controller.closeConnection();
        return actors;
    }
    
    /**
     * This function will retrieve all relevant movie data.
     * It is required for the statement to retrieve the id, title and year
     * using those exact names.
     * @param query
     * @return 
     */
    public static ArrayList<Actor> getActorsFull(Statement query) {
        Controller.keepOpen();
        ArrayList<Actor> actors = getActors(query);
        for (Actor a : actors){
            for (Movie m : getMoviesInformation(a.GetId(), null))
                a.AddMovie(m);
            a.SetStatistic(getActorStatistics(a.GetId()));
        }
        Controller.forceClose();
        return actors;
    }
    
    public static ArrayList<Movie> getMoviesInformation(long actorId, String sort){
        if (sort==null || sort.equals("idmovies")){
            sort="";
        } else {
            sort="ORDER BY m."+sort;
        }
        Statement s = new Statement("MATCH (a:actors {idactors:" + actorId
                + "})-[:ACTED_CHARACTER]->(m:movies)"
                + " RETURN DISTINCT m.idmovies, m.title, m.year"
                + sort
        );
        StatementResult sr = Controller.query(s);
        Controller.closeConnection();
        ArrayList<Movie> movies = new ArrayList<>();
        while (sr.hasNext()){
            Record r = sr.next();
            movies.add(new Movie(
                    r.get("m.idmovies").asLong(),
                    r.get("m.title").asString(),
                    r.get("m.year").isNull() ? 0 : r.get("m.year").asInt()));
        }
        return movies;
    }
    
    public static int getActorStatistics(long actorId){
        Statement s = new Statement("MATCH (a:actors {idactors:" + actorId
                + "})-[r:ACTED_CHARACTER]->(m:movies) RETURN COUNT(DISTINCT m.idmovies)"
        );
        StatementResult sr = Controller.query(s);
        Controller.closeConnection();
        while (sr.hasNext()){
            Record r = sr.next();
            return r.get("COUNT(DISTINCT m.idmovies)").asInt();
        }
        return 0;
    }
}
