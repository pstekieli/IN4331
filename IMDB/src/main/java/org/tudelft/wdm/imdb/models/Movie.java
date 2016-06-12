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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;
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
 * @version v0.4 (28.05.2016)
 * 
 **/
@JsonSerialize(include = Inclusion.NON_NULL) /* Omit empty ArrayLists */
//To prevent the getters being used in the JSON output
@JsonIgnoreProperties({"id","title","year"})
@XmlRootElement(name = "Movie")
public class Movie {    
   
    @XmlElement (name = "Movie ID")
    private final long idMovie;      
    @XmlElement (name = "Title")
    private final String Title;    
    @XmlElement (name = "Year")
    private final Integer Year;    
    
    @XmlElement (name = "Actors")
    private ArrayList<Actor> Actors;
    @XmlElement (name = "Genres")
    private ArrayList<Genre> Genres;
    @XmlElement (name = "GenreLabels")
    private ArrayList<String> GenreLabels;
    @XmlElement (name = "KeywordObjects")
    private ArrayList<Keyword> KeywordObjects;
    @XmlElement (name = "Keywords")
    private ArrayList<String> Keywords;
    @XmlElement (name = "Series")
    private ArrayList<Serie> Series;
    @XmlElement (name = "References")
    private ArrayList<Link> References;
    
    @XmlElement (name = "SeriesName")
    private String SeriesName;
    
    public Movie(long idMovie, String Title, Integer Year) {
        this.idMovie = idMovie;
        this.Title = Title;
        this.Year = Year;
    }
    
    public long getId() {
    	return idMovie;
    }
    
    public String getTitle() {
    	return Title;
    }
    
    public Integer getYear() {
    	return Year;
    }
    
    public void AddReference(Link Link) {
        if (References == null) {
            References = new ArrayList<>();
        }        
        References.add(Link);
    }
    
    public void AddActor(Actor actor) {
        if (Actors == null)
            Actors = new ArrayList<>();        
        Actors.add(actor);        
    }
    
    public void AddGenreLabel(String genreLabel) {
        if (GenreLabels == null)
        	GenreLabels = new ArrayList<>();        
        GenreLabels.add(genreLabel);
    }
    
    public void AddGenre(Genre genre) {
        if (Genres == null)
        	Genres = new ArrayList<>();        
        Genres.add(genre);
    }
    
    public void AddKeywordObject(Keyword keyword) {
        if (KeywordObjects == null)
        	KeywordObjects = new ArrayList<>();        
        KeywordObjects.add(keyword);
    }
    
    public void AddKeyword(String keyword) {
    	if (Keywords == null) {
    		Keywords = new ArrayList<>();
    	}
    	Keywords.add(keyword);
    }
    
    public void AddSerie(Serie serie) {
        if (Series == null)
            Series = new ArrayList<>();        
        Series.add(serie);
    }
    
    public ArrayList<Actor> displayActors() {
        if (Genres==null) return new ArrayList<>();
        return Actors;
    }
    
    public ArrayList<Genre> displayGenres() {
        if (Genres==null) return new ArrayList<>();
        return Genres;
    }
    
    public ArrayList<Keyword> displayKeywordObjects() {
        if (Genres==null) return new ArrayList<>();
        return KeywordObjects;
    }
    
    public ArrayList<Serie> displaySeries() {
        if (Genres==null) return new ArrayList<>();
        return Series;
    }
    
    public void setSeriesName(String seriesName) {
    	this.SeriesName = seriesName;
    }
}
