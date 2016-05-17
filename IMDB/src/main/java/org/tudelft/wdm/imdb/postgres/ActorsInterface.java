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
import org.tudelft.wdm.imdb.controllers.ActorController;
import org.tudelft.wdm.imdb.models.Actor;

/**
 *
 * @author Piotr Tekieli <p.s.tekieli@student.tudelft.nl>
 * @version v0.1 (15.05.2016)
 * 
 **/
@Path("actors")
public class ActorsInterface {
    
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @param offset
     * @param limit
     * @return String that will be returned as a text/plain response.
     */
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Actor> getAllActors(@QueryParam("offset") String offset, @QueryParam("limit") String limit) {         
        ActorController handler = new ActorController();        
        if (limit != null && offset != null) {            
            handler.SetActorsFilter(Long.parseLong(limit), Long.parseLong(offset)); 
            return handler.GetActors();
        }
        else {
            handler.SetActorsFilter(100, 0); 
            return handler.GetActors();
        }        
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{actorId}")
    public Actor displayDetailed(@PathParam("actorId") String id, @QueryParam("details") String details, @QueryParam("orderby") String order) {        
        ActorController handler = new ActorController();        
        if ("true".equals(details)) {
            if (order == null) {
                return handler.GetDetailedActorInformation(Long.parseLong(id));
            }
            else {
                return handler.GetDetailedActorInformationFilter(Long.parseLong(id), order);
            }
        }                        
        else 
            return handler.GetActorInformation(Long.parseLong(id));
    }
    
}
