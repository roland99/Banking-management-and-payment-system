package com.montran.internship.roland_gonczel.mapper;

import com.montran.internship.roland_gonczel.entity.Payment;
import com.montran.internship.roland_gonczel.entity.PaymentHistory;

public class PaymentHistoryMapper {

    public static PaymentHistory toPaymentHistory(Payment payment){
        if(payment != null){
            return PaymentHistory.builder()
                    .paymentId(payment.getId())
                    .paymentType(payment.getPaymentType())
                    .paymentStatus(payment.getPaymentStatus())
                    .debitAccount(payment.getDebitAccount())
                    .creditAccount(payment.getCreditAccount())
                    .paymentDate(payment.getPaymentDate())
                    .amount(payment.getAmount())
                    .currency(payment.getCurrency())
                    .reference(payment.getReference())
                    .message(payment.getMessage())
                    .bank(payment.getBank())
                    .fullName(payment.getFullName())
                    .build();
        }
        return new PaymentHistory();
    }
    public static Payment toPayment(PaymentHistory paymentHistory){
        if(paymentHistory != null){
            return Payment.builder()
                    .id(paymentHistory.getPaymentId())
                    .paymentType(paymentHistory.getPaymentType())
                    .paymentStatus(paymentHistory.getPaymentStatus())
                    .debitAccount(paymentHistory.getDebitAccount())
                    .creditAccount(paymentHistory.getCreditAccount())
                    .paymentDate(paymentHistory.getPaymentDate())
                    .amount(paymentHistory.getAmount())
                    .currency(paymentHistory.getCurrency())
                    .reference(paymentHistory.getReference())
                    .build();
        }
        return new Payment();

    }
}
