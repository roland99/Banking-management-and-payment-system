package com.montran.internship.roland_gonczel.entity;

import com.montran.internship.roland_gonczel.status.PaymentStatus;
import com.montran.internship.roland_gonczel.status.PaymentType;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistory extends Audit implements Comparable<PaymentHistory>{

    @Column
    private String paymentId;

    @Column
    private PaymentType paymentType;

    @Column
    private PaymentStatus paymentStatus;

    @Column
    private String debitAccount;

    @Column
    private String creditAccount;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;

    @Column
    private long amount;

    @Column
    private String currency;

    @Column
    private String reference;

    @ManyToOne
    @JoinColumn(name = "paymentHistory")
    private Payment payment;

    @Column
    private String bank;

    @Column
    private String fullName;

    @Column
    private String message;



    @Override
    public int compareTo(PaymentHistory o) {
        if (this.getDate().before(o.getDate())) {
            return 1;
        } else if (this.getDate().after(o.getDate())) {
            return -1;
        } else {
            return 0;
        }
    }



}
