package org.legenkiy.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;


import java.net.Socket;

public interface ChatService {

    void handleChatRequest();

    void processMessage() ;


}
