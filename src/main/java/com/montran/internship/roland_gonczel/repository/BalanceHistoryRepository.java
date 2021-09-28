package com.montran.internship.roland_gonczel.repository;

import com.montran.internship.roland_gonczel.entity.BalanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BalanceHistoryRepository extends JpaRepository<BalanceHistory,String> {

    List<BalanceHistory> findAllByWaitingResponseTrue();
}
