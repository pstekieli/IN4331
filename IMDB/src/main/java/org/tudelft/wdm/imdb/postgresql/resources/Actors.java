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
import org.tudelft.wdm.imdb.models.Movie;
import org.tudelft.wdm.imdb.postgresql.controllers.ActorController;


/**
 *
 * @author Piotr Tekieli <p.s.tekieli@student.tudelft.nl>
 * @version v1.0f (22.06.2016)
 * 
 **/
@Path("postgresql/actors")
public class Actors {
    
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @param offset Setting offset for output OFFSET + 10
     * @param sort Sorting output by *sort*
     * @param fname Finding actors by their first name
     * @param lname Finding actors by their last name
     * @return List of actors fitting the description.
     */
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Actor> getAllActors(@QueryParam("offset") Long offset, @QueryParam("orderby") String sort, @QueryParam("firstname") String fname, @QueryParam("lastname") String lname) {         
        ActorController ActorController = new ActorController();        
        /* ---------------------SECURE INPUT-------------------------------- */        
        if (fname != null)
            fname = fname.replaceAll("[^A-Za-z]", "");
        if (lname != null)
            lname = lname.replaceAll("[^A-Za-z]", "");
        /* ----------------------------------------------------------------- */
        ArrayList<Long> IDs;
        if (fname == null && lname == null) 
            IDs = ActorController.SetActiveFiltersForCollection(offset, sort);         
        else              
            IDs = ActorController.SetActiveFiltersForCollectionByName(fname, lname, sort);        
        return ActorController.GetActorInformation(IDs, sort);
    }        
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{actorId}")
    public ArrayList<Actor> displayDetailed(@PathParam("actorId") Long id, @QueryParam("orderby") String sort) {        
        ActorController ActorController = new ActorController();        
        ArrayList<Long> single = new ArrayList<>();
        single.add(id);        
        return ActorController.GetActorInformation(single, sort);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{actorId}/movies")
    public ArrayList<Movie> displayMovies(@PathParam("actorId") Long id, @QueryParam("orderby") String sort) {        
        ActorController ActorController = new ActorController();          
        ArrayList<Long> single = new ArrayList<>();
        single.add(id);
        ArrayList<Actor> actor = ActorController.GetActorInformation(single, sort);
        return actor.get(0).displayMovies();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{actorId}/statistics")
    public Integer displayStatistics(@PathParam("actorId") Long id, @QueryParam("orderby") String sort) {        
        ActorController ActorController = new ActorController();          
        ArrayList<Long> single = new ArrayList<>();
        single.add(id);
        ArrayList<Actor> actor = ActorController.GetActorInformation(single, sort);
        return actor.get(0).displayStatistics();
    }
}