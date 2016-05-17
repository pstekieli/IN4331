/*
 * The MIT License
 *
 * Copyright 2016 Piotr Tekieli <p.s.tekieli@student.tudelft.nl>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.tudelft.wdm.imdb.postgres;

import java.util.ArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.tudelft.wdm.imdb.controllers.MovieController;
import org.tudelft.wdm.imdb.models.Actor;
import org.tudelft.wdm.imdb.models.Genre;
import org.tudelft.wdm.imdb.models.Keyword;
import org.tudelft.wdm.imdb.models.Movie;
import org.tudelft.wdm.imdb.models.Serie;

/**
 *
 * @author Piotr Tekieli <p.s.tekieli@student.tudelft.nl>
 * @version v0.1 (15.05.2016)
 * 
 **/
@Path("movies")
public class MoviesInterface {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type 
     * 
     * @param offset
     * @param limit
     * @param year
     * @param order
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Movie> getAllMovies(@QueryParam("offset") String offset, @QueryParam("limit") String limit, @QueryParam("year") String year, @QueryParam("orderby") String order) {         
        MovieController handler = new MovieController();        
        if (limit != null && offset != null) {            
            if (year == null) {
                if (order == null) {
                    handler.SetMoviesFilter(Long.parseLong(limit), Long.parseLong(offset));                    
                }
                else {
                    handler.SetMoviesFilter(Long.parseLong(limit), Long.parseLong(offset), order);                    
                }
            }
            else
            {
                if (order == null) {
                    handler.SetMoviesFilter(Long.parseLong(limit), Long.parseLong(offset), Integer.parseInt(year));                    
                }
                else {
                    handler.SetMoviesFilter(Long.parseLong(limit), Long.parseLong(offset), Integer.parseInt(year), order);                    
                }
            }
        }
        else {
            if (year == null) {
                if (order == null) {
                    handler.SetMoviesFilter(100, 0); 
                }
                else {
                    handler.SetMoviesFilter(100, 0, order); 
                }
            }
            else {
                if (order == null) {
                    handler.SetMoviesFilter(100, 0, Integer.parseInt(year));
                }
                else {
                    handler.SetMoviesFilter(100, 0, Integer.parseInt(year), order);
                }
            }
        }        
        return handler.GetMovies();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{movieId}")
    public Movie displayDetailed(@PathParam("movieId") String id, @QueryParam("details") String details) {        
        MovieController controller = new MovieController();        
        if ("true".equals(details))
            return controller.GetDetailedMovieInformation(Long.parseLong(id));
        else 
            return controller.GetMovieInformation(Long.parseLong(id));
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{movieId}/actors")
    public ArrayList<Actor> displayActors(@PathParam("movieId") String id) {        
        MovieController controller = new MovieController();        
        return controller.GetDetailedMovieInformation(Long.parseLong(id)).getActors();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{movieId}/genres")
    public ArrayList<Genre> displayGenres(@PathParam("movieId") String id) {        
        MovieController controller = new MovieController();        
        return controller.GetDetailedMovieInformation(Long.parseLong(id)).getGenres();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{movieId}/keywords")
    public ArrayList<Keyword> displayKeywords(@PathParam("movieId") String id) {        
        MovieController controller = new MovieController();        
        return controller.GetDetailedMovieInformation(Long.parseLong(id)).getKeywords();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{movieId}/series")
    public ArrayList<Serie> displaySeries(@PathParam("movieId") String id) {        
        MovieController controller = new MovieController();        
        return controller.GetDetailedMovieInformation(Long.parseLong(id)).getSeries();
    }
}
