/*
 * The MIT License
 *
 * Copyright 2016 Piotr Tekieli <p.s.tekieli@student.tudelft.nl>
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
package org.tudelft.wdm.imdb.postgresql.resources;

import java.util.ArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.tudelft.wdm.imdb.models.Actor;
import org.tudelft.wdm.imdb.models.Genre;
import org.tudelft.wdm.imdb.models.Keyword;

import org.tudelft.wdm.imdb.models.Movie;
import org.tudelft.wdm.imdb.models.Serie;
import org.tudelft.wdm.imdb.postgresql.controllers.MovieController;


/**
 *
 * @author Piotr Tekieli <p.s.tekieli@student.tudelft.nl>
 * @version v0.1 (15.05.2016)
 * @version v0.2 (18.05.2016)
 * @version v0.3s (19.05.2016)
 * @version v0.4 (28.05.2016)
 * @version v0.5 (08.05.2016)
 * 
 **/
@Path("postgresql/movies")
public class Movies {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type 
     * 
     * @param offset
     * @param sort
     * @param title
     * @param syear
     * @param eyear
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Movie> getAllMovies(@QueryParam("offset") String offset, @QueryParam("orderby") String sort, @QueryParam("title") String title, @QueryParam("syear") String syear, @QueryParam("eyear") String eyear) {         
        MovieController MovieController = new MovieController();        
        /* ---------------------PARSE WHAT POSSIBLE------------------------ */        
        Long voffset = null;                
        if (offset != null) {voffset = Long.parseLong(offset);}        
         /* ----------------------------------------------------------------- */
        ArrayList<Long> IDs = null;
        if (title != null) {
            IDs = MovieController.SetActiveFiltersForCollectionByTitle(title, sort, syear, eyear);
        } else {              
            IDs = MovieController.SetActiveFiltersForCollection(voffset, sort);
        }
        return MovieController.GetMovieInformation(IDs);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{movieId}")
    public ArrayList<Movie> displayDetailed(@PathParam("movieId") Long id) {        
        MovieController MovieController = new MovieController();        
        ArrayList<Long> single = new ArrayList<>();
        single.add(id);        
        return MovieController.GetMovieInformation(single);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{movieId}/actors")
    public ArrayList<Actor> displayActors(@PathParam("movieId") Long id) {        
        MovieController MovieController = new MovieController();        
        ArrayList<Long> single = new ArrayList<>();
        single.add(id);
        ArrayList<Movie> movie = MovieController.GetMovieInformation(single);
        return movie.get(0).displayActors();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{movieId}/genres")
    public ArrayList<Genre> displayGenres(@PathParam("movieId") Long id) {        
        MovieController MovieController = new MovieController();        
        ArrayList<Long> single = new ArrayList<>();
        single.add(id);
        ArrayList<Movie> movie = MovieController.GetMovieInformation(single);
        return movie.get(0).displayGenres();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{movieId}/keywords")
    public ArrayList<Keyword> displayKeywords(@PathParam("movieId") Long id) {        
        MovieController MovieController = new MovieController();        
        ArrayList<Long> single = new ArrayList<>();
        single.add(id);
        ArrayList<Movie> movie = MovieController.GetMovieInformation(single);
        return movie.get(0).displayKeywordObjects();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{movieId}/series")
    public ArrayList<Serie> displaySeries(@PathParam("movieId") Long id) {        
        MovieController MovieController = new MovieController();        
        ArrayList<Long> single = new ArrayList<>();
        single.add(id);
        ArrayList<Movie> movie = MovieController.GetMovieInformation(single);
        return movie.get(0).displaySeries();
    }
}
