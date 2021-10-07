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

        User user = User.builder()
                .name("Jhon Dole")
                .email("jhon@dole.com")
                .role(1).cellphone("99999999999")
                .password("12345678")
                .build();

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

}
