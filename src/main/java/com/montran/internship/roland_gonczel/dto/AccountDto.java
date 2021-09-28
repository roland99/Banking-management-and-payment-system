package com.montran.internship.roland_gonczel.dto;

import com.montran.internship.roland_gonczel.entity.AccountHistory;
import com.montran.internship.roland_gonczel.entity.Balance;
import com.montran.internship.roland_gonczel.status.AccountStatus;
import com.montran.internship.roland_gonczel.status.Status;
import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AccountDto {
    private String id;
    private String number;
    private String name;
    private String currency;
    private AccountStatus accountStatus;
    private Status status;
    private String state;
    private String city;
    private String address;
    private String zipCode;
    private Balance accountBalance;
    private List<AccountHistory> accountHistory;
}
