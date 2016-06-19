package org.tudelft.wdm.imdb.mongodb.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.tudelft.wdm.imdb.models.Movie;
import org.tudelft.wdm.imdb.mongodb.controllers.Controller;

/**
 * @author Alexander Overvoorde
 * 
 * Views for the MongoDB movie related APIs.
 */
@Path("mongodb/movies")
public class Movies {
    /**
     * Retrieve a single movie by its id.
     * 
     * @param id Id of movie.
     * @return Movie details or nothing.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{movieId}")
    public Movie movieById(@PathParam("movieId") String id) {
        return Controller.getMovieById(Long.parseLong(id));
    }
    
    /**
     * Retrieve matching movies by a (partial) title. An empty list is
     * returned if no movie titles meet the specified query.
     * 
     * @param title Title or substring of movie title.
     * @param year Optional filter for a specific year.
     * @return List of movie details.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Movie> moviesByTitle(@QueryParam("title") String title, @QueryParam("year") String year, @QueryParam("endyear") String endYear) {
    	Integer yearNumeric = Integer.valueOf(year);
		Integer endYearNumeric = endYear != null ? Integer.valueOf(endYear) : null;
    	
    	List<Movie> movies = Controller.getMoviesByTitleYearRange(title, yearNumeric, endYearNumeric);

        return movies;
    }
}
