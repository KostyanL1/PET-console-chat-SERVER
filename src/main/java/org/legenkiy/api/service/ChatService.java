package org.legenkiy.api.service;

import org.legenkiy.protocol.ClientMessage;

public interface ChatService {

    void processMessage(ClientMessage clientMessage);


}
