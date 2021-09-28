package com.montran.internship.roland_gonczel.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BalanceHistory extends Audit implements Comparable<BalanceHistory>{

    @Column
    private String balanceId;

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

    @ManyToOne
    @JoinColumn(name="balanceHistory")
    private Balance balance;

    @Override
    public int compareTo(BalanceHistory o) {
        if (this.getDate().before(o.getDate())) {
            return 1;
        } else if (this.getDate().after(o.getDate())) {
            return -1;
        } else {
            return 0;
        }
    }
}
