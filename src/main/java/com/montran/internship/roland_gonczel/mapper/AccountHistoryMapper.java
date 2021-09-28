package com.montran.internship.roland_gonczel.mapper;


import com.montran.internship.roland_gonczel.dto.AccountDto;
import com.montran.internship.roland_gonczel.entity.Account;
import com.montran.internship.roland_gonczel.entity.AccountHistory;
import com.montran.internship.roland_gonczel.entity.UserHistory;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class AccountHistoryMapper {

    public static AccountHistory toAccountHistory(Account account){
        if(account != null){
            return AccountHistory.builder()
                    .accountId(account.getId())
                    .number(account.getNumber())
                    .name(account.getName())
                    .currency(account.getCurrency())
                    .accountStatus(account.getAccountStatus())
                    .status(account.getStatus())
                    .state(account.getState())
                    .city(account.getCity())
                    .address(account.getAddress())
                    .zipCode(account.getZipCode())
                    .build();
        }
        return new AccountHistory();
    }

    public static Account toAccount(AccountHistory accountHistory){
        if(accountHistory != null){
            return Account.builder()
                    .id(accountHistory.getAccountId())
                    .number(accountHistory.getNumber())
                    .name(accountHistory.getName())
                    .currency(accountHistory.getCurrency())
                    .accountStatus(accountHistory.getAccountStatus())
                    .status(accountHistory.getStatus())
                    .state(accountHistory.getState())
                    .city(accountHistory.getCity())
                    .address(accountHistory.getAddress())
                    .zipCode(accountHistory.getZipCode())
                    .build();
        }
        return new Account();
    }

    /**
     * Populating the accountHistory from the data of the accountDto.
     * @param accountDto
     * @param accountHistory
     */
    public static void updateAccountFromDto(AccountDto accountDto, AccountHistory accountHistory){
        accountHistory.setAccountId(accountDto.getId());
        accountHistory.setNumber(accountDto.getNumber());
        accountHistory.setName(accountDto.getName());
        accountHistory.setCurrency(accountDto.getCurrency());
        accountHistory.setAccountStatus(accountDto.getAccountStatus());
        accountHistory.setStatus(accountDto.getStatus());
        accountHistory.setState(accountDto.getState());
        accountHistory.setCity(accountDto.getCity());
        accountHistory.setAddress(accountDto.getAddress());
        accountHistory.setZipCode(accountDto.getZipCode());
    }
}
