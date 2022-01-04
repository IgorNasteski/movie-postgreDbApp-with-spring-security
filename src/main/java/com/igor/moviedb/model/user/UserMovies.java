package com.igor.moviedb.model.user;

import javax.persistence.*;

//@Entity
//@Table(name = "user_movies")
public class UserMovies {

   // @Id
   // @GeneratedValue(strategy = GenerationType.IDENTITY)
   // @Column(name = "movie_id")
    private int id;
   // @Column(name = "movie_name")
    private String movieName;
  //  @ManyToOne
   // @JoinColumn(name="id_user")
    private Users users;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserMovies that = (UserMovies) o;

        if (id != that.id) return false;
        if (movieName != null ? !movieName.equals(that.movieName) : that.movieName != null) return false;
        return users != null ? users.equals(that.users) : that.users == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (movieName != null ? movieName.hashCode() : 0);
        result = 31 * result + (users != null ? users.hashCode() : 0);
        return result;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getMovieName() {
        return movieName;
    }
    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }
    public Users getUsers() {
        return users;
    }
    public void setUsers(Users users) {
        this.users = users;
    }

}
