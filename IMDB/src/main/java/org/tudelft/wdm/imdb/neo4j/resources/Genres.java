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
import org.tudelft.wdm.imdb.models.Genre;
import org.tudelft.wdm.imdb.models.Movie;
import org.tudelft.wdm.imdb.neo4j.controllers.GenreController;
import org.tudelft.wdm.imdb.neo4j.controllers.MovieController;

/**
 *
 * @author Tom
 */
@Path("neo4j/genres")
public class Genres {
    
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @param sort
     * @param year
     * @param endyear
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Genre> getAllGenres(@QueryParam("sort") String sort, @QueryParam("year") String year, @QueryParam("endyear") String endyear){
        String where = GenreController.formulateWhere(year, endyear);
        String orderby = " ORDER BY id";
        if (sort!=null){
            switch (sort.toLowerCase()){
                case "genre":
                case "name": orderby = " ORDER BY name"; break;
                case "movies":
                case "number of movies": orderby = " ORDER BY moviecount"; break;
            }
        }
        Statement s = new Statement("MATCH (m:movies)-[r:MOVIE_GENRE]->(g:genres)" + where + " RETURN g.idgenres AS id, g.genre AS name, COUNT(m.idmovies) AS moviecount" + orderby);
        return GenreController.getGenresFull(s);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{genreId}")
    public Genre displayDetailed(@PathParam("genreId") Long id, @QueryParam("offset") String offset, @QueryParam("limit") String limit, @QueryParam("year") String year, @QueryParam("endyear") String endyear, @QueryParam("orderby1") String order, @QueryParam("orderby2") String order2){
        Statement s = new Statement("MATCH (g:genres {idgenres:" + id
                + "}) RETURN g.idgenres AS id, g.genre AS name"
        );
        Genre g = GenreController.getGenre(s);
        g.SetStatistic(GenreController.getGenreStatistics(id, year, endyear));
        for (Movie m : displayMovies(id, offset, limit, year, endyear, order, order2))
            g.AddMovie(m);
        return g;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{genreId}/movies")
    public ArrayList<Movie> displayMovies(@PathParam("genreId") Long id, @QueryParam("offset") String offset, @QueryParam("limit") String limit, @QueryParam("year") String year, @QueryParam("endyear") String endyear, @QueryParam("orderby1") String order, @QueryParam("orderby2") String order2){
        long offset_arg = 0;
        if (offset!=null){
            try {
                offset_arg = Long.parseLong(offset);
            } catch (NumberFormatException ex){}
        }
        
        String sort_arg;
        if (order==null) sort_arg = " ORDER BY id";
        else {
            switch (order.toLowerCase()){
                case "title":
                case "year": sort_arg = " ORDER BY " + order; break;
                case "number":
                case "type":
                case "location":
                case "language": sort_arg = " ORDER BY m." + order; break;
                default: sort_arg = " ORDER BY id";
            }
            if (order2!=null){
                switch (order2.toLowerCase()){
                    case "title":
                    case "year": sort_arg += ", " + order2; break;
                    case "number":
                    case "type":
                    case "location":
                    case "language": sort_arg += ", " + order2; break;
                    default: sort_arg += ", id";
                }
            }
        }
        
        String where = GenreController.formulateWhere(year, endyear);
        
        String query = "MATCH (m:movies)-[r:MOVIE_GENRE]->(g:genres {idgenres:" + id
                + "}) " + where
                + "RETURN m.idmovies AS id, m.title AS title, m.year AS year"
                + sort_arg
                + " SKIP " + offset_arg
                + " LIMIT 10";
        
        System.out.println(query);
        Statement s = new Statement(query);
        ArrayList<Movie> movies = MovieController.getMovies(s);
        return movies;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{genreId}/statistics")
    public Integer displayStatistics(@PathParam("genreId") Long id){
        return GenreController.getGenreStatistics(id);
    }
}
