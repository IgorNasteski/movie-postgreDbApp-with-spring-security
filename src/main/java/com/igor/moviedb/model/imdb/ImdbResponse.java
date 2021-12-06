package com.igor.moviedb.model.imdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;
import java.util.List;

@JsonIgnoreProperties
@Generated("com.robohorse.robopojogenerator")
public class ImdbResponse {

    @JsonProperty("d")
    List<ImdbDetails> imdbDetails;

    public List<ImdbDetails> getImdbDetails() {
        return imdbDetails;
    }

    public void setImdbDetails(List<ImdbDetails> imdbDetails) {
        this.imdbDetails = imdbDetails;
    }
}
