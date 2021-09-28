package com.montran.internship.roland_gonczel.entity;


import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@MappedSuperclass
@SuperBuilder
public class Address {

    @Column
    private String state;

    @Column
    private String city;

    @Column
    private String address;

    @Column
    private String zipCode;
}
