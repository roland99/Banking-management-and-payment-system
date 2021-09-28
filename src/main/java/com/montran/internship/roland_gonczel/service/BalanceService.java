package com.montran.internship.roland_gonczel.service;

import com.montran.internship.roland_gonczel.dto.BalanceDto;
import com.montran.internship.roland_gonczel.entity.Account;
import com.montran.internship.roland_gonczel.entity.Balance;
import com.montran.internship.roland_gonczel.entity.BalanceHistory;
import com.montran.internship.roland_gonczel.mapper.BalanceHistoryMapper;
import com.montran.internship.roland_gonczel.mapper.BalanceMapper;
import com.montran.internship.roland_gonczel.repository.BalanceHistoryRepository;
import com.montran.internship.roland_gonczel.repository.BalanceRepository;
import com.montran.internship.roland_gonczel.status.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BalanceService {

    private static final Logger log = LoggerFactory.getLogger(BalanceService.class);


    @Autowired
    BalanceRepository balanceRepository;

    @Autowired
    BalanceHistoryRepository balanceHistoryRepository;

    @Autowired
    UserService userService;

    public List<BalanceDto> viewAllBalancies(){
        List<Balance> balanceList = balanceRepository.findAll();
        List<BalanceDto> balanceDtoList = new ArrayList<>();
        for(Balance b: balanceList){
            balanceDtoList.add(BalanceMapper.toBalanceDto(b, new BalanceDto()));
        }

        return balanceDtoList;
    }

    public BalanceDto findById(String id){
        Optional<Balance> b = balanceRepository.findById(id);
        Balance balance = Optional.ofNullable(b).map(o->o.get()).orElse(null);
        if(balance != null){
            BalanceDto balanceDto = BalanceMapper.toBalanceDto(balance,new BalanceDto());
            return balanceDto;
        }else{
            log.warn("No balance with this id!");
            return new BalanceDto();
        }

    }

    public List<BalanceDto> viewBalancesWithDateRange(Date start, Date end){
        List<Balance> balanceList = balanceRepository.findAllByLastUpdateBetween(start,end);
        List<BalanceDto> balanceDtos = new ArrayList<>();
        for(Balance b: balanceList){
            balanceDtos.add(BalanceMapper.toBalanceDto(b,new BalanceDto()));
        }
        return balanceDtos;
    }



    public void  saveWithHistory(Balance balance, Operation operation, boolean waitResponse){
        BalanceHistory balanceHistory = BalanceHistoryMapper.toBalanceHistory(balance);
        balanceHistory.setInitiatorUsername(userService.getSessionUserUsername());
        balanceHistory.setOperation(operation);
        balanceHistory.setWaitingResponse(waitResponse);
        balanceHistory.setDate(new Timestamp(System.currentTimeMillis()));
        balanceHistory.setBalance(balance);

        balance.setLastUpdate(new Timestamp(System.currentTimeMillis()));
        balance.addAccountHistory(balanceHistory);
        balanceRepository.saveAndFlush(balance);
    }

    public Balance createBalanceForAccount(Account account){
        Balance balance = BalanceMapper.createNewBalance(account);
        saveWithHistory(balance, Operation.ADD, false);
        return balance;
    }

    public void setBalance(String id, String amount){
        try
        {
            Long.parseLong(amount);
            Optional<Balance> b = balanceRepository.findById(id);
            Balance balance = Optional.ofNullable(b).map(o->o.get()).orElse(null);
            if( balance != null){
                balance.setAvailableAmountCredit(Long.parseLong(amount));
                saveWithHistory(balance,Operation.MODIFY,false);
            }
        }
        catch (NumberFormatException e)
        {
            log.warn("Input amount is not a number");
        }
    }


}
