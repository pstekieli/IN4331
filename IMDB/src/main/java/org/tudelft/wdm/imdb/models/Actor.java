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
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Piotrek
 */
@JsonSerialize(include = Inclusion.NON_NULL)

@XmlRootElement (name = "Actor")
public class Actor {
    
    @XmlElement
    private final long idActor;
    @XmlElement
    private final String FirstName;
    @XmlElement
    private final String LastName;
    @XmlElement
    private final String Gender;    
    @XmlElement (name = "Movies")
    private ArrayList<Movie> Movies;
       
    public Actor(long idActor, String FirstName, String LastName, String Gender) {
        this.idActor = idActor;
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.Gender = Gender;
    }

    public void AddMovie(long idMovie, String Title, int Year) {
        if (gotMovies() == null)
            Movies = new ArrayList<>();        
        gotMovies().add(new Movie (idMovie, Title, Year));
    }   
    
     /**
     * @return the Movies
     */
    public ArrayList<Movie> gotMovies() {
        return Movies;
    }
}
