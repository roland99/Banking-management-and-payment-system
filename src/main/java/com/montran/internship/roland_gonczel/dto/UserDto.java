package com.montran.internship.roland_gonczel.dto;

import com.montran.internship.roland_gonczel.entity.UserHistory;
import com.montran.internship.roland_gonczel.status.Status;
import lombok.*;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserDto {
    private String id;

    private String username;

    private String password;

    private String email;

    private String fullName;

    private Status status;      //APROVE, ACTIVE, AUDIT

    private String state;

    private String city;

    private String address;

    private String zipCode;

    private List<UserHistory> userHistory;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginTime;
}
