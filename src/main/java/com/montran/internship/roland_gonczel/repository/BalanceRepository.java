package com.montran.internship.roland_gonczel.repository;

import com.montran.internship.roland_gonczel.entity.Account;
import com.montran.internship.roland_gonczel.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface BalanceRepository extends JpaRepository<Balance, String> {

    List<Balance> findAllByLastUpdateBetween(Date start, Date end);

    Balance findByAccount(Account account);
}
