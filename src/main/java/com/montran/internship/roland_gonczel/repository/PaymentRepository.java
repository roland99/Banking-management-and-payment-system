package com.montran.internship.roland_gonczel.repository;

import com.montran.internship.roland_gonczel.entity.Payment;
import com.montran.internship.roland_gonczel.status.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    List<Payment> findAllByOrderByPaymentDateDesc();

    List<Payment> findAllByPaymentStatus(PaymentStatus paymentStatus);
}
