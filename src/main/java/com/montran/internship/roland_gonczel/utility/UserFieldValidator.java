package com.montran.internship.roland_gonczel.utility;

import com.montran.internship.roland_gonczel.dto.UserDto;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserFieldValidator {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[a-z]{2,6}$", Pattern.CASE_INSENSITIVE);


    public static boolean verifyUserDto(UserDto userDto){
        if(!StringUtils.hasText(userDto.getState())){
            return false;
        }
        if(!StringUtils.hasText(userDto.getCity())){
            return false;
        }if(!StringUtils.hasText(userDto.getAddress())){
            return false;
        }
        if(!StringUtils.hasText(userDto.getZipCode())){
            return false;
        }if(!StringUtils.hasText(userDto.getFullName())){
            return false;
        }if(!StringUtils.hasText(userDto.getUsername())){
            return false;
        }
        if(!StringUtils.hasText(userDto.getPassword())){
            return false;
        }
        if(validateEmail(userDto.getEmail())){
            return true;
        }

        return true;
    }

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.matches();
    }


}
