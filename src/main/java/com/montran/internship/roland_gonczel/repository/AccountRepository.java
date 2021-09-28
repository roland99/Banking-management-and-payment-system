package com.montran.internship.roland_gonczel.repository;

import com.montran.internship.roland_gonczel.entity.Account;
import com.montran.internship.roland_gonczel.entity.User;
import com.montran.internship.roland_gonczel.status.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,String> {

    Account findByNumber(String number);

    Optional<Account> findById(String id);

    List<Account> findAllByStatusOrStatus(Status accountStatus1, Status accountStatus2);


}
