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
import org.tudelft.wdm.imdb.models.Genre;
import org.tudelft.wdm.imdb.models.Keyword;
import org.tudelft.wdm.imdb.models.Movie;
import org.tudelft.wdm.imdb.models.Serie;

/**
 *
 * @author Piotr Tekieli <p.s.tekieli@student.tudelft.nl>
 * @version v0.1 (15.05.2016)
 * @version v0.2 (18.05.2016)
 * @version v0.3s (19.05.2016)
 * @version v0.4 (28.05.2016)
 * 
 **/
public class MovieController {    
       
    private final JDBC JDBC = new JDBC();    
    private final ArrayList<String> Queries = new ArrayList<>();
    
    public ArrayList<Long> SetActiveFiltersForCollection(Long offset, String sort) {        
        /* ---------------------SET DEFAULTS IF NULL------------------------ */        
        if (offset == null) 
            offset = (long)0;        
        if (sort == null)
            sort = "year";
        /* ----------------------------------------------------------------- */
        
        String Query = ("SELECT DISTINCT m.idmovies, m.title, m.year "
            + "FROM movies m "            
            + "ORDER BY m." + sort + " "            
            + "LIMIT 10 OFFSET " + offset + ";"); /* Q0 */
        
        ArrayList<Long> TemporaryCollection = new ArrayList<>();
        JDBC.PerformQuery(Query);        
        try {                
            while (JDBC.getResultSet().next()) {
                TemporaryCollection.add(JDBC.getResultSet().getLong("idmovies"));
            }
        } catch (SQLException ex) {
                Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return TemporaryCollection;
    }    
    public ArrayList<Long> SetActiveFiltersForCollectionByTitle (String title, String sort) {
        if (sort == null)
            sort = "year";
        ArrayList<Long> TemporaryCollection = new ArrayList<>();
        JDBC.PerformQuery("SELECT DISTINCT m.idmovies, m.year FROM movies m WHERE m.title ILIKE '" + title + "' ORDER BY m." + sort + ";");        
        try {                
            while (JDBC.getResultSet().next()) {
                TemporaryCollection.add(JDBC.getResultSet().getLong("idmovies"));
            }
        } catch (SQLException ex) {
                Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return TemporaryCollection;
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
    public ArrayList<Movie> GetMovieInformation(ArrayList<Long> id) {                        
        ArrayList<Movie> TemporaryCollection = new ArrayList<>(); 
        for (Long x : id) {
            if (Queries != null)
                Queries.clear();
            SetActiveFiltersForSingle(x);            
            Movie movie = GetBasicInformation();            
            if (movie != null) {
                GetActorsInformation(movie);
                GetGenresInformation(movie);
                GetKeywordsInformation(movie);
                GetSeriesInformation(movie);  
            }            
            TemporaryCollection.add(movie);
        }
        return TemporaryCollection;
    }
    public Movie GetBasicInformation() {
        JDBC.PerformQuery(Queries.get(0));
        Movie movie = null;
        try {
            while (JDBC.getResultSet().next()) {
                movie = new Movie(JDBC.getResultSet().getLong("idmovies"), JDBC.getResultSet().getString("title"), JDBC.getResultSet().getInt("year"));
            }
        } 
        catch (SQLException ex) {
            Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
        }
        JDBC.CloseConnection(); 
        return movie;
    }
    public void GetActorsInformation(Movie movie) {
        JDBC.PerformQuery(Queries.get(1));
        try {
            while (JDBC.getResultSet().next()) {
                movie.AddActor(new Actor(JDBC.getResultSet().getLong("idactors"), JDBC.getResultSet().getString("fname"), JDBC.getResultSet().getString("lname"), JDBC.getResultSet().getString("gender")));    
            }
        } 
        catch (SQLException ex) {
            Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
        }
        JDBC.CloseConnection();        
    }
    public void GetGenresInformation(Movie movie) {
        JDBC.PerformQuery(Queries.get(2));
        try {
            while (JDBC.getResultSet().next()) {
                movie.AddGenre(new Genre(JDBC.getResultSet().getLong("idgenres"), JDBC.getResultSet().getString("genre")));
            } 
        } catch (SQLException ex) {
            Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
        } 
        JDBC.CloseConnection();         
    }
    public void GetKeywordsInformation(Movie movie) {
        JDBC.PerformQuery(Queries.get(3));
        try {
            while (JDBC.getResultSet().next()) {
                movie.AddKeywordObject(new Keyword(JDBC.getResultSet().getLong("idkeywords"), JDBC.getResultSet().getString("keyword")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
        }
        JDBC.CloseConnection();         
    }
    public void GetSeriesInformation(Movie movie) {
        JDBC.PerformQuery(Queries.get(4));
        try {
            while (JDBC.getResultSet().next()) {
                movie.AddSerie(new Serie(JDBC.getResultSet().getLong("idseries"), JDBC.getResultSet().getString("name"), JDBC.getResultSet().getInt("season"), JDBC.getResultSet().getInt("number")));
            } 
        } catch (SQLException ex) {
            Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
        }
        JDBC.CloseConnection();    
    }   
}
