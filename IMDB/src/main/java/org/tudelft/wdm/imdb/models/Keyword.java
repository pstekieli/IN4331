package org.tudelft.wdm.imdb.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@JsonSerialize(include = Inclusion.NON_NULL) /* Omit empty ArrayLists */
@XmlRootElement (name = "Keyword")
public class Keyword {
    @XmlElement (name = "Keyword ID")
    private final long idKeyword;
    @XmlElement (name = "Label")
    private final String Label;
    
    public Keyword(long idKeyword, String Label) {
        this.idKeyword = idKeyword;
        this.Label = Label;
    }
}