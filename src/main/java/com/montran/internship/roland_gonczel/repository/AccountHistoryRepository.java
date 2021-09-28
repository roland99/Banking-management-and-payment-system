package com.montran.internship.roland_gonczel.repository;

import com.montran.internship.roland_gonczel.entity.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountHistoryRepository extends JpaRepository<AccountHistory,String> {

    List<AccountHistory> findAllByWaitingResponseTrue();
}
