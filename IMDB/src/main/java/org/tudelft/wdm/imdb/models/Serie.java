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
