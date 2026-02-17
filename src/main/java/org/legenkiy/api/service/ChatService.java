package org.legenkiy.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.legenkiy.protocol.message.ClientMessage;
import org.legenkiy.protocol.message.ServerMessage;

import java.io.PrintWriter;
import java.net.Socket;

public interface ChatService {

    void processMessage(ClientMessage clientMessage, Socket clientSocket, PrintWriter printWriter) throws JsonProcessingException;

    void processMessage(ServerMessage serverMessage);

    void processMessage(ClientMessage clientMessage, ServerMessage serverMessage);

}
