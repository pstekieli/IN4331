package org.tudelft.wdm.imdb.mongodb.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.tudelft.wdm.imdb.models.Movie;
import org.tudelft.wdm.imdb.mongodb.controllers.Controller;

/**
 * @author Alexander Overvoorde
 *
 * Views for the MongoDB genre related APIs.
 */
@Path("mongodb/genres")
public class Genres {
	/**
	 * Retrieve details of movies within the specified genre and
	 * year range. The end of the year range is optional and will
	 * default to the current year.
	 * 
	 * @param genre Genre, e.g. adventure
	 * @param year Starting year.
	 * @param endYear Ending year (optional).
	 * @return List of movies with specified genre and within specified year range.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Movie> moviesByGenreYear(@QueryParam("genre") String genre, @QueryParam("year") String year, @QueryParam("endyear") String endYear) {
		// Normalize genre casing
		genre = genre.toLowerCase();
		
		Integer yearNumeric = Integer.valueOf(year);
		Integer endYearNumeric = endYear != null ? Integer.valueOf(endYear) : null;
		
		List<Movie> movies = Controller.getMoviesByGenreYearRange(genre, yearNumeric, endYearNumeric);
		
		return movies;
	}
}
