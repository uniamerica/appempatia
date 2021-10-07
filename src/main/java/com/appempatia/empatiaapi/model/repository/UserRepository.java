package com.appempatia.empatiaapi.model.repository;

import com.appempatia.empatiaapi.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

    boolean existsByEmail(String email);
}
