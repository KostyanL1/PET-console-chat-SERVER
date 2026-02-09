package org.legenkiy.services;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legenkiy.api.service.AuthService;
import org.legenkiy.connection.ConnectionsManager;
import org.legenkiy.enums.ClientState;
import org.legenkiy.exceptions.AuthException;
import org.legenkiy.models.ActiveConnection;
import org.springframework.stereotype.Service;

import java.net.Socket;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final static Logger LOGGER = LogManager.getLogger(AuthServiceImpl.class);

    private final ConnectionsManager connectionsManager;


    @Override
    public void login(Socket socket, String username) {
        if (!isAuthenticate(socket)){
            connectionsManager.authenticate(socket, username);
            LOGGER.info("AUTHENTICATED {}" , username);
        }else {
            throw new AuthException("AUTHENTICATED");
        }
    }



    @Override
    public boolean isAuthenticate(Socket socket) {
        LOGGER.info("CHECKING IS AUTHENTICATE {}", socket);
        Optional<ActiveConnection> activeConnection = Optional.ofNullable(connectionsManager.findConnectionBySocket(socket));
        return activeConnection.map(connection -> connection.getClientState().equals(ClientState.AUTHENTICATED)).orElse(false);
    }
}
