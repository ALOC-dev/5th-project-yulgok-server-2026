package com.dormmatch.domain.user.repository;

import com.dormmatch.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    public Users findUserByOauthId(String oauth);
}
