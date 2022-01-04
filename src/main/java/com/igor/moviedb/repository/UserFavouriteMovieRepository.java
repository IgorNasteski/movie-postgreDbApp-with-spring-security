package com.igor.moviedb.repository;

import com.igor.moviedb.model.movie.favourites.UserFavouriteMovie;
import com.igor.moviedb.model.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFavouriteMovieRepository extends JpaRepository<UserFavouriteMovie, Integer>{

    //po userovom id-u
    //public List<UserFavouriteMovie> findByUsers(int userId);
    //po useru
    List<UserFavouriteMovie> findByUsers(Users user);

    //UserFavouriteMovie findDistinctByTitle(String title);
    UserFavouriteMovie findByTitleContainingAndUsers(String title, Users user);

    UserFavouriteMovie deleteByTitle(String title);

}
