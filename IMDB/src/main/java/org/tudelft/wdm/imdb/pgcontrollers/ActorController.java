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
package org.tudelft.wdm.imdb.pgcontrollers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tudelft.wdm.imdb.models.Actor;
import org.tudelft.wdm.imdb.models.MessageJSON;

/**
 *
 * @author Piotr Tekieli <p.s.tekieli@student.tudelft.nl>
 * @version v0.1 (15.05.2016)
 * @version v0.2 (18.05.2016)
 * @version v0.3s (19.05.2016)
 * 
 **/
public class ActorController {
    
    private final ArrayList<String> Queries = new ArrayList<>();
    private final JDBC JDBC = new JDBC();
    private final MessageJSON MessageJSON = new MessageJSON();
    
    public void SetActiveFiltersForCollection(Long limit, Long offset, String sort) {        
        /* ---------------------SET DEFAULTS IF NULL------------------------ */
        if (limit == null)
            limit = Long.parseLong("100");
        if (offset == null) 
            offset = Long.parseLong("0");        
        if (sort == null)
            sort = "idactors";
        /* ----------------------------------------------------------------- */
        
        Queries.add("SELECT DISTINCT a.idactors, a.fname, a.lname, a.gender "
            + "FROM actors a "            
            + "ORDER BY a." + sort + " " 
            + "LIMIT " + limit + " OFFSET " + offset + ";");    
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
   
    public ArrayList<Actor> GetActorListFromDB() {
        ArrayList<Actor> TemporaryCollection = new ArrayList<>();        
            try {                
                while (JDBC.getResultSet().next()) {
                    TemporaryCollection.add(new Actor(JDBC.getResultSet().getLong("idactors"), JDBC.getResultSet().getString("fname"), JDBC.getResultSet().getString("lname"), JDBC.getResultSet().getString("gender")));
                }
            } catch (SQLException ex) {
                Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
            }
        JDBC.CloseConnection();
        return TemporaryCollection;
    }
    public MessageJSON GetAllActors(Long limit, Long offset, String sort) {
        SetActiveFiltersForCollection(limit, offset, sort);
        JDBC.PerformQuery(Queries.get(0));
        ArrayList<Actor> Actors = GetActorListFromDB();
        for (int i = 0; i < Actors.size(); i++)        
            getMessageJSON().AddActor(Actors.get(i));
        JDBC.CloseConnection();
        return getMessageJSON();
    }
    public MessageJSON GetShortActorInformation(long id, String sort) {               
        SetActiveFiltersForSingle(id, sort);
        JDBC.PerformQuery(Queries.get(0));
        getMessageJSON().AddActor(GetActorListFromDB().get(0));
        JDBC.CloseConnection();
        return getMessageJSON(); /* Get first (and only) position */
    }
    public MessageJSON GetDetailedActorInformation(long id, String sort) {        
        GetShortActorInformation(id, sort);
        GetMoviesInformation();
        GetActorStatistics();
        return getMessageJSON();
    }   
    public MessageJSON GetMoviesInformation() {
        JDBC.PerformQuery(Queries.get(1));
        try {
            while (JDBC.getResultSet().next()) {
                getMessageJSON().AddMovie(JDBC.getResultSet().getLong("idmovies"), JDBC.getResultSet().getString("title"), JDBC.getResultSet().getInt("year"));    
            }
        } 
        catch (SQLException ex) {
            Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
        }
        JDBC.CloseConnection(); 
        return getMessageJSON();
    }
    public MessageJSON GetActorStatistics() {        
        JDBC.PerformQuery(Queries.get(2));
        try {
            while (JDBC.getResultSet().next()) {
                getMessageJSON().SetStatistic(JDBC.getResultSet().getInt("number"));    
            }
        } 
        catch (SQLException ex) {
            Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
        }
        JDBC.CloseConnection(); 
        return getMessageJSON();
    }
    
     /**
     * @return the MessageJSON
     */
    public MessageJSON getMessageJSON() {
        return MessageJSON;
    }
}
