package com.igor.moviedb.model.imdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;

@JsonIgnoreProperties
@Generated("com.robohorse.robopojogenerator")
public class ImdbDetails {

    //posto mi treba samo movie id, ignorisacu sve ostalo. Treba mi movie id jer tako mogu da dohvatim film
    @JsonProperty("id")
    private String movieId;

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

}
