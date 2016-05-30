package org.tudelft.wdm.imdb.mongodb.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.tudelft.wdm.imdb.models.Movie;
import org.tudelft.wdm.imdb.mongodb.controllers.MovieController;

/**
 * @author Alexander Overvoorde
 * 
 * Views for the MongoDB movie related APIs.
 */
@Path("mongodb/movies")
public class Movies {
    /**
     * Retrieve a single movie by its id. An empty document is returned if the
     * id does not match any movie.
     * 
     * @param id Id of movie.
     * @return Movie details or empty document.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{movieId}")
    public Movie movieById(@PathParam("movieId") String id) {
        Movie movie = MovieController.getMovieById(Long.parseLong(id));

        if (movie != null) {
            return movie;
        } else {
            return null;
        }
    }
    
    /**
     * Retrieve a single movie by its title. An empty document is returned if
     * no movie titles meet the specified query.
     * 
     * @param title Title or substring of movie title.
     * @param year Optional filter for a specific year.
     * @return Movie details or empty document.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Movie> moviesByTitle(@QueryParam("title") String title, @QueryParam("year") String year) {
    	Integer yearNumeric = year != null ? Integer.valueOf(year) : null;
    	List<Movie> movies = MovieController.getMoviesByTitleYear(title, yearNumeric);

        return movies;
    }
}
