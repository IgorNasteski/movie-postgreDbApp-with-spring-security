package com.igor.moviedb.repository;

import com.igor.moviedb.model.user.Authorities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthoritiesRepository extends JpaRepository<Authorities, Integer> {

    public List<Authorities> findByUsername(String username);
    public List<Authorities> findByUsernameAndAuthority(String username, String authority);

    //@Query("delete from Users u where u.username = ?1")
    //void deleteUserRecordsByUsername(String username);

    public void deleteByUsername(String username);

    List<Authorities> findByUsernameContaining(String username);
}
