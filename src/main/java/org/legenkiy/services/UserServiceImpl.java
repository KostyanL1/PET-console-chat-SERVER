package org.legenkiy.services;

import lombok.RequiredArgsConstructor;
import org.legenkiy.api.dao.UserDao;
import org.legenkiy.api.service.UserService;
import org.legenkiy.dto.UserDto;
import org.legenkiy.exceptions.ObjectNotFoundException;
import org.legenkiy.models.Chat;
import org.legenkiy.models.User;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public User findById(Long id) {
        return userDao.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("User with id=" + id + " not found")
        );
    }

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username).orElseThrow(
                () -> new ObjectNotFoundException("User with username=" + username + " not found")
        );
    }

    @Override
    public void save(UserDto userDto) {
        User user = User.builder()
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .build();
        userDao.save(user);

    }

    @Override
    public void update(UserDto userDto, Long id) {
        User user = new User();
        String username;
        String password;
        List<Chat> chats;
        if ((username = userDto.getUsername()) != null) {
            user.setUsername(username);
        }
        if ((password = userDto.getPassword()) != null) {
            user.setPassword(password);
        }
        if (!(chats = userDto.getChats()).isEmpty()) {
            user.setChats(chats);
        }
        userDao.update(user);
    }

    @Override
    public void delete(String username) {
        userDao.deleteByUsername(username);
    }

    @Override
    public void delete(Long id) {
        userDao.deleteById(id);
    }
}
