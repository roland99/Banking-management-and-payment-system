package com.montran.internship.roland_gonczel.mapper;

import com.montran.internship.roland_gonczel.dto.PaymentDto;
import com.montran.internship.roland_gonczel.entity.Payment;

import java.util.UUID;

public class PaymentMapper {

    public static PaymentDto toPaymentDto(Payment payment){
         if(payment != null){
             return PaymentDto.builder()
                     .id(payment.getId())
                     .paymentType(payment.getPaymentType())
                     .paymentStatus(payment.getPaymentStatus())
                     .debitAccount(payment.getDebitAccount())
                     .creditAccount(payment.getCreditAccount())
                     .paymentDate(payment.getPaymentDate())
                     .amount(payment.getAmount())
                     .currency(payment.getCurrency())
                     .reference(payment.getReference())
                     .paymentHistory(payment.getPaymentHistory())
                     .bank(payment.getBank())
                     .fullName(payment.getFullName())
                     .message(payment.getMessage())
                     .build();
         }else{
             return new PaymentDto();
         }
    }

    public static Payment toPayment(PaymentDto paymentDto){
        if(paymentDto != null){
            return Payment.builder()
                    .id(UUID.randomUUID().toString())
                    .paymentType(paymentDto.getPaymentType())
                    .paymentStatus(paymentDto.getPaymentStatus())
                    .debitAccount(paymentDto.getDebitAccount())
                    .creditAccount(paymentDto.getCreditAccount())
                    .paymentDate(paymentDto.getPaymentDate())
                    .amount(paymentDto.getAmount())
                    .currency(paymentDto.getCurrency())
                    .reference(paymentDto.getReference())
                    .bank(paymentDto.getBank())
                    .fullName(paymentDto.getFullName())
                    .message(paymentDto.getMessage())
                    .build();
        }else{
            return new Payment();
        }
    }
}
