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
package org.tudelft.wdm.imdb.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;
import java.net.URI;
import java.util.ArrayList;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Piotr Tekieli <p.s.tekieli@student.tudelft.nl>
 * @version v0.1 (15.05.2016)
 * @version v0.2 (18.05.2016)
 * @version v0.3s (19.05.2016)
 * 
 **/
@JsonSerialize(include = Inclusion.NON_NULL) /* Omit empty ArrayLists */
@XmlRootElement(name = "JSON Response")
public class MessageJSON {
    
    public MessageJSON() {}    
    @XmlElement (name = "Actors")
    private ArrayList<Actor> Actors;    
    @XmlElement (name = "Genres")
    private ArrayList<Genre> Genres;    
    @XmlElement (name = "Keywords")
    private ArrayList<Keyword> Keywords;    
    @XmlElement (name = "Series")
    private ArrayList<Serie> Series; 
    @XmlElement (name = "Movies")
    private ArrayList<Movie> Movies;
    @XmlElement (name = "References")
    private ArrayList<Link> References;
    @XmlElement (name = "Number Of Movies")
    private Integer Statistic = null;
        
    public void AddActor(long idActor, String FirstName, String LastName, String Gender) {
        if (Actors == null)
            Actors = new ArrayList<>();        
        Actors.add(new Actor(idActor, FirstName, LastName, Gender));        
    }
    
    public void AddGenre(long idGenre, String Label) {
        if (Genres == null)
            Genres = new ArrayList<>();        
        Genres.add(new Genre(idGenre, Label));
    }
    
    public void AddKeyword(long idKeyword, String Label) {
        if (Keywords == null)
            Keywords = new ArrayList<>();        
        Keywords.add(new Keyword(idKeyword, Label));
    }
    
    public void AddSerie(long idSerie, String Title, int Season, int Number) {
        if (Series == null)
            Series = new ArrayList<>();        
        Series.add(new Serie(idSerie, Title, Season, Number));
    }
    
    public void AddMovie(long idMovie, String Title, int Year) {
        if (Movies == null)
            Movies = new ArrayList<>();        
        Movies.add(new Movie(idMovie, Title, Year));
    }
    
    public void AddReference(String RefURI, String RefRel) {
        if (References == null) {
            References = new ArrayList<>();
        }
        URI uri = URI.create(RefURI);
        Link link = Link.fromUri(uri).rel(RefRel).build();
        References.add(link);
    }
    
    public void AddActor(Actor actor) {
        if (Actors == null)
            Actors = new ArrayList<>();        
        Actors.add(actor);        
    }
    
    public void AddGenre(Genre genre) {
        if (Genres == null)
            Genres = new ArrayList<>();        
        Genres.add(genre);
    }
    
    public void AddKeyword(Keyword keyword) {
        if (Keywords == null)
            Keywords = new ArrayList<>();        
        Keywords.add(keyword);
    }
    
    public void AddSerie(Serie serie) {
        if (Series == null)
            Series = new ArrayList<>();        
        Series.add(serie);
    }
    
    public void AddMovie(Movie movie) {
        if (Movies == null)
            Movies = new ArrayList<>();        
        Movies.add(movie);
    }
    
    public void SetStatistic(Integer Statistic) {
        this.Statistic = Statistic;
    }
    
}
