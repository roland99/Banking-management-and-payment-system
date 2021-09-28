package com.montran.internship.roland_gonczel.entity;

import com.montran.internship.roland_gonczel.status.AccountStatus;
import com.montran.internship.roland_gonczel.status.Status;
import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountHistory extends Audit implements Comparable<AccountHistory>{

    @Column
    private String accountId;

    @Column
    private String number;

    @Column
    private String name;

    @Column
    private String currency;

    @Column
    private AccountStatus accountStatus;

    @Column
    private Status status;

    @Column
    private String state;

    @Column
    private String city;

    @Column
    private String address;

    @Column
    private String zipCode;

    @ManyToOne
    @JoinColumn(name = "accountHistory")
    private Account account;



    @Override
    public int compareTo(AccountHistory o) {
        if (this.getDate().before(o.getDate())) {
            return 1;
        } else if (this.getDate().after(o.getDate())) {
            return -1;
        } else {
            return 0;
        }
    }
}
