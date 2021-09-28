package com.montran.internship.roland_gonczel.service;

import com.montran.internship.roland_gonczel.dto.UserDto;
import com.montran.internship.roland_gonczel.entity.User;
import com.montran.internship.roland_gonczel.entity.UserHistory;
import com.montran.internship.roland_gonczel.mapper.UserHistoryMapper;
import com.montran.internship.roland_gonczel.mapper.UserMapper;
import com.montran.internship.roland_gonczel.repository.UserHistoryRepository;
import com.montran.internship.roland_gonczel.repository.UserRepository;
import com.montran.internship.roland_gonczel.status.Operation;
import com.montran.internship.roland_gonczel.status.Status;
import com.montran.internship.roland_gonczel.utility.UserFieldValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService, CrudOperationService<UserDto, UserHistory> {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserHistoryRepository userHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
            User user = userRepository.findByUsername(s);
            if (user == null) {
                log.info("Not loaded user with username: " + s);
                throw new UsernameNotFoundException("User null");

            } else {
                log.info("Loaded user with username: " + s);
            }
            return user;

    }
    public UserDto findByUsername(String username){
        User user = userRepository.findByUsername(username);
        if(user != null){
            UserDto userDto = new UserDto();
            userDto = UserMapper.toUserDto(user, userDto);
            log.info("User with username: " + userDto.getUsername()+ " found and returned successfully");
            return userDto;
        }
        log.warn("Empty UserDto returned");
        return new UserDto();
    }

    public UserDto findById(String id){
        Optional<User> u = userRepository.findById(id);
        User user = Optional.ofNullable(u).map(o -> o.get()).orElse(null);
        if (user != null) {
            UserDto userDto =  new UserDto();
            userDto = UserMapper.toUserDto(user,userDto);
            log.info("User with id " + id + "is returned");
            return userDto;

        } else {
            log.info("No user with id " + id);
            return new UserDto();
        }

    }

    public String getSessionUserUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }

    public void updateLastLogin(String username){
        User user = userRepository.findByUsername(username);
        user.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
        userRepository.saveAndFlush(user);
        log.info("User " + user.getUsername() +" last login time updated to " + user.getLastLoginTime());
    }


    private boolean hasUniqueUsername(String username){
        if(userRepository.findByUsername(username) == null){
            return true;
        }else{
            return false;
        }
    }

    public boolean validNewUsername(String oldUsername, String newUsername){
        if(oldUsername.equals(newUsername)){
            return true;
        }else{
            if(userRepository.findByUsername(newUsername) == null){
                return true;
            }else{
                return false;
            }
        }
    }

    /**
     * Saves the user's  user history with the operation type and initiator.
     * Used at modify, to add new entry in the history the user
     * with the new user fields
     * @param user
     * @param operation
     */
    private void updateUserHistory(User user, Operation operation, UserDto userDto){
        userDto.setId(user.getId());

        UserHistory userHistory = new UserHistory();
        UserHistoryMapper.updateUserHisotoryFromDto(userDto, userHistory);
        userHistory.setInitiatorUsername(getSessionUserUsername());
        userHistory.setOperation(operation);
        userHistory.setWaitingResponse(true);
        userHistory.setDate(new Timestamp(System.currentTimeMillis()));
        userHistory.setUser(user);

        user.addUserHistory(userHistory);
        user.setStatus(Status.APROVE);

        userRepository.saveAndFlush(user);
        log.info("User history updated for the user");
    }

    /**
     * Saves a new userHistory and links it with the user
     * @param user
     * @param operation
     */
    public void saveWithHistory(User user, Operation operation, boolean waitResponse){
        UserHistory userHistory = UserHistoryMapper.toUserHistory(user);
        userHistory.setInitiatorUsername(getSessionUserUsername());
        userHistory.setOperation(operation);
        userHistory.setWaitingResponse(waitResponse);
        userHistory.setDate(new Timestamp(System.currentTimeMillis()));
        userHistory.setUser(user);

        user.addUserHistory(userHistory);

        userRepository.saveAndFlush(user);
    }



    private void invalidateOtherRequests(User user){

        for(UserHistory u: user.getUserHistory()){
            u.setWaitingResponse(false);
            userHistoryRepository.saveAndFlush(u);
        }
        userRepository.saveAndFlush(user);
    }

    @Override
    public List<UserHistory> findAllApproveRequests(){
        List<UserHistory> list = new ArrayList<>();
        for(UserHistory uh: userHistoryRepository.findAllByWaitingResponseTrue() ){
            list.add(uh);
        }
        return list;
    }



    @Override
    public List<UserDto> viewAllActive() {
        List<UserDto> list = new ArrayList<>();
        for(User u: userRepository.findAllByStatusOrStatus(Status.ACTIVE, Status.APROVE)){
            list.add(UserMapper.toUserDto(u, new UserDto()));
        }

        return list;
    }

    @Override
    public void addNewEntry(UserDto userDto) {
        if(userDto != null){
            if(hasUniqueUsername(userDto.getUsername())) {
                User user = new User();
                user = UserMapper.toUser(userDto, user);
                user.setId(UUID.randomUUID().toString());
                user.setPassword(passwordEncoder.encode(userDto.getPassword()));
                user.setStatus(Status.CREATE);
                saveWithHistory(user, Operation.ADD,true);
                log.info("Successfully created pending user creation for user: " + user.getUsername());
            }else{
                log.info("Username already taken");
            }
        }else{
            log.warn("UserDto for user add is null");
        }
    }

    @Override
    public void modifyEntry(UserDto userDto, String id) {
        Optional<User> u = userRepository.findById(id);
        User user = Optional.ofNullable(u).map(o -> o.get()).orElse(null);

        if(user != null) {
            if (!StringUtils.hasText(userDto.getPassword())) {
                userDto.setPassword(user.getPassword());
            }

            if (UserFieldValidator.verifyUserDto(userDto)) {
                userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
                userDto.setStatus(Status.APROVE);
                updateUserHistory(user,Operation.MODIFY,userDto);

                log.info("Successfully created pending modification for user: " + user.getUsername());
            } else {
                log.warn("Invalid userDto data to modify the user");
            }
        }else{
            log.warn("No user found with that id!");
        }

    }



    @Override
    public void deleteEntry(String id) {
        Optional<User> u = userRepository.findById(id);
        User user = Optional.ofNullable(u).map(o -> o.get()).orElse(null);

        if(user != null){
            user.setStatus(Status.APROVE);
            saveWithHistory(user, Operation.DELETE, true);
            log.info("Successfully created pending deletion for user: " + user.getUsername());
        }else{
            log.warn("No user found with that id!");
        }

    }

    @Override
    public void approveEntry(String id) {
        Optional<UserHistory> uh = userHistoryRepository.findById(id);
        UserHistory userHistory = Optional.ofNullable(uh).map(o -> o.get()).orElse(null);
        if (userHistory != null && !userHistory.getInitiatorUsername().equals(getSessionUserUsername())) {
            switch (userHistory.getOperation()){
                case ADD:
                    addFromHistory(userHistory);
                    break;
                case DELETE:
                    deleteFromHistory(userHistory);
                    break;
                case MODIFY:
                    modifyFromHistory(userHistory);
                    break;

            }

        }
    }

    @Override
    public void rejectEntry(String id) {
        Optional<UserHistory> uh = userHistoryRepository.findById(id);
        UserHistory userHistory = Optional.ofNullable(uh).map(o -> o.get()).orElse(null);
        if (userHistory != null && !userHistory.getInitiatorUsername().equals(getSessionUserUsername())) {
            Optional<User> u = userRepository.findById(userHistory.getUserId());
            User user = Optional.ofNullable(u).map(o -> o.get()).orElse(null);
            if(user != null) {
                userHistory.setWaitingResponse(false);
                user.setStatus(Status.ACTIVE);
                saveWithHistory(user,Operation.REJECTED,false);
                //In case of reject on user add, deleted the temporary created user from db
                if(userHistory.getOperation().equals(Operation.ADD)){
                    userRepository.delete(user);
                }
                log.info("Request rejected for user: " + user.getUsername());
            }

        }
    }

    @Override
    public void addFromHistory(UserHistory userHistory){
        Optional<User> u = userRepository.findById(userHistory.getUserId());
        User user = Optional.ofNullable(u).map(o -> o.get()).orElse(null);
        if(user != null) {

            userHistory.setWaitingResponse(false); //hiding the already processed request
            user.setStatus(Status.ACTIVE);
            user.addUserHistory(userHistory);   //adding already existing userHistory to the new User

            saveWithHistory(user, Operation.APROVED, false);
            log.info("New user " + user.getUsername() + " added to the database");
        }else{
            log.warn("No such user in the database");
        }
    }

    @Override
    public void modifyFromHistory(UserHistory userHistory){
        Optional<User> u = userRepository.findById(userHistory.getUserId());
        User user = Optional.ofNullable(u).map(o->o.get()).orElse(null);
        if(user != null){
            user = UserHistoryMapper.toUser(userHistory);
            user.setStatus(Status.ACTIVE);
            userHistory.setWaitingResponse(false); //hiding the already processed request
            saveWithHistory(user, Operation.APROVED, false);
            log.info("User " + user.getUsername() + " modified successfully");
        }else{
            log.warn("No valid user to modify");
        }
    }

    @Override
    public void deleteFromHistory(UserHistory userHistory){
        Optional<User> u = userRepository.findById(userHistory.getUserId());
        User user = Optional.ofNullable(u).map(o->o.get()).orElse(null);
        if(user != null){
            user.setStatus(Status.DELETED);
            userHistory.setWaitingResponse(false); //hiding the already processed request

            invalidateOtherRequests(user);

            saveWithHistory(user,Operation.APROVED,false);
            log.info("User " + user.getUsername() + " deleted successfully");
        }else{
            log.warn("No valid user to delete");
        }
    }

}
