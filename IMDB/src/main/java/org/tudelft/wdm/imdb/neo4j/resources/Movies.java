/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tudelft.wdm.imdb.neo4j.resources;

import java.util.ArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.neo4j.driver.v1.*;
import org.tudelft.wdm.imdb.models.Actor;
import org.tudelft.wdm.imdb.models.Genre;
import org.tudelft.wdm.imdb.models.Keyword;
import org.tudelft.wdm.imdb.models.Movie;
import org.tudelft.wdm.imdb.models.Serie;
import org.tudelft.wdm.imdb.neo4j.controllers.MovieController;

/**
 *
 * @author Tom
 */
@Path("neo4j/movies")
public class Movies {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type 
     * 
     * @param offset
     * @param sort
     * @param title
     * @param year
     * @param endyear
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Movie> getAllMovies(@QueryParam("offset") String offset, @QueryParam("orderby") String sort, @QueryParam("title") String title, @QueryParam("year") String year, @QueryParam("endyear") String endyear){
        // This function is really susceptible to SQL injection, but I can't
        // figure out just how you're meant to do this stuff safely with the
        // Neo4j driver so considering the assignment doesn't require safety
        // from SQL injection, I decided to just ignore the security issues.
        long offset_arg = 0;
        if (offset!=null){
            try {
                offset_arg = Long.parseLong(offset);
            } catch (NumberFormatException ex){}
        }
        
        String sort_arg;
        if (sort==null) sort_arg = "ORDER BY id";
        else {
            switch (sort.toLowerCase()){ //Determine order by string
                case "title":
                case "year": sort_arg = " ORDER BY " + sort; break;
                case "number":
                case "type":
                case "location":
                case "language": sort_arg = " ORDER BY m." + sort; break;
                default: sort_arg = "ORDER BY id";
            }
        }
        
        ArrayList<String> where_args = new ArrayList<>();
        // Regex expression for SQL's "LIKE %str%". (?i) for case insensitive.
        if (title!=null) where_args.add("m.title =~ '.*(?i)" + title + ".*'");
        if (year!=null) where_args.add("m.year>=" + year);
        if (endyear!=null) where_args.add("m.year<=" + endyear);
        String where = "";
        if (!where_args.isEmpty()){ //Combine WHERE statements into a single string
            where = " WHERE ";
            for (String arg : where_args){
                where+=arg + " AND ";
            }
            where = where.substring(0, where.length()-5) + " ";
        }
        
        // Combine all components into a single query
        String query = "MATCH (m:movies)"
                + where
                + "RETURN m.idmovies AS id, m.title AS title, m.year AS year"
                + sort_arg
                + " SKIP "
                + offset_arg
                + " LIMIT 10";
        
        Statement s = new Statement(query);
        ArrayList<Movie> movies = MovieController.getMoviesFull(s);
        return movies;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{movieId}")
    public ArrayList<Movie> displayDetailed(@PathParam("movieId") Long id){
        Statement s = new Statement(
                "MATCH (m:movies {idmovies:"
                + id
                + "}) RETURN m.idmovies AS id, m.title AS title, m.year AS year"
        );
        ArrayList<Movie> movie = MovieController.getMoviesFull(s);
        return movie;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{movieId}/actors")
    public ArrayList<Actor> displayActors(@PathParam("movieId") Long id){
        return MovieController.getActorInformation(id);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{movieId}/genres")
    public ArrayList<Genre> displayGenres(@PathParam("movieId") Long id){
        return MovieController.getGenreInformation(id);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{movieId}/keywords")
    public ArrayList<Keyword> displayKeywords(@PathParam("movieId") Long id){
        return MovieController.getKeywordInformation(id);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{movieId}/series")
    public ArrayList<Serie> displaySeries(@PathParam("movieId") Long id){
        return MovieController.getSeriesInformation(id);
    }
}
