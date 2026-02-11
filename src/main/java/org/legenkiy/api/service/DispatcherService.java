package org.legenkiy.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.legenkiy.protocol.message.ClientMessage;

import java.io.PrintWriter;
import java.net.Socket;

public interface DispatcherService {

    void handle(ClientMessage clientMessage, Socket socket, PrintWriter printWriter) throws JsonProcessingException;

}
