package com.montran.internship.roland_gonczel.repository;

import com.montran.internship.roland_gonczel.entity.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserHistoryRepository extends JpaRepository<UserHistory,String> {

    List<UserHistory> findAllByWaitingResponseTrue();

}
