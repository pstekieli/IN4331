package org.tudelft.wdm.imdb.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;
import java.util.ArrayList;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@JsonSerialize(include = Inclusion.NON_NULL) /* Omit empty ArrayLists */
@XmlRootElement (name = "Actor")
public class Actor {
    
    @XmlElement (name = "Actor ID")
    private final Long idActor;
    @XmlElement (name = "First Name")
    public String FirstName;
    @XmlElement (name = "Last Name")
    public String LastName;
    @XmlElement (name = "Gender ID")
    public String Gender;
    
    @XmlElement (name = "Role")
    private String Role;
    
    @XmlElement (name = "Movies")
    private ArrayList<Movie> Movies;
    @XmlElement (name = "References")
    private ArrayList<Link> References;
    @XmlElement (name = "Number Of Movies")
    private Integer Statistic = null;
    
    public Actor(long idActor, String FirstName, String LastName, String Gender) {
        this.idActor = idActor;
        this.FirstName = FirstName;
        this.LastName = LastName;
        if (Gender == null || Gender.equals("female"))
            this.Gender = "Female";        
        else
            this.Gender = "Male";        
    }
    
    public long GetId() {
    	return idActor;
    }
    
    public void SetGender(String gender) {
    	this.Gender = gender;
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
    
    public void SetRole(String role) {
    	this.Role = role;
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