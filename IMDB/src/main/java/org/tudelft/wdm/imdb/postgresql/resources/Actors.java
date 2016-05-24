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
import org.tudelft.wdm.imdb.postgresql.controllers.ActorController;

/**
 *
 * @author Piotr Tekieli <p.s.tekieli@student.tudelft.nl>
 * @version v0.1 (15.05.2016)
 * @version v0.2 (18.05.2016)
 * @version v0.3s (19.05.2016)
 * 
 **/
@Path("postgresql/actors")
public class Actors {
    
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @param offset
     * @param limit
     * @param sort
     * @return String that will be returned as a text/plain response.
     */
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public MessageJSON getAllActors(@QueryParam("offset") String offset, @QueryParam("limit") String limit, @QueryParam("orderby") String sort) {         
        ActorController ActorController = new ActorController();        
        /* ---------------------PARSE WHAT POSSIBLE------------------------ */
        Long voffset = null, vlimit = null;             
        if (offset != null) {voffset = Long.parseLong(offset);}
        if (limit != null) {vlimit = Long.parseLong(limit);}        
         /* ----------------------------------------------------------------- */          
        ActorController.GetAllActors(vlimit, voffset, sort);
        return ActorController.getMessageJSON();
        }        
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{actorId}")
    public MessageJSON displayDetailed(@PathParam("actorId") String id, @QueryParam("details") String details, @QueryParam("orderby") String order) {        
        ActorController ActorController = new ActorController();        
        if ("true".equals(details)) {           
            ActorController.GetDetailedActorInformation(Long.parseLong(id), order);
        }                        
        else {           
            ActorController.GetShortActorInformation(Long.parseLong(id), order);                        
        }
        return ActorController.getMessageJSON();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{actorId}/movies")
    public MessageJSON displayMovies(@PathParam("actorId") String id, @QueryParam("orderby") String sort) {        
        ActorController ActorController = new ActorController();        
        ActorController.SetActiveFiltersForSingle(Long.parseLong(id), sort);
        ActorController.GetMoviesInformation();
        return ActorController.getMessageJSON();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{actorId}/statistics")
    public MessageJSON displayStatistics(@PathParam("actorId") String id, @QueryParam("orderby") String sort) {        
        ActorController ActorController = new ActorController();        
        ActorController.SetActiveFiltersForSingle(Long.parseLong(id), sort);
        ActorController.GetActorStatistics();
        return ActorController.getMessageJSON();
    }
}
