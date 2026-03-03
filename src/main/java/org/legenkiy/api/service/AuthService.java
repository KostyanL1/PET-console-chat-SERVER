package org.legenkiy.api.service;


import java.net.Socket;

public interface AuthService {

    void register();

    void login();

    boolean isAuthenticate();

    boolean isRegistered();

    void handShake();

}
