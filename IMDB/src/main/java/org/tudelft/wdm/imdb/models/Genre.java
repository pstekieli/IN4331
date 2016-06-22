package org.tudelft.wdm.imdb.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;
import java.util.ArrayList;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@JsonSerialize(include = Inclusion.NON_NULL) /* Omit empty ArrayLists */
//To prevent the getters being used in the JSON output
@JsonIgnoreProperties({"id"})
@XmlRootElement (name = "Genre")
public class Genre {
    @XmlElement (name = "Genre ID")
    private final Long idGenre;
    @XmlElement (name = "Name")
    private final String Label;    
    
    @XmlElement (name = "Movies")
    private ArrayList<Movie> Movies;
    @XmlElement (name = "References")
    private ArrayList<Link> References;
    @XmlElement (name = "Number Of Movies")
    private Integer Statistic = null;
    
    public Genre(Long idGenre, String Label) {
        this.Label = Label;
        this.idGenre = idGenre;
    }
    
    public Long getId(){
        return idGenre;
    }
    
    public void AddReference(Link Link) {
        if (References == null) {
            References = new ArrayList<>();
        }        
        References.add(Link);
    }
    
    public void AddMovie(Movie movie) {
        if (Movies == null)
            Movies = new ArrayList<>();        
        Movies.add(movie);
    }   
    
    public void SetStatistic(Integer Statistic) {
        this.Statistic = Statistic;
    }
    
    public ArrayList<Movie> displayMovies() {
        return Movies;
    }
    
    public Integer displayStatistics() {
        return Statistic;
    }
}