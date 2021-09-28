package com.montran.internship.roland_gonczel.controller;


import com.montran.internship.roland_gonczel.dto.BalanceDto;
import com.montran.internship.roland_gonczel.entity.BalanceHistory;
import com.montran.internship.roland_gonczel.service.BalanceService;
import org.dom4j.rule.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;

@Controller
public class BalanceController {
    private static final Logger log = LoggerFactory.getLogger(BalanceController.class);

    @Autowired
    BalanceService balanceService;

    @GetMapping("/viewBalances")
    public ModelAndView viewBalances(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("balanceList",balanceService.viewAllBalancies());
        mav.setViewName("balance/viewBalances");
        return mav;
    }

    @PostMapping("/balanceDetail")
    private ModelAndView balanceDetail(@RequestParam(name="id")String id, HttpSession httpSession){
        ModelAndView mav = new ModelAndView();
        if(id!=null){
            BalanceDto balanceDto = balanceService.findById(id);
            List<BalanceHistory> list = balanceDto.getBalanceHistory();
            Collections.sort(list);
            balanceDto.setBalanceHistory(list);
            httpSession.setAttribute("balanceDetail",balanceDto );

        }
        mav.setViewName("redirect:/detailBalance");
        return mav;
    }

    @GetMapping("/detailBalance")
    public ModelAndView balanceDetailView(HttpSession httpSession){
        ModelAndView mav = new ModelAndView();
        BalanceDto balanceDto = null;
        if(httpSession.getAttribute("balanceDetail")!=null){
            balanceDto = (BalanceDto) httpSession.getAttribute("balanceDetail");
        }else{
            log.warn("BalanceDetail from httpSession is null");
        }
        mav.addObject("balanceDetail",balanceDto);
        mav.setViewName("balance/detailBalance");
        return mav;

    }

    @GetMapping("/viewSetBalance")
    public ModelAndView setBalanceViewAll(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("balanceList", balanceService.viewAllBalancies());
        mav.setViewName("balance/viewSetBalance");
        return mav;
    }

    @GetMapping("/setBalance")
    public ModelAndView setBalanceForm(@RequestParam(name = "id")String id, HttpSession httpSession){
        ModelAndView mav = new ModelAndView();
        BalanceDto balanceDto = balanceService.findById(id);
        if(balanceDto != null){
            httpSession.setAttribute("id", id);
            mav.addObject("balanceDto", balanceDto);
        }
        mav.setViewName("balance/setBalance");
        return mav;
    }

    @PostMapping("/setBalance")
    public ModelAndView setBalance(String amount, HttpSession httpSession){
        ModelAndView mav = new ModelAndView();
        System.out.println("Balance amount: " + amount);
        if(httpSession.getAttribute("id") != null){
            String id = (String) httpSession.getAttribute("id");
            balanceService.setBalance(id,amount);
        }

        mav.setViewName("redirect:/viewSetBalance");
        return mav;
    }
}
