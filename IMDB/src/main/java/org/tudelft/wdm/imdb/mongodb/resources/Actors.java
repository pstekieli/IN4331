package org.tudelft.wdm.imdb.mongodb.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
	/**
	 * Retrieve a single actor by his/her id.
	 * 
	 * @param id Id of actor.
	 * @return Actor details or nothing.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{actorId}")
	public Actor actorById(@PathParam("actorId") String id) {
		return Controller.getActorById(Long.parseLong(id));
	}
	
	/**
	 * Retrieve matching actors by a first name and/or
	 * last name. An empty list is returned if no actors
	 * meet the specified query.
	 * 
	 * If no part of a name is specified, then an empty
	 * list is returned.
	 * 
	 * @param firstName (Partial) first name of actor.
	 * @param lastName (Partial) last name of actor.
	 * @return List of actor details.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Actor> actorsByName(@QueryParam("firstname") String firstName, @QueryParam("lastname") String lastName) {
		if (firstName == null && lastName == null) {
			return new ArrayList<>();
		}
		
		List<Actor> actors = Controller.getActorsByName(firstName, lastName);
		
		return actors;
	}
	
	/**
	 * Retrieve short actor statistics by id.
	 * Returns just the name and the number of movies.
	 * 
	 * @param id Id of actor.
	 * @return Actor object with name and number of movies.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{actorId}/stats")
	public Actor actorByIdStatistics(@PathParam("actorId") String id) {
		return Controller.getActorByIdStats(Long.parseLong(id));
	}
	
	/**
	 * Retrieve matching actors by a first name and/or
	 * last name. An empty list is returned if no actors
	 * meet the specified query.
	 * 
	 * If no part of a name is specified, then an empty
	 * list is returned.
	 * 
	 * @param firstName (Partial) first name of actor.
	 * @param lastName (Partial) last name of actor.
	 * @return List of short actor statistics.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/stats")
	public List<Actor> actorByNameStatistics(@QueryParam("firstname") String firstName, @QueryParam("lastname") String lastName) {
		if (firstName == null && lastName == null) {
			return new ArrayList<>();
		}
		
		List<Actor> actors = Controller.getActorsByNameStats(firstName, lastName);
		
		return actors;
	}
}
