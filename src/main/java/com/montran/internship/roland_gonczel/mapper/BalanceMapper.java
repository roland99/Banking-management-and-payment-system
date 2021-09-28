package com.montran.internship.roland_gonczel.mapper;

import com.montran.internship.roland_gonczel.dto.BalanceDto;
import com.montran.internship.roland_gonczel.entity.Account;
import com.montran.internship.roland_gonczel.entity.Balance;

import java.sql.Timestamp;
import java.util.UUID;

public class BalanceMapper {

    public static BalanceDto toBalanceDto(Balance balance, BalanceDto balanceDto){
        if(balance != null){
            return balanceDto.builder()
                    .id(balance.getId())
                    .account(balance.getAccount())
                    .lastUpdate(balance.getLastUpdate())
                    .availableAmountCredit(balance.getAvailableAmountCredit())
                    .availableAmountDebit(balance.getAvailableAmountDebit())
                    .availableCountCredit(balance.getAvailableCountCredit())
                    .availableCountDebit(balance.getAvailableCountDebit())
                    .pendingAmountCredit(balance.getPendingAmountCredit())
                    .pendingAmountDebit(balance.getPendingAmountDebit())
                    .pendingCountCredit(balance.getPendingCountCredit())
                    .pendingCountDebit(balance.getPendingCountDebit())
                    .balanceHistory(balance.getBalanceHistory())
                    .build();
        }else{
            return new BalanceDto();
        }
    }

    public static Balance toBalance(BalanceDto balanceDto,Balance balance){
        if(balanceDto != null){
            return balance.builder()
                    .id(balanceDto.getId())
                    .account(balanceDto.getAccount())
                    .lastUpdate(balanceDto.getLastUpdate())
                    .availableAmountCredit(balanceDto.getAvailableAmountCredit())
                    .availableAmountDebit(balanceDto.getAvailableAmountDebit())
                    .availableCountCredit(balanceDto.getAvailableCountCredit())
                    .availableCountDebit(balanceDto.getAvailableCountDebit())
                    .pendingAmountCredit(balanceDto.getPendingAmountCredit())
                    .pendingAmountDebit(balance.getAvailableAmountDebit())
                    .pendingCountCredit(balanceDto.getPendingCountCredit())
                    .pendingCountDebit(balanceDto.getPendingCountDebit())
                    .balanceHistory(balanceDto.getBalanceHistory())
                    .build();
        }
        return new Balance();
    }

    public static Balance createNewBalance(Account account){
        if(account != null){
            return Balance.builder()
                    .id(UUID.randomUUID().toString())
                    .account(account)
                    .lastUpdate(new Timestamp(System.currentTimeMillis()))
                    .availableAmountCredit(0)
                    .availableAmountDebit(0)
                    .availableCountCredit(0)
                    .availableCountDebit(0)
                    .pendingAmountCredit(0)
                    .pendingAmountDebit(0)
                    .pendingCountCredit(0)
                    .pendingCountDebit(0)
                    .build();
        }
        return new Balance();
    }
}
