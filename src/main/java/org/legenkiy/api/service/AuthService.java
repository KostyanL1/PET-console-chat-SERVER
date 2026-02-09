package org.legenkiy.api.service;

import java.net.Socket;

public interface AuthService {

    void login(Socket socket, String username);
    boolean isAuthenticate(Socket socket);

}
