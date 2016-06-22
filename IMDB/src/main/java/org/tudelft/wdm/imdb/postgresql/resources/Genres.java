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
import org.tudelft.wdm.imdb.models.Genre;
import org.tudelft.wdm.imdb.models.Movie;
import org.tudelft.wdm.imdb.postgresql.controllers.GenreController;

/**
 *
 * @author Piotr Tekieli <p.s.tekieli@student.tudelft.nl>
 * @version v1.0f (22.06.2016)
 * 
 **/
@Path("postgresql/genres")
public class Genres {
     
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type 
     * 
     * @param sort Sort results by *sort* value     
     * @param year Setting starting year for searching
     * @param endyear Settting ending year for searching
     * @return List of genres that fit the description.      
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Genre> getAllGenres(@QueryParam("sort") String sort, @QueryParam("year") Integer year, @QueryParam("endyear") Integer endyear) {      
        if (sort == null || (!sort.equals("idgenres") && !sort.equals("genre")))
            sort = "idgenres";
        GenreController GenreController = new GenreController();        
        return GenreController.SetActiveFiltersForCollection(sort, year, endyear);
        }        
    
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type 
     * 
     * @param id Seting actor id
     * @param offset Setting offset for results
     * @param year Setting starting year for searching
     * @param endyear Settting ending year for searching
     * @param order Setting first parameter for sorting
     * @param order2 Setting second parameter for sorting
     * @return List of genres that fit the description.       
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{genreId}")
    public ArrayList<Genre> displayDetailed(@PathParam("genreId") Long id, @QueryParam("offset") Long offset, @QueryParam("year") Integer year, @QueryParam("endyear") Integer endyear, @QueryParam("orderby1") String order, @QueryParam("orderby2") String order2) {        
        GenreController GenreController = new GenreController();        
        ArrayList<Long> single = new ArrayList<>();
        single.add(id);               
        return GenreController.GetGenreInformation(offset, single, year, endyear, order, order2);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{genreId}/movies")
    public ArrayList<Movie> displayMovies(@PathParam("genreId") Long id, @QueryParam("offset") Long offset, @QueryParam("year") Integer year, @QueryParam("endyear") Integer endyear, @QueryParam("orderby1") String order, @QueryParam("orderby2") String order2) {        
        GenreController GenreController = new GenreController();        
        ArrayList<Long> single = new ArrayList<>();
        single.add(id);
        ArrayList<Genre> genre = GenreController.GetGenreInformation(offset, single, year, endyear, order, order2);
        return genre.get(0).displayMovies();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{genreId}/statistics")
    public Integer displayStatistics(@PathParam("genreId") Long id, @QueryParam("offset") Long offset, @QueryParam("year") Integer year, @QueryParam("endyear") Integer endyear, @QueryParam("orderby1") String order, @QueryParam("orderby2") String order2) {        
        GenreController GenreController = new GenreController();        
        ArrayList<Long> single = new ArrayList<>();
        single.add(id);
        ArrayList<Genre> genre = GenreController.GetGenreInformation(offset, single, year, endyear, order, order2);
        return genre.get(0).displayStatistics();
    }
}