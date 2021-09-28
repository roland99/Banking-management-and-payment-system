package com.montran.internship.roland_gonczel.entity;

import com.montran.internship.roland_gonczel.status.PaymentStatus;
import com.montran.internship.roland_gonczel.status.PaymentType;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Payment implements Comparable<Payment>{

    @Id
    private String id;

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

    @Column(unique = true)
    private String reference;

    @OneToMany( fetch = FetchType.EAGER, mappedBy = "payment", cascade = CascadeType.ALL)
    private List<PaymentHistory> paymentHistory;

    @Column
    private String bank;

    @Column
    private String fullName;

    @Column
    private String message;


    public void addPaymentHistory(PaymentHistory history){
        if(paymentHistory == null){
            paymentHistory = new ArrayList<>();
            this.paymentHistory.add(history);
        }else{
            this.paymentHistory.add(history);
        }
    }

    @Override
    public int compareTo(Payment o) {
        if (this.getPaymentDate().before(o.getPaymentDate())) {
            return 1;
        } else if (this.getPaymentDate().after(o.getPaymentDate())) {
            return -1;
        } else {
            return 0;
        }
    }


}
