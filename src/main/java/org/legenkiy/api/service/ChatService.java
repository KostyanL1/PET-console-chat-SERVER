package org.legenkiy.api.service;

import org.legenkiy.protocol.message.ClientMessage;
import org.legenkiy.protocol.message.ServerMessage;

import java.io.PrintWriter;
import java.net.Socket;

public interface ChatService {

    void processMessage(ClientMessage clientMessage, Socket clientSocket, PrintWriter printWriter);

    void processMessage(ServerMessage serverMessage);

    void processMessage(ClientMessage clientMessage, ServerMessage serverMessage);

}
