/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tudelft.wdm.imdb.neo4j.controllers;

import java.util.ArrayList;
import org.neo4j.driver.v1.*;
import org.tudelft.wdm.imdb.models.Genre;
import org.tudelft.wdm.imdb.models.Movie;

/**
 *
 * @author Tom
 */
public class GenreController {
    
    /**
     * This function will retrieve all relevant genre data.
     * It is required for the statement to retrieve the id and name
     * using those exact names.
     * @param query
     * @return 
     */
    public static Genre getGenre(Statement query){
        StatementResult sr = Controller.query(query);
        Controller.closeConnection();
        while (sr.hasNext()){
            Record r = sr.next();
            Genre g = new Genre(
                    r.get("id").asLong(),
                    r.get("name").asString()
            );
            for (Movie m : getMoviesInformation(g.getId()))
                g.AddMovie(m);
            return g;
        }
        return null;
    }
    
    /**
     * This function will retrieve only basic genre data.
     * It is required for the statement to retrieve the id, name and moviecount
     * using those exact names.
     * @param query
     * @return 
     */
    public static ArrayList<Genre> getGenresFull(Statement query){
        StatementResult sr = Controller.query(query);
        Controller.closeConnection();
        ArrayList<Genre> genres = new ArrayList<>();
        while (sr.hasNext()){
            Record r = sr.next();
            Genre g = new Genre(
                    r.get("id").asLong(),
                    r.get("name").asString()
            );
            g.SetStatistic(r.get("moviecount").asInt());
            genres.add(g);
        }
        return genres;
    }
    
    public static ArrayList<Movie> getMoviesInformation(long genreId){
        Statement s = new Statement("MATCH (m:movies)-[r:MOVIE_GENRE]->(g:genres {idgenres:"
                + genreId + "}) RETURN m.idmovies, m.title, m.year"
        );
        StatementResult sr = Controller.query(s);
        Controller.closeConnection();
        ArrayList<Movie> movies = new ArrayList<>();
        while (sr.hasNext()){
            Record r = sr.next();
            Movie m = new Movie(
                    r.get("m.idmovies").asLong(),
                    r.get("m.title").asString(),
                    r.get("m.year").isNull() ? 0 : r.get("m.year").asInt());
        }
        return movies;
    }
    
    public static int getGenreStatistics(long genreId){
        Statement s = new Statement("MATCH ()-[r:MOVIE_GENRE]->(g:genres {idgenres:"
                + genreId + "}) RETURN COUNT(r)"
        );
        StatementResult sr = Controller.query(s);
        Controller.closeConnection();
        while (sr.hasNext()){
            Record r = sr.next();
            return r.get("COUNT(r)").asInt();
        }
        return 0;
    }
    
    public static int getGenreStatistics(long genreId, String year, String endyear){
        String where = formulateWhere(year, endyear);
        Statement s = new Statement("MATCH ()-[r:MOVIE_GENRE]->(g:genres {idgenres:"
                + genreId + "})" + where  + " RETURN COUNT(r)"
        );
        StatementResult sr = Controller.query(s);
        Controller.closeConnection();
        while (sr.hasNext()){
            Record r = sr.next();
            return r.get("COUNT(r)").asInt();
        }
        return 0;
    }
    
    public static String formulateWhere(String year, String endyear){
        ArrayList<String> where_args = new ArrayList<>();
        if (year!=null) where_args.add("m.year>=" + year);
        if (endyear!=null) where_args.add("m.year<=" + endyear);
        String where = "";
        if (!where_args.isEmpty()){
            where = " WHERE ";
            for (String arg : where_args){
                where+=arg + " AND ";
            }
            where = where.substring(0, where.length()-5) + " ";
        }
        return where;
    }
}
