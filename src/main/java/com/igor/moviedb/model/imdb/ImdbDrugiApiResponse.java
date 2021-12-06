package com.igor.moviedb.model.imdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;
import java.util.List;

@JsonIgnoreProperties
@Generated("com.robohorse.robopojogenerator")
public class ImdbDrugiApiResponse {

    @JsonProperty("results")
    private List<ImdbDrugiApi> imdbDrugiApi;

    public List<ImdbDrugiApi> getImdbDrugiApi() {
        return imdbDrugiApi;
    }

    public void setImdbDrugiApi(List<ImdbDrugiApi> imdbDrugiApi) {
        this.imdbDrugiApi = imdbDrugiApi;
    }
}
