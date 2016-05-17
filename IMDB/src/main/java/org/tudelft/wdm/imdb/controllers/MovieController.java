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
import org.tudelft.wdm.imdb.models.Movie;
import org.tudelft.wdm.imdb.postgres.JDBC;

/**
 *
 * @author Piotrek
 */
public class MovieController {
    
    private String Query;   
    private final String[] Queries = new String[4];
    private final JDBC connection = new JDBC();
    
    public void SetMoviesFilter(long limit, long offset) {
        Query = "SELECT DISTINCT m.idmovies, m.title, m.year "
            + "FROM movies m "            
            + "ORDER BY idmovies "
            + "LIMIT " + limit + " OFFSET " + offset + ";";
    }    
    public void SetMoviesFilter(long limit, long offset, String sort) {
        Query = "SELECT DISTINCT m.idmovies, m.title, m.year "
            + "FROM movies m "            
            + "ORDER BY " + sort + " "
            + "LIMIT " + limit + " OFFSET " + offset + ";";
    }    
    public void SetMoviesFilter(long limit, long offset, int year) {
        Query = "SELECT DISTINCT m.idmovies, m.title, m.year "
            + "FROM movies m "            
            + "WHERE m.year = " + year + " "
            + "ORDER BY idmovies "
            + "LIMIT " + limit + " OFFSET " + offset + ";";
    }    
    public void SetMoviesFilter(long limit, long offset, int year, String sort) {
        Query = "SELECT DISTINCT m.idmovies, m.title, m.year "
            + "FROM movies m "
            + "WHERE m.year = " + year + " " 
            + "ORDER BY " + sort + " "
            + "LIMIT " + limit + " OFFSET " + offset + ";";
    }   
    public void SetMoviesFilter (long id) {
        Query = "SELECT DISTINCT m.idmovies, m.title, m.year "
            + "FROM movies m "
            + "WHERE m.idmovies = " + id + ";";            
    }
    public void SetDetailedMoviesFilter (long id) {
        Queries[0] = "SELECT DISTINCT a.idactors, a.fname, a.lname, a.gender " +
            "FROM actors a " +
            "JOIN acted_in ai " +
            "ON a.idactors = ai.idactors " +
            "JOIN movies m " +
            "ON m.idmovies = ai.idmovies " +
            "WHERE m.idmovies = " + id + " " +
            "ORDER BY a.idactors;";
        Queries[1] = "SELECT DISTINCT g.idgenres, g.genre " +
            "FROM genres g " +
            "JOIN movies_genres mg " +
            "ON g.idgenres = mg.idgenres " +
            "JOIN movies m " +
            "ON m.idmovies = mg.idmovies " +
            "WHERE m.idmovies = " + id + " " +
            "ORDER BY g.idgenres;";
        Queries[2] = "SELECT DISTINCT k.idkeywords, k.keyword " +
            "FROM keywords k " +
            "JOIN movies_keywords mk " +
            " ON k.idkeywords = mk.idkeywords " +
            "JOIN movies m " +
            " ON m.idmovies = mk.idmovies " +
            "WHERE m.idmovies = " + id + " " + 
            " ORDER BY k.idkeywords;";
        Queries[3] = "SELECT DISTINCT s.idseries, s.name, s.season, s.number " +
            "FROM series s " +
            "JOIN movies m " +
            " ON m.idmovies = s.idmovies " +
            "WHERE m.idmovies = " + id + " " +
            "ORDER BY s.idseries";
            
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
    
    public ArrayList<Movie> GetMovies() {
        ArrayList<Movie> collection = new ArrayList<>();
        ResultSet rs = ExecuteStatement();
            try {                
                while (rs.next()) {
                    collection.add(new Movie(rs.getLong("idmovies"), rs.getString("title"), rs.getInt("year")));
                }
            } catch (SQLException ex) {
                Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
            }
        connection.CloseConnection();
        return collection;
    }
    public Movie GetMovieInformation(long id) {
        SetMoviesFilter(id);        
        return GetMovies().get(0);
    }
    public Movie GetDetailedMovieInformation(long id) {
        SetMoviesFilter(id);
        Movie movie = GetMovies().get(0);
        SetDetailedMoviesFilter(id);        
        for (int i = 0; i < 4; i++) {
            Query = Queries[i];
            ResultSet rs = ExecuteStatement();
            switch (i) {
                case 0 :
                    try {
                        while (rs.next()) {
                            movie.AddActor(rs.getLong("idactors"), rs.getString("fname"), rs.getString("lname"), (rs.getString("gender")));    
                        }      
                    } catch (SQLException ex) {
                        Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
                    }                    
                    break;
                case 1 :
                    try {
                        while (rs.next()) {
                            movie.AddGenre(rs.getLong("idgenres"), rs.getString("genre"));
                        } 
                    } catch (SQLException ex) {
                        Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
                    }                    
                    break;
                case 2 :
                    try {
                        while (rs.next()) {
                            movie.AddKeyword(rs.getLong("idkeywords"), rs.getString("keyword"));
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
                    }                    
                    break;
                case 3 :
                    try {
                        while (rs.next()) {
                            movie.AddSerie(rs.getLong("idseries"), rs.getString("name"), rs.getInt("season"), rs.getInt("number"));
                        } 
                    } catch (SQLException ex) {
                            Logger.getLogger(MovieController.class.getName()).log(Level.SEVERE, null, ex);
                    }                    
                    break;                    
            }  
            connection.CloseConnection();
        }        
        return movie;
    }    

}
