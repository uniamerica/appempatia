package com.appempatia.empatiaapi.service;

import com.appempatia.empatiaapi.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    User save(User any);

    Optional<User> getById(Long id);

    void delete(User user);

    User update(User user);

    Page<User> find(User any, Pageable pageRequest);
}
