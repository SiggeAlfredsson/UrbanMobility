package com.siggebig.demo.repository;

import com.siggebig.demo.DTO.LoginDto;
import com.siggebig.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    User findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsById(long id);
}
