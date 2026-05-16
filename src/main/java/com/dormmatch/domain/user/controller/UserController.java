package com.dormmatch.domain.user.controller;

import com.dormmatch.domain.user.dto.UsersResponseDto;
import com.dormmatch.domain.user.repository.UsersRepository;
import com.dormmatch.global.response.GlobalApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController("/users")
public class UserController {

    private UsersRepository usersRepository;

    @Autowired
    public UserController(UsersRepository usersRepository){
        this.usersRepository = usersRepository;
    }

    public GlobalApiResponse<UsersResponseDto>

}
