package com.igor.moviedb.repository;

import com.igor.moviedb.model.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer>{

    Users findByUsername(String username);

    Users findByUsernameContaining(String username);

}
