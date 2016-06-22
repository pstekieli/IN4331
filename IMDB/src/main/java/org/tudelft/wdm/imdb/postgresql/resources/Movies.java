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
 * @version v1.0f (22.06.2016)
 * 
 **/
@Path("postgresql/movies")
public class Movies {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type 
     * 
     * @param offset Display results starting at ID
     * @param sort Sort results by *sort* value
     * @param title Search by *title*
     * @param startYear Starting year (syear) for the search
     * @param endYear Ending year (eyear) for the search
     * @return List of movies that fit selected criterias. 
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Movie> searchForMovies(@QueryParam("offset") Long offset, @QueryParam("orderby") String sort, @QueryParam("title") String title, @QueryParam("syear") Integer startYear, @QueryParam("eyear") Integer endYear) {         
        MovieController MovieController = new MovieController();        
        /* ---------------------SECURE INPUT-------------------------------- */        
        if (title != null)
            title = title.replaceAll("/(//+)([^A-Za-z])", "");         
        /* ----------------------------------------------------------------- */
        ArrayList<Long> IDs;
        if (title != null) {
            IDs = MovieController.SetActiveFiltersForCollectionByTitle(title, sort, startYear, endYear);
        } else {              
            IDs = MovieController.SetActiveFiltersForCollection(offset, sort);
        }
        return MovieController.GetMovieInformation(IDs);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{movieId}")
    public ArrayList<Movie> AccessMovie(@PathParam("movieId") Long id) {        
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