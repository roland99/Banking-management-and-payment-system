package com.montran.internship.roland_gonczel.mapper;

import com.montran.internship.roland_gonczel.dto.UserDto;
import com.montran.internship.roland_gonczel.entity.User;

public class UserMapper {

    public static UserDto toUserDto(User user, UserDto userDto){
        if(user != null){
            return userDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .status(user.getStatus())
                    .state(user.getState())
                    .city(user.getCity())
                    .address(user.getAddress())
                    .zipCode(user.getZipCode())
                    .lastLoginTime(user.getLastLoginTime())
                    .userHistory(user.getUserHistory())
                    .build();
        }
        return new UserDto();
    }


    public static User toUser(UserDto userDto, User user){
        if(userDto != null){
            return user.builder()
                    .username(userDto.getUsername())
                    .password(userDto.getPassword())
                    .email(userDto.getEmail())
                    .fullName(userDto.getFullName())
                    .status(userDto.getStatus())
                    .state(userDto.getState())
                    .city(userDto.getCity())
                    .address(userDto.getAddress())
                    .zipCode(userDto.getZipCode())
                    .build();
        }
        return new User();
    }

}
