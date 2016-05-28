package org.tudelft.wdm.imdb.mongodb.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
    @Path("/id/{movieId}")
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
     * @return Movie details or empty document.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/title/{movieTitle}")
    public Movie movieByTitle(@PathParam("movieTitle") String title) {
    	Movie movie = MovieController.getMovieByTitle(title);

        if (movie != null) {
            return movie;
        } else {
            return null;
        }
    }
}
