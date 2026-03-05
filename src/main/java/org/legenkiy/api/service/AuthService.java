package org.legenkiy.api.service;


import org.legenkiy.protocol.message.Envelope;

import java.net.Socket;

public interface AuthService {

    void register(Socket socket, Envelope envelope);

    void login(Socket socket, Envelope envelope);

    boolean isAuthenticated(Socket socket);

    boolean isRegisteredUsername(String username);

    void handshake(Socket socket, Envelope envelope);


}
