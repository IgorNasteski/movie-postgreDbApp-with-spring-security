package com.igor.moviedb.model.movie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;

@JsonIgnoreProperties
@Generated("com.robohorse.robopojogenerator")
public class MovieResults {

    @JsonProperty("id")
    private int id;
    @JsonProperty("overview")
    private String movieOverview;
    @JsonProperty("popularity")
    private int popularity;
    @JsonProperty("poster_path")
    private String moviePosterPath;
    @JsonProperty("release_date")
    private String releaseDate;
    @JsonProperty("title")
    private String title;
    @JsonProperty("vote_average")
    private double movieVotesAverage;
    @JsonProperty("vote_count")
    private int numberOfPeopleVoted;

    //nevezano za rest poziv ka movie api-ju, napravio cisto da smestim link od imdb-a jer na thymeleaf stranici vec prolazim kroz petlju
    private String imdbLink;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMovieOverview() {
        return movieOverview;
    }

    public void setMovieOverview(String movieOverview) {
        this.movieOverview = movieOverview;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public String getMoviePosterPath() {
        return moviePosterPath;
    }

    public void setMoviePosterPath(String moviePosterPath) {
        this.moviePosterPath = moviePosterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getMovieVotesAverage() {
        return movieVotesAverage;
    }

    public void setMovieVotesAverage(double movieVotesAverage) {
        this.movieVotesAverage = movieVotesAverage;
    }

    public int getNumberOfPeopleVoted() {
        return numberOfPeopleVoted;
    }

    public void setNumberOfPeopleVoted(int numberOfPeopleVoted) {
        this.numberOfPeopleVoted = numberOfPeopleVoted;
    }

    public String getImdbLink() {
        return imdbLink;
    }

    public void setImdbLink(String imdbLink) {
        this.imdbLink = imdbLink;
    }

}
