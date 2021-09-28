package com.montran.internship.roland_gonczel.service;

import com.montran.internship.roland_gonczel.dto.PaymentDto;
import com.montran.internship.roland_gonczel.entity.*;
import com.montran.internship.roland_gonczel.mapper.PaymentHistoryMapper;
import com.montran.internship.roland_gonczel.mapper.PaymentMapper;
import com.montran.internship.roland_gonczel.repository.PaymentHistoryRepository;
import com.montran.internship.roland_gonczel.repository.PaymentRepository;
import com.montran.internship.roland_gonczel.status.Operation;
import com.montran.internship.roland_gonczel.status.PaymentStatus;
import com.montran.internship.roland_gonczel.status.PaymentType;
import com.montran.internship.roland_gonczel.utility.GeneralOverview;
import com.montran.internship.roland_gonczel.utility.ReadXmlFile;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    PaymentHistoryRepository paymentHistoryRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    UserService userService;


    public PaymentDto findById(String id){
        Optional<Payment> p = paymentRepository.findById(id);
        Payment payment = Optional.ofNullable(p).map(o->o.get()).orElse(null);
        if(payment != null){
            PaymentDto paymentDto = PaymentMapper.toPaymentDto(payment);
            log.info("Payment with reference: " + payment.getReference() + " is returned");
            return paymentDto;
        }else{
            log.warn("No payment with that id");
            return new PaymentDto();
        }
    }

    public List<PaymentDto> viewAllPayments(){
        List<PaymentDto> paymentDtoList = new ArrayList<>();
        for(Payment p: paymentRepository.findAllByOrderByPaymentDateDesc()){
            paymentDtoList.add(PaymentMapper.toPaymentDto(p));
        }
        return paymentDtoList;

    }

    private List<PaymentHistory> viewPaymentByStatus(PaymentStatus paymentStatus){
        List<PaymentHistory> paymentHistoryList = paymentHistoryRepository.findAllByPaymentStatusAndWaitingResponseTrue(paymentStatus);
        return paymentHistoryList;
    }

    public List<PaymentHistory> viewVerifyPayment(){
        return viewPaymentByStatus(PaymentStatus.VERIFY);
    }

    public List<PaymentHistory> viewApprovePayment(){
        return viewPaymentByStatus(PaymentStatus.APPROVE);
    }

    public List<PaymentHistory> viewAuthorizePayment(){
        return viewPaymentByStatus(PaymentStatus.AUTHORIZE);
    }

    public String newPaymentReference(){
        String reference = RandomStringUtils.randomNumeric(8);
        return reference;
    }

    public String newPayment(PaymentDto paymentDto){
        StringBuilder messageBack = new StringBuilder();
        if(paymentDto != null){
            //replace the empty space between characters
            paymentDto.setDebitAccount(paymentDto.getDebitAccount().replaceAll("\\s+",""));
            paymentDto.setCreditAccount(paymentDto.getCreditAccount().replaceAll("\\s+",""));
            if(PaymentType.EXTERNAL.ordinal() == paymentDto.getPaymentTypeNumber()){
                paymentDto.setPaymentType(PaymentType.EXTERNAL);
            }else{
                paymentDto.setPaymentType(PaymentType.INTERNAL);
            }

            if(accountService.validateAccountsForPayment(paymentDto.getDebitAccount(),paymentDto.getCreditAccount(), messageBack, paymentDto.getPaymentType())) {
                if (paymentDto.getAmount() > 0) {

                    accountService.initializePayment(paymentDto.getDebitAccount(),paymentDto.getCreditAccount(),paymentDto.getAmount(), paymentDto.getPaymentType());

                    paymentDto.setPaymentStatus(PaymentStatus.VERIFY);
                    paymentDto.setPaymentDate(new Timestamp(System.currentTimeMillis()));
                    paymentDto.setCurrency(accountService.findByNumber(paymentDto.getDebitAccount()).getCurrency());
                    Payment payment = PaymentMapper.toPayment(paymentDto);
                    saveWithHistory(payment, Operation.ADD, true);
                }

            }else{
                return messageBack.toString();
            }
        }
        return messageBack.toString();
    }

    private void saveWithHistory(Payment payment, Operation operation, boolean waitResponse){
        PaymentHistory paymentHistory = PaymentHistoryMapper.toPaymentHistory(payment);
        paymentHistory.setInitiatorUsername(userService.getSessionUserUsername());
        paymentHistory.setOperation(operation);
        paymentHistory.setWaitingResponse(waitResponse);
        paymentHistory.setDate(new Timestamp(System.currentTimeMillis()));
        paymentHistory.setPayment(payment);

        payment.addPaymentHistory(paymentHistory);
        paymentRepository.saveAndFlush(payment);
    }

    public String verifyPayment(String id, String amount){
        PaymentHistory paymentHistory = paymentHistoryRepository.findTopByPaymentIdOrderByPaymentDateDesc(id);
        if(paymentHistory != null){
            if(paymentHistory.getAmount() == Long.parseLong(amount)){
                 if(Long.parseLong(amount) > 100000 ){
                     paymentHistory.getPayment().setPaymentStatus(PaymentStatus.APPROVE);
                     paymentHistory.setWaitingResponse(false);
                     saveWithHistory(paymentHistory.getPayment(),Operation.MODIFY,true);
                     log.info("Payment needs to be approved");
                     return "ok";
                 }
                 Balance balance = accountService.findByNumber(paymentHistory.getDebitAccount()).getAccountBalance();
                 if(balance.getAvailableAmountCredit() - balance.getAvailableAmountDebit() - paymentHistory.getAmount() < 0 ){
                     paymentHistory.getPayment().setPaymentStatus(PaymentStatus.AUTHORIZE);
                     paymentHistory.setWaitingResponse(false);
                     saveWithHistory(paymentHistory.getPayment(),Operation.MODIFY,true);
                     log.info("Payment needs to be authorized");
                     return "ok";
                 }


                accountService.finalizePayment(paymentHistory.getDebitAccount(), paymentHistory.getCreditAccount(), paymentHistory.getAmount(), paymentHistory.getPaymentType());

                paymentHistory.getPayment().setPaymentStatus(PaymentStatus.COMPLETED);
                paymentHistory.setWaitingResponse(false);
                saveWithHistory(paymentHistory.getPayment(),Operation.APROVED, false);
                log.info("Payment was verified and sent");
                if(paymentHistory.getPaymentType().equals(PaymentType.INTERNAL)){
                    return "ok";
                }else{
                    return "external";
                }

            }
        }
        return "bad";
    }

    public String approvePayment(String id){
        PaymentHistory paymentHistory = paymentHistoryRepository.findTopByPaymentIdOrderByPaymentDateDesc(id);
        if( !userService.getSessionUserUsername().equals(paymentHistory.getInitiatorUsername())) {
            if (paymentHistory != null) {
                Balance balance = accountService.findByNumber(paymentHistory.getDebitAccount()).getAccountBalance();
                if (balance.getAvailableAmountCredit() - balance.getAvailableAmountDebit() - paymentHistory.getAmount() < 0) {
                    paymentHistory.getPayment().setPaymentStatus(PaymentStatus.AUTHORIZE);
                    paymentHistory.setWaitingResponse(false);
                    saveWithHistory(paymentHistory.getPayment(), Operation.MODIFY, true);
                    log.info("Payment was approved and needs to be authorized");
                    return "ok";
                }
                accountService.finalizePayment(paymentHistory.getDebitAccount(),paymentHistory.getCreditAccount(),paymentHistory.getAmount(),paymentHistory.getPaymentType());

                paymentHistory.getPayment().setPaymentStatus(PaymentStatus.COMPLETED);
                paymentHistory.setWaitingResponse(false);
                saveWithHistory(paymentHistory.getPayment(), Operation.APROVED, false);
                log.info("Payment was approved and sent");
                if(paymentHistory.getPaymentType().equals(PaymentType.EXTERNAL)){
                    return "external";
                }else{
                    return "ok";
                }

            }
        }
        return "bad";
    }

    public String authorizePayment(String id){
        PaymentHistory paymentHistory = paymentHistoryRepository.findTopByPaymentIdOrderByPaymentDateDesc(id);
        if(!userService.getSessionUserUsername().equals(paymentHistory.getInitiatorUsername())) {
            if (paymentHistory != null) {
                accountService.finalizePayment(paymentHistory.getDebitAccount(),paymentHistory.getCreditAccount(),paymentHistory.getAmount(),paymentHistory.getPaymentType());

                paymentHistory.getPayment().setPaymentStatus(PaymentStatus.COMPLETED);
                paymentHistory.setWaitingResponse(false);
                saveWithHistory(paymentHistory.getPayment(), Operation.APROVED, false);
                log.info("Payment was authorized and sent");
                if(paymentHistory.getPaymentType().equals(PaymentType.EXTERNAL)){
                    return "external";
                }else{
                    return "ok";
                }
            }
        }
        return "bad";
    }

    public void rejectPayment(String id){
        PaymentHistory paymentHistory = paymentHistoryRepository.findTopByPaymentIdOrderByPaymentDateDesc(id);
        if(paymentHistory != null){
            accountService.cancelPayment(paymentHistory.getDebitAccount(),paymentHistory.getCreditAccount(),paymentHistory.getAmount(), paymentHistory.getPaymentType());
            paymentHistory.getPayment().setPaymentStatus(PaymentStatus.CANCELLED);
            paymentHistory.setWaitingResponse(false);
            saveWithHistory(paymentHistory.getPayment(),Operation.REJECTED, false);
            log.info("Payment rejected");
        }
    }

    public String populateXmlFromPayment(String id){
        Optional<Payment> p = paymentRepository.findById(id);
        Payment payment = Optional.ofNullable(p).map(o->o.get()).orElse(null);
        if(payment != null){
            String amount = Long.toString(payment.getAmount());
            amount = amount.substring(0, amount.length()-2) + "." + amount.substring(amount.length()-2);

            String xml = ReadXmlFile.fillXml(payment);

            xml = xml.replace("$AMOUNT$", amount);
            xml = xml.replace("$DEBTOR NAME$", accountService.findByNumber(payment.getDebitAccount()).getName());

            
            return xml;
        }

        return  new String();
    }

    public List<GeneralOverview> generalOverviewList(){
        List<Payment> list = paymentRepository.findAll();
        GeneralOverview  verify = new GeneralOverview(PaymentStatus.VERIFY, 0 ,0);
        GeneralOverview  approve = new GeneralOverview(PaymentStatus.APPROVE, 0 ,0);
        GeneralOverview  authorize = new GeneralOverview(PaymentStatus.AUTHORIZE, 0 ,0);
        GeneralOverview  complete = new GeneralOverview(PaymentStatus.COMPLETED, 0 ,0);
        GeneralOverview  cancel = new GeneralOverview(PaymentStatus.CANCELLED, 0 ,0);
        List<GeneralOverview> generalList = new ArrayList<>();

        for(Payment p : list){
            if(p.getPaymentStatus().equals(PaymentStatus.VERIFY)){
                verify.setAmount(verify.getAmount() + p.getAmount());
                verify.setCount(verify.getCount() + 1);
            }
            if(p.getPaymentStatus().equals(PaymentStatus.APPROVE)){
                approve.setAmount(approve.getAmount() + p.getAmount());
                approve.setCount(approve.getCount() + 1);
            }
            if(p.getPaymentStatus().equals(PaymentStatus.AUTHORIZE)){
                authorize.setAmount(authorize.getAmount() + p.getAmount());
                authorize.setCount(authorize.getCount() + 1);
            }
            if(p.getPaymentStatus().equals(PaymentStatus.COMPLETED)){
                complete.setAmount(complete.getAmount() + p.getAmount());
                complete.setCount(complete.getCount() + 1);
            }
            if(p.getPaymentStatus().equals(PaymentStatus.CANCELLED)){
                cancel.setAmount(cancel.getAmount() + p.getAmount());
                cancel.setCount(cancel.getCount() + 1);
            }
        }
        generalList.add(verify);
        generalList.add(approve);
        generalList.add(authorize);
        generalList.add(complete);
        generalList.add(cancel);

        return generalList;
    }


}
