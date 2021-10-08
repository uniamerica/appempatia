package com.appempatia.empatiaapi.service.impl;

import com.appempatia.empatiaapi.api.exception.BusinessException;
import com.appempatia.empatiaapi.model.entity.User;
import com.appempatia.empatiaapi.model.repository.UserRepository;
import com.appempatia.empatiaapi.service.UserService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    @Override
    public Optional<User> getById(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public void delete(User user) {
        if(user == null || user.getId() == null){
            throw new IllegalArgumentException("User id can't be null");
        }

        this.repository.delete(user);
    }

    @Override
    public User update(User user) {
        if(user == null || user.getId() == null){
            throw new IllegalArgumentException("User id can't be null");
        }

        return this.repository.save(user);
    }

    @Override
    public Page<User> find(User filter, Pageable pageRequest) {
        Example<User> example = Example.of(filter,
                ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
        );

        return repository.findAll(example,pageRequest);
    }
}
