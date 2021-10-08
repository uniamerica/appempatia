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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @Test
    @DisplayName("Should get an user by id")
    public void getByIdTest(){
        //cenário
        Long id = 1l;
        User user = createNewUser();
        user.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(user));

        //ex

         Optional<User> foundUser = service.getById(id);

         //verify

        assertThat(foundUser.isPresent()).isTrue();
        assertThat(foundUser.get().getId()).isEqualTo(id);
        assertThat(foundUser.get().getName()).isEqualTo(user.getName());
        assertThat(foundUser.get().getEmail()).isEqualTo(user.getEmail());
        assertThat(foundUser.get().getCellphone()).isEqualTo(user.getCellphone());
        assertThat(foundUser.get().getRole()).isEqualTo(user.getRole());
        assertThat(foundUser.get().getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    @DisplayName("Should return empty when user does not exists")
    public void userNotFoundByIdTest(){
        //cenário
        Long id = 1l;

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        //ex

        Optional<User> user = service.getById(id);

        //verify

        assertThat(user.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Should delete an user")
    public void deleteUserTest(){
        User user = User.builder().id(1l).build();

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(()->service.delete(user));

        Mockito.verify(repository, Mockito.times(1)).delete(user);
    }

    @Test
    @DisplayName("Should throw error when try to delete inexistent user")
    public void deleteInvalidUserTest(){
        User user = new User();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(user));

        Mockito.verify(repository, Mockito.never()).delete(user);
    }

    @Test
    @DisplayName("Should update an user")
    public void updateUserTest(){
        Long id = 1l;

        User updatingUser = User.builder().id(id).build();

        User updatedUser = createNewUser();
        updatedUser.setId(id);

        Mockito.when(repository.save(updatingUser)).thenReturn(updatedUser);

         User user = service.update(updatingUser);

        assertThat(user.getId()).isEqualTo(updatedUser.getId());
        assertThat(user.getName()).isEqualTo(updatedUser.getName());
        assertThat(user.getEmail()).isEqualTo(updatedUser.getEmail());
        assertThat(user.getRole()).isEqualTo(updatedUser.getRole());
        assertThat(user.getCellphone()).isEqualTo(updatedUser.getCellphone());
        assertThat(user.getPassword()).isEqualTo(updatedUser.getPassword());

    }

    @Test
    @DisplayName("Should throw error when try to update inexistent user")
    public void updateInvalidUserTest(){
        User user = new User();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(user));

        Mockito.verify(repository, Mockito.never()).save(user);
    }

    @Test
    @DisplayName("Should filter user by params")
    public void findUserTest(){
        //cenario
        User user = createNewUser();

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<User> list = Arrays.asList(user);
        Page<User> page = new PageImpl<User>(list, pageRequest,1);

        Mockito.when(repository.findAll(Mockito.any(Example.class),Mockito.any(PageRequest.class)))
                .thenReturn(page);

        //execução
        Page<User> result = service.find(user, pageRequest);

        //verificações
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(list);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    private User createNewUser() {
        return User.builder().name("Jhon Dole").email("jhon@dole.com").role(1).cellphone("99999999999").password("12345678").build();
    }
}
