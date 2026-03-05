package org.legenkiy.services;

import lombok.RequiredArgsConstructor;
import org.legenkiy.api.service.AuthService;
import org.legenkiy.api.service.SenderService;
import org.legenkiy.api.service.UserService;
import org.legenkiy.connection.ConnectionsManagerImpl;
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

    private final ConnectionsManagerImpl connectionsManagerImpl;
    private final UserService userService;
    private final SenderService senderService;


    @Override
    public void register(Socket socket, Envelope envelope) {
        AuthPayload authPayload = extractAuthPayload(envelope);
        String username = authPayload.getUsername();
        String password = authPayload.getPassword();
        if (isAuthenticated(socket)) {
            if (!isRegisteredUsername(username)) {
                UserDto userDto = new UserDto();
                userDto.setUsername(username);
                userDto.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
                userService.save(userDto);
                connectionsManagerImpl.authenticate(socket, username);
            } else {
                throw new AuthException("This username registered");
            }
        } else {
            throw new AuthException("This socket authenticated");
        }

    }

    @Override
    public void login(Socket socket, Envelope envelope) {
        if (isAuthenticated(socket)) {
            AuthPayload authPayload = extractAuthPayload(envelope);
            String username = authPayload.getUsername();
            if (isRegisteredUsername(username) && isPasswordCorrect(authPayload)) {
                connectionsManagerImpl.authenticate(socket, authPayload.getUsername());
            } else {
                throw new AuthException("Username or password incorrect");
            }
        } else {
            throw new AuthException("This socket authenticated");
        }
    }

    @Override
    public boolean isAuthenticated(Socket socket) {
        Optional<ActiveConnection> activeConnection = Optional.ofNullable(connectionsManagerImpl.findConnectionBySocket(socket));
        return activeConnection.map
                        (connection -> connection.getClientState().equals(ClientState.AUTHENTICATED))
                .orElse(false);
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
        if (envelope.getVersion().equals(ProtocolVersion.current())) {
            senderService.send(
                    socket,
                    Envelope.builder()
                            .type(MessageType.HELLO_ACK)
                            .build()
            );
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
        if (payload instanceof AuthPayload) {
            return (AuthPayload) payload;
        } else {
            throw new RuntimeException("Incorrect auth payload");
        }

    }
}
