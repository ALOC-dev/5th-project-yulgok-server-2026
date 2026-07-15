package com.irummate.domain.user.repository;

import com.irummate.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    public Optional<Users> findByOauthId(String oauthId);
    public Optional<Users> findById(Long userId);
}
