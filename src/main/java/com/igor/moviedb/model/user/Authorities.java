package com.igor.moviedb.model.user;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Authorities /*implements Serializable*/{   //da bih dohvatio role iz tabele "authorities" moram da implementiram Serializable, ne znam zasto

//    @Id
 //   @GeneratedValue(strategy = GenerationType.IDENTITY)
//    Column(name = "authorities_id")
//    private int id;
//    @ManyToOne
    //@JoinColumn(name="id")


    //@EmbeddedId     //posto ova tabela sluzi za role(spring security po defaultu), a nema svoj primary key vec joj je pk kao fk od tabele user
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    //@ManyToOne
    //@JoinColumn(name="username")
    //private Users users;
    private String username;
    private String authority;

    /*public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }
    */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Authorities that = (Authorities) o;

        if (id != that.id) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        return authority != null ? authority.equals(that.authority) : that.authority == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (authority != null ? authority.hashCode() : 0);
        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    /*public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }*/
}
