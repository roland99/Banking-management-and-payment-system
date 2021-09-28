package com.montran.internship.roland_gonczel.controller;


import com.montran.internship.roland_gonczel.dto.AccountDto;
import com.montran.internship.roland_gonczel.entity.AccountHistory;
import com.montran.internship.roland_gonczel.service.AccountService;
import com.montran.internship.roland_gonczel.status.AccountStatus;
import com.montran.internship.roland_gonczel.utility.AccountFieldValidator;
import com.montran.internship.roland_gonczel.utility.Currency;
import com.montran.internship.roland_gonczel.utility.ReadXmlFile;
import org.dom4j.rule.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);


    @Autowired
    private AccountService accountService;

    @GetMapping("/viewAccounts")
    public ModelAndView viewAllAccounts(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("accountList", accountService.viewAllActive());
        mav.setViewName("account/viewAccounts");
        return mav;
    }

    @PostMapping("/accountDetail")
    private ModelAndView accountDetail(@RequestParam(name="id") String id, HttpSession httpSession){
        ModelAndView mav = new ModelAndView();
        AccountDto accountDto = accountService.findById(id);
        List<AccountHistory> list = accountDto.getAccountHistory();
        Collections.sort(list);
        accountDto.setAccountHistory(list);
        httpSession.setAttribute("accountDetail",accountDto);
        mav.setViewName("redirect:/detailAccount");
        return mav;
    }

    @GetMapping("/detailAccount")
    private ModelAndView accountDetailView(HttpSession httpSession){
        ModelAndView mav = new ModelAndView();
        AccountDto accountDto = null;
        if(httpSession.getAttribute("accountDetail")!=null){
            accountDto = (AccountDto) httpSession.getAttribute("accountDetail");
        }else{
            log.warn("AccountDetail from httpSession is null");
        }
        mav.addObject("accountDetail", accountDto);
        mav.setViewName("account/detailAccount");
        return mav;
    }

    @GetMapping("/addAccount")
    public ModelAndView addAccountView(){
        ModelAndView mav = new ModelAndView();
        List<Currency> list = new ArrayList<>();
        mav.addObject("list", list);
        mav.addObject("currencyList", ReadXmlFile.getCurrencies());
        mav.setViewName("account/addAccount");
        return mav;
    }

    @PostMapping("/addAccount")
    private ModelAndView addAccount(@ModelAttribute AccountDto accountDto){
        ModelAndView mav = new ModelAndView();
        if(AccountFieldValidator.verifyDto(accountDto)){
            accountService.addNewEntry(accountDto);
        }else{
            log.warn("User");
        }
        mav.setViewName("redirect:/addAccount");
        return mav;
    }

    @GetMapping("/viewModifyAccount")
    public ModelAndView viewModifyAccount(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("accountList", accountService.viewAllActive());
        mav.setViewName("account/viewModifyAccount");
        return mav;
    }

    @GetMapping("/modifyAccount")
    private ModelAndView viewModifyAccountForm(@RequestParam(name="id") String id, HttpSession httpSession){
        ModelAndView mav = new ModelAndView();
        AccountDto accountDto = accountService.findById(id);
        if(accountDto != null){
            mav.addObject("accountDto",accountDto);
            mav.addObject("accountStatusList", AccountStatus.values());
            httpSession.setAttribute("id", accountDto.getId());
        }
        mav.setViewName("account/modifyAccount");
        return mav;
    }

    @PostMapping("/modifyAccount")
    private ModelAndView modifyAccount(AccountDto accountDto,HttpSession httpSession){
        ModelAndView mav = new ModelAndView();
        String id = null;
        if(httpSession.getAttribute("id") != null){
            id = (String) httpSession.getAttribute("id");
            accountService.modifyEntry(accountDto,id);
        }
        mav.setViewName("redirect:/viewModifyAccount");
        return mav;

    }

    @GetMapping("/viewDeleteAccount")
    private ModelAndView viewDeleteAccount(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("accountList", accountService.viewAllActive());
        mav.setViewName("account/viewDeleteAccount");
        return mav;
    }

    @PostMapping("/deleteAccount")
    private ModelAndView deleteAccount(@RequestParam(name="id")String id){
        ModelAndView mav = new ModelAndView();
        accountService.deleteEntry(id);
        mav.setViewName("redirect:/viewDeleteAccount");
        return mav;
    }

    @GetMapping("/viewApproveAccount")
    private ModelAndView viewApprove(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("accountHistoryList", accountService.findAllApproveRequests());
        mav.setViewName("account/viewApproveAccount");
        return mav;
    }

    @PostMapping("/approveOperationAccount")
    private ModelAndView approveOperationAccount(@RequestParam(name="id") String id){
        ModelAndView mav = new ModelAndView();
        accountService.approveEntry(id);
        mav.setViewName("redirect:/viewApproveAccount");
        return mav;
    }

    @PostMapping("/rejectOperationAccount")
    private ModelAndView rejectOperationAccount(@RequestParam(name="id") String id){
        ModelAndView mav = new ModelAndView();
        accountService.rejectEntry(id);
        mav.setViewName("redirect:/viewApproveAccount");
        return mav;
    }
}
