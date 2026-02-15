package org.legenkiy.api.service;

import org.legenkiy.protocol.dtos.AuthDto;

import java.net.Socket;

public interface AuthService {

    void register(Socket socket, AuthDto authDto);
    void login(Socket socket, String username);
    boolean isAuthenticate(Socket socket);
    boolean isRegistered(String username);

}
