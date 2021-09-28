package com.montran.internship.roland_gonczel.repository;

import com.montran.internship.roland_gonczel.entity.User;
import com.montran.internship.roland_gonczel.status.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

     User findByUsername(String username);

     Optional<User> findById(String id);

     List<User> findAllByStatusOrStatus(Status userStatus1, Status userStatus2);
}
