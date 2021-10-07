package com.appempatia.empatiaapi.service.impl;

import com.appempatia.empatiaapi.api.exception.BusinessException;
import com.appempatia.empatiaapi.model.entity.User;
import com.appempatia.empatiaapi.model.repository.UserRepository;
import com.appempatia.empatiaapi.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        if(repository.existsByEmail(user.getEmail())){
            throw  new BusinessException("E-mail já cadastrado.");
        }

        return repository.save(user);
    }
}
