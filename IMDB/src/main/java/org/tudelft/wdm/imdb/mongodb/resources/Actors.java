package org.tudelft.wdm.imdb.mongodb.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.tudelft.wdm.imdb.models.Actor;
import org.tudelft.wdm.imdb.mongodb.controllers.Controller;

/**
 * @author Alexander Overvoorde
 *
 * Views for MongoDB actor related APIs.
 */
@Path("mongodb/actors")
public class Actors {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{actorId}")
	public Actor actorById(@PathParam("actorId") String id) {
		return Controller.getActorById(Long.parseLong(id));
	}
}
