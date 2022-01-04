package com.igor.moviedb.model.movie.favourites;

import com.igor.moviedb.model.user.Users;

import javax.persistence.*;

@Entity
@Table(name = "user_favourite_movies")
public class UserFavouriteMovie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "movie_poster_path")
    private String moviePosterPath;

    @Column(name = "imdb_link_path")
    private String movieImdbId;

    @Column(name = "release_date")
    public String releaseDate;

    @Column(name = "popularity")
    public int popularity;

    @Column(name = "movie_votes_average")
    public double movieVotesAverage;

    @Column(name = "number_of_people_voted")
    public int numberOfPeopleVoted;

    @ManyToOne
    @JoinColumn(name="id_user")
    private Users users;

    public UserFavouriteMovie() {}

    public UserFavouriteMovie(String title, String moviePosterPath, String movieImdbId, String releaseDate, int popularity, double movieVotesAverage, int numberOfPeopleVoted, Users users) {
        this.title = title;
        this.moviePosterPath = moviePosterPath;
        this.movieImdbId = movieImdbId;
        this.releaseDate = releaseDate;
        this.popularity = popularity;
        this.movieVotesAverage = movieVotesAverage;
        this.numberOfPeopleVoted = numberOfPeopleVoted;
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserFavouriteMovie that = (UserFavouriteMovie) o;

        if (id != that.id) return false;
        if (popularity != that.popularity) return false;
        if (Double.compare(that.movieVotesAverage, movieVotesAverage) != 0) return false;
        if (numberOfPeopleVoted != that.numberOfPeopleVoted) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (moviePosterPath != null ? !moviePosterPath.equals(that.moviePosterPath) : that.moviePosterPath != null)
            return false;
        if (movieImdbId != null ? !movieImdbId.equals(that.movieImdbId) : that.movieImdbId != null) return false;
        if (releaseDate != null ? !releaseDate.equals(that.releaseDate) : that.releaseDate != null) return false;
        return users != null ? users.equals(that.users) : that.users == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (moviePosterPath != null ? moviePosterPath.hashCode() : 0);
        result = 31 * result + (movieImdbId != null ? movieImdbId.hashCode() : 0);
        result = 31 * result + (releaseDate != null ? releaseDate.hashCode() : 0);
        result = 31 * result + popularity;
        temp = Double.doubleToLongBits(movieVotesAverage);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + numberOfPeopleVoted;
        result = 31 * result + (users != null ? users.hashCode() : 0);
        return result;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getMoviePosterPath() {
        return moviePosterPath;
    }
    public void setMoviePosterPath(String moviePosterPath) {
        this.moviePosterPath = moviePosterPath;
    }
    public Users getUsers() {
        return users;
    }
    public void setUsers(Users users) {
        this.users = users;
    }
    public String getMovieImdbId() {
        return movieImdbId;
    }
    public void setMovieImdbId(String movieImdbId) {
        this.movieImdbId = movieImdbId;
    }
    public String getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
    public int getPopularity() {
        return popularity;
    }
    public void setPopularity(int popularity) {
        this.popularity = popularity;
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
}
