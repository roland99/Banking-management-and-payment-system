package com.montran.internship.roland_gonczel.controller;

import com.montran.internship.roland_gonczel.dto.UserDto;
import com.montran.internship.roland_gonczel.entity.UserHistory;
import com.montran.internship.roland_gonczel.service.UserService;
import com.montran.internship.roland_gonczel.utility.UserFieldValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;

@Controller
public class
UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("login")
    public String login(){
        return "login";
    }

    @GetMapping("/loginRedirect")
    public ModelAndView showPages(HttpSession httpSession) {
        ModelAndView mav = new ModelAndView();
        httpSession.setAttribute("userSessionDetail", userService.findByUsername(userService.getSessionUserUsername()));
        userService.updateLastLogin(userService.getSessionUserUsername());
        mav.setViewName("redirect:/home");
        return mav;
    }


    @GetMapping("/home")
    public ModelAndView home(HttpSession httpSession){
        ModelAndView mav = new ModelAndView();
        UserDto userDto = null;
        if(httpSession.getAttribute("userSessionDetail")!= null){
            userDto = (UserDto) httpSession.getAttribute("userSessionDetail");
        }

        mav.addObject("userDetail", userDto);
        mav.setViewName("home");
        return mav;
    }

    @GetMapping("/viewUsers")
    public ModelAndView viewAllUsers(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("userList", userService.viewAllActive());
        mav.setViewName("user/viewUsers");
        return mav;
    }

    @PostMapping("/userDetail")
    public ModelAndView userDetail(@RequestParam(name="id") String id, HttpSession httpSession){
        ModelAndView mav = new ModelAndView();
        UserDto userDto = userService.findById(id);
        List<UserHistory> list = userDto.getUserHistory();
        Collections.sort(list);
        userDto.setUserHistory(list);
        httpSession.setAttribute("userDetail", userDto);
        mav.setViewName("redirect:detailUser");
        return mav;
    }

    @GetMapping("/detailUser")
    public ModelAndView userDetail(HttpSession httpSession){
        ModelAndView mav = new ModelAndView();
        UserDto userDto = null;
        if(httpSession.getAttribute("userDetail")!= null){
           userDto = (UserDto) httpSession.getAttribute("userDetail");
        }
        mav.addObject("userDetail", userDto);
        mav.setViewName("user/detailUser");
        return mav;
    }
    @GetMapping("/addUser")
    public ModelAndView addUserView(){
        ModelAndView mav = new ModelAndView();
        mav.setViewName("user/addUser");
        return mav;
    }

    @PostMapping("/addUser")
    public ModelAndView addUser(UserDto userDto, RedirectAttributes attributes){
        ModelAndView mav = new ModelAndView();
        if(UserFieldValidator.verifyUserDto(userDto)) {
            userService.addNewEntry(userDto);
            attributes.addFlashAttribute("successMsg", " User created");
        }else{
            log.warn("User Dto is not valid");
        }
        mav.setViewName("user/addUser");
        return mav;
    }

    @GetMapping("/viewModifyUser")
    public ModelAndView viewModifyUser(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("userList", userService.viewAllActive());
        mav.setViewName("user/viewModifyUser");
        return mav;
    }


    @GetMapping("/modifyUser")
    private ModelAndView viewModifyUserForm(@RequestParam(name="username") String username, HttpSession httpSession){
        ModelAndView mav = new ModelAndView();
        UserDto userDto = userService.findByUsername(username);
        mav.addObject("userDto", userDto);
        httpSession.setAttribute("oldUsername", username);
        httpSession.setAttribute("id", userDto.getId());
        mav.setViewName("user/modifyUser");
        return mav;
    }

    @PostMapping("/modifyUser")
    private ModelAndView modifyUser(UserDto userDto, HttpSession httpSession){
        ModelAndView mav = new ModelAndView();
        String oldUsername = null;
        String id = null;
        if(httpSession.getAttribute("oldUsername") != null && httpSession.getAttribute("id") != null && userDto != null){
            oldUsername = (String) httpSession.getAttribute("oldUsername");
            id = (String) httpSession.getAttribute("id");
            if(userService.validNewUsername(oldUsername, userDto.getUsername())){
                userService.modifyEntry(userDto,id);
            }else{

            }
        }
        mav.setViewName("redirect:/viewModifyUser");
        return mav;
    }

    @GetMapping("/viewDeleteUser")
    private ModelAndView viewDeleteUser(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("userList", userService.viewAllActive());
        mav.setViewName("user/viewDeleteUser");
        return mav;
    }


    @PostMapping("/deleteUser")
    private ModelAndView deleteUser(@RequestParam(name="id")String id){
        ModelAndView mav = new ModelAndView();
        userService.deleteEntry(id);
        mav.setViewName("redirect:/viewDeleteUser");
        return mav;
    }

    @GetMapping("/viewApproveUser")
    private ModelAndView viewApprove(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("userHistoryList", userService.findAllApproveRequests());
        mav.setViewName("user/viewApproveUser");
        return mav;
    }

    @PostMapping("/aproveOperationUser")
    private ModelAndView approveOperation(@RequestParam(name="id") String id){
        ModelAndView mav = new ModelAndView();
        if(id != null){
            userService.approveEntry(id);
        }else{
            log.warn("User history id is null!!");
        }
        mav.setViewName("redirect:/viewApproveUser");
        return mav;
    }

    @PostMapping("/rejectOperationUser")
    private ModelAndView rejectOperation(@RequestParam(name="id") String id){
        ModelAndView mav = new ModelAndView();
        if(id!= null){
            userService.rejectEntry(id);
        }else{
            log.warn("User history id is null!!");
        }
        mav.setViewName("redirect:/viewApproveUser");
        return mav;
    }

}
