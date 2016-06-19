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
 * @version v0.1 (15.05.2016)
 * @version v0.2 (18.05.2016)
 * @version v0.3s (19.05.2016)
 * @version v0.4 (28.05.2016)
 * @version v1.0 (19.06.2016)
 * 
 **/
@Path("postgresql/genres")
public class Genres {
   
    Long voffset = null, vlimit = null;
    Integer vyear = null, vendyear = null;  
    
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type 
     * 
     * @param sort Sort results by *sort* value
     * @param offset Setting offset for results
     * @param year Setting starting year for search
     * @param endyear Settting ending year for search
     * @param orderby1 Setting first sorting parameter
     * @param orderby2 Setting second sorting parameter
     * @return      
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Genre> getAllGenres(@QueryParam("sort") String sort, @QueryParam("year") String year, @QueryParam("endyear") String endyear) {      
        ParseWhatPossible(null, year, endyear);
        if (sort == null || (!sort.equals("idgenres") && !sort.equals("genre")))
            sort = "idgenres";
        GenreController GenreController = new GenreController();        
        return GenreController.SetActiveFiltersForCollection(sort, vyear, vendyear);
        }        
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{genreId}")
    public ArrayList<Genre> displayDetailed(@PathParam("genreId") Long id, @QueryParam("offset") String offset, @QueryParam("year") String year, @QueryParam("endyear") String endyear, @QueryParam("orderby1") String order, @QueryParam("orderby2") String order2) {        
        GenreController GenreController = new GenreController();        
        ParseWhatPossible(offset, year, endyear);             
        ArrayList<Long> single = new ArrayList<>();
        single.add(id);               
        return GenreController.GetGenreInformation(voffset, single, vyear, vendyear, order, order2);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{genreId}/movies")
    public ArrayList<Movie> displayMovies(@PathParam("genreId") Long id, @QueryParam("offset") String offset, @QueryParam("year") String year, @QueryParam("endyear") String endyear, @QueryParam("orderby1") String order, @QueryParam("orderby2") String order2) {        
        GenreController GenreController = new GenreController();        
        ParseWhatPossible(offset, year, endyear);             
        ArrayList<Long> single = new ArrayList<>();
        single.add(id);
        ArrayList<Genre> genre = GenreController.GetGenreInformation(voffset, single, vyear, vendyear, order, order2);
        return genre.get(0).displayMovies();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{genreId}/statistics")
    public Integer displayStatistics(@PathParam("genreId") Long id, @QueryParam("offset") String offset, @QueryParam("year") String year, @QueryParam("endyear") String endyear, @QueryParam("orderby1") String order, @QueryParam("orderby2") String order2) {        
        GenreController GenreController = new GenreController();        
        ParseWhatPossible(offset, year, endyear);             
        ArrayList<Long> single = new ArrayList<>();
        single.add(id);
        ArrayList<Genre> genre = GenreController.GetGenreInformation(voffset, single, vyear, vendyear, order, order2);
        return genre.get(0).displayStatistics();
    }
    
    private void ParseWhatPossible(String offset, String year, String endyear) {                   
        if (offset != null) {voffset = Long.parseLong(offset);}        
        if (year != null) {vyear = Integer.parseInt(year);}
        if (endyear != null) {vendyear = Integer.parseInt(endyear);}          
    }
}
