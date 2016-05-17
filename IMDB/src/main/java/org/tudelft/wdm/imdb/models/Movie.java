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
package org.tudelft.wdm.imdb.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Piotrek
 */
@JsonSerialize(include = Inclusion.NON_NULL)

@XmlRootElement(name = "Movie")
public class Movie {    
   
    @XmlElement
    private final long idMovie;      
    @XmlElement
    private final String Title;    
    @XmlElement
    private final int Year;    
    @XmlElement
    private ArrayList<Actor> Actors;    
    @XmlElement
    private ArrayList<Genre> Genres;    
    @XmlElement
    private ArrayList<Keyword> Keywords;    
    @XmlElement
    private ArrayList<Serie> Series; 
    
    public Movie(long idMovie, String Title, int Year) {
        this.idMovie = idMovie;
        this.Title = Title;
        this.Year = Year;       
    }
    
    public void AddActor(long idActor, String FirstName, String LastName, String Gender) {
        if (getActors() == null)
            Actors = new ArrayList<>();
        Actor actor = new Actor(idActor, FirstName, LastName, Gender);
        getActors().add(actor);        
    }
    
    public void AddGenre(long idGenre, String Label) {
        if (getGenres() == null)
            Genres = new ArrayList<>();
        Genre genre = new Genre(idGenre, Label);
        getGenres().add(genre);
    }
    
    public void AddKeyword(long idKeyword, String Label) {
        if (getKeywords() == null)
            Keywords = new ArrayList<>();
        Keyword keyword = new Keyword(idKeyword, Label);
        getKeywords().add(keyword);
    }
    
    public void AddSerie(long idSerie, String Title, int Season, int Number) {
        if (getSeries() == null)
            Series = new ArrayList<>();
        Serie serie = new Serie(idSerie, Title, Season, Number);
        getSeries().add(serie);
    }

    /**
     * @return the Actors
     */
    public ArrayList<Actor> getActors() {        
        return Actors;
    }

    /**
     * @return the Genres
     */
    public ArrayList<Genre> getGenres() {
        return Genres;
    }

    /**
     * @return the Keywords
     */
    public ArrayList<Keyword> getKeywords() {
        return Keywords;
    }

    /**
     * @return the Series
     */
    public ArrayList<Serie> getSeries() {
        return Series;
    }
}
