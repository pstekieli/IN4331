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
import org.tudelft.wdm.imdb.models.MessageJSON;
import org.tudelft.wdm.imdb.models.Movie;

/**
 *
 * @author Piotr Tekieli <p.s.tekieli@student.tudelft.nl>
 * @version v0.1 (15.05.2016)
 * @version v0.2 (18.05.2016)
 * @version v0.3s (19.05.2016)
 * 
 **/
public class MovieController {    
       
    private final ArrayList<String> Queries = new ArrayList<>();
    private final JDBC JDBC = new JDBC();
    private final MessageJSON MessageJSON = new MessageJSON();
    
    public void SetActiveFiltersForCollection(Long limit, Long offset, Integer year, Integer endyear, String sort) {        
        /* ---------------------SET DEFAULTS IF NULL------------------------ */
        if (limit == null)
            limit = (long)100;
        if (offset == null) 
            offset = (long)0;
        if (year == null && endyear == null) {
            if (sort != null) {
                Queries.add("SELECT DISTINCT m.idmovies, m.title, m.year "
                    + "FROM movies m "            
                    + "ORDER BY m." + sort + " "
                    + "LIMIT " + limit + " OFFSET " + offset + ";"); /* Q0 */
            }
            else {
               Queries.add("SELECT DISTINCT m.idmovies, m.title, m.year "
                    + "FROM movies m "            
                    + "ORDER BY m.idmovies "
                    + "LIMIT " + limit + " OFFSET " + offset + ";"); /* Q0 */ 
            }
            return;
        }            
        if (endyear == null)
            endyear = year;
        if (sort == null)
            sort = "idmovies";
        /* ----------------------------------------------------------------- */
        
        Queries.add("SELECT DISTINCT m.idmovies, m.title, m.year "
            + "FROM movies m "            
            + "WHERE m.year >= " + year + " AND m.year <= " + endyear + " "
            + "ORDER BY m." + sort + " "            
            + "LIMIT " + limit + " OFFSET " + offset + ";"); /* Q0 */
    }    
    public void SetActiveFiltersForSingle (long id) {
        Queries.add("SELECT DISTINCT m.idmovies, m.title, m.year "
            + "FROM movies m "            
            + "WHERE m.idmovies = " + id + ";"); /* Q0 */
        Queries.add("SELECT DISTINCT a.idactors, a.fname, a.lname, a.gender " +
            "FROM actors a " +
            "JOIN acted_in ai " +
            "ON a.idactors = ai.idactors " +
            "JOIN movies m " +
            "ON m.idmovies = ai.idmovies " +
            "WHERE m.idmovies = " + id + " " +
            "ORDER BY a.idactors;"); /* Q1 */
        Queries.add("SELECT DISTINCT g.idgenres, g.genre " +
            "FROM genres g " +
            "JOIN movies_genres mg " +
            "ON g.idgenres = mg.idgenres " +
            "JOIN movies m " +
            "ON m.idmovies = mg.idmovies " +
            "WHERE m.idmovies = " + id + " " +
            "ORDER BY g.idgenres;"); /* Q2 */
        Queries.add("SELECT DISTINCT k.idkeywords, k.keyword " +
            "FROM keywords k " +
            "JOIN movies_keywords mk " +
            " ON k.idkeywords = mk.idkeywords " +
            "JOIN movies m " +
            " ON m.idmovies = mk.idmovies " +
            "WHERE m.idmovies = " + id + " " + 
            " ORDER BY k.idkeywords;");
        Queries.add("SELECT DISTINCT s.idseries, s.name, s.season, s.number " +
            "FROM series s " +
            "JOIN movies m " +
            " ON m.idmovies = s.idmovies " +
            "WHERE m.idmovies = " + id + " " +
            "ORDER BY s.idseries"); /* Q3 */
            
    }
    
    public ArrayList<Movie> GetMovieListFromDB() {
        ArrayList<Movie> TemporaryCollection = new ArrayList<>();        
            try {                
                while (JDBC.getResultSet().next()) {
                    TemporaryCollection.add(new Movie(JDBC.getResultSet().getLong("idmovies"), JDBC.getResultSet().getString("title"), JDBC.getResultSet().getInt("year")));
                }
            } catch (SQLException ex) {
                Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
            }        
        JDBC.CloseConnection();
        return TemporaryCollection;
    }
    public MessageJSON GetShortMovieInformation(long id) {               
        SetActiveFiltersForSingle(id);
        JDBC.PerformQuery(Queries.get(0));
        getMessageJSON().AddMovie(GetMovieListFromDB().get(0));
        JDBC.CloseConnection();
        return getMessageJSON(); /* Get first (and only) position */
    }
    public MessageJSON GetDetailedMovieInformation(long id) {                        
        GetShortMovieInformation(id);
        GetActorsInformation();
        GetGenresInformation();
        GetKeywordsInformation();
        GetSeriesInformation();                       
        return getMessageJSON();
    }
    public MessageJSON GetAllMovies(Long limit, Long offset, Integer year, Integer endyear, String sort) {               
        SetActiveFiltersForCollection(limit, offset, year, endyear, sort);
        JDBC.PerformQuery(Queries.get(0));
        ArrayList<Movie> Movies = GetMovieListFromDB();
        for (int i = 0; i < Movies.size(); i++)        
            getMessageJSON().AddMovie(Movies.get(i));
        JDBC.CloseConnection();
        return getMessageJSON();
    }
    public MessageJSON GetActorsInformation() {
        JDBC.PerformQuery(Queries.get(1));
        try {
            while (JDBC.getResultSet().next()) {
                getMessageJSON().AddActor(JDBC.getResultSet().getLong("idactors"), JDBC.getResultSet().getString("fname"), JDBC.getResultSet().getString("lname"), JDBC.getResultSet().getString("gender"));    
            }
        } 
        catch (SQLException ex) {
            Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
        }
        JDBC.CloseConnection(); 
        return getMessageJSON();
    }
    public MessageJSON GetGenresInformation() {
        JDBC.PerformQuery(Queries.get(2));
        try {
            while (JDBC.getResultSet().next()) {
                getMessageJSON().AddGenre(JDBC.getResultSet().getLong("idgenres"), JDBC.getResultSet().getString("genre"));
            } 
        } catch (SQLException ex) {
            Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
        } 
        JDBC.CloseConnection(); 
        return getMessageJSON();
    }
    public MessageJSON GetKeywordsInformation() {
        JDBC.PerformQuery(Queries.get(3));
        try {
            while (JDBC.getResultSet().next()) {
                getMessageJSON().AddKeyword(JDBC.getResultSet().getLong("idkeywords"), JDBC.getResultSet().getString("keyword"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
        }
        JDBC.CloseConnection(); 
        return getMessageJSON();
    }
    public MessageJSON GetSeriesInformation() {
        JDBC.PerformQuery(Queries.get(4));
        try {
            while (JDBC.getResultSet().next()) {
                getMessageJSON().AddSerie(JDBC.getResultSet().getLong("idseries"), JDBC.getResultSet().getString("name"), JDBC.getResultSet().getInt("season"), JDBC.getResultSet().getInt("number"));
            } 
        } catch (SQLException ex) {
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
