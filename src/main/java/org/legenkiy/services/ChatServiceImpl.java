package org.legenkiy.services;

import lombok.RequiredArgsConstructor;
import org.legenkiy.api.service.ChatService;
import org.legenkiy.connection.ConnectionsManager;
import org.legenkiy.protocol.ClientMessage;
import org.legenkiy.protocol.ServerMessage;
import org.springframework.stereotype.Service;

import java.net.Socket;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ConnectionsManager connectionsManager;

    @Override
    public void processMessage(ClientMessage clientMessage) {
        String recpientUsername = clientMessage.getFrom();
        ServerMessage serverMessage = ServerMessage.chat(clientMessage.getFrom(), clientMessage.getContent());
        Socket recipientSocket  = connectionsManager.findConnectionByUsername(recpientUsername).getSocket();
    }

    @Override
    public void processMessage(ServerMessage serverMessage) {

    }

    @Override
    public void processMessage(ClientMessage clientMessage, ServerMessage serverMessage) {

    }
}
