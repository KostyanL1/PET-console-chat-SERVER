package org.legenkiy.api.service;


import org.legenkiy.dto.UserDto;
import org.legenkiy.models.User;

import java.util.List;

public interface UserService {

    List<User> findAll();
    User findById(Long id);
    User findByUsername(String username);
    void save(UserDto userDto);
    void update(UserDto userDto, Long id);
    void delete(String username);
    void delete(Long id);

}
