package com.montran.internship.roland_gonczel.mapper;

import com.montran.internship.roland_gonczel.entity.Balance;
import com.montran.internship.roland_gonczel.entity.BalanceHistory;

public class BalanceHistoryMapper {

    public static BalanceHistory toBalanceHistory(Balance balance){
        if(balance != null){
            return BalanceHistory.builder()
                    .balanceId(balance.getId())
                    .lastUpdate(balance.getLastUpdate())
                    .availableAmountCredit(balance.getAvailableAmountCredit())
                    .availableAmountDebit(balance.getAvailableAmountDebit())
                    .availableCountCredit(balance.getAvailableCountCredit())
                    .availableCountDebit(balance.getAvailableCountDebit())
                    .pendingAmountCredit(balance.getPendingAmountCredit())
                    .pendingAmountDebit(balance.getPendingAmountDebit())
                    .pendingCountCredit(balance.getPendingCountCredit())
                    .pendingCountDebit(balance.getPendingCountDebit())
                    .build();
        }
        return new BalanceHistory();
    }


}
