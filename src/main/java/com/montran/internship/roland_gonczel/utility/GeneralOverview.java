package com.montran.internship.roland_gonczel.utility;

import com.montran.internship.roland_gonczel.status.PaymentStatus;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GeneralOverview {

    private PaymentStatus paymentStatus;

    private int count;

    private long amount;
}
