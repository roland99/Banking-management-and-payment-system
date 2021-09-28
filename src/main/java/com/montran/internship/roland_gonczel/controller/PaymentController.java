package com.montran.internship.roland_gonczel.controller;


import com.montran.internship.roland_gonczel.dto.PaymentDto;
import com.montran.internship.roland_gonczel.entity.PaymentHistory;
import com.montran.internship.roland_gonczel.service.PaymentService;
import com.montran.internship.roland_gonczel.status.PaymentStatus;
import com.montran.internship.roland_gonczel.status.PaymentType;
import com.montran.internship.roland_gonczel.utility.BankPairs;
import com.montran.internship.roland_gonczel.utility.ReadXmlFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.Collections;
import java.util.List;

@Controller
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final String messageUrl = "https://ipsdemo.montran.ro/rtp/Message";
    private final String positionUrl = "https://ipsdemo.montran.ro/rtp/Positions";

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @GetMapping("/viewPayments")
    private ModelAndView viewAllPayments(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("paymentList", paymentService.viewAllPayments());
        mav.setViewName("payment/viewPayment");
        return mav;
    }

    @PostMapping("/paymentDetail")
    private ModelAndView paymentDetail(@RequestParam(name="id") String id, HttpSession httpSession){
        ModelAndView mav = new ModelAndView();
        PaymentDto paymentDto = paymentService.findById(id);
        List<PaymentHistory> list= paymentDto.getPaymentHistory();
        Collections.sort(list);
        paymentDto.setPaymentHistory(list);
        httpSession.setAttribute("paymentDto", paymentDto);
        mav.setViewName("redirect:/detailPayment");
        return mav;
    }

    @GetMapping("/detailPayment")
    private ModelAndView detailPayment(HttpSession httpSession){
        ModelAndView mav = new ModelAndView();
        PaymentDto paymnetDto = null;
        if(httpSession.getAttribute("paymentDto")!= null){
            paymnetDto = (PaymentDto) httpSession.getAttribute("paymentDto");
        }else{
            log.warn("PaymentDto from httpSession in null");
        }
        mav.addObject("paymentDetail", paymnetDto);
        mav.setViewName("payment/detailPayment");
        return mav;
    }

    @GetMapping("/makePayment")
    private ModelAndView makePaymentView(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("paymentReference", paymentService.newPaymentReference());
        mav.addObject("paymentType",PaymentType.values());
        mav.addObject("currencyList", ReadXmlFile.getCurrencies());
        mav.addObject("bankList", BankPairs.getBanks());
        mav.setViewName("payment/makePayment");
        return mav;
    }

    @PostMapping("/makePayment")
    private ModelAndView makePayment(PaymentDto paymentDto){
        ModelAndView mav = new ModelAndView();

        String messageBack = paymentService.newPayment(paymentDto);
        if( messageBack != null){
            StringBuilder url = new StringBuilder("redirect:/makePayment?message=");
            url.append(messageBack);
            mav.setViewName(url.toString());
            return mav;
        }
        mav.setViewName("redirect:/viewPayments");
        return mav;
    }

    @GetMapping("/verifyPayment")
    private ModelAndView verifyPaymentView(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("verifyPaymentList", paymentService.viewVerifyPayment());
        mav.setViewName("payment/viewVerifyPayment");
        return mav;
    }

    @PostMapping("/verifyPayment")
    private ModelAndView verifyPayment(@RequestParam(name="id")String id, @RequestParam(name="amount")String amount, HttpSession httpSession){
        ModelAndView mav = new ModelAndView();
        int index = amount.indexOf('.');
        if(index == -1){
            amount += "00";
        }else{
            String rest = amount.substring(amount.lastIndexOf(".") +1);
            if( rest.length() == 1){
                amount =amount.replace(".","");
                amount += "0";
            }else{
                amount =amount.replace(".","");
            }

        }
        String feedback = paymentService.verifyPayment(id, amount);
        if(feedback.equals("bad")){
            mav.setViewName("redirect:/verifyPayment?error=true");
            return mav;
        }
        if(feedback.equals("ok")) {
            mav.setViewName("redirect:/verifyPayment");
        }
        if(feedback.equals("external")){

            String xml = paymentService.populateXmlFromPayment(id);
            //headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-MONTRAN-RTP-Channel","INTBROB0");
            headers.set("X-MONTRAN-RTP-Version","1");

            //build the reques
            HttpEntity<String> request = new HttpEntity(xml,headers);

            //call
            ResponseEntity<String> response = restTemplate.postForEntity(messageUrl, request,String.class);


            if(response.getStatusCode() == HttpStatus.OK){
                httpSession.setAttribute("success", "Succes");
            }else{
                httpSession.setAttribute("success", "Error");
                log.warn("Call status: " +response.getStatusCode() );
            }

            String prettyResponse = ReadXmlFile.xmlFormatter(response.getBody());

            mav.setViewName("redirect:/viewResponse");
            httpSession.setAttribute("responseMessage", prettyResponse);

        }
        return mav;
    }

    @GetMapping("/approvePayment")
    private ModelAndView approvePaymentView(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("approvePaymentList", paymentService.viewApprovePayment());
        mav.setViewName("payment/viewApprovePayment");
        return mav;
    }

    @PostMapping("/approvePayment")
    private ModelAndView approvePayment(@RequestParam(name="id")String id, HttpSession httpSession) {
        ModelAndView mav = new ModelAndView();

        String feedback = paymentService.approvePayment(id);
        if(feedback.equals("bad")){
            mav.setViewName("redirect:/approvePayment?error=true");
            return mav;
        }
        if(feedback.equals("ok")) {
            mav.setViewName("redirect:/approvePayment");
        }
        if(feedback.equals("external")){

            String xml = paymentService.populateXmlFromPayment(id);

            //headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-MONTRAN-RTP-Channel","INTBROB0");
            headers.set("X-MONTRAN-RTP-Version","1");

            //build the reques
            HttpEntity<String> request = new HttpEntity(xml,headers);

            //call
            ResponseEntity<String> response = restTemplate.postForEntity(messageUrl, request,String.class);

            if(response.getStatusCode() == HttpStatus.OK){
                httpSession.setAttribute("success", "Succes");
            }else{
                httpSession.setAttribute("success", "Error");
                log.warn("Call status: " +response.getStatusCode() );
            }

            String prettyResponse = ReadXmlFile.xmlFormatter(response.getBody());

            mav.setViewName("redirect:/viewResponse");
            httpSession.setAttribute("responseMessage", prettyResponse);

        }
        return mav;
    }

    @GetMapping("/authorizePayment")
    private ModelAndView authorizePaymentView(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("authorizePaymentList", paymentService.viewAuthorizePayment());
        mav.setViewName("payment/viewAuthorizePayment");
        return mav;
    }

    @PostMapping("/authorizePayment")
    private ModelAndView authorizePayment(@RequestParam(name="id")String id, HttpSession httpSession){
        ModelAndView mav = new ModelAndView();

        String feedback = paymentService.authorizePayment(id);
        if(feedback.equals("bad")){
            mav.setViewName("redirect:/authorizePayment?error=true");
            return mav;
        }
        if(feedback.equals("ok")) {
            mav.setViewName("redirect:/authorizePayment");
        }
        if(feedback.equals("external")){

            String xml = paymentService.populateXmlFromPayment(id);
            //headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-MONTRAN-RTP-Channel","INTBROB0");
            headers.set("X-MONTRAN-RTP-Version","1");

            HttpEntity<String> request = new HttpEntity(xml,headers);

            ResponseEntity<String> response = restTemplate.postForEntity(messageUrl, request,String.class);


            if(response.getStatusCode() == HttpStatus.OK){
                httpSession.setAttribute("success", "Succes");
            }else{
                httpSession.setAttribute("success", "Error");
                log.warn("Call status: " +response.getStatusCode() );
            }

            String prettyResponse = ReadXmlFile.xmlFormatter(response.getBody());
            httpSession.setAttribute("responseMessage", prettyResponse);
            mav.setViewName("redirect:/viewResponse");


        }
        return mav;
    }

    @PostMapping("/rejectPayment")
    private ModelAndView rejectPayment(@RequestParam(name="id")String id){
        ModelAndView mav = new ModelAndView();
        paymentService.rejectPayment(id);
        mav.setViewName("redirect:/viewPayments");
        return mav;

    }

    @GetMapping("/viewPosition")
    private ModelAndView viewPosition(HttpSession httpSession){
        ModelAndView mav = new ModelAndView();
        if(httpSession.getAttribute("positionMessage") != null){
            mav.addObject("positionMessage", httpSession.getAttribute("positionMessage"));
        }
        mav.setViewName("payment/viewPosition");
        return mav;
    }

    @PostMapping("/viewPosition")
    private ModelAndView viewPositionPost(HttpSession httpSession){
        ModelAndView mav = new ModelAndView();

        ReadXmlFile.readXmlFileAsString();

        //headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-MONTRAN-RTP-Channel","INTBROB0");
        headers.set("X-MONTRAN-RTP-Version","1");

        //build the reques
        HttpEntity request = new HttpEntity(headers);

        //call
        ResponseEntity<String> response = restTemplate.exchange(positionUrl, HttpMethod.GET,request,String.class);


        if(response.getStatusCode() == HttpStatus.OK){
            httpSession.setAttribute("positionMessage", response.getBody());
        }else{
            log.warn("Call status: " +response.getStatusCode() );
        }

        mav.setViewName("redirect:/viewPosition");
        return mav;
    }

    @GetMapping("/viewResponse")
    private ModelAndView viewResponse(HttpSession httpSession){
        ModelAndView mav = new ModelAndView();
        if(httpSession.getAttribute("responseMessage") != null && httpSession.getAttribute("success") != null){
            mav.addObject("responseMessage", (String)httpSession.getAttribute("responseMessage"));
            mav.addObject("success",(String)httpSession.getAttribute("success"));

        }
        mav.setViewName("payment/viewResponse");
        return mav;

    }

    @GetMapping("/viewGeneral")
    public ModelAndView viewGeneral(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("generalList", paymentService.generalOverviewList());
        mav.setViewName("payment/viewGeneral");
        return mav;
    }



}
