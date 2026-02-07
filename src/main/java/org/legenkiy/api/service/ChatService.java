package org.legenkiy.api.service;

import org.legenkiy.protocol.ClientMessage;
import org.legenkiy.protocol.ServerMessage;

public interface ChatService {

    void processMessage(ClientMessage clientMessage);

    void processMessage(ServerMessage serverMessage);

    void processMessage(ClientMessage clientMessage, ServerMessage serverMessage);

}
