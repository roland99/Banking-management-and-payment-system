package com.montran.internship.roland_gonczel.entity;

import com.montran.internship.roland_gonczel.status.Status;
import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserHistory extends Audit implements Comparable<UserHistory>{

    @Column
    private String userId;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private String email;

    @Column
    private String fullName;

    @Column
    private Status status;      //APROVE, ACTIVE, AUDIT

    @Column
    private String state;

    @Column
    private String city;

    @Column
    private String address;

    @Column
    private String zipCode;


    @ManyToOne
    @JoinColumn(name = "userHistory")
    private User user;

    @Override
    public String toString() {
        return "UserHistory{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", status=" + status +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", user=" + user +
                "} " + super.toString();
    }

    @Override
    public int compareTo(UserHistory o) {
        if (this.getDate().before(o.getDate())) {
            return 1;
        } else if (this.getDate().after(o.getDate())) {
            return -1;
        } else {
            return 0;
        }
    }
}
