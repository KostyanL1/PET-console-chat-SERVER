package org.legenkiy.services;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legenkiy.api.service.AuthService;
import org.legenkiy.api.service.UserService;
import org.legenkiy.connection.ConnectionsManagerImpl;
import org.legenkiy.dto.UserDto;
import org.legenkiy.enums.ClientState;
import org.legenkiy.exceptions.AuthException;
import org.legenkiy.exceptions.ObjectNotFoundException;
import org.legenkiy.models.ActiveConnection;
import org.legenkiy.protocol.dtos.AuthDto;
import org.legenkiy.protocol.message.ClientMessage;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.net.Socket;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final static Logger LOGGER = LogManager.getLogger(AuthServiceImpl.class);

    private final ConnectionsManagerImpl connectionsManagerImpl;
    private final UserService userService;


    @Override
    public void register(Socket socket, AuthDto authDto) {
        if (!isAuthenticate(socket)) {
            if (!isRegistered(authDto.getUsername())) {
                UserDto userDto = new UserDto();
                userDto.setUsername(authDto.getUsername());
                userDto.setPassword(BCrypt.hashpw(authDto.getPassword(), BCrypt.gensalt()));
                userService.save(userDto);
                connectionsManagerImpl.authenticate(socket, authDto.getUsername());
            } else {
                LOGGER.info("This username already exist {}", authDto.getUsername());
                throw new AuthException("This username already exist " + authDto.getUsername());
            }
        } else {
            LOGGER.info("This socket authenticated {}", socket.getRemoteSocketAddress());
            throw new AuthException("This socket authenticated " + socket.getRemoteSocketAddress());
        }

    }

    @Override
    public void login(Socket socket, AuthDto authDto) {
        if (!isAuthenticate(socket)) {
            String username = authDto.getUsername();
            if (isRegistered(username)) {
                if (isPasswordCorrect(authDto)) {
                    connectionsManagerImpl.authenticate(socket, authDto.getUsername());
                } else {
                    LOGGER.info("Password incorrect for username : {}", username);
                    throw new AuthException("Password incorrect for username : " + username);
                }
            } else {
                LOGGER.info("This username doesn`t exist {}", username);
                throw new AuthException("This username doesn`t exist " + username);
            }
        } else {
            LOGGER.info("This socket authenticated {}", socket.getRemoteSocketAddress());
            throw new AuthException("This socket authenticated " + socket.getRemoteSocketAddress());
        }
    }

    @Override
    public boolean isAuthenticate(Socket socket) {
        Optional<ActiveConnection> activeConnection = Optional.ofNullable(connectionsManagerImpl.findConnectionBySocket(socket));
        return activeConnection.map(connection -> connection.getClientState().equals(ClientState.AUTHENTICATED)).orElse(false);
    }

    @Override
    public boolean isRegistered(String username) {
        try {
            userService.findByUsername(username);
            return true;
        } catch (ObjectNotFoundException e) {
            return false;
        }
    }

    @Override
    public void handShake(ClientMessage clientMessage){

    }



    private boolean isPasswordCorrect(AuthDto authDto) {
        return BCrypt.checkpw(authDto.getPassword(), userService.findByUsername(authDto.getUsername()).getPassword());
    }
}
