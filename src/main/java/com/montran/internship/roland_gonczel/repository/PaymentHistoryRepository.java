package com.montran.internship.roland_gonczel.repository;

import com.montran.internship.roland_gonczel.entity.PaymentHistory;
import com.montran.internship.roland_gonczel.status.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory,String> {

    PaymentHistory findTopByPaymentIdOrderByPaymentDateDesc(String id);
    List<PaymentHistory> findAllByPaymentStatusAndWaitingResponseTrue(PaymentStatus paymentStatus);
}
