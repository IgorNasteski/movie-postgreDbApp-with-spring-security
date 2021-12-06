package com.igor.moviedb.model.movie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;
import java.util.List;

@JsonIgnoreProperties
@Generated("com.robohorse.robopojogenerator")
public class MovieResponse {

    @JsonProperty("results")
    private List<MovieResults> movieResults;

    public List<MovieResults> getMovieResults() {
        return movieResults;
    }

    public void setMovieResults(List<MovieResults> movieResults) {
        this.movieResults = movieResults;
    }
}
