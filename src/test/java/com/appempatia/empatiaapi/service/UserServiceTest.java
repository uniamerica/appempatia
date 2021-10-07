package com.appempatia.empatiaapi.service;

import com.appempatia.empatiaapi.api.exception.BusinessException;
import com.appempatia.empatiaapi.model.entity.User;
import com.appempatia.empatiaapi.model.repository.UserRepository;
import com.appempatia.empatiaapi.service.impl.UserServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UserServiceTest {
    UserService service;

    @MockBean
    UserRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new UserServiceImpl(repository);
    }

    @Test
    @DisplayName("Should save a user")
    public void saveUserTest(){
        //cenario
        User user = createNewUser();

        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

        Mockito.when(repository.save(user)).thenReturn(User.builder()
                        .id(1l)
                        .name("Jhon Dole")
                        .email("jhon@dole.com")
                        .role(1).cellphone("99999999999")
                        .password("12345678")
                        .build());

        // execução
        User savedUser = service.save(user);

        // verificação
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Jhon Dole");
        assertThat(savedUser.getEmail()).isEqualTo("jhon@dole.com");
        assertThat(savedUser.getRole()).isEqualTo(1);
        assertThat(savedUser.getCellphone()).isEqualTo("99999999999");
        assertThat(savedUser.getPassword()).isEqualTo("12345678");
    }



    @Test
    @DisplayName("Should throw an business error when try to save a user with duplicated e-mail")
    public void shouldNotSaveUserWithDuplicatedEmail(){
        // cenário

        User user = createNewUser();
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

        //execução

        Throwable execption = Assertions.catchThrowable(()->service.save(user));

        //verificação
        assertThat(execption)
                .isInstanceOf(BusinessException.class)
                .hasMessage("E-mail já cadastrado.");

        Mockito.verify(repository,Mockito.never()).save(user);


    }

    private User createNewUser() {
        return User.builder().name("Jhon Dole").email("jhon@dole.com").role(1).cellphone("99999999999").password("12345678").build();
    }
}
