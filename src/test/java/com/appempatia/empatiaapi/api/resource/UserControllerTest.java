package com.appempatia.empatiaapi.api.resource;

import com.appempatia.empatiaapi.api.dto.UserDTO;
import com.appempatia.empatiaapi.api.exception.BusinessException;
import com.appempatia.empatiaapi.model.entity.User;
import com.appempatia.empatiaapi.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasSize;
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

    private UserDTO createNewUser() {
        return UserDTO.builder().name("Jhon Dole").email("jhon@dole.com").password("123456789").role(1).cellphone("99999999999").build();
    }
}
