package com.igor.moviedb.model.user;

import com.igor.moviedb.model.movie.favourites.UserFavouriteMovie;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Entity
public class Users{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user", nullable = false)
    private int userId;

    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull(message="is required")
    @Size(min = 4, max = 15, message = "Must be between 4 and 15 characters")
    //@Column(name = "username", nullable = true, length = 45)
    //@Min(value = 4 , message = "Username must contain more then 4 letters/numbers")   - ovo je samo za brojeve, npr ako stavim min 4 moram da unesem 4 ili 5 ili 6
    //@Max(value = 10 , message = "Username can't contain more then 10 letters/numbers")
    private String username;
    @NotNull(message="is required")
    @Size(min = 4, max = 25, message = "Must be between 4 and 25 characters")
    private String password;
    private boolean enabled;

    @NotNull(message="First Name is required")
    @Column(name = "first_name")
    private String firstName;

    @NotNull(message="Last Name is required")
    @Column(name = "last_name")
    private String lastName;

    //@Embedded
    //private List<Authorities> authorities;
    @OneToMany(mappedBy = "users")
    private List<UserFavouriteMovie> userMovies;

    @Transient                  //@Transient anotacija je za polja koja ne mapiram za tabelu, koristim radi ispisa
    private String authority;   //nema veze sa bazom, samo da ispisem role koje cu pokupiti iz tabele authorities(inace nisu povezane)

    public Users() {}

    public Users(String username, String password, boolean enabled, String firstName, String lastName, List<UserFavouriteMovie> userMovies, String authority) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userMovies = userMovies;
        this.authority = authority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Users users = (Users) o;

        if (userId != users.userId) return false;
        if (enabled != users.enabled) return false;
        if (username != null ? !username.equals(users.username) : users.username != null) return false;
        if (password != null ? !password.equals(users.password) : users.password != null) return false;
        if (firstName != null ? !firstName.equals(users.firstName) : users.firstName != null) return false;
        if (lastName != null ? !lastName.equals(users.lastName) : users.lastName != null) return false;
        if (userMovies != null ? !userMovies.equals(users.userMovies) : users.userMovies != null) return false;
        return authority != null ? authority.equals(users.authority) : users.authority == null;
    }

    @Override
    public int hashCode() {
        int result = userId;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (enabled ? 1 : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (userMovies != null ? userMovies.hashCode() : 0);
        result = 31 * result + (authority != null ? authority.hashCode() : 0);
        return result;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public String getAuthority() {
        return authority;
    }
    public void setAuthority(String authority) {
        this.authority = authority;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    /*public List<Authorities> getAuthorities() {
        return authorities;
    }
    public void setAuthorities(List<Authorities> authorities) {
        this.authorities = authorities;
    }*/
    public List<UserFavouriteMovie> getUserMovies() {
        return userMovies;
    }
    public void setUserMovies(List<UserFavouriteMovie> userMovies) {
        this.userMovies = userMovies;
    }
}

/*@ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private Set<UserRole> userRoles;*/