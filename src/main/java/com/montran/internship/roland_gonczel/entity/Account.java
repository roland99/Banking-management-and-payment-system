package com.montran.internship.roland_gonczel.entity;

import com.montran.internship.roland_gonczel.status.AccountStatus;
import com.montran.internship.roland_gonczel.status.Status;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Account extends Address {

    @Id
    private String id;

    @Column(unique = true)
    private String number;

    @Column
    private String name;

    @Column
    private String currency;

    @Column
    private AccountStatus accountStatus;

    @Column
    private Status status;


    @OneToMany( fetch = FetchType.EAGER, mappedBy = "account", cascade = CascadeType.ALL)
    private List<AccountHistory> accountHistory;

    @OneToOne(mappedBy = "account")
    private Balance accountBalance;


    public void addAccountHistory(AccountHistory history){
        if(accountHistory == null){
            accountHistory = new ArrayList<>();
            this.accountHistory.add(history);
        }else{
            this.accountHistory.add(history);
        }
    }

}
