package com.montran.internship.roland_gonczel.utility;

import com.montran.internship.roland_gonczel.dto.AccountDto;
import org.springframework.util.StringUtils;

public class AccountFieldValidator {

    public static boolean verifyDto(AccountDto accountDto){
        if(!StringUtils.hasText(accountDto.getName())){
            return false;
        }
        if(!StringUtils.hasText(accountDto.getNumber()) && !accountDto.getNumber().substring(0,1).matches("[a-zA-Z]") ){
            return false;
        }
        if(!StringUtils.hasText(accountDto.getCurrency())){
            return false;
        }
        if(!StringUtils.hasText(accountDto.getState())){
            return false;
        }
        if(!StringUtils.hasText(accountDto.getCity())){
            return false;
        }
        if(!StringUtils.hasText(accountDto.getAddress())){
            return false;
        }
        if(!StringUtils.hasText(accountDto.getZipCode())){
            return false;
        }
        return true;

    }
}
