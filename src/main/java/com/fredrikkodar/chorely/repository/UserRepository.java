package com.fredrikkodar.chorely.repository;

import com.fredrikkodar.chorely.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailOrUsername(String email, String username);

    List<User> findAllByParent(User user);

}
