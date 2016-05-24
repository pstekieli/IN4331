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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.tudelft.wdm.imdb.models.MessageJSON;
import org.tudelft.wdm.imdb.postgresql.controllers.GenreController;

/**
 *
 * @author Piotr Tekieli <p.s.tekieli@student.tudelft.nl>
 * @version v0.1 (15.05.2016)
 * @version v0.2 (18.05.2016)
 * @version v0.3s (19.05.2016)
 * 
 **/
@Path("postgresql/genres")
public class Genres {

    Long voffset = null, vlimit = null;
    Integer vyear = null, vendyear = null;  
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public MessageJSON getAllGenres(@QueryParam("orderby") String sort) {         
        GenreController ActorController = new GenreController();        
        if (sort == null)
            ActorController.GetAllGenres(null);
        else
            ActorController.GetAllGenres(sort);
        return ActorController.getMessageJSON();
        }        
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{genreId}")
    public MessageJSON displayDetailed(@PathParam("genreId") String id, @QueryParam("offset") String offset, @QueryParam("limit") String limit, @QueryParam("year") String year, @QueryParam("endyear") String endyear, @QueryParam("orderby1") String order, @QueryParam("orderby2") String order2, @QueryParam("details") String details) {        
        GenreController GenreController = new GenreController();        
        ParseWhatPossible(limit, offset, year, endyear);             
        if ("true".equals(details))
            GenreController.GetDetailedGenreInformation(vlimit, voffset, Long.parseLong(id), vyear, vendyear, order, order2);        
        else
            GenreController.GetShortGenreInformation(vlimit, voffset, Long.parseLong(id), vyear, vendyear, order, order2);
        return GenreController.getMessageJSON();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{genreId}/movies")
    public MessageJSON displayMovies(@PathParam("genreId") String id, @QueryParam("offset") String offset, @QueryParam("limit") String limit, @QueryParam("year") String year, @QueryParam("endyear") String endyear, @QueryParam("orderby1") String order, @QueryParam("orderby2") String order2, @QueryParam("details") String details) {        
        GenreController GenreController = new GenreController();        
        ParseWhatPossible(limit, offset, year, endyear);
        GenreController.SetActiveFiltersForSingle(vlimit, voffset, Long.parseLong(id), vyear, vendyear, order, order2);
        GenreController.GetMoviesInformation();
        return GenreController.getMessageJSON();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{genreId}/statistics")
    public MessageJSON displayStatistics(@PathParam("genreId") String id, @QueryParam("offset") String offset, @QueryParam("limit") String limit, @QueryParam("year") String year, @QueryParam("endyear") String endyear, @QueryParam("orderby1") String order, @QueryParam("orderby2") String order2, @QueryParam("details") String details) {        
        GenreController GenreController = new GenreController();        
        ParseWhatPossible(limit, offset, year, endyear);
        GenreController.SetActiveFiltersForSingle(vlimit, voffset, Long.parseLong(id), vyear, vendyear, order, order2);
        GenreController.GetGenreStatistics();
        return GenreController.getMessageJSON();
    }
    
    private void ParseWhatPossible(String limit, String offset, String year, String endyear) {                   
        if (offset != null) {voffset = Long.parseLong(offset);}
        if (limit != null) {vlimit = Long.parseLong(limit);}
        if (year != null) {vyear = Integer.parseInt(year);}
        if (endyear != null) {vendyear = Integer.parseInt(endyear);}          
    }
}
