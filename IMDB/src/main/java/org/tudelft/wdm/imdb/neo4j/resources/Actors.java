/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tudelft.wdm.imdb.neo4j.resources;

import java.util.ArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.neo4j.driver.v1.*;
import org.tudelft.wdm.imdb.models.Actor;
import org.tudelft.wdm.imdb.models.Movie;
import org.tudelft.wdm.imdb.neo4j.controllers.ActorController;
import org.tudelft.wdm.imdb.neo4j.controllers.Controller;

/**
 *
 * @author Tom
 */
@Path("neo4j/actors")
public class Actors {
    
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @param offset
     * @param sort
     * @param fname
     * @param lname
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Actor> getAllActors(@QueryParam("offset") String offset, @QueryParam("orderby") String sort, @QueryParam("firstname") String fname, @QueryParam("lastname") String lname){
        long offset_arg = 0;
        if (offset!=null){
            try {
                offset_arg = Long.parseLong(offset);
            } catch (NumberFormatException ex){}
        }
        
        String sort_arg;
        if (sort==null) sort_arg = "";
        else {
            switch (sort.toLowerCase()){
                case "fname":
                case "lname":
                case "gender": sort_arg = " ORDER BY " + sort; break;
                case "mname":
                case "number": sort_arg = " ORDER BY a." + sort; break;
                default: sort_arg = "";
            }
        }
        
        ArrayList<String> where_args = new ArrayList<>();
        // Regex expression for SQL's "LIKE %str%". (?i) for case insensitive.
        if (fname!=null) where_args.add("a.fname =~ '.*(?i)" + fname + ".*'");
        if (lname!=null) where_args.add("a.lname>=~ '.*(?i)" + lname + ".*'");
        String where = "";
        if (!where_args.isEmpty()){
            where = " WHERE ";
            for (String arg : where_args){
                where+=arg + " AND ";
            }
            where = where.substring(0, where.length()-5) + " ";
        }
        
        String query = "MATCH (a:actors)" + where
                + "RETURN a.idactors AS id, a.fname AS fname, a.lname AS lname, a.gender AS gender"
                + sort_arg
                + " SKIP " + offset_arg
                + " LIMIT 10";
        
        Statement s = new Statement(query);
        ArrayList<Actor> actors = ActorController.getActorsFull(s);
        return actors;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{actorId}")
    public ArrayList<Actor> displayDetailed(@PathParam("actorId") Long id, @QueryParam("orderby") String sort){
        Statement s = new Statement("MATCH (a:actors {idactors:" + id
                + "}) RETURN a.idactors AS id, a.fname AS fname, a.lname AS lname, a.gender AS gender");
        Controller.keepOpen();
        ArrayList<Actor> actor = ActorController.getActorsFull(s);
        Controller.forceClose();
        return actor;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{actorId}/movies")
    public ArrayList<Movie> displayMovies(@PathParam("actorId") Long id, @QueryParam("orderby") String sort){
        Statement s = new Statement("MATCH (a:actors {idactors:" + id
                + "}) RETURN a.idactors AS id, a.fname AS fname, a.lname AS lname, a.gender AS gender");
        return ActorController.getMoviesInformation(id, sort);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{actorId}/statistics")
    public Integer displayStatistics(@PathParam("actorId") Long id){
        Statement s = new Statement("MATCH (a:actors {idactors:" + id
                + "}) RETURN a.idactors AS id, a.fname AS fname, a.lname AS lname, a.gender AS gender");
        return ActorController.getActorStatistics(id);
    }
}
