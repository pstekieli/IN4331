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
package org.tudelft.wdm.imdb.pgresources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.tudelft.wdm.imdb.pgcontrollers.MovieController;
import org.tudelft.wdm.imdb.models.MessageJSON;


/**
 *
 * @author Piotr Tekieli <p.s.tekieli@student.tudelft.nl>
 * @version v0.1 (15.05.2016)
 * @version v0.2 (18.05.2016)
 * @version v0.3s (19.05.2016)
 * 
 **/
@Path("pg/movies")
public class Movies {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type 
     * 
     * @param offset
     * @param limit
     * @param year
     * @param endyear
     * @param order
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public MessageJSON getAllMovies(@QueryParam("offset") String offset, @QueryParam("limit") String limit, @QueryParam("year") String year, @QueryParam("endyear") String endyear, @QueryParam("orderby") String order) {         
        MovieController MovieController = new MovieController();        
        /* ---------------------PARSE WHAT POSSIBLE------------------------ */
        Long voffset = null, vlimit = null;
        Integer vyear = null, vendyear = null;        
        if (offset != null) {voffset = Long.parseLong(offset);}
        if (limit != null) {vlimit = Long.parseLong(limit);}
        if (year != null) {vyear = Integer.parseInt(year);}
        if (endyear != null) {vendyear = Integer.parseInt(endyear);}
         /* ----------------------------------------------------------------- */
        MovieController.GetAllMovies(vlimit, voffset, vyear, vendyear, order);
        return MovieController.getMessageJSON();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{movieId}")
    public MessageJSON displayDetailed(@PathParam("movieId") String id, @QueryParam("details") String details) {        
        MovieController MovieController = new MovieController();        
        if ("true".equals(details))
            MovieController.GetDetailedMovieInformation(Long.parseLong(id));
        else
            MovieController.GetShortMovieInformation(Long.parseLong(id));
        return MovieController.getMessageJSON();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{movieId}/actors")
    public MessageJSON displayActors(@PathParam("movieId") String id) {        
        MovieController MovieController = new MovieController();        
        MovieController.SetActiveFiltersForSingle(Long.parseLong(id));
        MovieController.GetActorsInformation();
        return MovieController.getMessageJSON();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{movieId}/genres")
    public MessageJSON displayGenres(@PathParam("movieId") String id) {        
        MovieController MovieController = new MovieController();        
        MovieController.SetActiveFiltersForSingle(Long.parseLong(id));
        MovieController.GetGenresInformation();
        return MovieController.getMessageJSON();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{movieId}/keywords")
    public MessageJSON displayKeywords(@PathParam("movieId") String id) {        
        MovieController MovieController = new MovieController();        
        MovieController.SetActiveFiltersForSingle(Long.parseLong(id));
        MovieController.GetKeywordsInformation();
        return MovieController.getMessageJSON();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{movieId}/series")
    public MessageJSON displaySeries(@PathParam("movieId") String id) {        
        MovieController MovieController = new MovieController();        
        MovieController.SetActiveFiltersForSingle(Long.parseLong(id));
        MovieController.GetSeriesInformation();
        return MovieController.getMessageJSON();
    }
}
