package com.igor.moviedb.model.imdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;

@JsonIgnoreProperties
@Generated("com.robohorse.robopojogenerator")
public class ImdbDrugiApi {

    @JsonProperty("imdb_id")
    private String imdbMovieId;

    public String getImdbMovieId() {
        return imdbMovieId;
    }

    public void setImdbMovieId(String imdbMovieId) {
        this.imdbMovieId = imdbMovieId;
    }
}
