package org.legenkiy.api.dao;

import org.legenkiy.models.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    Optional<User> findByUsername(String username);

    List<User> findAll();

    Optional<User> findById(Long id);

    Long save(User newUser);

    Long update(User updateUser);

    void deleteById(Long id);
    void deleteByUsername(String username);


}
