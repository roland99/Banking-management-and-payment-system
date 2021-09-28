package com.montran.internship.roland_gonczel.mapper;

import com.montran.internship.roland_gonczel.dto.AccountDto;
import com.montran.internship.roland_gonczel.entity.Account;

public class AccountMapper {

    public static AccountDto toAccountDto(Account account, AccountDto accountDto){
        if(account!=null){
            return accountDto.builder()
                    .id(account.getId())
                    .number(account.getNumber())
                    .name(account.getName())
                    .currency(account.getCurrency())
                    .accountStatus(account.getAccountStatus())
                    .status(account.getStatus())
                    .state(account.getState())
                    .city(account.getCity())
                    .address(account.getAddress())
                    .zipCode(account.getZipCode())
                    .accountHistory(account.getAccountHistory())
                    .accountBalance(account.getAccountBalance())
                    .build();
        }
        return new AccountDto();
    }

    public static Account toAccount(AccountDto accountDto, Account account){
        if(accountDto != null) {
            account = Account.builder()
                    .id(accountDto.getId())
                    .number(accountDto.getNumber())
                    .name(accountDto.getName())
                    .currency(accountDto.getCurrency())
                    .accountStatus(accountDto.getAccountStatus())
                    .status(accountDto.getStatus())
                    .state(accountDto.getState())
                    .city(accountDto.getCity())
                    .address(accountDto.getAddress())
                    .zipCode(accountDto.getZipCode())
                    .build();
            return account;
        }
        return new Account();
    }
}
