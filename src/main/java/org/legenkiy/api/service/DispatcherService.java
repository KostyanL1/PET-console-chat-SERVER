package org.legenkiy.api.service;

import org.legenkiy.protocol.ClientMessage;

import java.io.PrintWriter;
import java.net.Socket;

public interface DispatcherService {

    void handle(ClientMessage clientMessage, Socket socket, PrintWriter printWriter);

}
