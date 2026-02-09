package org.legenkiy.api.service;

import org.legenkiy.protocol.ClientMessage;
import org.legenkiy.protocol.ServerMessage;

import java.io.PrintWriter;
import java.net.Socket;

public interface ChatService {

    void processMessage(ClientMessage clientMessage, Socket clientSocket, PrintWriter printWriter);

    void processMessage(ServerMessage serverMessage);

    void processMessage(ClientMessage clientMessage, ServerMessage serverMessage);

}
