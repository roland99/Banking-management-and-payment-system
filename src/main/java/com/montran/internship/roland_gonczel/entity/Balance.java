package com.montran.internship.roland_gonczel.entity;


import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Balance implements Comparable<Balance> {

    @Id
    private String id;

    @OneToOne(cascade = CascadeType.ALL , orphanRemoval = true)
    @JoinColumn(name = "accountBalance")
    private Account account;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    @Column
    private long availableAmountCredit;

    @Column
    private long availableAmountDebit;

    @Column
    private long availableCountCredit;

    @Column
    private long availableCountDebit;

    @Column
    private long pendingAmountCredit;

    @Column
    private long pendingAmountDebit;

    @Column
    private long pendingCountCredit;

    @Column
    private long pendingCountDebit;


    @OneToMany(mappedBy = "balance", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<BalanceHistory> balanceHistory;


    public void addAccountHistory(BalanceHistory history){
        if(balanceHistory == null){
            balanceHistory = new ArrayList<>();
            this.balanceHistory.add(history);
        }else{
            this.balanceHistory.add(history);
        }
    }

    @Override
    public int compareTo(Balance o) {
        if (this.getLastUpdate().before(o.getLastUpdate())) {
            return 1;
        } else if (this.getLastUpdate().after(o.getLastUpdate())) {
            return -1;
        } else {
            return 0;
        }
    }

}
