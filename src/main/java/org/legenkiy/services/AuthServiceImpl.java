package org.legenkiy.services;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legenkiy.api.service.AuthService;
import org.legenkiy.api.service.UserService;
import org.legenkiy.connection.ConnectionsManager;
import org.legenkiy.dto.UserDto;
import org.legenkiy.enums.ClientState;
import org.legenkiy.exceptions.AuthException;
import org.legenkiy.exceptions.ObjectNotFoundException;
import org.legenkiy.models.ActiveConnection;
import org.legenkiy.protocol.dtos.AuthDto;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.net.Socket;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final static Logger LOGGER = LogManager.getLogger(AuthServiceImpl.class);

    private final ConnectionsManager connectionsManager;
    private final UserService userService;


    @Override
    public void register(Socket socket, AuthDto authDto){
        if (!isAuthenticate(socket)){
            if (!isRegistered(authDto.getUsername())){
                UserDto userDto = new UserDto();
                userDto.setUsername(userDto.getUsername());
                userDto.setPassword(BCrypt.hashpw(authDto.getPassword(), BCrypt.gensalt()));
                userService.save(userDto);


            }else {
                LOGGER.info("This username already exist {}", authDto.getUsername());
                throw new AuthException("This username already exist " + socket.getRemoteSocketAddress());
            }
        }else {
            LOGGER.info("This socket authenticated {}", socket.getRemoteSocketAddress());
            throw new AuthException("This socket authenticated " + socket.getRemoteSocketAddress());
        }

    }


    @Override
    public void login(Socket socket, String username) {
        if (!isAuthenticate(socket)){
            connectionsManager.authenticate(socket, username);
        }else {

            throw new AuthException("This socket authenticated " + socket.getRemoteSocketAddress());
        }
    }



    @Override
    public boolean isAuthenticate(Socket socket) {
        Optional<ActiveConnection> activeConnection = Optional.ofNullable(connectionsManager.findConnectionBySocket(socket));
        return activeConnection.map(connection -> connection.getClientState().equals(ClientState.AUTHENTICATED)).orElse(false);
    }

    @Override
    public boolean isRegistered(String username){
        try {
            userService.findByUsername(username);
            return true;
        }catch (ObjectNotFoundException e){
            return false;
        }
    }
}
