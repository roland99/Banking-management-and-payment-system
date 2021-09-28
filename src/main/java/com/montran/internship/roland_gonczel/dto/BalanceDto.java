package com.montran.internship.roland_gonczel.dto;

import com.montran.internship.roland_gonczel.entity.Account;
import com.montran.internship.roland_gonczel.entity.BalanceHistory;
import lombok.*;

import java.util.Date;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BalanceDto {
    private String id;
    private Account account;
    private Date lastUpdate;

    private long availableAmountCredit;
    private long availableAmountDebit;

    private long availableCountCredit;
    private long availableCountDebit;

    private long pendingAmountCredit;
    private long pendingAmountDebit;

    private long pendingCountCredit;
    private long pendingCountDebit;

    private List<BalanceHistory> balanceHistory;
}
