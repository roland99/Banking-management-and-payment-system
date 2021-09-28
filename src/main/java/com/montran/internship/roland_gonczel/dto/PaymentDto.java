package com.montran.internship.roland_gonczel.dto;

import com.montran.internship.roland_gonczel.entity.PaymentHistory;
import com.montran.internship.roland_gonczel.status.PaymentStatus;
import com.montran.internship.roland_gonczel.status.PaymentType;
import lombok.*;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    private String id;
    private PaymentType paymentType;
    private int paymentTypeNumber;
    private PaymentStatus paymentStatus;
    private String debitAccount;
    private String creditAccount;
    private Date paymentDate;
    private long amount;
    private String currency;
    private String reference;
    private List<PaymentHistory> paymentHistory;
    private String bank;
    private String fullName;
    private String message;
}
