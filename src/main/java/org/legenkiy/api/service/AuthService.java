package org.legenkiy.api.service;

import org.legenkiy.protocol.dtos.AuthDto;
import org.legenkiy.protocol.message.ClientMessage;

import java.net.Socket;

public interface AuthService {

    void register(Socket socket, AuthDto authDto);
    void login(Socket socket, AuthDto authDto);
    boolean isAuthenticate(Socket socket);
    boolean isRegistered(String username);
    void handShake(ClientMessage clientMessage);

}
