package com.montran.internship.roland_gonczel.mapper;

import com.montran.internship.roland_gonczel.dto.UserDto;
import com.montran.internship.roland_gonczel.entity.User;
import com.montran.internship.roland_gonczel.entity.UserHistory;

public class UserHistoryMapper {

    public static UserHistory toUserHistory(User user){
        if(user != null){
            return UserHistory.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .status(user.getStatus())
                    .state(user.getState())
                    .city(user.getCity())
                    .address(user.getAddress())
                    .zipCode(user.getZipCode())
                    .build();
        }
        return new UserHistory();
    }

    public static User toUser(UserHistory userHistory){
        if (userHistory != null) {
            return User.builder()
                    .id(userHistory.getUserId())
                    .username(userHistory.getUsername())
                    .password(userHistory.getPassword())
                    .fullName(userHistory.getFullName())
                    .email(userHistory.getEmail())
                    .status(userHistory.getStatus())
                    .state(userHistory.getState())
                    .city(userHistory.getCity())
                    .address(userHistory.getAddress())
                    .zipCode(userHistory.getZipCode())
                    .build();
        }
        return new User();
    }

    /**
     * Populating the userHistory from the data of the userDto.
     * @param userDto
     * @param userHistory
     */
    public static void updateUserHisotoryFromDto(UserDto userDto, UserHistory userHistory){
        userHistory.setUserId(userDto.getId());
        userHistory.setUsername(userDto.getUsername());
        userHistory.setPassword(userDto.getPassword());
        userHistory.setEmail(userDto.getEmail());
        userHistory.setFullName(userDto.getFullName());
        userHistory.setStatus(userDto.getStatus());
        userHistory.setState(userDto.getState());
        userHistory.setCity(userDto.getCity());
        userHistory.setAddress(userDto.getAddress());
        userHistory.setZipCode(userDto.getZipCode());

    }
}
