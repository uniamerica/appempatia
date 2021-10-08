package com.appempatia.empatiaapi.api.resource;

import com.appempatia.empatiaapi.api.dto.UserDTO;
import com.appempatia.empatiaapi.api.exception.BusinessException;
import com.appempatia.empatiaapi.model.entity.User;
import com.appempatia.empatiaapi.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class UserControllerTest {

    static String USER_API = "/api/users";

    @Autowired
    MockMvc mvc;

    @MockBean
    UserService service;

    @Test
    @DisplayName("Should create a new user")
    public void createUserTest() throws Exception {
        UserDTO dto = createNewUser();

        User savedUser = User.builder().id(1l).name("Jhon Dole").email("jhon@dole.com").password("123456789").role(1).cellphone("99999999999").build();

        BDDMockito.given(service.save(Mockito.any(User.class))).willReturn(savedUser);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(USER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1l))
                .andExpect(jsonPath("name").value(dto.getName()))
                .andExpect(jsonPath("email").value(dto.getEmail()))
                .andExpect(jsonPath("password").value(dto.getPassword()))
                .andExpect(jsonPath("role").value(dto.getRole()))
                .andExpect(jsonPath("cellphone").value(dto.getCellphone()));
    }



    @Test
    @DisplayName("Should return a validation error when has an empty field")
    public void createInvalidUserTest() throws Exception{
        String json = new ObjectMapper().writeValueAsString(new UserDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(USER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(4)));

    }

    @Test
    @DisplayName("Should not create a user with duplicated e-mail")
    public void createUserWithDuplicatedEmail() throws Exception{
        String errorMessage =  "E-mail já cadastrado.";

        UserDTO user = createNewUser();

        String json = new ObjectMapper().writeValueAsString(user);

        BDDMockito.given(service.save(Mockito.any(User.class)))
                .willThrow(new BusinessException(errorMessage));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(USER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors",hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(errorMessage));
    }

    @Test
    @DisplayName("Should get informations of an user")
    public void getUserDetailTest() throws Exception{
        // cenário
        Long id = 1l;

        User user = User.builder().id(id)
                .name(createNewUser().getName())
                .email(createNewUser().getEmail())
                .role(1).cellphone(createNewUser().getCellphone())
                .password(createNewUser().getPassword())
                .build();

        BDDMockito.given(service.getById(id)).willReturn(Optional.of(user));

        // execução

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(USER_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        // verificação

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("name").value(createNewUser().getName()))
                .andExpect(jsonPath("email").value(createNewUser().getEmail()))
                .andExpect(jsonPath("password").value(createNewUser().getPassword()))
                .andExpect(jsonPath("role").value(createNewUser().getRole()))
                .andExpect(jsonPath("cellphone").value(createNewUser().getCellphone()));
    }

    @Test
    @DisplayName("Should return not found when user does not exists")
    public void userNotFoundTest() throws Exception{
        BDDMockito.given(service.getById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(USER_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should remove an user")
    public void deleteUserTest() throws Exception{
        // cenário
        BDDMockito.given(service.getById(anyLong())).willReturn(Optional.of(User.builder().id(1l).build()));

        // execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(USER_API.concat("/" + 1));


        // verificação

        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return user not found when try to remove unexistable user")
    public void deleteInexistentUserTest() throws Exception{
        // cenário
        BDDMockito.given(service.getById(anyLong())).willReturn(Optional.empty());

        // execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(USER_API.concat("/" + 1));


        // verificação

        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update user details")
    public void updateUserTest() throws Exception{
        // cenário
        Long id = 1l;

        String json = new ObjectMapper().writeValueAsString(createNewUser());

        User updatingUser = User.builder()
                .id(1l)
                .name("Jhony Duly")
                .email("jhony@dule.com")
                .cellphone("123456789")
                .role(1)
                .password("123456789")
                .build();

        BDDMockito.given( service.getById(id) )
                .willReturn(Optional.of(updatingUser));

        User updatedUser = User.builder().id(id).name("Jhon Dole").email("jhon@dole.com").password("123456789").role(1).cellphone("99999999999").build();

        BDDMockito
                .given(service.update(updatingUser))
                .willReturn(updatedUser);

        // execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(USER_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);




        // verificação

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("name").value(createNewUser().getName()))
                .andExpect(jsonPath("email").value("jhon@dole.com"))
                .andExpect(jsonPath("password").value(createNewUser().getPassword()))
                .andExpect(jsonPath("role").value(createNewUser().getRole()))
                .andExpect(jsonPath("cellphone").value(createNewUser().getCellphone()));
    }

    @Test
    @DisplayName("Should return an error when book is not found")
    public void updateInexistentUserTest() throws Exception{
        // cenário

        String json = new ObjectMapper().writeValueAsString(createNewUser());

        User updatingUser = User.builder()
                .id(1l)
                .name("Jhony Duly")
                .email("jhony@dule.com")
                .cellphone("123456789")
                .role(1)
                .password("123456789")
                .build();

        BDDMockito.given( service.getById(anyLong()) )
                .willReturn(Optional.empty());

        // execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(USER_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        // verificação

        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should filter users")
    public void findUserTest() throws Exception{
        Long id = 1l;

        User user = User.builder()
                .id(id)
                .name(createNewUser().getName())
                .email(createNewUser().getEmail())
                .cellphone(createNewUser().getCellphone())
                .password(createNewUser().getPassword())
                .role(createNewUser().getRole())
                .build();

        BDDMockito.given(service.find(Mockito.any(User.class),Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<User>(Arrays.asList(user), PageRequest.of(0,100),1));

        String queryString = String.format("?email=%s&name=%s&page=0&size=100",
                user.getEmail(),
                user.getName());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(USER_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }



    private UserDTO createNewUser() {
        return UserDTO.builder().name("Jhon Dole").email("jhon@dole.com").password("123456789").role(1).cellphone("99999999999").build();
    }
}
