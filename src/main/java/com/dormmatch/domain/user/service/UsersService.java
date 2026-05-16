package com.dormmatch.domain.user.service;

import com.dormmatch.domain.user.dto.UsersRequestDto;
import com.dormmatch.domain.user.dto.UsersResponseDto;
import com.dormmatch.domain.user.entity.Users;
import com.dormmatch.domain.user.repository.UsersRepository;
import com.dormmatch.global.response.GlobalApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

@Service
public class UsersService {

    private UsersRepository usersRepository;

    @Autowired
    public UsersService(UsersRepository usersRepository){
        this.usersRepository = usersRepository;
    }


    public GlobalApiResponse<UsersResponseDto> createUser(UsersRequestDto usersRequestDto){

        usersRepository


    }

}
