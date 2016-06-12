/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tudelft.wdm.imdb.neo4j.controllers;

import java.util.ArrayList;
import org.neo4j.driver.v1.*;
import org.tudelft.wdm.imdb.models.Actor;
import org.tudelft.wdm.imdb.models.Genre;
import org.tudelft.wdm.imdb.models.Keyword;
import org.tudelft.wdm.imdb.models.Movie;
import org.tudelft.wdm.imdb.models.Serie;

/**
 *
 * @author Tom
 */
public class MovieController {
    
    /**
     * This function will retrieve only the basic movie data.
     * It is required for the statement to retrieve the id, title and year
     * using those exact names.
     * @param query
     * @return 
     */
    public static ArrayList<Movie> getMovies(Statement query) {
        StatementResult sr = Controller.query(query);
        ArrayList<Movie> movies = new ArrayList<>();
        while (sr.hasNext()){
            Record r = sr.next();
            Movie m = new Movie(
                    r.get("id").asLong(),
                    r.get("title").asString(),
                    r.get("year").isNull() ? 0 : r.get("year").asInt());
            movies.add(m);
        }
        Controller.closeConnection();
        return movies;
    }
    
    /**
     * This function will retrieve all relevant movie data.
     * It is required for the statement to retrieve the id, title and year
     * using those exact names.
     * @param query
     * @return 
     */
    public static ArrayList<Movie> getMoviesFull(Statement query) {
        Controller.keepOpen();
        ArrayList<Movie> movies = getMovies(query);
        for (Movie m : movies){
            for (Actor a : getActorInformation(m.getId()))
                m.AddActor(a);
            for (Genre g : getGenreInformation(m.getId()))
                m.AddGenre(g);
            for (Keyword k : getKeywordInformation(m.getId()))
                m.AddKeywordObject(k);
            for (Serie s : getSeriesInformation(m.getId()))
                m.AddSerie(s);
        }
        Controller.forceClose();
        return movies;
    }
    
    public static ArrayList<Actor> getActorInformation(long movieId){
        Statement s = new Statement("MATCH (a:actors)-[r:ACTED_CHARACTER]->(b:movies {idmovies:"
                + movieId + "}) RETURN a.idactors, a.fname, a.lname, a.gender, r.character");
        StatementResult sr = Controller.query(s);
        Controller.closeConnection();
        ArrayList<Actor> actors = new ArrayList<>();
        while (sr.hasNext()){
            Record r = sr.next();
            Actor a = new Actor(
                    r.get("a.idactors").asLong(),
                    r.get("a.fname").asString(),
                    r.get("a.lname").asString(),
                    r.get("a.gender").isNull() ? null : ""+r.get("a.gender").asInt()
            );
            a.SetRole(r.get("r.character").asString());
            actors.add(a);
        }
        return actors;
    }
    
    public static ArrayList<Genre> getGenreInformation(long movieId){
        Statement s = new Statement("MATCH (a:movies {idmovies:" + movieId + "})-[r:GENRE_OF_MOVIE]->(b:genres)"
                + " RETURN b.idgenres, b.genre");
        StatementResult sr = Controller.query(s);
        Controller.closeConnection();
        ArrayList<Genre> genres = new ArrayList<>();
        while (sr.hasNext()){
            Record r = sr.next();
            genres.add(new Genre(
                    r.get("b.idgenres").asLong(),
                    r.get("b.genre").asString()
            ));
        }
        return genres;
    }
    
    public static ArrayList<Keyword> getKeywordInformation(long movieId){
        Statement s = new Statement("MATCH (a:movies {idmovies:" + movieId + "})-[r:KEYWORD_OF_MOVIE]->(b:keywords)"
                + "RETURN b.idkeywords, b.keyword");
        StatementResult sr = Controller.query(s);
        Controller.closeConnection();
        ArrayList<Keyword> keywords = new ArrayList<>();
        while (sr.hasNext()){
            Record r = sr.next();
            keywords.add(new Keyword(
                    r.get("b.idkeywords").asLong(),
                    r.get("b.keyword").asString()
            ));
        }
        return keywords;
    }
    
    public static ArrayList<Serie> getSeriesInformation(long movieId){
        Statement s = new Statement("MATCH (a:movies {idmovies:" + movieId + "})-[r:ADAPTED_AS_SERIES]->(b:series)"
                + "RETURN b.idseries, b.name, b.season, b.episode");
        StatementResult sr = Controller.query(s);
        Controller.closeConnection();
        ArrayList<Serie> series = new ArrayList<>();
        while (sr.hasNext()){
            Record r = sr.next();
            series.add(new Serie(
                    r.get("b.idseries").asLong(),
                    r.get("b.name").asString(),
                    r.get("b.season").isNull() ? 0 : r.get("b.season").asInt(),
                    r.get("b.episode").isNull() ? 0 : r.get("b.episode").asInt()
            ));
        }
        return series;
    }
}
