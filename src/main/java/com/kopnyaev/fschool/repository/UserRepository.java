package com.kopnyaev.fschool.repository;

import com.kopnyaev.fschool.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User getByLoginAndPassword(String login, String password);
}
