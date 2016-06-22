package org.tudelft.wdm.imdb.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@JsonSerialize(include = Inclusion.NON_NULL) /* Omit empty ArrayLists */
@XmlRootElement (name = "Serie")
public class Serie {
    @XmlElement (name = "Serie ID")
    private final long idSerie;
    @XmlElement (name = "Title")
    private final String Title;
    @XmlElement (name = "Season")
    private final int Season;
    @XmlElement (name = "Episode")
    private final int Number;

    public Serie(long idSerie, String Title, int Season, int Number) {
        this.idSerie = idSerie;
        this.Title = Title;
        this.Season = Season;
        this.Number = Number;
    }
}