package com.appempatia.empatiaapi.api.resources;

import com.appempatia.empatiaapi.api.dto.UserDTO;
import com.appempatia.empatiaapi.api.exception.ApiErrors;
import com.appempatia.empatiaapi.api.exception.BusinessException;
import com.appempatia.empatiaapi.model.entity.User;
import com.appempatia.empatiaapi.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService service;
    private ModelMapper modelMapper;

    public UserController(UserService service, ModelMapper mapper) {
        this.service = service;
        this.modelMapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@RequestBody @Valid UserDTO dto){
        User entity = modelMapper.map(dto, User.class);

        entity = service.save(entity);

        return modelMapper.map(entity,UserDTO.class);
    }

    @GetMapping("{id}")
    public UserDTO get(@PathVariable Long id){

        return service.getById(id)
                .map(user ->modelMapper.map(user,UserDTO.class))
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));


    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        User user = service.getById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));;

        service.delete(user);
    }

    @PutMapping("{id}")
    public UserDTO update(@PathVariable Long id, UserDTO dto){
        return service.getById(id).map(user -> {
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            user.setPassword(dto.getPassword());
            user.setCellphone(dto.getCellphone());
            user.setRole(dto.getRole());

            user = service.update(user);

            return modelMapper.map(user,UserDTO.class);
        }).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));


    }

    @GetMapping
    public Page<UserDTO> find(UserDTO dto, Pageable pageRequest){
        User filter = modelMapper.map(dto, User.class);

        Page<User> result = service.find(filter, pageRequest);

         List<UserDTO> list = result.getContent().stream()
                .map(entity -> modelMapper.map(entity,UserDTO.class))
                .collect(Collectors.toList());

         return new PageImpl<UserDTO>(list,pageRequest,result.getTotalElements());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationExceptions(MethodArgumentNotValidException ex){
        BindingResult bindingResult = ex.getBindingResult();

        return new ApiErrors(bindingResult);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleBusinessException(BusinessException ex){
        return new ApiErrors(ex);
    }
}


