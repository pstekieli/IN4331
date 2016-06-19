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
package org.tudelft.wdm.imdb.postgresql.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tudelft.wdm.imdb.models.Actor;
import org.tudelft.wdm.imdb.models.Movie;

/**
 *
 * @author Piotr Tekieli <p.s.tekieli@student.tudelft.nl>
 * @version v0.1 (15.05.2016)
 * @version v0.2 (18.05.2016)
 * @version v0.3s (19.05.2016)
 * @version v0.4 (28.05.2016)
 * @version v0.5 (08.06.2016)
 * @version v1.0 (19.06.2016)
 * 
 **/
public class ActorController {
    
    private final ArrayList<String> Queries = new ArrayList<>();
    private final JDBC JDBC = new JDBC();    
    private final Long limit = Long.parseLong("10");
    private Long offset;
    private String sort;    
    
    public void SetDefaultsIfNull(Long voffset, String vsort) {
        /* ---------------------SET DEFAULTS IF NULL------------------------ */        
        if (offset == null) 
            this.offset = Long.parseLong("0");
        else
            this.offset = voffset;
        if (sort == null || !sort.equals("fname") || !sort.equals("lname"))
            this.sort = "idactors";
        else
            this.sort = vsort;
        /* ----------------------------------------------------------------- */  
    }    
    public ArrayList<Long> SetActiveFiltersForCollection(Long voffset, String vsort) {        
        SetDefaultsIfNull(voffset, vsort);      
        String Query = "SELECT DISTINCT a.idactors, a.fname, a.lname, a.gender "
            + "FROM actors a "            
            + "ORDER BY a." + sort + " " 
            + "LIMIT " + limit + " OFFSET " + offset + ";";    
        ArrayList<Long> TemporaryCollection = new ArrayList<>();
        JDBC.PerformQuery(Query);        
        try {                
            while (JDBC.getResultSet().next()) {
                TemporaryCollection.add(JDBC.getResultSet().getLong("idactors"));
            }
        } catch (SQLException ex) {
                Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return TemporaryCollection;
    }  
    public ArrayList<Long> SetActiveFiltersForCollectionByName(String fname, String lname, String vsort) {        
        SetDefaultsIfNull(null, vsort);
        String Query;
        if (fname != null && lname != null) {
          Query = "SELECT DISTINCT a.idactors, a.fname, a.lname, a.gender "
            + "FROM actors a " 
            + "WHERE a.fname ILIKE '%" + fname + "%' AND a.lname ILIKE '%" + lname + "%' "      
            + "ORDER BY a." + sort + ";";               
        }
        else if (fname != null && lname == null) {
          Query = "SELECT DISTINCT a.idactors, a.fname, a.lname, a.gender "
            + "FROM actors a " 
            + "WHERE a.fname ILIKE '%" + fname + "%' "      
            + "ORDER BY a." + sort + ";";    
        }
        else if (fname == null && lname != null) {
          Query = "SELECT DISTINCT a.idactors, a.fname, a.lname, a.gender "
            + "FROM actors a " 
            + "WHERE a.lname ILIKE '%" + lname + "%' "      
            + "ORDER BY a." + sort + ";";    
        }
        else {
          Query = "SELECT DISTINCT a.idactors, a.fname, a.lname, a.gender "
            + "FROM actors a "                
            + "ORDER BY a." + sort + ";";    
        }
        ArrayList<Long> TemporaryCollection = new ArrayList<>();
        JDBC.PerformQuery(Query);        
        try {                
            while (JDBC.getResultSet().next()) {
                TemporaryCollection.add(JDBC.getResultSet().getLong("idactors"));
            }
        } catch (SQLException ex) {
                Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return TemporaryCollection; 
    }
    public void SetActiveFiltersForSingle (long id, String sort) {
        Queries.add("SELECT DISTINCT a.idactors, a.fname, a.lname, a.gender "
            + "FROM actors a "            
            + "WHERE a.idactors=" + id + ";"); /* Q0 */
        if (sort == null) {
            Queries.add("SELECT DISTINCT m.idmovies, m.title, m.year " +
                "FROM movies m " +
                "JOIN acted_in ai " +
                "ON m.idmovies = ai.idmovies " +
                "JOIN actors a " +
                "ON a.idactors = ai.idactors " +
                "WHERE a.idactors = " + id + " " +
                "ORDER BY m.idmovies;");
        }
        else {
            if (!sort.equals("idmovies") && !sort.equals("title") && !sort.equals("year")) {
                sort = "title";
            }
            Queries.add("SELECT DISTINCT m.idmovies, m.title, m.year " +
                "FROM movies m " +
                "JOIN acted_in ai " +
                "ON m.idmovies = ai.idmovies " +
                "JOIN actors a " +
                "ON a.idactors = ai.idactors " +
                "WHERE a.idactors = " + id + " " +
                "ORDER BY m." + sort + ";");            
        }
        Queries.add("SELECT COUNT(DISTINCT m.title) AS number " +
            "FROM actors a " +
            "JOIN acted_in ai " +
            "ON a.idactors=ai.idactors " +
            "JOIN movies m " +
            "ON ai.idmovies=m.idmovies " +                
            "WHERE a.idactors=" + id + ";");
    }   
    public ArrayList<Actor> GetActorInformation(ArrayList<Long> id, String sort) {
        ArrayList<Actor> TemporaryCollection = new ArrayList<>(); 
        for (Long x : id) {
            if (Queries != null)
                Queries.clear();
            SetActiveFiltersForSingle(x, sort);            
            Actor actor = GetBasicInformation();            
            if (actor != null) {
                GetMoviesInformation(actor);
                GetActorStatistics(actor);  
            }            
            TemporaryCollection.add(actor);
        }
        return TemporaryCollection;
    }
    public Actor GetBasicInformation() {
        JDBC.PerformQuery(Queries.get(0));
        Actor actor = null;
        try {
            while (JDBC.getResultSet().next()) {
                actor = new Actor(JDBC.getResultSet().getLong("idactors"), JDBC.getResultSet().getString("fname"), JDBC.getResultSet().getString("lname"), JDBC.getResultSet().getString("gender"));
            }
        } 
        catch (SQLException ex) {
            Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
        }
        JDBC.CloseConnection(); 
        return actor;
    }
    public void GetMoviesInformation(Actor actor) {
        JDBC.PerformQuery(Queries.get(1));
        try {
            while (JDBC.getResultSet().next()) {
                actor.AddMovie(new Movie(JDBC.getResultSet().getLong("idmovies"), JDBC.getResultSet().getString("title"), JDBC.getResultSet().getInt("year")));    
            }
        } 
        catch (SQLException ex) {
            Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
        }
        JDBC.CloseConnection();        
    }
    public void GetActorStatistics(Actor actor) {        
        JDBC.PerformQuery(Queries.get(2));
        try {
            while (JDBC.getResultSet().next()) {
                actor.SetStatistic(JDBC.getResultSet().getInt("number"));    
            }
        } 
        catch (SQLException ex) {
            Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
        }
        JDBC.CloseConnection();        
    }    
}
