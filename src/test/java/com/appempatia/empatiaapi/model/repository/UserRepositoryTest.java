package com.appempatia.empatiaapi.model.repository;

import com.appempatia.empatiaapi.model.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    UserRepository repository;

    @Test
    @DisplayName("Should return true when exists an email on database with the same email requested")
    public void returnTrueWhenEmailExists(){
        // cenário

        String email = "jhon@dole.com";
        User user = createNewUser(email);
        entityManager.persist(user);

        //execução

        Boolean exists = repository.existsByEmail(email);

        //verificação

        assertThat(exists).isTrue();
    }



    @Test
    @DisplayName("Should return false when not exists an email on database with the same email requested")
    public void returnFalseWhenEmailDoesntExists(){
        // cenário

        String email = "jhon@dole.com";

        //execução

        Boolean exists = repository.existsByEmail(email);

        //verificação

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should return an user by id")
    public void findByIdTest(){
        // cenário

        User user = createNewUser("jhon@dole.com");
        entityManager.persist(user);

        // ex

        Optional<User> foundUser = repository.findById(user.getId());

        assertThat(foundUser.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Should save an user")
    public void saveUserTest(){
        User user = createNewUser("jhon@dole.com");

        User savedUser = repository.save(user);

        assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    @DisplayName("Should delete an user")
    public void deleteUserTest(){
        User user = createNewUser("jhon@dole.com");

        entityManager.persist(user);

         User foundUser = entityManager.find(User.class,user.getId());

         repository.delete(foundUser);

        User deletedUser = entityManager.find(User.class,user.getId());

        assertThat(deletedUser).isNull();
    }

    private User createNewUser(String email) {
        return User.builder()
                .name("Jhon Dole")
                .email(email)
                .role(1).cellphone("99999999999")
                .password("12345678")
                .build();
    }
}
