package org.legenkiy.services;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legenkiy.api.connection.ConnectionManager;
import org.legenkiy.api.service.AuthService;
import org.legenkiy.api.service.SenderService;
import org.legenkiy.api.service.UserService;
import org.legenkiy.dto.UserDto;
import org.legenkiy.enums.ClientState;
import org.legenkiy.exceptions.AuthException;
import org.legenkiy.exceptions.ObjectNotFoundException;
import org.legenkiy.models.ActiveConnection;
import org.legenkiy.protocol.dtos.AuthPayload;
import org.legenkiy.protocol.enums.MessageType;
import org.legenkiy.protocol.message.Envelope;
import org.legenkiy.protocol.ver.ProtocolVersion;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.net.Socket;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final Logger LOGGER = LogManager.getLogger(AuthServiceImpl.class);

    private final ConnectionManager connectionsManager;
    private final UserService userService;
    private final SenderService senderService;


    @Override
    public void register(Socket socket, Envelope envelope) {
        AuthPayload authPayload = extractAuthPayload(envelope);
        String username = authPayload.getUsername();
        String password = authPayload.getPassword();
        if (!isAuthenticated(socket)) {
            if (!isRegisteredUsername(username)) {
                UserDto userDto = new UserDto();
                userDto.setUsername(username);
                userDto.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
                userService.save(userDto);
                connectionsManager.authenticate(socket, username);
                AuthPayload authPayloadForUser = new AuthPayload();
                authPayloadForUser.setUsername(username);
                senderService.send(socket,
                        Envelope.builder()
                                .type(MessageType.AUTH_OK)
                                .payload(authPayloadForUser)
                                .build()
                );
                LOGGER.info("Register success for {}", username);
            } else {
                throw new AuthException("This username registered");
            }
        } else {
            throw new AuthException("Socket is not authenticated");
        }

    }

    @Override
    public void login(Socket socket, Envelope envelope) {
        if (!isAuthenticated(socket)) {
            AuthPayload authPayload = extractAuthPayload(envelope);
            String username = authPayload.getUsername();
            if (isRegisteredUsername(username) && isPasswordCorrect(authPayload)) {
                connectionsManager.authenticate(socket, authPayload.getUsername());
                AuthPayload authPayloadForUser = new AuthPayload();
                authPayloadForUser.setUsername(username);
                senderService.send(socket,
                        Envelope.builder()
                                .type(MessageType.AUTH_OK)
                                .payload(authPayloadForUser)
                                .build()
                );
                LOGGER.info("Login success for {}", username);
            } else {
                throw new AuthException("Username or password incorrect");
            }
        } else {
            throw new AuthException("Socket is not authenticated");
        }
    }

    @Override
    public boolean isAuthenticated(Socket socket) {
        Optional<ActiveConnection> activeConnection = Optional.ofNullable(connectionsManager.findConnectionBySocket(socket));
        boolean isAuthenticated = activeConnection.map
                        (connection -> connection.getClientState().equals(ClientState.AUTHENTICATED))
                .orElse(false);
        LOGGER.info("Authentication socket {} is {}", socket.getRemoteSocketAddress(), isAuthenticated);
        return isAuthenticated;
    }

    @Override
    public boolean isRegisteredUsername(String username) {
        try {
            userService.findByUsername(username);
            return true;
        } catch (ObjectNotFoundException e) {
            return false;
        }
    }

    @Override
    public void handshake(Socket socket, Envelope envelope) {
        try {
            if (envelope.getVersion().equals(ProtocolVersion.current())) {
                senderService.send(
                        socket,
                        Envelope.builder()
                                .type(MessageType.HELLO_ACK)
                                .build()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Hand shake failed :" + e.getMessage());
        }

    }

    private boolean isPasswordCorrect(AuthPayload authPayload) {
        return BCrypt.checkpw(
                authPayload.getPassword(),
                userService.findByUsername(
                        authPayload.getUsername()).getPassword()
        );
    }

    public AuthPayload extractAuthPayload(Envelope envelope) {
        Object payload = envelope.getPayload();

        if (payload == null) {
            throw new IllegalArgumentException("Payload cannot be null");
        }

        if (!(payload instanceof AuthPayload)) {
            throw new IllegalStateException("Expected AuthPayload, but got: " + payload.getClass().getSimpleName());
        }

        return (AuthPayload) payload;
    }
}
