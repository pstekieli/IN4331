/*
 * The MIT License
 *
 * Copyright 2016 Piotrek.
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
package org.tudelft.wdm.imdb.controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tudelft.wdm.imdb.models.Actor;
import org.tudelft.wdm.imdb.postgres.JDBC;

/**
 *
 * @author Piotrek
 */
public class ActorController {
    
    private String Query;
    private final JDBC connection = new JDBC();
    
    public void SetActorsFilter(long limit, long offset) {
        Query = "SELECT DISTINCT a.idactors, a.fname, a.lname, a.gender "
            + "FROM actors a "            
            + "ORDER BY idactors "
            + "LIMIT " + limit + " OFFSET " + offset + ";";
    }    
    public void SetActorsFilter(long limit, long offset, String sort) {
        Query = "SELECT DISTINCT a.idactors, a.fname, a.lname, a.gender "
            + "FROM actors a "            
            + "ORDER BY " + sort + " "
            + "LIMIT " + limit + " OFFSET " + offset + ";";
    }
    public void SetActorsFilter (long id) {
        Query = "SELECT DISTINCT a.idactors, a.fname, a.lname, a.gender "
            + "FROM actors a "
            + "WHERE a.idactors = " + id + ";";            
    }    
    public void GetMoviesByActorNoFilter (long id) {
        Query = "SELECT DISTINCT m.idmovies, m.title, m.year " +
            "FROM movies m " +
            "JOIN acted_in ai " +
            "ON m.idmovies = ai.idmovies " +
            "JOIN actors a " +
            "ON a.idactors = ai.idactors " +
            "WHERE a.idactors = " + id + " " +
            "ORDER BY m.idmovies;" ;
    }
    public void GetMoviesByActorFilter (long id, String order) {
        Query = "SELECT DISTINCT m.idmovies, m.title, m.year " +
            "FROM movies m " +
            "JOIN acted_in ai " +
            "ON m.idmovies = ai.idmovies " +
            "JOIN actors a " +
            "ON a.idactors = ai.idactors " +
            "WHERE a.idactors = " + id + " " +
            "ORDER BY m." + order + ";";
    }
    
    public ResultSet ExecuteStatement() {        
        ResultSet rs = null;        
        try {
            connection.EstablishConnection();
            rs = connection.PerformQuery(Query);            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
        }    
        return rs;
    }  
    
    public ArrayList<Actor> GetActors() {
        ArrayList<Actor> collection = new ArrayList<>();
        ResultSet rs = ExecuteStatement();
            try {                
                while (rs.next()) {
                    collection.add(new Actor(rs.getLong("idactors"), rs.getString("fname"), rs.getString("lname"), rs.getString("gender")));
                }
            } catch (SQLException ex) {
                Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
            }
        connection.CloseConnection();
        return collection;
    }
    public Actor GetActorInformation(long id) {
        SetActorsFilter(id);        
        return GetActors().get(0);
    }
    public Actor GetDetailedActorInformation(long id) {
        SetActorsFilter(id);
        Actor actor = GetActors().get(0);
        GetMoviesByActorNoFilter(id);
        ResultSet rs = ExecuteStatement();
        try {
            while (rs.next())
                actor.AddMovie(rs.getLong("idmovies"), rs.getString("title"), rs.getInt("year"));
        } catch (SQLException ex) {
            Logger.getLogger(ActorController.class.getName()).log(Level.SEVERE, null, ex);
        }
        connection.CloseConnection();
        return actor;
    }
    
    public Actor GetDetailedActorInformationFilter(long id, String filter) {
        SetActorsFilter(id);
        Actor actor = GetActors().get(0);
        GetMoviesByActorFilter(id, filter);
        ResultSet rs = ExecuteStatement();
        try {
            while (rs.next())
                actor.AddMovie(rs.getLong("idmovies"), rs.getString("title"), rs.getInt("year"));
        } catch (SQLException ex) {
            Logger.getLogger(ActorController.class.getName()).log(Level.SEVERE, null, ex);
        }
        connection.CloseConnection();
        return actor;
    }
}
