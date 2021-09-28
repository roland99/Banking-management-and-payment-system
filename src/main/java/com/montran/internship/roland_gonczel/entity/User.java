package com.montran.internship.roland_gonczel.entity;

import com.montran.internship.roland_gonczel.status.Status;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="\"user\"")
public class User extends Address implements UserDetails {

    @Id
    private String id;

    @Column(unique=true)
    private String username;

    @Column
    private String password;

    @Column
    private String email;

    @Column
    private String fullName;

    @Column
    private Status status;      //APROVE, ACTIVE, AUDIT, DELETED


    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginTime;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserHistory> userHistory;

    public void addUserHistory(UserHistory history){
        if(userHistory == null){
            userHistory = new ArrayList<>();
            this.userHistory.add(history);
        }else{
            this.userHistory.add(history);
        }
    }

    public void deleteUserHystory(UserHistory history){
        this.userHistory.remove(history);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        if(this.status == Status.ACTIVE){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public String toString() {
        return username;
    }
}
